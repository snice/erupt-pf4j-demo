package com.github.snice.erupt.pf4j.tpl.aop;

import com.github.snice.erupt.pf4j.tpl.PluginUtils;
import com.github.snice.erupt.pf4j.tpl.engine.PluginFreemarkerEngine;
import com.github.snice.spring.pf4j.SpringPlugin;
import com.github.snice.spring.pf4j.SpringPluginManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import xyz.erupt.annotation.sub_erupt.Tpl;
import xyz.erupt.core.service.EruptCoreService;
import xyz.erupt.core.view.EruptModel;
import xyz.erupt.tpl.annotation.EruptTpl;
import xyz.erupt.tpl.annotation.TplAction;
import xyz.erupt.tpl.engine.EngineTemplate;
import xyz.erupt.tpl.engine.NativeEngine;
import xyz.erupt.tpl.service.EruptTplService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Order(1)
@Slf4j
public class PluginTplActionAspect {

    @Lazy
    @Resource
    private EruptTplService eruptTplService;

    @Lazy
    @Resource
    private SpringPluginManager pluginManager;

    private Field fieldTplEngines;

    private Map<Tpl.Engine, EngineTemplate<Object>> eruptTplEngines;

    private static final Map<Tpl.Engine, EngineTemplate<Object>> pluginTplEngines = new HashMap<>();

    private static final Class<?>[] pluginEngineTemplates = {NativeEngine.class, PluginFreemarkerEngine.class};

    static {
        for (Class<?> tpl : pluginEngineTemplates) {
            try {
                EngineTemplate<Object> engineTemplate = (EngineTemplate) tpl.newInstance();
                engineTemplate.setEngine(engineTemplate.init());
                pluginTplEngines.put(engineTemplate.engine(), engineTemplate);
            } catch (NoClassDefFoundError ignored) {
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Pointcut("execution(public * xyz.erupt.tpl.controller.EruptTplController.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object tplBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Erupt-pf4j TPL 拦截器");
        if (fieldTplEngines == null) {
            fieldTplEngines = ReflectionUtils.findField(EruptTplService.class, "tplEngines");
            if (fieldTplEngines != null) {
                fieldTplEngines.setAccessible(true);
                if (Modifier.isFinal(fieldTplEngines.getModifiers())) {
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(fieldTplEngines, fieldTplEngines.getModifiers() & ~Modifier.FINAL);
                }
            }
        }
        if (joinPoint instanceof MethodInvocationProceedingJoinPoint) {
            MethodInvocationProceedingJoinPoint invocationProceedingJoinPoint = ((MethodInvocationProceedingJoinPoint) joinPoint);
            Signature signature = invocationProceedingJoinPoint.getSignature();
            Object[] args = invocationProceedingJoinPoint.getArgs();
            String fileName = (String) args[0];
            if ("eruptTplPage".equals(signature.getName())) {
                HttpServletResponse response = (HttpServletResponse) args[1];
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                Method method = this.eruptTplService.getAction(fileName);
                if (method != null) {
                    Class tplClass = method.getDeclaringClass();
                    PluginWrapper pluginWrapper =
                            pluginManager.getPlugins(PluginState.STARTED).stream().filter(it -> it.getPluginClassLoader() == tplClass.getClassLoader()).findFirst().orElse(null);
                    if (pluginWrapper != null) {
                        Object tplObj = null;
                        Collection collection = ((SpringPlugin) pluginWrapper.getPlugin()).getApplicationContext().getBeansOfType(tplClass).values();
                        if (collection != null) {
                            tplObj = collection.stream().findFirst().orElse(null);
                        }
                        if (tplObj != null) {
                            PluginUtils.pluginWrapper = pluginWrapper;
                            renderPluginTpl(fileName, method, tplObj, response);
                            PluginUtils.pluginWrapper = null;
                            return null;
                        }
                    }
                }
            } else if ("getEruptFieldHtml".equals(signature.getName()) || "getOperationTpl".equals(signature.getName())) {
                EruptModel eruptModel = EruptCoreService.getErupt(fileName);
                PluginWrapper pluginWrapper =
                        pluginManager.getPlugins(PluginState.STARTED).stream().filter(it -> it.getPluginClassLoader() == eruptModel.getClazz().getClassLoader()).findFirst().orElse(null);
                if (pluginWrapper != null) {
                    PluginUtils.pluginWrapper = pluginWrapper;
                    ReflectionUtils.setField(fieldTplEngines, eruptTplService, pluginTplEngines);
                }
            }
        }
        Object obj = joinPoint.proceed();
        ReflectionUtils.setField(fieldTplEngines, eruptTplService, eruptTplEngines);
        PluginUtils.pluginWrapper = null;
        return obj;
    }

    private void renderPluginTpl(String fileName, Method method, Object obj, HttpServletResponse response) throws IOException, IllegalAccessException,
            InvocationTargetException {
        if (eruptTplEngines == null) {
            eruptTplEngines = (Map<Tpl.Engine, EngineTemplate<Object>>) ReflectionUtils.getField(fieldTplEngines, eruptTplService);
        }

        EruptTpl eruptTpl = obj.getClass().getAnnotation(EruptTpl.class);
        TplAction tplAction = method.getAnnotation(TplAction.class);
        String path = "/tpl/" + fileName;
        if (StringUtils.isNotBlank(tplAction.path())) {
            path = tplAction.path();
        }
        ReflectionUtils.setField(fieldTplEngines, eruptTplService, pluginTplEngines);
        this.eruptTplService.tplRender(eruptTpl.engine(), path, (Map) method.invoke(obj), response.getWriter());
        ReflectionUtils.setField(fieldTplEngines, eruptTplService, eruptTplEngines);
    }

}
