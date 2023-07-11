package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.CustomerDao;
import bit.project.server.entity.*;
import bit.project.server.util.dto.PageQuery;
import bit.project.server.util.dto.ResourceLink;
import bit.project.server.util.exception.ConflictException;
import bit.project.server.util.exception.DataValidationException;
import bit.project.server.util.exception.ObjectNotFoundException;
import bit.project.server.util.helper.CodeGenerator;
import bit.project.server.util.helper.PageHelper;
import bit.project.server.util.helper.PersistHelper;
import bit.project.server.util.security.AccessControlManager;
import bit.project.server.util.validation.EntityValidator;
import bit.project.server.util.validation.ValidationErrorBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "tocreation");

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private AccessControlManager accessControlManager;

    @Autowired
    private CodeGenerator codeGenerator;
    private final CodeGenerator.CodeGeneratorConfig codeConfig;

    public CustomerController() {
        codeConfig = new CodeGenerator.CodeGeneratorConfig("customer");
        codeConfig.setColumnName("code");
        codeConfig.setLength(10);
        codeConfig.setPrefix("CU");
        codeConfig.setYearlyRenew(true);
    }

    @GetMapping
    public Page<Customer> getAll(PageQuery pageQuery, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to get all customers", UsecaseList.GET_ALL_CUSTOMERS);

        if (pageQuery.isEmptySearch()) {
            return customerDao.findAll(PageRequest.of(pageQuery.getPage(), pageQuery.getSize(), DEFAULT_SORT));
        }


        String name = pageQuery.getSearchParam("name");
        String nic = pageQuery.getSearchParam("nic");
        String contact = pageQuery.getSearchParam("contact");

        List<Customer> customers = customerDao.findAll(DEFAULT_SORT);
        Stream<Customer> stream = customers.parallelStream();

        List<Customer> filteredCustomers = stream.filter(customer -> {

            if (name != null)
                if (!customer.getName().toLowerCase().contains(name.toLowerCase())) return false;
            if (nic != null)
                if (!customer.getNic().toLowerCase().contains(nic.toLowerCase())) return false;
            if (contact != null) {
                boolean crit1 = customer.getContact1().toLowerCase().contains(contact.toLowerCase());
                boolean crit2 = false;
                if (customer.getContact2() != null)
                    crit2 = customer.getContact2().toLowerCase().contains(contact.toLowerCase());
                if (!crit1 && !crit2) return false;
            }

            return true;
        }).collect(Collectors.toList());

        return PageHelper.getAsPage(filteredCustomers, pageQuery.getPage(), pageQuery.getSize());
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable Integer id, HttpServletRequest request) throws InterruptedException {
        accessControlManager.authorize(request, "No privilege to get details of a customer", UsecaseList.GET_CUSTOMER);

        Optional<Customer> optionalCustomer = customerDao.findById(id);
        if (optionalCustomer.isEmpty()) throw new ObjectNotFoundException("Customer not found");
        return optionalCustomer.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to get details of a customer", UsecaseList.DELETE_CUSTOMER);

        try {
            if (customerDao.existsById(id)) customerDao.deleteById(id);
        } catch (DataIntegrityViolationException | RollbackException e) {
            throw new ConflictException("Cannot delete. Because this customer already used in another module");
        }
    }

    @PostMapping
    public ResourceLink add(@RequestBody Customer customer, HttpServletRequest request) throws InterruptedException {
        User authUser = accessControlManager.authorize(request, "No privilege to add new customer", UsecaseList.ADD_CUSTOMER);

        customer.setTocreation(LocalDateTime.now());
        customer.setCreator(authUser);
        EntityValidator.validate(customer);
        customer.setId(null);
        customer.setCustomertype(new Customertype(1));
        customer.setRegdate(LocalDate.now());


        ValidationErrorBag errorBag = new ValidationErrorBag();
        Customer customerByContact1 = customerDao.findByContact1(customer.getContact1());
        if (customerByContact1 != null) errorBag.add("contact1", "Contact number already exists");
        if (errorBag.count() > 0) throw new DataValidationException(errorBag);

        PersistHelper.save(() -> {
            customer.setCode(codeGenerator.getNextId(codeConfig));
            return customerDao.save(customer);
        });

        return new ResourceLink(customer.getId(), "/customers/" + customer.getId());
    }

    @PutMapping("/{id}")
    public ResourceLink update(@PathVariable Integer id, @RequestBody Customer customer, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to update customer details", UsecaseList.UPDATE_CUSTOMER);

        Optional<Customer> optionalCustomer = customerDao.findById(id);
        if (optionalCustomer.isEmpty()) throw new ObjectNotFoundException("Customer not found");
        Customer oldCustomer = optionalCustomer.get();

        customer.setId(id);
        customer.setCreator(oldCustomer.getCreator());
        customer.setTocreation(oldCustomer.getTocreation());
        customer.setCode(oldCustomer.getCode());
        customer.setRegdate(oldCustomer.getRegdate());
        customer.setCustomertype(oldCustomer.getCustomertype());

        EntityValidator.validate(customer);

        ValidationErrorBag errorBag = new ValidationErrorBag();
        Customer customerByContact1 = customerDao.findByContact1(customer.getContact1());
        if (customerByContact1 != null)
            if (!customerByContact1.getId().equals(id)) errorBag.add("contact1", "Contact number already exists");
        if (errorBag.count() > 0) throw new DataValidationException(errorBag);

        customer = customerDao.save(customer);
        return new ResourceLink(customer.getId(), "/customers/" + customer.getId());
    }
}
