package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.InventorycustomertypeDao;
import bit.project.server.dao.ItembranchDao;
import bit.project.server.util.exception.ConflictException;
import bit.project.server.util.security.AccessControlManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/inventorycustomertypes")
public class InventorycustomertypeController {


    @Autowired
    private InventorycustomertypeDao inventorycustomertypeDao;

    @Autowired
    private AccessControlManager accessControlManager;


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request){
        accessControlManager.authorize(request, "No privilege to get details of a item", UsecaseList.DELETE_INVENTORYCUSTOMERTYPE);

        try{
            if(inventorycustomertypeDao.existsById(id)) inventorycustomertypeDao.deleteById(id);
        }catch (DataIntegrityViolationException | RollbackException e){
            throw new ConflictException("Cannot delete. Because this item already used in another module");
        }
    }
}
