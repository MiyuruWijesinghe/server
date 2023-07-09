package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.PorderDao;
import bit.project.server.dao.SupplierDao;
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
@RequestMapping("/porders")
public class PorderController {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "tocreation");

    @Autowired
    private PorderDao porderdao;
    private SupplierDao supplierdao;

    @Autowired
    private AccessControlManager accessControlManager;

    @Autowired
    private CodeGenerator codeGenerator;
    private final CodeGenerator.CodeGeneratorConfig codeConfig;

    public PorderController(){
        codeConfig = new CodeGenerator.CodeGeneratorConfig("porder");
        codeConfig.setColumnName("code");
        codeConfig.setLength(10);
        codeConfig.setPrefix("PO");
        codeConfig.setYearlyRenew(true);
    }

    @GetMapping
    public Page<Porder> getAll(PageQuery pageQuery, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to get all porders", UsecaseList.GET_ALL_PORDERS);

        if (pageQuery.isEmptySearch()) {
            return porderdao.findAll(PageRequest.of(pageQuery.getPage(), pageQuery.getSize(), DEFAULT_SORT));
        }


        Integer branch = pageQuery.getSearchParamAsInteger("branch");
        Integer supplier = pageQuery.getSearchParamAsInteger("supplier");
        Integer porderstatus = pageQuery.getSearchParamAsInteger("porderstatus");

        List<Porder> porders = porderdao.findAll(DEFAULT_SORT);
        Stream<Porder> stream = porders.parallelStream();

        List<Porder> filteredPorders = stream.filter(porder -> {
            if (supplier != null)
                if (!porder.getSupplier().getId().equals(supplier)) return false;
            if (branch != null)
                if (!porder.getBranch().getId().equals(branch)) return false;
            if (porderstatus != null)
                if (!porder.getPorderstatus().getId().equals(porderstatus)) return false;

            return true;
        }).collect(Collectors.toList());

        return PageHelper.getAsPage(filteredPorders, pageQuery.getPage(), pageQuery.getSize());

    }


    @GetMapping("/{id}")
    public Porder get(@PathVariable Integer id, HttpServletRequest request) throws InterruptedException {
        accessControlManager.authorize(request, "No privilege to get details of a porder", UsecaseList.GET_PORDER);

        Optional<Porder> optionalPorder = porderdao.findById(id);
        if(optionalPorder.isEmpty()) throw new ObjectNotFoundException("Porder not found");
        return optionalPorder.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get details of a porder", UsecaseList.DELETE_PORDER);

        try{
            if(porderdao.existsById(id)) porderdao.deleteById(id);
        }catch (DataIntegrityViolationException | RollbackException e){
            throw new ConflictException("Cannot delete. Because this porder already used in another module");
        }
    }

    @PostMapping
    public ResourceLink add(@RequestBody Porder porder, HttpServletRequest request) throws InterruptedException {
        User authUser = accessControlManager.authorize(request, "No privilege to add new porder", UsecaseList.ADD_PORDER);

        porder.setTocreation(LocalDateTime.now());
        porder.setDoordered(LocalDateTime.now());
        porder.setPorderstatus(new Porderstatus(1));
        porder.setCreator(authUser);
        EntityValidator.validate(porder);
        porder.setId(null);

        for (Porderitem porderitem:porder.getPorderitemList()){
            porderitem.setPorder(porder);
        }
        

        PersistHelper.save(()->{
            porder.setCode(codeGenerator.getNextId(codeConfig));
            return porderdao.save(porder);
        });

        return new ResourceLink(porder.getId(),"/porders/"+porder.getId());
    }

    @PutMapping("/{id}")
    public ResourceLink update(@PathVariable Integer id, @RequestBody Porder porder, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to update porder details", UsecaseList.UPDATE_PORDER);

        Optional<Porder> optionalPorder = porderdao.findById(id);
        if(optionalPorder.isEmpty()) throw new ObjectNotFoundException("Porder not found");
        Porder oldPorder = optionalPorder.get();

        porder.setId(id);
        porder.setCreator(oldPorder.getCreator());
        porder.setDoordered(oldPorder.getDoordered());
        porder.setBranch(oldPorder.getBranch());
        porder.setSupplier(oldPorder.getSupplier());
        porder.setDorequired(oldPorder.getDorequired());
        porder.setTocreation(oldPorder.getTocreation());
        porder.setCode(oldPorder.getCode());

        for (Porderitem porderitem:porder.getPorderitemList()){
            porderitem.setPorder(porder);
        }

        EntityValidator.validate(porder);








        porder = porderdao.save(porder);
        return new ResourceLink(porder.getId(),"/porders/"+porder.getId());
    }
}

