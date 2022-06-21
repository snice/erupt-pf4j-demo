package com.github.snice.erupt.pf4j.inject;

import com.github.snice.erupt.pf4j.config.EruptPf4jProperties;
import com.github.snice.spring.pf4j.SpringPluginManager;
import com.github.snice.spring.pf4j.inject.ISpringInjector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.core.service.EruptCoreService;
import xyz.erupt.core.util.EruptSpringUtil;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.jpa.dao.EruptJpaDao;
import xyz.erupt.upms.model.EruptMenu;
import xyz.erupt.upms.service.EruptContextService;
import xyz.erupt.upms.service.EruptUserService;

import javax.annotation.Resource;
import java.util.HashMap;

@Lazy
@Component
@Slf4j
public class EruptInjector extends ISpringInjector {

    @Lazy
    @Resource
    EruptDao eruptDao;

    @Lazy
    @Resource
    EruptJpaDao eruptJpaDao;

    @Lazy
    @Resource
    EruptPf4jProperties properties;

    @Lazy
    @Resource
    TransactionInterceptor transactionInterceptor;

    public EruptInjector(ApplicationContext applicationContext, SpringPluginManager springPluginManager) {
        super(applicationContext, springPluginManager);
    }

    @Override
    public boolean isSupport(Class c) {
        return AnnotatedElementUtils.hasAnnotation(c, Erupt.class);
    }

    @Override
    public void register(Class c) throws Exception {
        EruptCoreService.registerErupt(c);
        if (!properties.isDev()) {
            EruptMenu menu = getMenu(c);
            if (menu != null) {
                if (menu.getStatus() == 1) return;
                // 菜单启用
                menu.setStatus(1);
                transactionInterceptor.setTransactionManager(null);
                eruptJpaDao.editEntity(EruptMenu.class, menu);
            } else {
                // 添加菜单
//                menu = new EruptMenu();
//                menu.setStatus(1);
//                menu.setName(c.getSimpleName());
//                menu.setValue(c.getSimpleName());
//                eruptJpaDao.addEntity(EruptMenu.class, menu);
            }
            flushCache();
        }
        log.info("register @Erupt " + c);
    }

    @Override
    public void unregister(Class c) throws Exception {
        EruptCoreService.unregisterErupt(c);
        if (!properties.isDev()) {
            EruptMenu menu = getMenu(c);
            if (menu != null) {
                if (menu.getStatus() != 1) return;
                // 隐藏启用
                menu.setStatus(2);
                transactionInterceptor.setTransactionManager(null);
                eruptJpaDao.editEntity(EruptMenu.class, menu);
                flushCache();
            }
        }
        log.info("unregister @Erupt " + c);
    }

    private EruptMenu getMenu(Class c) {
        return eruptDao.queryEntityList(EruptMenu.class, " value = :name", new HashMap<>() {{
            put("name", c.getSimpleName());
        }}).stream().findFirst().orElse(null);
    }

    private void flushCache() {
        EruptContextService eruptContextService = EruptSpringUtil.getBean(EruptContextService.class);
        EruptUserService eruptUserService = EruptSpringUtil.getBean(EruptUserService.class);
        eruptUserService.cacheUserInfo(eruptUserService.getCurrentEruptUser(), eruptContextService.getCurrentToken());
    }
}
