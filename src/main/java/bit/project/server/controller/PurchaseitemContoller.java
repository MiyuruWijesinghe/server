package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.ItembranchDao;
import bit.project.server.dao.PurchaseitemDao;
import bit.project.server.entity.Branch;
import bit.project.server.entity.Item;
import bit.project.server.entity.Purchaseitem;
import bit.project.server.util.exception.ConflictException;
import bit.project.server.util.security.AccessControlManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@CrossOrigin
@RestController
@RequestMapping("/purchaseitems")
public class PurchaseitemContoller {

    @Autowired
    private PurchaseitemDao purchaseitemDao;

    @Autowired
    private AccessControlManager accessControlManager;


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get details of a item", UsecaseList.DELETE_PURCHASEITEM);

        try{
            if(purchaseitemDao.existsById(id)) purchaseitemDao.deleteById(id);
        }catch (DataIntegrityViolationException | RollbackException e){
            throw new ConflictException("Cannot delete. Because this item already used in another module");
        }
    }
}
