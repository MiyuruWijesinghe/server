package bit.project.server.controller;

import bit.project.server.dao.CivilstatusDao;
import bit.project.server.entity.Civilstatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/civilstatuses")
public class CivilstatusController {

    @Autowired
    CivilstatusDao civilstatusDao;

    @GetMapping
    public List<Civilstatus> getAll() {
        return civilstatusDao.findAll();
    }
}

