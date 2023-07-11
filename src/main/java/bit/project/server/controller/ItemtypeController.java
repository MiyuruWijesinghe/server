package bit.project.server.controller;


import bit.project.server.dao.ItemtypeDao;
import bit.project.server.entity.Itemtype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/itemtypes")
public class ItemtypeController {

    @Autowired
    ItemtypeDao itemtypeDao;

    @GetMapping
    public List<Itemtype> getAll() {
        return itemtypeDao.findAll();
    }
}
