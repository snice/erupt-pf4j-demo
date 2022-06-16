package com.github.snice.erupt.pf4j.jpa;

import com.github.snice.erupt.pf4j.jpa.aop.EruptTransactionInterceptor;
import com.github.snice.spring.pf4j.listener.PluginAppListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionManager;
import xyz.erupt.jpa.service.EntityManagerService;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.util.Map;

@Slf4j
@Component
public class EruptPluginAppListener implements PluginAppListener {

    @Lazy
    @Resource
    EntityManagerService entityManagerService;

    @Lazy
    @Resource
    EruptTransactionInterceptor eruptTransactionInterceptor;


    @Override
    public void onPluginStarted(ContextStartedEvent event) {
        log.info("EruptPluginAppListener stared");
        Map<String, TransactionManager> transactionManagerMap = event.getApplicationContext().getBeansOfType(TransactionManager.class);
        if (!transactionManagerMap.isEmpty()) {
            transactionManagerMap.values().forEach(tm -> {
                eruptTransactionInterceptor.addTransactionManager(tm);
            });
        }
        Map<String, EntityManager> entityManagerMap = event.getApplicationContext().getBeansOfType(EntityManager.class);
        if (!entityManagerMap.isEmpty()) {
            entityManagerMap.values().forEach(em -> {
                entityManagerService.addExtEntityManager(em);
            });
        }
    }

    @Override
    public void onPluginStopped(ContextStoppedEvent event) {
        log.info("EruptPluginAppListener stopped");
        Map<String, TransactionManager> transactionManagerMap = event.getApplicationContext().getBeansOfType(TransactionManager.class);
        if (!transactionManagerMap.isEmpty()) {
            transactionManagerMap.values().forEach(tm -> {
                eruptTransactionInterceptor.removeTransactionManager(tm);
            });
        }
        Map<String, EntityManager> entityManagerMap = event.getApplicationContext().getBeansOfType(EntityManager.class);
        if (!entityManagerMap.isEmpty()) {
            entityManagerMap.values().forEach(em -> {
                entityManagerService.removeExtEntityManager(em);
            });
        }
    }
}
