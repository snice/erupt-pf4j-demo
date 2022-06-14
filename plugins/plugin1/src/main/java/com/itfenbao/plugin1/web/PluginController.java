package com.itfenbao.plugin1.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Controller
public class PluginController {

    @Resource
    DataSource dataSource;

    @GetMapping("/abc")
    public ResponseEntity home() {
        return ResponseEntity.ok("OK" + dataSource);
    }

    @GetMapping("/p/test")
    public String test() {
        return "plugin/list";
    }

}
