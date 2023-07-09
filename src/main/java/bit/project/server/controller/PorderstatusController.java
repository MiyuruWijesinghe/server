package bit.project.server.controller;


import bit.project.server.dao.PorderstatusDao;
import bit.project.server.entity.Branchstatus;

import bit.project.server.entity.Porderstatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/porderstatuses")
public class PorderstatusController {

    @Autowired
    PorderstatusDao porderstatusDao;

    @GetMapping
    public List<Porderstatus> getAll(){
        return porderstatusDao.findAll();
    }
    }

