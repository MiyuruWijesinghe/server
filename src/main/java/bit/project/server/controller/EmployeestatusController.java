package bit.project.server.controller;

import bit.project.server.dao.EmployeestatusDao;
import bit.project.server.entity.Employeestatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/employeestatuses")
public class EmployeestatusController {

    @Autowired EmployeestatusDao employeestatusDao;

    @GetMapping
    public List<Employeestatus> getAll(){
        return employeestatusDao.findAll();
    }
}
    
