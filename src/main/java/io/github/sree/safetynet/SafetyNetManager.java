package io.github.sree.safetynet;

import io.github.sree.LobmanPlugin;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SafetyNetManager {

    private final LobmanPlugin plugin;
    private final Map<String, SafetyNet> activeSafetyNets = new HashMap<>();

    public SafetyNetManager(LobmanPlugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, SafetyNet> getActiveSafetyNets() {
        return activeSafetyNets;
    }

    public void createAndRegisterSafetyNet(String name, int yLevel, int x1, int x2, int z1, int z2, Location tpLocation) {
        SafetyNet safetyNet = new SafetyNet(yLevel, x1, x2, z1, z2, tpLocation);

        FileConfiguration config = plugin.getConfig();

        config.set("safety-nets." + name, safetyNet);
        plugin.saveConfig();

        activeSafetyNets.put(name, safetyNet);
    }

    public void deleteAndRemoveSafetyNet(String name) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("safety-nets");

        section.set(name, null);
        activeSafetyNets.remove(name);

        plugin.saveConfig();
        plugin.getLogger().info("Successfully removed safety nets.");
    }

    public void loadSafetyNetsFromConfig() {

        // Clear cache
        activeSafetyNets.clear();

        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("safety-nets");

        if(section == null) {
            plugin.getLogger().info("There are no safety nets saved.");
            return;
        }

        for(String key: section.getKeys(false)) {
            SafetyNet safetyNet = (SafetyNet) section.get(key);
            activeSafetyNets.put(key, safetyNet);
        }

        plugin.getLogger().info("Successfully loaded " + activeSafetyNets.size() + " safety net(s).");
    }

    public void scheduleSafetyNets() {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        scheduler.runTaskTimer(plugin, task -> {
            for(SafetyNet safetyNet : activeSafetyNets.values()) {
                safetyNet.teleportPlayers();
            }
        }, 0L, 1L);
    }
}
