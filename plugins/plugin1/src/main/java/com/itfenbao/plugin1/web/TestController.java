package com.itfenbao.plugin1.web;

import com.itfenbao.plugin1.repository.TeacherRepository;
import com.itfenbao.plugin1.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(path = "/test")
public class TestController {

    @Resource
    TeacherRepository repository;

    @Resource
    TestService testService;

    @GetMapping
    @RequestMapping(path = {"", "/"})
    public String index() {
        testService.say("plugin1");
        repository.findById(1L);
        return "from plugin1===" + repository;
//        return "from plugin1===";
    }

}
