package com.github.snice.erupt.pf4j.configuration;

import org.pf4j.PluginManager;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginResourceHandlerMapping extends SimpleUrlHandlerMapping {

    private static final String DEFAULT_PATH = "/_plugins/static/";

    private final javax.servlet.ServletContext servletContext;

    private final PluginManager pluginManager;

    public PluginResourceHandlerMapping(ServletContext servletContext, PluginManager pluginManager) {
        this.servletContext = servletContext;
        this.pluginManager = pluginManager;
        init();
    }

    private void init() {
        setOrder(Ordered.HIGHEST_PRECEDENCE);
        Map<String, ResourceHttpRequestHandler> urlMap = new HashMap<>();
        urlMap.put(DEFAULT_PATH + "/**", createResourceRequestHandler("/static/"));
        setUrlMap(urlMap);
    }

    private ResourceHttpRequestHandler createResourceRequestHandler(String... path) {
        Stream<ClassPathResource> stream = Arrays.stream(path).map(it -> new ClassPathResource(it));
        List<Resource> resourceList = stream.collect(Collectors.toList());

        PathResourceResolver staticResourceResolver = new PluginResourceResolver(pluginManager);
        staticResourceResolver.setAllowedLocations(resourceList.toArray(new Resource[resourceList.size()]));
        staticResourceResolver.setUrlPathHelper(new UrlPathHelper());

        ResourceHttpRequestHandler staticResourceHttpRequestHandler = new ResourceHttpRequestHandler();
        staticResourceHttpRequestHandler.setLocations(resourceList);
        staticResourceHttpRequestHandler.setResourceResolvers(Arrays.asList(staticResourceResolver));
        staticResourceHttpRequestHandler.setServletContext(servletContext);
        try {
            staticResourceHttpRequestHandler.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return staticResourceHttpRequestHandler;
    }
}
