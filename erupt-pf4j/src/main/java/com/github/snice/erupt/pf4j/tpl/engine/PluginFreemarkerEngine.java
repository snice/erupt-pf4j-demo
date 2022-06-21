package com.github.snice.erupt.pf4j.tpl.engine;

import com.github.snice.erupt.pf4j.tpl.PluginUtils;
import freemarker.cache.URLTemplateLoader;
import freemarker.template.Configuration;
import lombok.SneakyThrows;
import xyz.erupt.annotation.sub_erupt.Tpl;
import xyz.erupt.tpl.engine.EngineTemplate;

import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PluginFreemarkerEngine extends EngineTemplate<Configuration> {
    @Override
    public Tpl.Engine engine() {
        return Tpl.Engine.FreeMarker;
    }
    @Override
    public Configuration init() {
        Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_29);
        freemarkerConfig.setDefaultEncoding(StandardCharsets.UTF_8.name());
        freemarkerConfig.setTemplateLoader(new PluginTemplateLoader());
        return freemarkerConfig;
    }
    @SneakyThrows
    @Override
    public void render(Configuration configuration, String filePath, Map<String, Object> bindingMap, Writer out) {
        configuration.getTemplate(filePath).process(bindingMap, out);
    }
    private class PluginTemplateLoader extends URLTemplateLoader {
        @Override
        protected URL getURL(String s) {
            return PluginUtils.pluginWrapper.getPluginClassLoader().getResource(s);
        }
    }
}
