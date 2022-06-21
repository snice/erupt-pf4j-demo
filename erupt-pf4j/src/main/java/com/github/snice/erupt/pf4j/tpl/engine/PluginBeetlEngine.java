package com.github.snice.erupt.pf4j.tpl.engine;

import com.github.snice.erupt.pf4j.tpl.PluginUtils;
import lombok.SneakyThrows;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import xyz.erupt.annotation.sub_erupt.Tpl;
import xyz.erupt.tpl.engine.EngineTemplate;

import java.io.Writer;
import java.util.Map;

public class PluginBeetlEngine extends EngineTemplate<GroupTemplate> {
    @Override
    public Tpl.Engine engine() {
        return Tpl.Engine.Beetl;
    }

    @SneakyThrows
    @Override
    public GroupTemplate init() {
        ClasspathResourceLoader resourceLoader = new PluginClasspathResourceLoader("/");
        Configuration cfg = Configuration.defaultConfiguration();
        return new GroupTemplate(resourceLoader, cfg);
    }

    @Override
    public void render(GroupTemplate groupTemplate, String filePath, Map<String, Object> bindingMap, Writer out) {
        Template template = groupTemplate.getTemplate(filePath);
        template.binding(bindingMap);
        template.renderTo(out);
    }

    private class PluginClasspathResourceLoader extends ClasspathResourceLoader {
        public PluginClasspathResourceLoader(String root) {
            super(root);
        }

        @Override
        public boolean exist(String key) {
            setClassLoader(getClassLoader());
            return super.exist(key);
        }

        @Override
        public ClassLoader getClassLoader() {
            return PluginUtils.pluginWrapper.getPluginClassLoader();
        }
    }

}
