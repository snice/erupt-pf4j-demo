package com.github.snice.erupt.pf4j.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "erupt.pf4j", ignoreUnknownFields = false)
public class EruptPf4jProperties {

    /**
     * 是否开启开发模式
     */
    private boolean dev = false;

    /**
     * 插件存放目录
     */
    private String[] pluginLocation;

    /**
     * 插件静态资源映射
     */
    private String staticPathPattern = "/_plugin/**";

}
