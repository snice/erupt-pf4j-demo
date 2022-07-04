package com.github.snice.erupt.pf4j.tpl.engine.velocity;

import com.github.snice.erupt.pf4j.tpl.PluginUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.Reader;

public class PluginClasspathResourceLoader extends ClasspathResourceLoader {

    public Reader getResourceReader(String name, String encoding) throws ResourceNotFoundException {
        if (PluginUtils.pluginWrapper == null) return null;
        String s = name.replaceFirst("\\/", "");
        while (s.startsWith("/")) {
            s = name.replaceFirst("\\/", "");
        }
        try {
            return this.buildReader(PluginUtils.pluginWrapper.getPluginClassLoader().getResourceAsStream(s), encoding);
        } catch (IOException e) {
            return null;
        }
    }

}
