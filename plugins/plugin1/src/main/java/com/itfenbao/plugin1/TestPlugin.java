package com.itfenbao.plugin1;

import com.github.snice.spring.pf4j.SpringPlugin;
import org.pf4j.PluginWrapper;

public class TestPlugin extends SpringPlugin {

    public TestPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class[] componentClasses() {
        return new Class[]{PluginConfiguration.class};
    }

    @Override
    public String basePackage() {
        return "com.itfenbao.plugin1";
    }

}
