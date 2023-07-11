package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.SaleitemDao;
import bit.project.server.util.dto.PageQuery;
import bit.project.server.util.exception.ConflictException;
import bit.project.server.util.helper.PageHelper;
import bit.project.server.util.security.AccessControlManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/saleitem")
public class SaleitemController {


    @Autowired
    private SaleitemDao saleitemDao;

    @Autowired
    private AccessControlManager accessControlManager;

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to delete details of a item", UsecaseList.DELETE_SALEITEM);

        try {
            if (saleitemDao.existsById(id)) saleitemDao.deleteById(id);
        } catch (DataIntegrityViolationException | RollbackException e) {
            throw new ConflictException("Cannot delete. Because this item already used in another module");
        }
    }
}
