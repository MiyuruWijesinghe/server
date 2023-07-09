package bit.project.server.controller;


import bit.project.server.dao.UsecaseDao;
import bit.project.server.entity.Usecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/usercases")
public class UsercaseController {

    @Autowired
    UsecaseDao usercaseDao;

    @GetMapping
    public List<Usecase> getAll(){return usercaseDao.findAll();}
}
