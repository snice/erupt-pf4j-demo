package com.itfenbao.plugin1;

import com.github.snice.spring.pf4j.SpringPlugin;
import com.github.snice.spring.pf4j.SpringPluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestPlugin extends SpringPlugin {

    public TestPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        ApplicationContext appContext = ((SpringPluginManager) getWrapper().getPluginManager()).getApplicationContext();
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.setParent(appContext);
        applicationContext.register(PluginConfiguration.class);
        applicationContext.refresh();
        return applicationContext;
    }

    @Override
    public String basePackage() {
        return "com.itfenbao.plugin1";
    }

}
