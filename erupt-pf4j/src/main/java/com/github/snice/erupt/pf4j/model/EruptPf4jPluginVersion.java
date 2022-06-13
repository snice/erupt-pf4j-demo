package com.github.snice.erupt.pf4j.model;

import com.github.snice.erupt.pf4j.service.Pf4jPluginService;
import com.github.snice.spring.pf4j.SpringPluginManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.EruptI18n;
import xyz.erupt.annotation.fun.DataProxy;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.AttachmentType;
import xyz.erupt.annotation.sub_field.sub_edit.BoolType;
import xyz.erupt.annotation.sub_field.sub_edit.ReferenceTreeType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.core.exception.EruptWebApiRuntimeException;
import xyz.erupt.core.prop.EruptProp;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.jpa.model.MetaModel;

import javax.annotation.Resource;
import javax.persistence.*;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

@EruptI18n
@Table(name = "e_pf4j_plugin_version")
@Erupt(name = "插件版本", dataProxy = EruptPf4jPluginVersion.class)
@Entity
@Component
@Getter
@Setter
@Slf4j
public class EruptPf4jPluginVersion extends MetaModel implements DataProxy<EruptPf4jPluginVersion> {

    @ManyToOne
    @JoinColumn(name = "plugin_id")
    @EruptField(views = {@View(title = "插件名称", column = "pluginName"), @View(title = "PLUGIN_ID", column = "pluginId", show = false)}, edit = @Edit(title =
            "插件名称", show = false, search = @Search, type = EditType.REFERENCE_TREE, referenceTreeType = @ReferenceTreeType(label = "pluginName")))
    private EruptPf4jPlugin eruptPf4jPlugin;

    @EruptField(views = @View(title = "插件版本", width = "100px"), edit = @Edit(title = "插件版本", notNull = true))
    private String version;

    @EruptField(views = @View(title = "插件包"), edit = @Edit(title = "插件包", notNull = true, type = EditType.ATTACHMENT, attachmentType = @AttachmentType(path =
            "/pf4j-plugins", fileTypes = {"jar", "zip"})))
    private String attachment;

    @Transient
    @EruptField(views = @View(title = "使用状态"), edit = @Edit(title = "使用状态", boolType = @BoolType(trueText = "使用中", falseText = "未使用"), show = false))
    private Boolean used;

    @Transient
    @Lazy
    @Resource
    EruptDao eruptDao;

    @Transient
    @Lazy
    @Resource
    SpringPluginManager pluginManager;

    @Transient
    @Lazy
    @Resource
    Pf4jPluginService pluginService;

    @Transient
    @Lazy
    @Resource
    EruptProp eruptProp;

    @Override
    public void afterAdd(EruptPf4jPluginVersion eruptPf4jPluginVersion) {
        Path pluginPath = Path.of(URI.create("file://" + eruptProp.getUploadPath() + eruptPf4jPluginVersion.attachment));
        PluginDescriptor pluginDescriptor = pluginService.getPluginDescriptor(pluginPath);
        if (eruptPf4jPluginVersion.getEruptPf4jPlugin().getPluginId() == null) {
            EruptPf4jPlugin plugin = eruptDao.getEntityManager().find(EruptPf4jPlugin.class, eruptPf4jPluginVersion.getEruptPf4jPlugin().getId());
            if (!plugin.getPluginId().equals(pluginDescriptor.getPluginId())) {
                throw new EruptWebApiRuntimeException("插件包pluginId错误[" + pluginDescriptor.getPluginId() + "]，必须是[" + plugin.getPluginId() + "]");
            }
        }
        if (!pluginDescriptor.getVersion().equals(eruptPf4jPluginVersion.getVersion())) {
            throw new EruptWebApiRuntimeException("插件包版本为[" + pluginDescriptor.getVersion() + "],必须保持一致");
        }
        pluginService.refresh();
    }

    @Override
    public void afterDelete(EruptPf4jPluginVersion eruptPf4jPluginVersion) {
        pluginService.refresh();
    }

    @Override
    public void afterUpdate(EruptPf4jPluginVersion eruptPf4jPluginVersion) {
        afterAdd(eruptPf4jPluginVersion);
    }

    @Override
    public void afterFetch(Collection<Map<String, Object>> list) {
        list.forEach(it -> {
            String version = it.get("version") != null ? it.get("version").toString() : null;
            String pluginId = it.get("eruptPf4jPlugin_pluginId") != null ? it.get("eruptPf4jPlugin_pluginId").toString() : null;
            if (StringUtils.hasLength(pluginId)) {
                PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginId);
                boolean started = pluginWrapper != null && pluginWrapper.getPluginState() == PluginState.STARTED;
                it.put("used", started && pluginWrapper.getDescriptor().getVersion().equals(version));
            } else {
                it.put("used", false);
            }
        });
    }
}
