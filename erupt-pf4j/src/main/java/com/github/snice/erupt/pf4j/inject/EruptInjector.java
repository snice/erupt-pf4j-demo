package com.github.snice.erupt.pf4j.inject;

import com.github.snice.spring.pf4j.SpringPluginManager;
import com.github.snice.spring.pf4j.inject.ISpringInjector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.core.service.EruptCoreService;

@Lazy
@Component
@Slf4j
public class EruptInjector extends ISpringInjector {
    public EruptInjector(ApplicationContext applicationContext, SpringPluginManager springPluginManager) {
        super(applicationContext, springPluginManager);
    }

    @Override
    public boolean isSupport(Class c) {
        return AnnotatedElementUtils.hasAnnotation(c, Erupt.class);
    }

    @Override
    public void register(Class c) throws Exception {
        EruptCoreService.registerErupt(c);
        log.info("register @Erupt " + c);
    }

    @Override
    public void unregister(Class c) throws Exception {
        log.info("unregister @Erupt " + c);
    }
}
