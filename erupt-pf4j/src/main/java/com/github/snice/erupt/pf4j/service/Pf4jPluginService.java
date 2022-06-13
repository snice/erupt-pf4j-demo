package com.github.snice.erupt.pf4j.service;

import com.github.snice.erupt.pf4j.model.EruptPf4jPlugin;
import com.github.snice.erupt.pf4j.model.EruptPf4jPluginVersion;
import com.github.snice.spring.pf4j.SpringPluginManager;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginState;
import org.pf4j.update.UpdateManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import xyz.erupt.jpa.dao.EruptDao;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Pf4jPluginService {

    @Lazy
    @Resource
    UpdateManager updateManager;

    @Lazy
    @Resource
    SpringPluginManager pluginManager;

    @Lazy
    @Resource
    EruptDao eruptDao;

    public PluginDescriptor getPluginDescriptor(Path pluginPath) {
        Method method = ReflectionUtils.findMethod(SpringPluginManager.class, "getPluginDescriptorFinder");
        method.setAccessible(true);
        PluginDescriptorFinder pluginDescriptorFinder;
        try {
            pluginDescriptorFinder = (PluginDescriptorFinder) method.invoke(pluginManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return pluginDescriptorFinder.find(pluginPath);
    }

    public void refresh() {
        updateManager.refresh();
    }

    public boolean hasPlugin(String pluginId) {
        return pluginManager.getPlugin(pluginId) != null;
    }

    public String getCurrentVersion(String pluginId) {
        if (pluginManager.getPlugin(pluginId) == null) return "0.0.0";
        return pluginManager.getPlugin(pluginId).getDescriptor().getVersion();
    }

    public String getLastUpdateVersion(String pluginId) {
        if (updateManager.hasUpdates()) return updateManager.getLastPluginRelease(pluginId).version;
        if (pluginManager.getPlugin(pluginId) == null) {
            EruptPf4jPlugin plugin = getPlugin(pluginId);
            if (plugin != null) {
                if (countPlugin(plugin.getId()) == 0) return "0.0.0";
                List<EruptPf4jPluginVersion> pluginVersions = pluginVersions(plugin.getId());
                if (pluginVersions == null || pluginVersions.isEmpty()) {
                    return "0.0.0";
                } else {
                    if (pluginVersions.size() > 1) {
                        pluginVersions.sort((c1, c2) -> pluginManager.getVersionManager().compareVersions(c1.getVersion(), c2.getVersion()));
                        Collections.reverse(pluginVersions);
                    }
                    return pluginVersions.get(0).getVersion();
                }
            }
        }
        return updateManager.getLastPluginRelease(pluginId).version;
    }

    public boolean hasUpdate(String pluginId) {
        if (pluginManager.getPlugin(pluginId) == null) {
            EruptPf4jPlugin plugin = getPlugin(pluginId);
            if (plugin != null) {
                return countPlugin(plugin.getId()) > 0;
            }
            return false;
        }
        return updateManager.hasPluginUpdate(pluginId);
    }

    public boolean isDisablePlugin(String pluginId) {
        if (pluginManager.getPlugin(pluginId) == null) return true;
        return pluginManager.getPlugin(pluginId).getPluginState() == PluginState.DISABLED;
    }

    @Transactional
    public void uninstallPlugin(EruptPf4jPlugin plugin) {
        if (updateManager.uninstallPlugin(plugin.getPluginId())) {
            eruptDao.getJdbcTemplate().update("delete from e_pf4j_plugin_version where plugin_id=?", plugin.getId());
            eruptDao.delete(plugin);
        }
    }


    @Transactional
    public void disablePlugin(EruptPf4jPlugin plugin) {
        String pluginId = plugin.getPluginId();
        pluginManager.disablePlugin(pluginId);
        plugin.setStatus(false);
        eruptDao.merge(plugin);
    }

    @Transactional
    public void enablePlugin(EruptPf4jPlugin plugin) {
        String pluginId = plugin.getPluginId();
        if (!isDisablePlugin(pluginId)) {
            pluginManager.stopPlugin(pluginId);
        }
        pluginManager.enablePlugin(pluginId);
        pluginManager.startPlugin(pluginId);
        plugin.setStatus(true);
        eruptDao.merge(plugin);
    }

    public void updatePlugin(EruptPf4jPlugin plugin) {
        String pluginId = plugin.getPluginId();
        if (hasPlugin(pluginId)) updateManager.uninstallPlugin(pluginId);
        updateManager.installPlugin(pluginId, getLastUpdateVersion(pluginId));
    }

    private EruptPf4jPlugin getPlugin(String pluginId) {
        EruptPf4jPlugin plugin = eruptDao.queryEntity(EruptPf4jPlugin.class, " pluginId = :pId", new HashMap<>() {{
            put("pId", pluginId);
        }});
        return plugin;
    }

    private Long countPlugin(Long id) {
        String querySql = "select count(*) as _count from e_pf4j_plugin_version where plugin_id = ?";
        Map<String, Object> queryMap = eruptDao.getJdbcTemplate().queryForMap(querySql, id);
        return Long.parseLong(queryMap.get("_count").toString());
    }

    private List<EruptPf4jPluginVersion> pluginVersions(Long id) {
        return eruptDao.queryEntityList(EruptPf4jPluginVersion.class, " plugin_id = :pId", new HashMap<>() {{
            put("pId", id);
        }});
    }

}
