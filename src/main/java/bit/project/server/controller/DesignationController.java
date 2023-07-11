package bit.project.server.controller;

import bit.project.server.dao.DesignationDao;
import bit.project.server.entity.Designation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/designations")
public class DesignationController {

    @Autowired
    DesignationDao designationDao;

    @GetMapping
    public List<Designation> getAll() {
        return designationDao.findAll();
    }
}
    
