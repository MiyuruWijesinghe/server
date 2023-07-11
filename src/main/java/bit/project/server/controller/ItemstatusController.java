package bit.project.server.controller;

import bit.project.server.dao.ItemstatusDao;
import bit.project.server.dao.ItemtypeDao;
import bit.project.server.entity.Item;
import bit.project.server.entity.Itemstatus;
import bit.project.server.entity.Itemtype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/itemstatuses")
public class ItemstatusController {
    @Autowired
    ItemstatusDao itemstatusDao;

    @GetMapping
    public List<Itemstatus> getAll() {
        return itemstatusDao.findAll();
    }
}
