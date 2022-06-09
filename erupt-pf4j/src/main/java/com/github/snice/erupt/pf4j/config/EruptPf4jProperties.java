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

    private boolean dev = false;

    private String[] pluginLocation;

}
