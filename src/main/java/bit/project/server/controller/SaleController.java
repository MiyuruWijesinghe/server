package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.InventoryDao;
import bit.project.server.dao.SaleDao;
import bit.project.server.dao.SaleDao;
import bit.project.server.dao.SaleitemDao;
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

import static java.time.YearMonth.now;

@CrossOrigin
@RestController
@RequestMapping("/sales")
public class SaleController {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "tocreation");

    @Autowired
    private SaleDao saleDao;
    @Autowired
    private InventoryDao inventoryDao;
    @Autowired
    private SaleitemDao saleitemDao;

    @Autowired
    private AccessControlManager accessControlManager;

    @Autowired
    private CodeGenerator codeGenerator;
    private final CodeGenerator.CodeGeneratorConfig codeConfig;

    public SaleController(){
        codeConfig = new CodeGenerator.CodeGeneratorConfig("sale");
        codeConfig.setColumnName("code");
        codeConfig.setLength(10);
        codeConfig.setPrefix("Sl");
        codeConfig.setYearlyRenew(true);
    }

    @GetMapping
    public Page<Sale> getAll(PageQuery pageQuery, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get all sales", UsecaseList.GET_ALL_SALES);

        if(pageQuery.isEmptySearch()){
            return saleDao.findAll(PageRequest.of(pageQuery.getPage(),pageQuery.getSize(), DEFAULT_SORT));
        }


/*
        String date = pageQuery.getSearchParam("date");
*/
        Integer branch = pageQuery.getSearchParamAsInteger("branch");

        List<Sale> sales = saleDao.findAll(DEFAULT_SORT);
        Stream<Sale> stream = sales.parallelStream();


        List<Sale> filteredSales = stream.filter(sale -> {

            if(branch!=null)
                if(!sale.getBranch().getId().equals(branch)) return false;

          /*  if(date!=null)
                if(!date.getDate().toLowerCase().contains(date.toLowerCase())) return false;
*/
            return true;
        }).collect(Collectors.toList());

        return PageHelper.getAsPage(filteredSales, pageQuery.getPage(), pageQuery.getSize());
    }

    @GetMapping("/{id}")
    public Sale get(@PathVariable Integer id, HttpServletRequest request) throws InterruptedException {
        accessControlManager.authorize(request, "No privilege to get details of a sale", UsecaseList.GET_SALE);

        Optional<Sale> optionalCustomer = saleDao.findById(id);
        if(optionalCustomer.isEmpty()) throw new ObjectNotFoundException("Sale not found");
        return optionalCustomer.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get details of a sale", UsecaseList.DELETE_SALE);

        try{
            if(saleDao.existsById(id)) saleDao.deleteById(id);
        }catch (DataIntegrityViolationException | RollbackException e){
            throw new ConflictException("Cannot delete. Because this sale already used in another module");
        }
    }

    @PostMapping
    public ResourceLink add(@RequestBody Sale sale, HttpServletRequest request) throws InterruptedException {
        User authUser = accessControlManager.authorize(request, "No privilege to add new sale", UsecaseList.ADD_SALE);

        sale.setTocreation(LocalDateTime.now());
        sale.setCreator(authUser);
        EntityValidator.validate(sale);
        sale.setDate(LocalDate.now());
        sale.setId(null);

        for (Saleitem saleitem:sale.getSaleitemList()){
            saleitem.setSale(sale);
        }
        /*for (Salepayment salepayment:sale.getSalepaymentList()){
            salepayment.setSale(sale);
        }*/


        PersistHelper.save(()-> {
            sale.setCode(codeGenerator.getNextId(codeConfig));
          /*  sale.getSaleitemList().forEach(saleitem -> {
                Inventory inventorySaleitem =  inventoryDao.findByItem(saleitem.getI;

                {
                    System.out.println(inventorySaleitem);
                    saleitem.getItem().setId(inventorySaleitem.getId());
                }
            });
*/
            return saleDao.save(sale);
        });


            for (Saleitem saleitem:sale.getSaleitemList()) {


                Optional<Inventory> optionalInventory = inventoryDao.findByItemAndBranch(saleitem.getItem(), sale.getBranch());
                if (optionalInventory.isPresent()) {
                    Inventory updatedInventory = optionalInventory.get();
                    updatedInventory.setQty(updatedInventory.getQty() - saleitem.getQty());
                    inventoryDao.save(updatedInventory);
                }


            }

            return new ResourceLink(sale.getId(),"/sales/"+sale.getId());
    }

    @PutMapping("/{id}")
    public ResourceLink update(@PathVariable Integer id, @RequestBody Sale sale, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to update sale details", UsecaseList.UPDATE_SALE);

        Optional<Sale> optionalSale = saleDao.findById(id);
        if(optionalSale.isEmpty()) throw new ObjectNotFoundException("Sale not found");
        Sale oldSale = optionalSale.get();

        sale.setId(id);
        sale.setCreator(oldSale.getCreator());
        sale.setTocreation(oldSale.getTocreation());
        sale.setCode(oldSale.getCode());
        sale.setDate(oldSale.getDate());


        EntityValidator.validate(sale);

        for (Saleitem saleitem:sale.getSaleitemList()){
            saleitem.setSale(sale);
        }
       /* for (Salepayment salepayment:sale.getSalepaymentList()){
            salepayment.setSale(sale);
        }
*/




         return new ResourceLink(sale.getId(),"/sales/"+sale.getId());
    }
}
