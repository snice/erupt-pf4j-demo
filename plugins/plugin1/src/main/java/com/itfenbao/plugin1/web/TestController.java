package com.itfenbao.plugin1.web;

import com.itfenbao.plugin1.entity.Teacher;
import com.itfenbao.plugin1.repository.TeacherRepository;
import com.itfenbao.plugin1.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@RestController
@RequestMapping(path = "/test")
public class TestController {

    @Resource
    TeacherRepository repository;

    @Resource
    TestService testService;

    @GetMapping
    @RequestMapping(path = {"", "/"})
    @Transactional
    public String index() {
        testService.say("plugin1");
        repository.findById(1L);
        Teacher t = new Teacher();
        t.setUsername("t");
        t.setFirstname("t");
        t.setLastname("t");
        repository.save(t);
        return "from plugin1===" + repository;
//        return "from plugin1===";
    }

}
