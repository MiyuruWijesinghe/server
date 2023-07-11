package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.EmployeeDao;
import bit.project.server.dao.FileDao;
import bit.project.server.entity.*;
import bit.project.server.util.dto.PageQuery;
import bit.project.server.util.dto.ResourceLink;
import bit.project.server.util.exception.ConflictException;
import bit.project.server.util.exception.DataValidationException;
import bit.project.server.util.exception.ObjectNotFoundException;
import bit.project.server.util.helper.CodeGenerator;
import bit.project.server.util.helper.FileHelper;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "tocreation");

    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private AccessControlManager accessControlManager;

    @Autowired
    private CodeGenerator codeGenerator;
    private final CodeGenerator.CodeGeneratorConfig codeConfig;

    public EmployeeController() {
        codeConfig = new CodeGenerator.CodeGeneratorConfig("employee");
        codeConfig.setColumnName("code");
        codeConfig.setLength(10);
        codeConfig.setPrefix("EM");
        codeConfig.setYearlyRenew(true);
    }

    @GetMapping
    public Page<Employee> getAll(PageQuery pageQuery, HttpServletRequest request) {

        accessControlManager.authorize(request, "No privilege to get all employees", UsecaseList.GET_ALL_EMPLOYEES);

        if (pageQuery.isEmptySearch()) {
            return employeeDao.findAll(PageRequest.of(pageQuery.getPage(), pageQuery.getSize(), DEFAULT_SORT));
        }

        String name = pageQuery.getSearchParam("name");
        String nic = pageQuery.getSearchParam("nic");
        String mobile = pageQuery.getSearchParam("mobile");
        Integer designation = pageQuery.getSearchParamAsInteger("designation");

        List<Employee> employees = employeeDao.findAll(DEFAULT_SORT);
        Stream<Employee> stream = employees.parallelStream();

        List<Employee> filteredUsers = stream.filter(employee -> {
            if (name != null) {
                boolean crit1 = employee.getCallingname().toLowerCase().contains(name.toLowerCase());
                boolean crit2 = employee.getFullname().toLowerCase().contains(name.toLowerCase());
                if (!crit1 && !crit2) return false;
            }
            if (nic != null)
                if (!employee.getNic().toLowerCase().contains(nic.toLowerCase())) return false;
            if (mobile != null)
                if (!employee.getMobile().contains(mobile)) return false;
            if (designation != null)
                if (!employee.getDesignation().getId().equals(designation)) return false;
            return true;
        }).collect(Collectors.toList());

        return PageHelper.getAsPage(filteredUsers, pageQuery.getPage(), pageQuery.getSize());

    }

    @GetMapping("/basic")
    public Page<Employee> getAllBasic(PageQuery pageQuery, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to get all employees' basic data", UsecaseList.GET_BASIC_EMPLOYEES);
        return employeeDao.findAllBasic(PageRequest.of(pageQuery.getPage(), pageQuery.getSize(), DEFAULT_SORT));
    }

    @GetMapping("/{id}")
    public Employee get(@PathVariable Integer id, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to get employee", UsecaseList.GET_EMPLOYEE);
        Optional<Employee> optionalEmployee = employeeDao.findById(id);
        if (optionalEmployee.isEmpty()) throw new ObjectNotFoundException("Employee not found");
        return optionalEmployee.get();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to delete employees", UsecaseList.DELETE_EMPLOYEE);
        try {
            Optional<Employee> optionalEmployee = employeeDao.findById(id);
            if (optionalEmployee.isEmpty()) return;
            String photoId = optionalEmployee.get().getPhoto();
            if (photoId != null) fileDao.updateIsusedById(photoId, false);
            employeeDao.deleteById(id);
        } catch (DataIntegrityViolationException | RollbackException e) {
            throw new ConflictException("Cannot delete. Because this employee already used in another module");
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceLink add(@RequestBody Employee employee, HttpServletRequest request) throws InterruptedException {
        User authUser = accessControlManager.authorize(request, "No privilege to add new employee", UsecaseList.ADD_EMPLOYEE);

        employee.setTocreation(LocalDateTime.now());
        employee.setCreator(authUser);
        employee.setId(null);
        employee.setEmployeestatus(new Employeestatus(1));
        if (employee.getPhoto() != null) fileDao.updateIsusedById(employee.getPhoto(), true);

        EntityValidator.validate(employee);

        ValidationErrorBag errorBag = new ValidationErrorBag();
        Employee employeeByNic = employeeDao.findByNic(employee.getNic());
        Employee employeeByMobile = employeeDao.findByMobile(employee.getMobile());
        if (employeeByNic != null) errorBag.add("nic", "NIC number already exists");
        if (employeeByMobile != null) errorBag.add("mobile", "Mobile already exists");
        if (errorBag.count() > 0) throw new DataValidationException(errorBag);

        PersistHelper.save(() -> {
            employee.setCode(codeGenerator.getNextId(codeConfig));
            return employeeDao.save(employee);
        });
        return new ResourceLink(employee.getId(), "/employees/" + employee.getId());
    }

    @PutMapping("/{id}")
    public ResourceLink update(@PathVariable Integer id, @RequestBody Employee employee, HttpServletRequest request) throws InterruptedException {
        accessControlManager.authorize(request, "No privilege to update employee details", UsecaseList.UPDATE_EMPLOYEE);

        Optional<Employee> optionalEmployee = employeeDao.findById(id);
        if (optionalEmployee.isEmpty()) throw new ObjectNotFoundException("Employee not found");
        Employee oldEmployee = optionalEmployee.get();

        employee.setId(id);
        employee.setCode(oldEmployee.getCode());
        employee.setCreator(oldEmployee.getCreator());
        employee.setTocreation(oldEmployee.getTocreation());

        String oldPhotoId = oldEmployee.getPhoto();
        String newPhotoId = employee.getPhoto();
        if (oldPhotoId != newPhotoId) {
            fileDao.updateIsusedById(oldPhotoId, false);
            fileDao.updateIsusedById(newPhotoId, true);
        }

        EntityValidator.validate(employee);

        ValidationErrorBag errorBag = new ValidationErrorBag();
        Employee employeeByNic = employeeDao.findByNic(employee.getNic());
        Employee employeeByMobile = employeeDao.findByMobile(employee.getMobile());
        if (employeeByNic != null)
            if (!employeeByNic.getId().equals(id)) errorBag.add("nic", "NIC number already exists");
        if (employeeByMobile != null)
            if (!employeeByMobile.getId().equals(id)) errorBag.add("mobile", "Mobile already exists");
        if (errorBag.count() > 0) throw new DataValidationException(errorBag);


        employee = employeeDao.save(employee);
        return new ResourceLink(employee.getId(), "/employees/" + employee.getId());
    }

    @GetMapping("/{id}/photo")
    public HashMap getPhoto(@PathVariable Integer id, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to get employee photo", UsecaseList.GET_EMPLOYEE);

        Optional<Employee> optionalEmployee = employeeDao.findById(id);
        if (optionalEmployee.isEmpty()) throw new ObjectNotFoundException("Employee not found");
        Employee employee = optionalEmployee.get();

        Optional<File> optionalFile = fileDao.findFileById(employee.getPhoto());

        if (optionalFile.isEmpty()) {
            throw new ObjectNotFoundException("Photo not found");
        }

        File photo = optionalFile.get();
        HashMap<String, String> data = new HashMap<>();

        data.put("file", FileHelper.byteArrayToBase64(photo.getFile(), photo.getFilemimetype()));

        return data;
    }


}
