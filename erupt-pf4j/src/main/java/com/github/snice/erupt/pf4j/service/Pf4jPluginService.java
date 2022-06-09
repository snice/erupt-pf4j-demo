package com.github.snice.erupt.pf4j.service;

import com.github.snice.spring.pf4j.SpringPluginManager;
import org.pf4j.update.UpdateManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class Pf4jPluginService {

    @Lazy
    @Resource
    UpdateManager updateManager;

    @Lazy
    @Resource
    SpringPluginManager pluginManager;

    public boolean installPlugin(String pluginId, String version) {
        return updateManager.installPlugin(pluginId, version);
    }

    public void uninstallPlugin(String pluginId, String version) {

    }
}
