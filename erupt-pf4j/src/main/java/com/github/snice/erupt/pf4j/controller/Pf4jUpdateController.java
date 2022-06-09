package com.github.snice.erupt.pf4j.controller;

import com.github.snice.erupt.pf4j.model.EruptPf4jPlugin;
import com.github.snice.erupt.pf4j.model.EruptPf4jPluginVersion;
import org.pf4j.update.PluginInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.erupt.core.constant.EruptRestPath;
import xyz.erupt.jpa.dao.EruptDao;

import javax.annotation.Resource;
import java.sql.Date;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class Pf4jUpdateController {

    @Lazy
    @Resource
    EruptDao eruptDao;

    @GetMapping("/plugins.json")
    public List<PluginInfo> plugins() {
        return eruptDao.queryEntityList(EruptPf4jPlugin.class).stream().map(it -> {
            PluginInfo info = new PluginInfo();
            info.id = it.getPluginId();
            info.description = it.getPluginDesc();
            info.releases = eruptDao.queryEntityList(EruptPf4jPluginVersion.class, "eruptPf4jPlugin.id = " + it.getId()).stream().map(v -> {
                PluginInfo.PluginRelease release = new PluginInfo.PluginRelease();
                release.version = v.getVersion();
                release.date = Date.from(v.getCreateTime().toInstant(ZoneOffset.UTC));
                release.url = EruptRestPath.ERUPT_ATTACHMENT + v.getAttachment();
                return release;
            }).collect(Collectors.toList());
            return info;
        }).collect(Collectors.toList());
    }
}
