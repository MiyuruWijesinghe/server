package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.CustomerDao;
import bit.project.server.dao.PurchaseDao;
import bit.project.server.dao.SaleDao;
import bit.project.server.dao.SaleitemDao;
import bit.project.server.entity.Inventory;
import bit.project.server.entity.Saleitem;
import bit.project.server.util.security.AccessControlManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/reports")
public class ReportController {
    @Autowired
    CustomerDao customerDao;
    @Autowired
    SaleDao saleDao;

    @Autowired
    PurchaseDao purchaseDao;

    @Autowired
    SaleitemDao saleitemDao;

    @Autowired
    private AccessControlManager accessControlManager;

    @GetMapping("/year-wise-customer-count/{yearCount}")
    public ArrayList<HashMap<String, Object>> yearWiseCustomerCount(@PathVariable Integer yearCount, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to show this report", UsecaseList.SHOW_YEAR_WISE_CUSTOMER_COUNT);

        ArrayList<HashMap<String, Object>> data = new ArrayList<>();

        ArrayList<LocalDate[]> years = new ArrayList<>();

        LocalDate[] currentYear = new LocalDate[2];
        currentYear[0] = LocalDate.parse(LocalDate.now().getYear() + "-01-01");
        currentYear[1] = LocalDate.parse(LocalDate.now().getYear() + "-12-31");
        years.add(currentYear);

        for (int i = 0; i < yearCount - 1; i++) {
            LocalDate[] year = new LocalDate[2];
            LocalDate[] lastYear = years.get(years.size() - 1);
            year[0] = lastYear[0].minusYears(1);
            year[1] = lastYear[1].minusYears(1);
            years.add(year);
        }

        for (LocalDate[] year : years) {
            String y = String.valueOf(year[0].getYear());
            Long count = customerDao.getCustomerCountByRange(year[0], year[1]);
            HashMap<String, Object> d = new HashMap<>();
            d.put("year", y);
            d.put("count", count);
            data.add(d);
        }
        return data;

    }

    @GetMapping("/month-wise-sale/{year}")
    public ArrayList<HashMap<String, Object>> monthWiseSaleCount(@PathVariable String year, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to show this report", UsecaseList.SHOW_MONTH_WISE_SALE);

        ArrayList<HashMap<String, Object>> data2 = new ArrayList<>();

        String[] months = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        for (String month : months) {
            Integer amount = saleDao.getMonthlySaleAmount(year, month);
            HashMap<String, Object> d = new HashMap<>();
            d.put("month", year + "-" + month + "-01");
            d.put("amount", (amount != null) ? amount : 0);
            data2.add(d);
        }
        return data2;

    }

    @GetMapping("/year-wise-sale-count/{yearCount}")
    public ArrayList<HashMap<String, Object>> yearWiseSaleCount(@PathVariable Integer yearCount, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to show this report", UsecaseList.SHOW_YEAR_WISE_SALE_COUNT);

        ArrayList<HashMap<String, Object>> data1 = new ArrayList<>();

        ArrayList<LocalDate[]> years = new ArrayList<>();

        LocalDate[] currentYear = new LocalDate[2];
        currentYear[0] = LocalDate.parse(LocalDate.now().getYear() + "-01-01");
        currentYear[1] = LocalDate.parse(LocalDate.now().getYear() + "-12-31");
        years.add(currentYear);

        for (int i = 0; i < yearCount - 1; i++) {
            LocalDate[] year = new LocalDate[2];
            LocalDate[] lastYear = years.get(years.size() - 1);
            year[0] = lastYear[0].minusYears(1);
            year[1] = lastYear[1].minusYears(1);
            years.add(year);
        }


        for (LocalDate[] year : years) {
            String y = String.valueOf(year[0].getYear());
            Long count = saleDao.getSaleCountByRange(year[0], year[1]);
            HashMap<String, Object> d = new HashMap<>();
            d.put("year", y);
            d.put("count", count);
            data1.add(d);
        }
        return data1;
    }

    @GetMapping("/year-wise-sale/{yearCount}")
    public ArrayList<HashMap<String, Object>> yearWiseSale(@PathVariable Integer yearCount, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to show this report", UsecaseList.SHOW_YEAR_WISE_SALE);

        ArrayList<HashMap<String, Object>> data1 = new ArrayList<>();

        ArrayList<LocalDate[]> years = new ArrayList<>();

        LocalDate[] currentYear = new LocalDate[2];
        currentYear[0] = LocalDate.parse(LocalDate.now().getYear() + "-01-01");
        currentYear[1] = LocalDate.parse(LocalDate.now().getYear() + "-12-31");
        years.add(currentYear);

        for (int i = 0; i < yearCount - 1; i++) {
            LocalDate[] year = new LocalDate[2];
            LocalDate[] lastYear = years.get(years.size() - 1);
            year[0] = lastYear[0].minusYears(1);
            year[1] = lastYear[1].minusYears(1);
            years.add(year);
        }

        for (LocalDate[] year : years) {
            String y = String.valueOf(year[0].getYear());
            Long sum1 = saleDao.getSaleByRange(year[0], year[1]);
            HashMap<String, Object> d = new HashMap<>();
            d.put("year", y);
            d.put("sum", sum1);
            data1.add(d);
        }
        return data1;
    }

    @GetMapping("/year-wise-purchase/{yearCount}")
    public ArrayList<HashMap<String, Object>> yearWisePurchase(@PathVariable Integer yearCount, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to show this report", UsecaseList.SHOW_YEAR_WISE_PURCHASE);

        ArrayList<HashMap<String, Object>> data1 = new ArrayList<>();

        ArrayList<LocalDate[]> years = new ArrayList<>();

        LocalDate[] currentYear = new LocalDate[2];
        currentYear[0] = LocalDate.parse(LocalDate.now().getYear() + "-01-01");
        currentYear[1] = LocalDate.parse(LocalDate.now().getYear() + "-12-31");
        years.add(currentYear);

        for (int i = 0; i < yearCount - 1; i++) {
            LocalDate[] year = new LocalDate[2];
            LocalDate[] lastYear = years.get(years.size() - 1);
            year[0] = lastYear[0].minusYears(1);
            year[1] = lastYear[1].minusYears(1);
            years.add(year);
        }

        for (LocalDate[] year : years) {
            String y = String.valueOf(year[0].getYear());
            Long sum2 = purchaseDao.getPurchaseByRange(year[0], year[1]);
            HashMap<String, Object> d = new HashMap<>();
            d.put("year", y);
            d.put("sum", sum2);
            data1.add(d);
        }
        return data1;
    }

    @GetMapping("/year-wise-income/{yearCount}")
    public ArrayList<HashMap<String, Object>> yearWiseIncome(@PathVariable Integer yearCount, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to show this report", UsecaseList.SHOW_YEAR_WISE_INCOME);

        ArrayList<HashMap<String, Object>> data1 = new ArrayList<>();

        ArrayList<LocalDate[]> years = new ArrayList<>();

        LocalDate[] currentYear = new LocalDate[2];
        currentYear[0] = LocalDate.parse(LocalDate.now().getYear() + "-01-01");
        currentYear[1] = LocalDate.parse(LocalDate.now().getYear() + "-12-31");
        years.add(currentYear);

        for (int i = 0; i < yearCount - 1; i++) {
            LocalDate[] year = new LocalDate[2];
            LocalDate[] lastYear = years.get(years.size() - 1);
            year[0] = lastYear[0].minusYears(1);
            year[1] = lastYear[1].minusYears(1);
            years.add(year);
        }

        for (LocalDate[] year : years) {
            String y = String.valueOf(year[0].getYear());
            Long sum2 = saleDao.getSaleByRange(year[0], year[1]);
            Long sum1 = purchaseDao.getPurchaseByRange(year[0], year[1]);
            if (sum1 != null && sum2 != null) {
                Long sum = sum2 - sum1;
                HashMap<String, Object> d = new HashMap<>();
                d.put("year", y);
                d.put("sum", sum);
                data1.add(d);
            }
        }
        return data1;
    }

    @GetMapping("/day-wise-sale/2021/{month}")
    public ArrayList<HashMap<String, Object>> dayWiseSale(@PathVariable String month, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to show this report", UsecaseList.SHOW_DAY_WISE_SALE);

        ArrayList<HashMap<String, Object>> data2 = new ArrayList<>();

        String[] months = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String[] days = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};


        for (String day : days) {
            Integer amount = saleDao.getDailySaleAmount(month, day);
            HashMap<String, Object> d = new HashMap<>();
            d.put("day", 2021 + "-" + month + "-" + day);
            d.put("amount", (amount != null) ? amount : 0);
            data2.add(d);
        }
        return data2;

    }

    @GetMapping("/month-wise-itemcategory-sale/{year}")
    public ArrayList<HashMap<String, Object>> monthWiseItemCategorySale(@PathVariable String year, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to show this report", UsecaseList.SHOW_MONTH_WISE_ITEMCATEGORY_SALE);

        ArrayList<HashMap<String, Object>> data2 = new ArrayList<>();

        String[] months = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        for (String month : months) {
            Integer amount = saleDao.getMonthlySaleitemCategoryAmount(year, month);
            HashMap<String, Object> d = new HashMap<>();
            d.put("month", year + "-" + month + "-01");
            d.put("amount", (amount != null) ? amount : 0);
            data2.add(d);
        }
        return data2;

    }


}


