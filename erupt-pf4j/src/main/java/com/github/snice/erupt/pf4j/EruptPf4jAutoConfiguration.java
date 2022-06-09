package com.github.snice.erupt.pf4j;

import com.github.snice.erupt.pf4j.configuration.Pf4jConfiguration;
import com.github.snice.erupt.pf4j.configuration.Pf4jPluginConfiguration;
import com.github.snice.erupt.pf4j.model.EruptPf4jPlugin;
import com.github.snice.erupt.pf4j.model.EruptPf4jPluginVersion;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import xyz.erupt.core.annotation.EruptScan;
import xyz.erupt.core.module.EruptModule;
import xyz.erupt.core.module.EruptModuleInvoke;
import xyz.erupt.core.module.MetaMenu;
import xyz.erupt.core.module.ModuleInfo;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan("com.github.snice")
@EntityScan
@EruptScan
@EnableConfigurationProperties
@Import({Pf4jConfiguration.class, Pf4jPluginConfiguration.class})
public class EruptPf4jAutoConfiguration implements EruptModule {

    static {
        EruptModuleInvoke.addEruptModule(EruptPf4jAutoConfiguration.class);
    }

    @Override
    public ModuleInfo info() {
        return ModuleInfo.builder().name("erupt-pf4j").build();
    }

    @Override
    public void run() {

    }

    @Override
    public List<MetaMenu> initMenus() {
        List<MetaMenu> menus = new ArrayList<>();
        menus.add(MetaMenu.createRootMenu("$pf4j", "插件管理", "fa fa-cubes", 30));
        menus.add(MetaMenu.createEruptClassMenu(EruptPf4jPlugin.class, menus.get(0), 0));
        menus.add(MetaMenu.createEruptClassMenu(EruptPf4jPluginVersion.class, menus.get(0), 10));
        return menus;
    }

}
