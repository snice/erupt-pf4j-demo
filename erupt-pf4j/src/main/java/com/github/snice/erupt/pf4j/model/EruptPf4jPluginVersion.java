package com.github.snice.erupt.pf4j.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
import xyz.erupt.jpa.model.MetaModel;

import javax.persistence.*;
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
    @EruptField(views = @View(title = "插件名称", column = "pluginName"), edit = @Edit(title = "插件名称", show = false, search = @Search, type = EditType.REFERENCE_TREE, referenceTreeType = @ReferenceTreeType(label = "pluginName")))
    private EruptPf4jPlugin eruptPf4jPlugin;

    @EruptField(views = @View(title = "插件版本", width = "100px"), edit = @Edit(title = "插件版本", notNull = true))
    private String version;

    @EruptField(views = @View(title = "插件包"), edit = @Edit(title = "插件包", type = EditType.ATTACHMENT, attachmentType = @AttachmentType))
    private String attachment;

    @Transient
    @EruptField(views = @View(title = "使用状态"), edit = @Edit(title = "使用状态", boolType = @BoolType(trueText = "使用中", falseText = "未使用"), show = false))
    private Boolean used;

    @Override
    public void afterFetch(Collection<Map<String, Object>> list) {
        log.info("afterFetch");
        list.forEach(it -> {

            it.put("used", true);
        });
    }
}
