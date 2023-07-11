package bit.project.server.controller;


import bit.project.server.UsecaseList;
import bit.project.server.dao.SupplierDao;
import bit.project.server.dao.SupplierDao;
import bit.project.server.entity.Supplier;
import bit.project.server.entity.Customertype;
import bit.project.server.entity.Supplierstatus;
import bit.project.server.entity.User;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin
@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "tocreation");

    @Autowired
    private SupplierDao supplierDao;

    @Autowired
    private AccessControlManager accessControlManager;

    @Autowired
    private CodeGenerator codeGenerator;
    private final CodeGenerator.CodeGeneratorConfig codeConfig;

    public SupplierController() {
        codeConfig = new CodeGenerator.CodeGeneratorConfig("supplier");
        codeConfig.setColumnName("code");
        codeConfig.setLength(10);
        codeConfig.setPrefix("SU");
        codeConfig.setYearlyRenew(true);
    }

    @GetMapping
    public Page<Supplier> getAll(PageQuery pageQuery, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to get all suppliers", UsecaseList.GET_ALL_SUPPLIERS);

        if (pageQuery.isEmptySearch()) {
            return supplierDao.findAll(PageRequest.of(pageQuery.getPage(), pageQuery.getSize(), DEFAULT_SORT));
        }


        String name = pageQuery.getSearchParam("name");
        String contact = pageQuery.getSearchParam("contact");

        List<Supplier> suppliers = supplierDao.findAll(DEFAULT_SORT);
        Stream<Supplier> stream = suppliers.parallelStream();

        List<Supplier> filteredCustomers = stream.filter(supplier -> {

            if (name != null)
                if (!supplier.getName().toLowerCase().contains(name.toLowerCase())) return false;

            if (contact != null) {
                boolean crit1 = supplier.getContact1().toLowerCase().contains(contact.toLowerCase());
                boolean crit2 = false;
                if (supplier.getContact2() != null)
                    crit2 = supplier.getContact2().toLowerCase().contains(contact.toLowerCase());
                if (!crit1 && !crit2) return false;
            }

            return true;
        }).collect(Collectors.toList());

        return PageHelper.getAsPage(filteredCustomers, pageQuery.getPage(), pageQuery.getSize());
    }

    @GetMapping("/{id}")
    public Supplier get(@PathVariable Integer id, HttpServletRequest request) throws InterruptedException {
        accessControlManager.authorize(request, "No privilege to get details of a supplier", UsecaseList.GET_SUPPLIER);

        Optional<Supplier> optionalCustomer = supplierDao.findById(id);
        if (optionalCustomer.isEmpty()) throw new ObjectNotFoundException("Supplier not found");
        return optionalCustomer.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to get details of a supplier", UsecaseList.DELETE_SUPPLIER);

        try {
            if (supplierDao.existsById(id)) supplierDao.deleteById(id);
        } catch (DataIntegrityViolationException | RollbackException e) {
            throw new ConflictException("Cannot delete. Because this supplier already used in another module");
        }
    }

    @PostMapping
    public ResourceLink add(@RequestBody Supplier supplier, HttpServletRequest request) throws InterruptedException {
        User authUser = accessControlManager.authorize(request, "No privilege to add new supplier", UsecaseList.ADD_SUPPLIER);

        supplier.setTocreation(LocalDateTime.now());
        supplier.setCreator(authUser);
        EntityValidator.validate(supplier);
        supplier.setId(null);
        supplier.setSupplierstatus(new Supplierstatus(1));

        ValidationErrorBag errorBag = new ValidationErrorBag();
        Supplier supplierByName = supplierDao.findByName(supplier.getName());
        Supplier supplierByContact1 = supplierDao.findByContact1(supplier.getContact1());
        if (supplierByName != null) errorBag.add("name", "Name number already exists");
        if (supplierByContact1 != null) errorBag.add("contact1", "Contact number already exists");
        if (errorBag.count() > 0) throw new DataValidationException(errorBag);

        PersistHelper.save(() -> {
            supplier.setCode(codeGenerator.getNextId(codeConfig));
            return supplierDao.save(supplier);
        });

        return new ResourceLink(supplier.getId(), "/suppliers/" + supplier.getId());
    }

    @PutMapping("/{id}")
    public ResourceLink update(@PathVariable Integer id, @RequestBody Supplier supplier, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to update supplier details", UsecaseList.UPDATE_SUPPLIER);

        Optional<Supplier> optionalCustomer = supplierDao.findById(id);
        if (optionalCustomer.isEmpty()) throw new ObjectNotFoundException("Supplier not found");
        Supplier oldSupplier = optionalCustomer.get();

        supplier.setId(id);
        supplier.setCreator(oldSupplier.getCreator());
        supplier.setTocreation(oldSupplier.getTocreation());
        supplier.setCode(oldSupplier.getCode());
        supplier.setSupplierstatus(new Supplierstatus(1));
        EntityValidator.validate(supplier);

        ValidationErrorBag errorBag = new ValidationErrorBag();
        Supplier supplierByName = supplierDao.findByName(supplier.getName());
        Supplier customerByContact1 = supplierDao.findByContact1(supplier.getContact1());
        if (supplierByName != null)
            if (!supplierByName.getId().equals(id)) errorBag.add("name", "NIC number already exists");
        if (customerByContact1 != null)
            if (!customerByContact1.getId().equals(id)) errorBag.add("contact1", "Contact number already exists");
        if (errorBag.count() > 0) throw new DataValidationException(errorBag);

        supplier = supplierDao.save(supplier);
        return new ResourceLink(supplier.getId(), "/suppliers/" + supplier.getId());
    }
}
