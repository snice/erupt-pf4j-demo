package com.itfenbao.plugin2;

import com.github.snice.spring.pf4j.SpringPlugin;
import org.pf4j.PluginWrapper;

public class Plugin2Plugin extends SpringPlugin {
    public Plugin2Plugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public Class[] componentClasses() {
        return new Class[]{PluginConfiguration.class};
    }

    @Override
    public String basePackage() {
        return "com.itfenbao.plugin2";
    }
}
