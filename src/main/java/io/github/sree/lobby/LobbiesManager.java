package io.github.sree.lobby;

import io.github.sree.LobmanPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class LobbiesManager {
    private final LobmanPlugin plugin;
    private final List<String> lobbies = new ArrayList<>();

    public LobbiesManager(LobmanPlugin plugin) {
        this.plugin = plugin;
    }

    public List<String> getLobbies() {
        return lobbies;
    }

    public void addAndRegisterLobbyWorld(String worldName) {

        if (Bukkit.getWorld(worldName) == null) {
            plugin.getLogger().warning("World does not exist.");
            return;
        }

        lobbies.add(worldName);
        FileConfiguration config = plugin.getConfig();
        config.set("lobby-worlds", lobbies);

        plugin.getLogger().info("Successfully added " + worldName + " to lobby-worlds.");
    }

    public void removeAndUnregisterLobbyWorld(String worldName) {
        if (Bukkit.getWorld(worldName) == null) {
            plugin.getLogger().warning("World does not exist.");
            return;
        }

        FileConfiguration config = plugin.getConfig();

        if (lobbies.contains(worldName)) {
            lobbies.remove(worldName);
            config.set("lobby-worlds", lobbies);

            plugin.getLogger().info("Successfully removed " + worldName + " from lobby-worlds.");
            return;
        }

        plugin.getLogger().info(worldName + " already not part of lobby-worlds.");
    }

    public void loadLobbyWorldsFromConfig() {
        lobbies.clear();

        FileConfiguration config = plugin.getConfig();

        if (config.get("lobby-worlds") == null) {
            plugin.getLogger().info("No worlds are marked as lobby worlds.");
            return;
        }

        lobbies.addAll(config.getStringList("lobby-worlds"));
        plugin.getLogger().info("Successfully registered " + lobbies.size() + " lobby-worlds.");
    }
}
