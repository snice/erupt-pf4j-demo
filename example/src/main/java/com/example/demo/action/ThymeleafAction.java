package com.example.demo.action;

import org.springframework.stereotype.Service;
import xyz.erupt.annotation.sub_erupt.Tpl;
import xyz.erupt.tpl.annotation.EruptTpl;
import xyz.erupt.tpl.annotation.TplAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YuePeng
 * date 2020-02-24
 */
@EruptTpl(engine = Tpl.Engine.Thymeleaf)
@Service
public class ThymeleafAction {

    @TplAction("thymeleaf.html")
    public Map<String, Object> dashboard2() {
        Map<String, Object> map = new HashMap<>();
        map.put("list", new String[]{
                "E", "R", "U", "P", "T", "-", "-", "-"
                , "-", "-", "-"
        });
        map.put("color", new String[]{
                "#eb776e", "#56aad6", "#69d5e7", "#f686e5", "#29ae94", "#fbd364",
                "#4da1ff", "#ff6e4b", "#ffc524", "#e07de9", "#42e9e1", "#a9f", "#a90",
                "#09f", "#555", "#92abff"
        });
        return map;
    }

}
