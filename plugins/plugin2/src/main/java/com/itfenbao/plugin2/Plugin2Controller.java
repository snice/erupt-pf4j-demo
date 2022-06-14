package com.itfenbao.plugin2;

import cn.hutool.core.date.DateUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plugin2")
public class Plugin2Controller {

    @GetMapping("/test")
    public String test() {
        return "plugin2 test" + DateUtil.now();
    }
}
