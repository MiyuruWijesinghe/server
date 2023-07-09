package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.BranchDao;
import bit.project.server.entity.Branch;
import bit.project.server.entity.Branchstatus;
import bit.project.server.entity.Itembranch;
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
@RequestMapping("/branches")
public class BranchController {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "tocreation");

    @Autowired
    private BranchDao branchDao;

    @Autowired
    private AccessControlManager accessControlManager;

    @Autowired
    private CodeGenerator codeGenerator;
    private final CodeGenerator.CodeGeneratorConfig codeConfig;

    public BranchController(){
        codeConfig = new CodeGenerator.CodeGeneratorConfig("branch");
        codeConfig.setColumnName("code");
        codeConfig.setLength(10);
        codeConfig.setPrefix("Br");
        codeConfig.setYearlyRenew(true);
    }

    @GetMapping
    public Page<Branch> getAll(PageQuery pageQuery, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get all branches", UsecaseList.GET_ALL_BRANCHES);

        if(pageQuery.isEmptySearch()){
            return branchDao.findAll(PageRequest.of(pageQuery.getPage(),pageQuery.getSize(), DEFAULT_SORT));
        }


        String name = pageQuery.getSearchParam("name");
        String contact = pageQuery.getSearchParam("contact");

        List<Branch> branches = branchDao.findAll(DEFAULT_SORT);
        Stream<Branch> stream = branches.parallelStream();

        List<Branch> filteredBranches = stream.filter(branch -> {

            if(name!=null) {
                if(!branch.getName().toLowerCase().contains(name.toLowerCase())) return false;
            }

            if(contact!=null) {
                boolean crit1 = branch.getContact1().toLowerCase().contains(contact.toLowerCase());
                boolean crit2 = false;
                if(branch.getContact2() != null) crit2 = branch.getContact2().toLowerCase().contains(contact.toLowerCase());
                if(!crit1 && !crit2) return false;
            }

            return true;
        }).collect(Collectors.toList());

        return PageHelper.getAsPage(filteredBranches, pageQuery.getPage(), pageQuery.getSize());
    }

    @GetMapping("/{id}")
    public Branch get(@PathVariable Integer id, HttpServletRequest request) throws InterruptedException {
        accessControlManager.authorize(request, "No privilege to get details of a branch", UsecaseList.GET_BRANCH);

        Optional<Branch> optionalCustomer = branchDao.findById(id);
        if(optionalCustomer.isEmpty()) throw new ObjectNotFoundException("Branch not found");
        return optionalCustomer.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get details of a branch", UsecaseList.DELETE_BRANCH);

        try{
            if(branchDao.existsById(id)) branchDao.deleteById(id);
        }catch (DataIntegrityViolationException | RollbackException e){
            throw new ConflictException("Cannot delete. Because this branch already used in another module");
        }
    }

    @PostMapping
    public ResourceLink add(@RequestBody Branch branch, HttpServletRequest request) throws InterruptedException {
        User authUser = accessControlManager.authorize(request, "No privilege to add new branch", UsecaseList.ADD_BRANCH);

        branch.setTocreation(LocalDateTime.now());
        branch.setCreator(authUser);
        EntityValidator.validate(branch);
        branch.setId(null);
        branch.setBranchstatus(new Branchstatus(1));



        ValidationErrorBag errorBag = new ValidationErrorBag();
        Branch branchByName = branchDao.findByName(branch.getName());
        Branch branchByContact1 = branchDao.findByContact1(branch.getContact1());
        if(branchByName!=null) errorBag.add("name","Name number already exists");
        if(branchByContact1!=null) errorBag.add("contact1","Contact number already exists");
        if(errorBag.count()>0) throw new DataValidationException(errorBag);

        PersistHelper.save(()->{
            branch.setCode(codeGenerator.getNextId(codeConfig));
            return branchDao.save(branch);
        });

        return new ResourceLink(branch.getId(),"/branches/"+branch.getId());
    }

    @PutMapping("/{id}")
    public ResourceLink update(@PathVariable Integer id, @RequestBody Branch branch, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to update branch details", UsecaseList.UPDATE_BRANCH);

        Optional<Branch> optionalBranch = branchDao.findById(id);
        if(optionalBranch.isEmpty()) throw new ObjectNotFoundException("Branch not found");
        Branch oldBranch = optionalBranch.get();

        branch.setId(id);
        branch.setCreator(oldBranch.getCreator());
        branch.setTocreation(oldBranch.getTocreation());
        branch.setCode(oldBranch.getCode());
        branch.setDorecruite(oldBranch.getDorecruite());
        EntityValidator.validate(branch);



        ValidationErrorBag errorBag = new ValidationErrorBag();
        Branch branchByName = branchDao.findByName(branch.getName());
        Branch branchByContact1 = branchDao.findByContact1(branch.getContact1());
        if(branchByName!=null) if(!branchByName.getId().equals(id)) errorBag.add("name","NIC number already exists");
        if(branchByContact1!=null) if(!branchByContact1.getId().equals(id)) errorBag.add("contact1","Contact number already exists");
        if(errorBag.count()>0) throw new DataValidationException(errorBag);

        branch = branchDao.save(branch);
        return new ResourceLink(branch.getId(),"/branches/"+branch.getId());
    }
}
