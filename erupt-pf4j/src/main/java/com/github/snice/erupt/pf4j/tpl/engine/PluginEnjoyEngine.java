package com.github.snice.erupt.pf4j.tpl.engine;

import com.github.snice.erupt.pf4j.tpl.PluginUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSource;
import com.jfinal.template.source.ISource;
import com.jfinal.template.source.ISourceFactory;
import xyz.erupt.annotation.sub_erupt.Tpl;
import xyz.erupt.tpl.engine.EngineTemplate;

import java.io.Writer;
import java.util.Map;

public class PluginEnjoyEngine extends EngineTemplate<Engine> {
    @Override
    public Tpl.Engine engine() {
        return Tpl.Engine.Enjoy;
    }

    @Override
    public Engine init() {
        Engine eng = new Engine();
        eng.setSourceFactory(new PluginClassPathSourceFactory());
        eng.addSharedMethod(StrKit.class);
        eng.setDevMode(true);
        return eng;
    }

    @Override
    public void render(Engine engine, String filePath, Map<String, Object> bindingMap, Writer out) {
        engine.getTemplate(filePath).render(bindingMap, out);
    }

    private class PluginClassPathSourceFactory implements ISourceFactory {
        @Override
        public ISource getSource(String baseTemplatePath, String fileName, String encoding) {
            return new PluginClassPathSource(baseTemplatePath, fileName, encoding);
        }
    }

    private class PluginClassPathSource extends ClassPathSource {

        @Override
        protected ClassLoader getClassLoader() {
            return PluginUtils.pluginWrapper.getPluginClassLoader();
        }

        public PluginClassPathSource(String fileName) {
            super(fileName);
        }

        public PluginClassPathSource(String baseTemplatePath, String fileName) {
            super(baseTemplatePath, fileName);
        }

        public PluginClassPathSource(String baseTemplatePath, String fileName, String encoding) {
            super(baseTemplatePath, fileName, encoding);
        }
    }
}
