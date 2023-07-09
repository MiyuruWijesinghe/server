package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.*;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@CrossOrigin
@RestController
@EnableScheduling
@RequestMapping("/items")
public class ItemController {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "tocreation");


    @Autowired
    private ItemDao itemDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private AccessControlManager accessControlManager;

    @Autowired
    private CodeGenerator codeGenerator;
    private final CodeGenerator.CodeGeneratorConfig codeConfig;

    public ItemController(){
        codeConfig = new CodeGenerator.CodeGeneratorConfig("item");
        codeConfig.setColumnName("code");
        codeConfig.setLength(10);
        codeConfig.setPrefix("IT");
        codeConfig.setYearlyRenew(true);
    }

    @GetMapping("/basic")
    public Page<Item> getAllBasic(PageQuery pageQuery, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get all item basic data", UsecaseList.GET_BASIC_ITEMS);
        return itemDao.findAllBasic(PageRequest.of(pageQuery.getPage(),pageQuery.getSize(), DEFAULT_SORT));
    }

    @GetMapping
    public Page<Item> getAll(PageQuery pageQuery, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get all items", UsecaseList.GET_ALL_ITEMS);


        if(pageQuery.isEmptySearch()){
            return itemDao.findAll(PageRequest.of(pageQuery.getPage(),pageQuery.getSize(), DEFAULT_SORT));
        }


        String name = pageQuery.getSearchParam("name");
        Integer Itemstatus = pageQuery.getSearchParamAsInteger("itemstatus");
        Integer Itemtype = pageQuery.getSearchParamAsInteger("itemtype");


        List<Item> items = itemDao.findAll(DEFAULT_SORT);
        Stream<Item> stream = items.parallelStream();

        List<Item> filtereditems = stream.filter(item -> {

            if(name!=null)
                if(!item.getName().toLowerCase().contains(name.toLowerCase())) return false;

            if(Itemstatus!=null)
                if(!item.getItemstatus().getId().equals(Itemstatus)) return false;

            if(Itemtype!=null)
                if(!item.getItemtype().getId().equals(Itemtype)) return false;
                

            return true;
        }).collect(Collectors.toList());

        return PageHelper.getAsPage(filtereditems, pageQuery.getPage(), pageQuery.getSize());
    }

    @GetMapping("/{id}")
    public Item get(@PathVariable Integer id, HttpServletRequest request) throws InterruptedException {
        accessControlManager.authorize(request, "No privilege to get details of a item", UsecaseList.GET_ITEM);

        Optional<Item> optionalCustomer = itemDao.findById(id);
        if(optionalCustomer.isEmpty()) throw new ObjectNotFoundException("Item not found");
        return optionalCustomer.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get details of a item", UsecaseList.DELETE_ITEM);

        try{
            if(itemDao.existsById(id)) itemDao.deleteById(id);
        }catch (DataIntegrityViolationException | RollbackException e){
            throw new ConflictException("Cannot delete. Because this item already used in another module");
        }
    }

    @PostMapping
    public ResourceLink add(@RequestBody Item item, HttpServletRequest request) throws InterruptedException {
        User authUser = accessControlManager.authorize(request, "No privilege to add new item", UsecaseList.ADD_ITEM);

        item.setTocreation(LocalDateTime.now());
        item.setCreator(authUser);
        EntityValidator.validate(item);
        item.setId(null);


        for (Itembranch itembranch:item.getItembranchList()){
            itembranch.setItem(item);
        }

        ValidationErrorBag errorBag = new ValidationErrorBag();
        Item itemByname = itemDao.findByName(item.getName());
        if(itemByname!=null) errorBag.add("name","Item name already exists");
        if(errorBag.count()>0) throw new DataValidationException(errorBag);

        PersistHelper.save(()->{
            item.setCode(codeGenerator.getNextId(codeConfig));
            return itemDao.save(item);
        });

        return new ResourceLink(item.getId(),"/items/"+item.getId());
    }

    @PutMapping("/{id}")
    public ResourceLink update(@PathVariable Integer id, @RequestBody Item item, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to update item details", UsecaseList.UPDATE_ITEM);

        Optional<Item> optionalItem = itemDao.findById(id);
        if(optionalItem.isEmpty()) throw new ObjectNotFoundException("Item not found");
        Item oldItem = optionalItem.get();

        item.setId(id);
        item.setCreator(oldItem.getCreator());
        item.setTocreation(oldItem.getTocreation());
        item.setCode(oldItem.getCode());
        EntityValidator.validate(item);

        for (Itembranch itembranch:item.getItembranchList()){
            itembranch.setItem(item);
        }

        ValidationErrorBag errorBag = new ValidationErrorBag();
        Item itemByName = itemDao.findByName(item.getName());
        if(itemByName!=null) if(!itemByName.getId().equals(id)) errorBag.add("name","Item name already exists");
        if(errorBag.count()>0) throw new DataValidationException(errorBag);

        item = itemDao.save(item);
        return new ResourceLink(item.getId(),"/items/"+item.getId());
    }
    @Async
    @Scheduled(fixedRate = 24*60*60*1000,initialDelay = 5000)
    public void checkRop() throws InterruptedException{
        System.out.println("Started");

         List<Item> items = itemDao.findAllActiveitems();
         for (Item item : items){
             for (Itembranch itembranch : item.getItembranchList()){
              Optional<Inventory> optionalInventory = inventoryDao.findByItemAndBranch(item , itembranch.getBranch());
              if (optionalInventory.isEmpty()){
                  continue;
              } else {
                 if (optionalInventory.get().getQty() < itembranch.getRop()){

                     System.out.println(item.getName());
                     System.out.println(itembranch.getBranch().getName());

                   List<Employee> employees = employeeDao.findAllByBranch( itembranch.getBranch());
                     System.out.println(employees.isEmpty());

                     if (!employees.isEmpty()){
                       for (Employee employee : employees){

                           try{
                               System.out.println(employee.getCallingname());

                              List<User>  userList =  userDao.findUsersByEmployee(employee.getId());

                               System.out.println(userList.isEmpty());


                              if (userList == null) continue;
                              if (userList.size()== 0) continue;
                              User user = userList.get(0);
                               System.out.println(item.getName());
                               Notification notification = new Notification();
                               notification.setId(UUID.randomUUID().toString());
                               notification.setDosend(LocalDateTime.now());
                               notification.setUser(user);

                               notification.setMessage("Following item is below ROP\n Item name : " + item.getName() + "\n Remaining quantity " +
                                       optionalInventory.get().getQty() + "\n Branch "+ itembranch.getBranch().getName());
                               notificationDao.save(notification);


                           } catch (Exception e ){
                               System.out.println(e.getMessage());
                           }
                       }
                   }
                 }
              }
             }
         }

    }
}
