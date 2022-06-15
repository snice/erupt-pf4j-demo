package com.github.snice.erupt.pf4j.listener;

import com.github.snice.spring.pf4j.listener.PluginAppListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class EruptPluginAppListener implements PluginAppListener {

    @Lazy
    @Resource
    ApplicationContext applicationContext;

    @Override
    public void onPluginStarted(ContextStartedEvent event) {
        log.info("EruptPluginAppListener stared");
    }

    @Override
    public void onPluginStopped(ContextStoppedEvent event) {
        log.info("EruptPluginAppListener stopped");
    }
}
