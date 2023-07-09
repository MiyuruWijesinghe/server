package bit.project.server.controller;


import bit.project.server.UsecaseList;
import bit.project.server.dao.ComplainDao;
import bit.project.server.entity.Complain;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@CrossOrigin
@RestController
@RequestMapping("/complains")
public class ComplainController {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "tocreation");

    @Autowired
    private ComplainDao complainDao;

    @Autowired
    private AccessControlManager accessControlManager;

    @Autowired
    private CodeGenerator codeGenerator;
    private final CodeGenerator.CodeGeneratorConfig codeConfig;

    public ComplainController(){
        codeConfig = new CodeGenerator.CodeGeneratorConfig("complain");
        codeConfig.setColumnName("code");
        codeConfig.setLength(10);
        codeConfig.setPrefix("CO");
        codeConfig.setYearlyRenew(true);
    }

    @GetMapping
    public Page<Complain> getAll(PageQuery pageQuery, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get all complains", UsecaseList.GET_ALL_COMPLAINS);

        if(pageQuery.isEmptySearch()){
            return complainDao.findAll(PageRequest.of(pageQuery.getPage(),pageQuery.getSize(), DEFAULT_SORT));
        }


        String name = pageQuery.getSearchParam("name");
        String nic = pageQuery.getSearchParam("nic");
        String contact = pageQuery.getSearchParam("contact");

        List<Complain> complains = complainDao.findAll(DEFAULT_SORT);
        Stream<Complain> stream = complains.parallelStream();

        List<Complain> filteredComplains = stream.filter(complain -> {

            if(nic!=null)
                if(!complain.getNic().toLowerCase().contains(nic.toLowerCase())) return false;
            if(contact!=null){
                if(!complain.getContact().toLowerCase().contains(contact.toLowerCase())) return false;

            }

            return true;
        }).collect(Collectors.toList());

        return PageHelper.getAsPage(filteredComplains, pageQuery.getPage(), pageQuery.getSize());
    }

    @GetMapping("/{id}")
    public Complain get(@PathVariable Integer id, HttpServletRequest request) throws InterruptedException {
        accessControlManager.authorize(request, "No privilege to get details of a complain", UsecaseList.GET_COMPLAIN);

        Optional<Complain> optionalComplain = complainDao.findById(id);
        if(optionalComplain.isEmpty()) throw new ObjectNotFoundException("Complain not found");
        return optionalComplain.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get details of a complain", UsecaseList.DELETE_COMPLAIN);

        try{
            if(complainDao.existsById(id)) complainDao.deleteById(id);
        }catch (DataIntegrityViolationException | RollbackException e){
            throw new ConflictException("Cannot delete. Because this complain already used in another module");
        }
    }

    @PostMapping
    public ResourceLink add(@RequestBody Complain complain, HttpServletRequest request) throws InterruptedException {
        User authUser = accessControlManager.authorize(request, "No privilege to add new complain", UsecaseList.ADD_COMPLAIN);

        complain.setTocreation(LocalDateTime.now());
        complain.setCreator(authUser);
        complain.setDate(LocalDate.now());
        EntityValidator.validate(complain);
        complain.setId(null);

        ValidationErrorBag errorBag = new ValidationErrorBag();
        Complain complainByNic = complainDao.findByNic(complain.getNic());
        Complain complainByContact = complainDao.findByContact(complain.getContact());
        if(complainByNic!=null) errorBag.add("nic","NIC number already exists");
        if(complainByContact!=null) errorBag.add("contact","Contact number already exists");
        if(errorBag.count()>0) throw new DataValidationException(errorBag);

        PersistHelper.save(()->{
            complain.setCode(codeGenerator.getNextId(codeConfig));
            return complainDao.save(complain);
        });

        return new ResourceLink(complain.getId(),"/complains/"+complain.getId());
    }

    @PutMapping("/{id}")
    public ResourceLink update(@PathVariable Integer id, @RequestBody Complain complain, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to update complain details", UsecaseList.UPDATE_COMPLAIN);

        Optional<Complain> optionalComplain = complainDao.findById(id);
        if(optionalComplain.isEmpty()) throw new ObjectNotFoundException("Complain not found");
        Complain oldComplain = optionalComplain.get();

        complain.setId(id);
        complain.setCreator(oldComplain.getCreator());
        complain.setTocreation(oldComplain.getTocreation());
        complain.setCode(oldComplain.getCode());
        complain.setDate(oldComplain.getDate());
        EntityValidator.validate(complain);

        ValidationErrorBag errorBag = new ValidationErrorBag();
        Complain complainByNic = complainDao.findByNic(complain.getNic());
        Complain complainByContact = complainDao.findByContact(complain.getContact());
        if(complainByNic!=null) if(!complainByNic.getId().equals(id)) errorBag.add("nic","NIC number already exists");
        if(complainByContact!=null) if(!complainByContact.getId().equals(id)) errorBag.add("contact","Contact number already exists");
        if(errorBag.count()>0) throw new DataValidationException(errorBag);

        complain = complainDao.save(complain);
        return new ResourceLink(complain.getId(),"/complains/"+complain.getId());
    }

}
