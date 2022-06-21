package com.github.snice.erupt.pf4j.tpl.engine.velocity;

import com.github.snice.erupt.pf4j.tpl.PluginUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.InputStream;

public class PluginClasspathResourceLoader extends ClasspathResourceLoader {

    @Override
    public InputStream getResourceStream(String name) throws ResourceNotFoundException {
        if(PluginUtils.pluginWrapper == null) return null;
        return PluginUtils.pluginWrapper.getPluginClassLoader().getResourceAsStream(name);
    }

}
