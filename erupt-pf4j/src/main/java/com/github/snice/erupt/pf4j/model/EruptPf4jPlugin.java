package com.github.snice.erupt.pf4j.model;

import com.github.snice.erupt.pf4j.service.Pf4jPluginService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.EruptI18n;
import xyz.erupt.annotation.fun.DataProxy;
import xyz.erupt.annotation.fun.OperationHandler;
import xyz.erupt.annotation.sub_erupt.Drill;
import xyz.erupt.annotation.sub_erupt.Link;
import xyz.erupt.annotation.sub_erupt.RowOperation;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.BoolType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.jpa.model.MetaModel;

import javax.annotation.Resource;
import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@EruptI18n
@Erupt(name = "插件列表", dataProxy = EruptPf4jPlugin.class, drills = @Drill(title = "版本", icon = "fa" + " fa-list", link = @Link(linkErupt = EruptPf4jPluginVersion.class, joinColumn = "eruptPf4jPlugin.id")), rowOperation = {@RowOperation(icon = "fa fa-download", title = "安装", operationParam = "install", operationHandler = EruptPf4jPlugin.class), @RowOperation(icon = "fa fa-cloud-upload", title = "更新", operationParam = "update", operationHandler = EruptPf4jPlugin.class)})
@Entity
@Table(name = "e_pf4j_plugin", uniqueConstraints = @UniqueConstraint(columnNames = "plugin_id"))
@Component
@Getter
@Setter
@Slf4j
public class EruptPf4jPlugin extends MetaModel implements DataProxy<EruptPf4jPlugin>, OperationHandler<EruptPf4jPlugin, Void> {

    @EruptField(views = @View(title = "插件ID", width = "150px"), edit = @Edit(title = "插件ID", notNull = true, search = @Search(vague = true)))
    @Column(name = "plugin_id")
    private String pluginId;

    @EruptField(views = @View(title = "插件名称"), edit = @Edit(title = "插件名称", notNull = true, search = @Search(vague = true)))
    @Column(name = "plugin_name")
    private String pluginName;

    @EruptField(views = @View(title = "插件描述"), edit = @Edit(title = "插件描述", notNull = true))
    private String pluginDesc;

    @Transient
    @EruptField(views = @View(title = "插件状态"), edit = @Edit(title = "插件状态", boolType = @BoolType(trueText = "启用", falseText = "禁用"), show = false))
    private Boolean status;

    @Transient
    @EruptField(views = @View(title = "启用版本", width = "100px"))
    private String version;

    @Transient
    @EruptField(views = @View(title = "最新版本", width = "100px"))
    private String lastVersion;

    @Transient
    @Resource
    Pf4jPluginService pf4jPluginService;

    @Transient
    @Resource
    EruptDao eruptDao;

    public Boolean getStatus() {
        return status;
    }

    @Override
    public void afterFetch(Collection<Map<String, Object>> list) {
        list.forEach(it -> {
            String pluginId = it.get("pluginId").toString();
            it.put("status", true);
            it.put("version", "0.0.1");
            it.put("lastVersion", "0.0.1");
        });
        log.info("afterFetch");
    }

    @Override
    public String exec(List<EruptPf4jPlugin> data, Void unused, String[] param) {
        if (param == null || param.length == 0) return null;
        String tag = param[0];
        if ("install".equals(tag)) {
//            pf4jPluginService.installPlugin(plugin.getPluginId(), eruptPf4jPluginVersion.version);
        } else if ("update".equals(tag)) {

        }
        return null;
    }
}
