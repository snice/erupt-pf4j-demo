package com.github.snice.erupt.pf4j.configuration;

import com.github.snice.spring.pf4j.SpringPluginManager;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginStateListener;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
@AutoConfigureAfter(Pf4jConfiguration.class)
public class Pf4jPluginConfiguration implements PluginStateListener {

    private final SpringPluginManager pluginManager;
    private final ApplicationContext applicationContext;

    public Pf4jPluginConfiguration(SpringPluginManager pluginManager, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.pluginManager = pluginManager;
        this.pluginManager.addPluginStateListener(this);
    }

    @PreDestroy
    public void cleanup() {
        pluginManager.stopPlugins();
    }

    @Override
    public void pluginStateChanged(PluginStateEvent event) {

    }
}
