package bit.project.server.controller;

import bit.project.server.dao.GenderDao;
import bit.project.server.entity.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/genders")
public class GenderController {

    @Autowired GenderDao genderDao;

    @GetMapping
    public List<Gender> getAll(){
        return genderDao.findAll();
    }
}
    
