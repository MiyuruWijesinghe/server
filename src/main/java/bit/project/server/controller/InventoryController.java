package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.InventoryDao;
import bit.project.server.entity.*;
import bit.project.server.entity.Inventory;
import bit.project.server.util.dto.PageQuery;
import bit.project.server.util.dto.ResourceLink;
import bit.project.server.util.exception.ConflictException;
import bit.project.server.util.exception.ObjectNotFoundException;
import bit.project.server.util.helper.CodeGenerator;
import bit.project.server.util.helper.PageHelper;
import bit.project.server.util.helper.PersistHelper;
import bit.project.server.util.security.AccessControlManager;
import bit.project.server.util.validation.EntityValidator;
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
@RequestMapping("/inventories")
public class InventoryController {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "code");

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private AccessControlManager accessControlManager;

    @Autowired
    private CodeGenerator codeGenerator;
    private final CodeGenerator.CodeGeneratorConfig codeConfig;

    public InventoryController(){
        codeConfig = new CodeGenerator.CodeGeneratorConfig("inventory");
        codeConfig.setColumnName("code");
        codeConfig.setLength(10);
        codeConfig.setPrefix("IN");
        codeConfig.setYearlyRenew(true);
    }

    @GetMapping
    public Page<Inventory> getAll(PageQuery pageQuery, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get all inventories", UsecaseList.GET_ALL_INVENTORIES);

        if(pageQuery.isEmptySearch()){
            return inventoryDao.findAll(PageRequest.of(pageQuery.getPage(),pageQuery.getSize(), DEFAULT_SORT));
        }


        Integer branch = pageQuery.getSearchParamAsInteger("branch");
        Integer item = pageQuery.getSearchParamAsInteger("item");

        List<Inventory> inventories = inventoryDao.findAll(DEFAULT_SORT);
        Stream<Inventory> stream = inventories.parallelStream();

        List<Inventory> filteredInventories = stream.filter(inventory -> {

            if(branch!=null)
                if(!inventory.getBranch().getId().equals(branch)) return false;

            if(item!=null)
                if(!inventory.getItem().getId().equals(item)) return false;
                
            return true;
        }).collect(Collectors.toList());

        return PageHelper.getAsPage(filteredInventories, pageQuery.getPage(), pageQuery.getSize());
    }

    @GetMapping("/{id}")
    public Inventory get(@PathVariable Integer id, HttpServletRequest request) throws InterruptedException {
        accessControlManager.authorize(request, "No privilege to get details of a inventory", UsecaseList.GET_INVENTORY);

        Optional<Inventory> optionalCustomer = inventoryDao.findById(id);
        if(optionalCustomer.isEmpty()) throw new ObjectNotFoundException("Inventory not found");
        return optionalCustomer.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get details of a inventory", UsecaseList.DELETE_INVENTORY);

        try{
            if(inventoryDao.existsById(id)) inventoryDao.deleteById(id);
        }catch (DataIntegrityViolationException | RollbackException e){
            throw new ConflictException("Cannot delete. Because this inventory already used in another module");
        }
    }

    @PostMapping
    public ResourceLink add(@RequestBody Inventory inventory, HttpServletRequest request) throws InterruptedException {
        User authUser = accessControlManager.authorize(request, "No privilege to add new inventory", UsecaseList.ADD_INVENTORY);

       
        inventory.setCreator(authUser);
        EntityValidator.validate(inventory);
        inventory.setId(null);

        for (Inventorycustomertype inventorycustomertype:inventory.getInventorycustomertypeList()){
            inventorycustomertype.setInventory(inventory);
        }


        PersistHelper.save(()->{
            inventory.setCode(codeGenerator.getNextId(codeConfig));
            return inventoryDao.save(inventory);
        });

        return new ResourceLink(inventory.getId(),"/inventories/"+inventory.getId());
    }

    @PutMapping("/{id}")
    public ResourceLink update(@PathVariable Integer id, @RequestBody Inventory inventory, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to update inventory details", UsecaseList.UPDATE_INVENTORY);

        Optional<Inventory> optionalInventory = inventoryDao.findById(id);
        if(optionalInventory.isEmpty()) throw new ObjectNotFoundException("Inventory not found");
        Inventory oldInventory = optionalInventory.get();

        inventory.setId(id);
        inventory.setCreator(oldInventory.getCreator());

        inventory.setCode(oldInventory.getCode());
        inventory.setBranch(oldInventory.getBranch());
        inventory.setItem(oldInventory.getItem());

        EntityValidator.validate(inventory);
        for (Inventorycustomertype inventorycustomertype:inventory.getInventorycustomertypeList()){
            inventorycustomertype.setInventory(inventory);
        }

        inventory = inventoryDao.save(inventory);
        return new ResourceLink(inventory.getId(),"/inventories/"+inventory.getId());
    }
}
