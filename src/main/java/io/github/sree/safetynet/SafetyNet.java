package io.github.sree.safetynet;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SafetyNet implements ConfigurationSerializable {
    private final String name;
    private final int yLevel;

    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;

    private final Location tpLocation;

    public SafetyNet(String name, int yLevel, int x1, int x2, int z1, int z2, Location tpLocation) {
        this.name = name;
        this.yLevel = yLevel;

        this.minX = Math.min(x1, x2);
        this.maxX = Math.max(x1, x2);
        this.minZ = Math.min(z1, z2);
        this.maxZ = Math.max(z1, z2);

        this.tpLocation = tpLocation;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getYLevel() {
        return yLevel;
    }

    public Location getTpLocation() {
        return tpLocation;
    }

    // Detect if player is within bounds
    public List<Player> detectPlayer() {
        List<Player> lobbyPlayers = tpLocation.getWorld().getPlayers();
        List<Player> detectedPlayers = new ArrayList<>();

        for(Player player : lobbyPlayers) {

            // Check player position
            if (player.getY() <= yLevel
                    && player.getX() >= minX
                    && player.getX() <= maxX
                    && player.getZ() >= minZ
                    && player.getZ() <= maxZ
            ) {
                detectedPlayers.add(player);
            }
        }

        return detectedPlayers;
    }

    // Teleport all players within bounds
    public void teleportPlayers() {
        for(Player player: this.detectPlayer()) {
            player.teleportAsync(tpLocation);
            player.setFallDistance(0.0f);
        }
    }

    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("name", this.name);
        data.put("y-level", this.yLevel);

        data.put("min-x", this.minX);
        data.put("max-x", this.maxX);
        data.put("min-z", this.minZ);
        data.put("max-z", this.maxZ);

        data.put("tp-location", this.tpLocation);
        data.put("world", this.tpLocation.getWorld());

        return data;
    }

    public static SafetyNet deserialize(Map<String, Object> args) {

        return new SafetyNet(
                (String) args.get("name"),
                (int) args.get("y-level"),
                (int) args.get("min-x"),
                (int) args.get("max-x"),
                (int) args.get("min-z"),
                (int) args.get("max-z"),
                (Location) args.get("tp-location")
        );

    }
}
