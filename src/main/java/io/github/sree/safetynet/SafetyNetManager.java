package io.github.sree.safetynet;

import io.github.sree.LobmanPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class SafetyNetManager {

    private final LobmanPlugin plugin;

    public SafetyNetManager(LobmanPlugin plugin) {
        this.plugin = plugin;
    }

    public void createAndRegisterSafetyNet(String name, int yLevel, int x1, int x2, int z1, int z2, Location tpLocation) {
        SafetyNet safetyNet = new SafetyNet(yLevel, x1, x2, z1, z2, tpLocation);

        FileConfiguration config = plugin.getConfig();

        config.set("safety-nets." + name, safetyNet);
        plugin.saveConfig();

        plugin.getActiveSafetyNets().put(name, safetyNet);
    }

    public void deleteAndRemoveSafetyNet(String name) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("safety-nets");

        section.set(name, null);
        plugin.getActiveSafetyNets().remove(name);

        plugin.saveConfig();
        plugin.getLogger().info("Successfully removed safety nets.");
    }

    public void loadSafetyNetsFromConfig() {

        // Clear cache
        plugin.getActiveSafetyNets().clear();

        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("safety-nets");

        if(section == null) {
            plugin.getLogger().info("There are no safety nets saved.");
            return;
        }

        for(String key: section.getKeys(false)) {
            SafetyNet safetyNet = (SafetyNet) section.get(key);
            plugin.getActiveSafetyNets().put(key, safetyNet);
        }

        plugin.getLogger().info("Successfully loaded " + plugin.getActiveSafetyNets().size() + " safety net(s).");
    }

    public void scheduleSafetyNets() {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        scheduler.runTaskTimer(plugin, task -> {
            for(SafetyNet safetyNet : plugin.getActiveSafetyNets().values()) {
                safetyNet.teleportPlayers();
            }
        }, 0L, 1L);
    }
}
