package bit.project.server.controller;

import bit.project.server.UsecaseList;
import bit.project.server.dao.SaleitemDao;
import bit.project.server.dao.SalepaymentDao;
import bit.project.server.util.exception.ConflictException;
import bit.project.server.util.security.AccessControlManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;

public class SalepaymentController {

    @Autowired
    private SalepaymentDao salepaymentDao;

    @Autowired
    private AccessControlManager accessControlManager;


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request) {
        accessControlManager.authorize(request, "No privilege to delete details of a item", UsecaseList.DELETE_SALEPAYMENT);

        try {
            if (salepaymentDao.existsById(id)) salepaymentDao.deleteById(id);
        } catch (DataIntegrityViolationException | RollbackException e) {
            throw new ConflictException("Cannot delete. Because this item already used in another module");
        }
    }
}
