package com.github.snice.erupt.pf4j.inject;

import com.github.snice.spring.pf4j.SpringPluginManager;
import com.github.snice.spring.pf4j.inject.ISpringInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class EruptInjector extends ISpringInjector {

    public EruptInjector(ApplicationContext applicationContext, SpringPluginManager springPluginManager) {
        super(applicationContext, springPluginManager);
    }

    @Override
    public boolean isSupport(Class c) {
        return false;
    }

    @Override
    public void register(Class c) throws Exception {

    }

    @Override
    public void unregister(Class c) throws Exception {

    }
}
