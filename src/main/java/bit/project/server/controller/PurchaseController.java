package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.InventoryDao;
import bit.project.server.dao.PorderDao;
import bit.project.server.dao.PurchaseDao;
import bit.project.server.entity.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin
@RestController
@RequestMapping("/purchases")
public class PurchaseController {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "tocreation");

    @Autowired
    private PurchaseDao purchasedao;

    @Autowired
    private InventoryDao inventoryDao;


    @Autowired
    private AccessControlManager accessControlManager;

    @Autowired
    private CodeGenerator codeGenerator;
    private final CodeGenerator.CodeGeneratorConfig codeConfig;


    public PurchaseController() {
        codeConfig = new CodeGenerator.CodeGeneratorConfig("purchase");
        codeConfig.setColumnName("code");
        codeConfig.setLength(10);
        codeConfig.setPrefix("PO");
        codeConfig.setYearlyRenew(true);
    }

    @GetMapping
    public Page<Purchase> getAll(PageQuery pageQuery, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to get all purchases", UsecaseList.GET_ALL_PURCHASES);

        if (pageQuery.isEmptySearch()) {
            return purchasedao.findAll(PageRequest.of(pageQuery.getPage(), pageQuery.getSize(), DEFAULT_SORT));
        }


        Integer branch = pageQuery.getSearchParamAsInteger("branch");


        List<Purchase> purchases = purchasedao.findAll(DEFAULT_SORT);
        Stream<Purchase> stream = purchases.parallelStream();

        List<Purchase> filteredPurchases = stream.filter(purchase -> {
            if (branch != null)
                if (!purchase.getBranch().getId().equals(branch)) return false;


            return true;
        }).collect(Collectors.toList());

        return PageHelper.getAsPage(filteredPurchases, pageQuery.getPage(), pageQuery.getSize());
    }

    @GetMapping("/{id}")
    public Purchase get(@PathVariable Integer id, HttpServletRequest request) throws InterruptedException {
        accessControlManager.authorize(request, "No privilege to get details of a purchase", UsecaseList.GET_PURCHASE);

        Optional<Purchase> optionalPurchase = purchasedao.findById(id);
        if (optionalPurchase.isEmpty()) throw new ObjectNotFoundException("Purchase not found");
        return optionalPurchase.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to get details of a purchase", UsecaseList.DELETE_PURCHASE);

        try {
            if (purchasedao.existsById(id)) purchasedao.deleteById(id);
        } catch (DataIntegrityViolationException | RollbackException e) {
            throw new ConflictException("Cannot delete. Because this purchase already used in another module");
        }
    }

    @PostMapping
    public ResourceLink add(@RequestBody Purchase purchase, HttpServletRequest request) throws InterruptedException {
        User authUser = accessControlManager.authorize(request, "No privilege to add new purchase", UsecaseList.ADD_PURCHASE);
        System.out.println("HERE .......");
        purchase.setTocreation(LocalDateTime.now());
        purchase.setDate(LocalDate.now());
        purchase.setCreator(authUser);
        EntityValidator.validate(purchase);
        purchase.setId(null);

        System.out.println("HERE  2.......");

        for (Purchaseitem purchaseitem : purchase.getPurchaseitemList()) {
            purchaseitem.setPurchase(purchase);
        }


        PersistHelper.save(() -> {
            purchase.setCode(codeGenerator.getNextId(codeConfig));
            return purchasedao.save(purchase);
        });


        for (Purchaseitem purchaseitem : purchase.getPurchaseitemList()) {
            System.out.println(purchaseitem.getItem().getId());
            System.out.println(purchase.getBranch().getId());
            System.out.println("HERE ....... 3");

            System.out.println("HERE ....... ERROR 1" + purchaseitem.getItem());
            System.out.println("HERE ....... ERROR 2" + purchase.getBranch());
            Optional<Inventory> optionalInventory = inventoryDao.findByItemAndBranch(purchaseitem.getItem(), purchase.getBranch());
            System.out.println("HERE ....... 4");
            if (optionalInventory.isPresent()) {
                System.out.println("Item in inventory");
                Inventory updatedInventory = optionalInventory.get();
                updatedInventory.setQty(updatedInventory.getQty() + purchaseitem.getQty());

                inventoryDao.save(updatedInventory);
            } else {
                System.out.println("Item not in inventory");
                Inventory inventory = new Inventory();
                inventory.setInitqty(purchaseitem.getQty());
                System.out.println("1111111111111111111" + purchaseitem.getQty());
                inventory.setQty(purchaseitem.getQty());
                System.out.println("22222222222222222222" + purchaseitem.getQty());
                inventory.setBatchno(purchaseitem.getBatchno());
                System.out.println("33333333333333333333" + purchaseitem.getQty());
                inventory.setDoexpired(purchaseitem.getDoexpired());
                System.out.println("44444444444444444444444" + purchaseitem.getDoexpired());
                inventory.setDomanufactured(purchaseitem.getDomanufactured());
                System.out.println("44444444444444444444444" + purchaseitem.getDomanufactured());
                inventory.setBranch(new Branch(purchase.getBranch().getId()));
                System.out.println("5555555555555555555555555" + purchase.getBranch().getId());
                inventory.setItem(purchaseitem.getItem());
                System.out.println("6666666666666666666666666" + purchaseitem.getItem());
                inventory.setPurchase(purchase);
                inventory.setCreator(authUser);
                inventoryDao.save(inventory);

            }
        }

        return new ResourceLink(purchase.getId(), "/purchases/" + purchase.getId());
    }


    @PutMapping("/{id}")
    public ResourceLink update(@PathVariable Integer id, @RequestBody Purchase purchase, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to update purchase details", UsecaseList.UPDATE_PURCHASE);

        Optional<Purchase> optionalPurchase = purchasedao.findById(id);
        if (optionalPurchase.isEmpty()) throw new ObjectNotFoundException("Purchase not found");
        Purchase oldPurchase = optionalPurchase.get();

        purchase.setId(id);
        purchase.setCreator(oldPurchase.getCreator());
        purchase.setDate(LocalDate.now());
        purchase.setBranch(oldPurchase.getBranch());
        purchase.setTocreation(oldPurchase.getTocreation());
        purchase.setCode(oldPurchase.getCode());

        EntityValidator.validate(purchase);


        for (Purchaseitem purchaseitem : purchase.getPurchaseitemList()) {
            purchaseitem.setPurchase(purchase);
        }


        purchase = purchasedao.save(purchase);
        return new ResourceLink(purchase.getId(), "/purchases/" + purchase.getId());
    }
}

