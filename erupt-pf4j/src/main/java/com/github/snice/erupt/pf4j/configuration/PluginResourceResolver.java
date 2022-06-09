package com.github.snice.erupt.pf4j.configuration;

import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

public class PluginResourceResolver extends PathResourceResolver {

    private final PluginManager pluginManager;

    public PluginResourceResolver(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
        if (!(location instanceof ClassPathResource)) return null;
        ClassPathResource classPathLocation = (ClassPathResource) location;

        for (PluginWrapper plugin : pluginManager.getPlugins(PluginState.STARTED)) {
            Resource pluginLocation = new ClassPathResource(classPathLocation.getPath(), plugin.getPluginClassLoader());
            Resource resource = pluginLocation.createRelative(resourcePath);
            if (resource.isReadable()) {
                if (checkResource(resource, pluginLocation)) {
                    return resource;
                } else if (logger.isWarnEnabled()) {
                    Resource[] allowedLocations = getAllowedLocations();
//                    logger.warn("Resource path \"" + resourcePath + "\" was successfully resolved " + "but resource \"" + resource.getURL() + "\" is neither under the " + "current location \"" + location.getURL() + "\" nor under any of the " + "allowed locations " + (allowedLocations != null ? Arrays.asList(allowedLocations) : "[]"));
                }
            }
        }

        return super.getResource(resourcePath, location);
    }

}
