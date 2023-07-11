package bit.project.server.controller;


import bit.project.server.dao.CustomertypeDao;
import bit.project.server.entity.Customertype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/customertypes")
public class CustomertypeController {

    @Autowired
    CustomertypeDao customertypeDao;

    @GetMapping
    public List<Customertype> getAll() {
        return customertypeDao.findAll();
    }
}
