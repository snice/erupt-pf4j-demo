package com.github.snice.erupt.pf4j.tpl;

import com.github.snice.spring.pf4j.SpringPluginManager;
import com.github.snice.spring.pf4j.inject.ISpringInjector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import xyz.erupt.tpl.annotation.EruptTpl;
import xyz.erupt.tpl.service.EruptTplService;

import javax.annotation.Resource;

@Lazy
@Component
@Slf4j
public class EruptTplInjector extends ISpringInjector {
    @Lazy
    @Resource
    EruptTplService tplService;

    public EruptTplInjector(ApplicationContext applicationContext, SpringPluginManager springPluginManager) {
        super(applicationContext, springPluginManager);
    }

    @Override
    public boolean isSupport(Class c) {
        return AnnotatedElementUtils.hasAnnotation(c, EruptTpl.class);
    }

    @Override
    public void register(Class c) throws Exception {
        tplService.registerTpl(c);
    }

    @Override
    public void unregister(Class c) throws Exception {
        tplService.unregisterTpl(c);
    }
}
