package com.github.snice.erupt.pf4j.tpl.engine;

import com.github.snice.erupt.pf4j.tpl.PluginUtils;
import lombok.SneakyThrows;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.UrlTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.UrlTemplateResource;
import xyz.erupt.annotation.sub_erupt.Tpl;
import xyz.erupt.tpl.engine.EngineTemplate;

import java.io.Writer;
import java.net.URL;
import java.util.Map;

public class PluginThymeleafEngine extends EngineTemplate<TemplateEngine> {
    @Override
    public Tpl.Engine engine() {
        return Tpl.Engine.Thymeleaf;
    }

    @Override
    public TemplateEngine init() {
        PluginThymeleafTemplateResolver resolver = new PluginThymeleafTemplateResolver();
        resolver.setCacheable(false);
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCheckExistence(true);
        resolver.setUseDecoupledLogic(true);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(resolver);
        return templateEngine;
    }

    @SneakyThrows
    @Override
    public void render(TemplateEngine templateEngine, String filePath, Map<String, Object> bindingMap, Writer out) {
        Context ctx = new Context();
        ctx.setVariables(bindingMap);
        out.write(templateEngine.process(filePath, ctx));
    }

    private class PluginThymeleafTemplateResolver extends UrlTemplateResolver {

        protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName,
                                                            String characterEncoding, Map<String, Object> templateResolutionAttributes) {
            ClassLoader classLoader = PluginUtils.pluginWrapper.getPluginClassLoader();
            String s = resourceName.replaceFirst("\\/", "");
            while (s.startsWith("/")) {
                s = resourceName.replaceFirst("\\/", "");
            }
            URL url = classLoader.getResource(s);
            if (url != null) return new UrlTemplateResource(url, characterEncoding);
            return null;
        }
    }
}
