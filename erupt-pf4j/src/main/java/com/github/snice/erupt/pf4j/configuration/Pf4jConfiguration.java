package com.github.snice.erupt.pf4j.configuration;

import com.github.snice.erupt.pf4j.config.EruptPf4jProperties;
import com.github.snice.spring.pf4j.SpringPluginManager;
import org.pf4j.update.UpdateManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class Pf4jConfiguration {
    private final EruptPf4jProperties properties;

    public Pf4jConfiguration(EruptPf4jProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SpringPluginManager pluginManager() {
        List<Path> pathList = Arrays.stream(this.properties.getPluginLocation()).map(it -> Path.of(URI.create("file://" + it))).collect(Collectors.toList());
        return new SpringPluginManager(pathList);
    }

    @Bean
    @DependsOn("pluginManager")
    public UpdateManager updateManager(SpringPluginManager pluginManager) {
        UpdateManager updateManager = new UpdateManager(pluginManager, new ArrayList<>());
        try {
            updateManager.addRepository("http", new URL("http://localhost:8080/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return updateManager;
    }

    @Bean
    @DependsOn("pluginManager")
    public PluginResourceHandlerMapping pluginResourceHandlerMapping(ServletContext servletContext, SpringPluginManager pluginManager) {
        return new PluginResourceHandlerMapping(servletContext, pluginManager, properties);
    }


}
