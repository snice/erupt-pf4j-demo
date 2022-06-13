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
import xyz.erupt.annotation.sub_erupt.Power;
import xyz.erupt.annotation.sub_erupt.RowOperation;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.BoolType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.core.exception.EruptWebApiRuntimeException;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.jpa.model.MetaModel;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@EruptI18n
@Erupt(name = "插件列表", power = @Power(delete = false), dataProxy = EruptPf4jPlugin.class, drills = @Drill(title = "版本", icon = "fa" + " fa-list", link =
@Link(linkErupt = EruptPf4jPluginVersion.class, joinColumn = "eruptPf4jPlugin.id")), rowOperation = {@RowOperation(icon = "fa fa-toggle-off", title = "启用",
        ifExpr = "item.hasPlugin &&  item.status == '禁用'", ifExprBehavior = RowOperation.IfExprBehavior.HIDE, mode = RowOperation.Mode.SINGLE,
        operationParam = "enable", operationHandler = EruptPf4jPlugin.class), @RowOperation(icon = "fa fa-toggle-on", title = "禁用", ifExpr = "item.hasPlugin "
        + "&& item.status == '启用'", ifExprBehavior = RowOperation.IfExprBehavior.HIDE, mode = RowOperation.Mode.SINGLE, tip = "会停止插件运行", operationParam =
        "disable", operationHandler = EruptPf4jPlugin.class),
//        @RowOperation(icon = "fa fa-download",
////                        show = @ExprBool(exprHandler = ViaMenuValueCtrl.class, params = "pf4j_plugin_install"),
//        mode = RowOperation.Mode.SINGLE, title = "安装", tip = "数据库相关", operationParam = "install", operationHandler = EruptPf4jPlugin.class),
        @RowOperation(icon = "fa fa-remove", title = "卸载", ifExpr = "item.hasPlugin", ifExprBehavior = RowOperation.IfExprBehavior.HIDE, mode =
                RowOperation.Mode.SINGLE, tip = "删除数据并删除插件及版本", operationParam = "uninstall", operationHandler = EruptPf4jPlugin.class), @RowOperation(icon =
        "fa fa-cloud-upload", title = "更新", mode = RowOperation.Mode.SINGLE, ifExpr = "item.lastVersion !== item.version", ifExprBehavior =
        RowOperation.IfExprBehavior.HIDE, operationParam = "update", operationHandler = EruptPf4jPlugin.class)})
@Entity
@Table(name = "e_pf4j_plugin", uniqueConstraints = @UniqueConstraint(columnNames = "pluginId"))
@Component
@Getter
@Setter
@Slf4j
public class EruptPf4jPlugin extends MetaModel implements DataProxy<EruptPf4jPlugin>, OperationHandler<EruptPf4jPlugin, Void> {

    @EruptField(views = @View(title = "插件ID", width = "150px"), edit = @Edit(title = "插件ID", notNull = true, search = @Search(vague = true)))
    private String pluginId;

    @EruptField(views = @View(title = "插件名称"), edit = @Edit(title = "插件名称", notNull = true, search = @Search(vague = true)))
    private String pluginName;

    @EruptField(views = @View(title = "插件描述"), edit = @Edit(title = "插件描述", notNull = true))
    private String pluginDesc;

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
    EruptDao eruptDao;

    @Transient
    @Resource
    Pf4jPluginService pf4jPluginService;

    @Override
    public void beforeAdd(EruptPf4jPlugin plugin) {
        String querySql = "select count(*) as _count from e_pf4j_plugin where plugin_id = ?";
        Map<String, Object> queryMap = eruptDao.getJdbcTemplate().queryForMap(querySql, plugin.getPluginId());
        if (Long.parseLong(queryMap.get("_count").toString()) > 0) {
            throw new EruptWebApiRuntimeException("插件[" + plugin.getPluginId() + "]已存在");
        }
    }

    @Override
    public void beforeUpdate(EruptPf4jPlugin plugin) {
        beforeAdd(plugin);
    }

    @Override
    public void afterFetch(Collection<Map<String, Object>> list) {
        list.forEach(it -> {
            if (!it.containsKey("pluginId")) return;
            String pluginId = it.get("pluginId").toString();
            it.put("hasPlugin", pf4jPluginService.hasPlugin(pluginId));
            it.put("status", !pf4jPluginService.isDisablePlugin(pluginId));
            it.put("version", pf4jPluginService.getCurrentVersion(pluginId));
            if (pf4jPluginService.hasUpdate(pluginId)) {
                it.put("lastVersion", "<font color=red>" + pf4jPluginService.getLastUpdateVersion(pluginId) + "↑</font>");
            } else {
                it.put("lastVersion", pf4jPluginService.getCurrentVersion(pluginId));
            }
        });
    }

    @Override
    public String exec(List<EruptPf4jPlugin> data, Void unused, String[] param) {
        if (param == null || param.length == 0) return null;
        EruptPf4jPlugin plugin = data.get(0);
        String tag = param[0];
        if ("enable".equals(tag)) {
            pf4jPluginService.enablePlugin(plugin);
            return "this.msg.success('启用成功')";
        } else if ("disable".equals(tag)) {
            pf4jPluginService.disablePlugin(plugin);
            return "this.msg.success('禁用成功')";
//        } else if ("install".equals(tag)) {
//            pf4jPluginService.installPlugin(plugin);
        } else if ("uninstall".equals(tag)) {
            pf4jPluginService.uninstallPlugin(plugin);
        } else if ("update".equals(tag)) {
            if (pf4jPluginService.hasUpdate(plugin.getPluginId())) {
                pf4jPluginService.updatePlugin(plugin);
            } else {
                return "this.msg.info('插件[" + plugin.pluginName + "]暂无更新')";
            }
        }
        return null;
    }
}
