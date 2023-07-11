package bit.project.server.controller;

import bit.project.server.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@CrossOrigin
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private BranchDao branchDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private PurchaseDao purchaseDao;
    @Autowired
    private SaleDao saleDao;
    @Autowired
    private ComplainDao complainDao;

    @GetMapping("/data-count")
    public HashMap<String, Long> getDataCount() {
        Long customersCount = 0L;
        Long employeesCount = 0L;
        Long usersCount = 0L;
        Long suppliersCount = 0L;
        Long branchCount = 0L;
        Long itemsCount = 0L;
        Long purchaseOrdersCount = 0L;
        Long salesCount = 0L;
        Long complainsCount = 0L;

        customersCount = customerDao.count();
        employeesCount = employeeDao.count();
        usersCount = userDao.count();
        suppliersCount = supplierDao.count();
        branchCount = branchDao.count();
        itemsCount = itemDao.count();
        purchaseOrdersCount = purchaseDao.count();
        salesCount = saleDao.count();
        complainsCount = complainDao.count();

        HashMap<String, Long> data = new HashMap<>();
        data.put("customersCount", customersCount);
        data.put("employeesCount", employeesCount);
        data.put("usersCount", usersCount);
        data.put("suppliersCount", suppliersCount);
        data.put("branchCount", branchCount);
        data.put("itemsCount", itemsCount);
        data.put("purchaseOrdersCount", purchaseOrdersCount);
        data.put("salesCount", salesCount);
        data.put("complainsCount", complainsCount);
        return data;
    }
}
