package io.github.sree.safetynet;

import io.github.sree.LobmanPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SafetyNetManager {

    private final LobmanPlugin plugin;

    public SafetyNetManager(LobmanPlugin plugin) {
        this.plugin = plugin;
    }

    public void createAndRegisterSafetyNet(String name, int yLevel, int x1, int x2, int z1, int z2, Location tpLocation) {
        SafetyNet safetyNet = new SafetyNet(name, yLevel, x1, x2, z1, z2, tpLocation);

        FileConfiguration config = plugin.getConfig();

        config.set("safety-nets." + safetyNet.getName(), safetyNet);
        plugin.saveConfig();

        plugin.getSafetyNets().add(safetyNet);
    }

    public void loadSafetyNetsFromConfig() {

        // Clear cache
        plugin.getSafetyNets().clear();

        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("safety-nets");

        if(section == null) {
            return;
        }

        for(String key: section.getKeys(false)) {
            try {
                SafetyNet safetyNet = (SafetyNet) section.get(key);

                if(safetyNet != null) {
                    plugin.getSafetyNets().add(safetyNet);
                }
            }
            catch (Exception e){
                plugin.getLogger().warning("Failed to load safety net: " + key);
            }
        }

        plugin.getLogger().info("Successfully loaded " + plugin.getSafetyNets().size() + " safety net(s).");
    }
}
