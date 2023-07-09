package bit.project.server.controller;

import bit.project.server.dao.ItemcategoryDao;
import bit.project.server.entity.Itemcategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/itemcategories")
public class ItemcategoryController {

    @Autowired
    ItemcategoryDao itemcategoryDao;

    @GetMapping
    public List<Itemcategory> getAll(){ return itemcategoryDao.findAll();
    }
}
