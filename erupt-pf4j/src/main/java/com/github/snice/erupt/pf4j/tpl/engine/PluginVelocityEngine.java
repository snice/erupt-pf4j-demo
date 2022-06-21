package com.github.snice.erupt.pf4j.tpl.engine;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import xyz.erupt.annotation.sub_erupt.Tpl;
import xyz.erupt.tpl.engine.EngineTemplate;

import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PluginVelocityEngine extends EngineTemplate<VelocityEngine> {

    @Override
    public Tpl.Engine engine() {
        return Tpl.Engine.Velocity;
    }

    @Override
    public VelocityEngine init() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(Velocity.INPUT_ENCODING, StandardCharsets.UTF_8);
        ve.setProperty("file.resource.loader.class", "com.github.snice.erupt.pf4j.tpl.engine.velocity.PluginClasspathResourceLoader");
        ve.init();
        return ve;
    }

    @Override
    public void render(VelocityEngine velocityEngine, String filePath, Map<String, Object> bindingMap, Writer out) {
        velocityEngine.getTemplate(filePath, StandardCharsets.UTF_8.name()).merge(new VelocityContext(bindingMap), out);
    }
}
