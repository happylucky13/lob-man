package io.github.sree.safetynet;

import io.github.sree.LobmanPlugin;
import io.papermc.paper.command.brigadier.argument.position.ColumnBlockPosition;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
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
    private final ColumnBlockPosition firstPosition;
    private final ColumnBlockPosition secondPosition;
    private final BlockPosition teleportPosition;

    private SafetyNet(String name, int yLevel, ColumnBlockPosition firstPosition, ColumnBlockPosition secondPosition, BlockPosition teleportPosition) {
        this.name = name;
        this.yLevel = yLevel;
        this.firstPosition = firstPosition;
        this.secondPosition = secondPosition;
        this.teleportPosition = teleportPosition;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getYLevel() {
        return yLevel;
    }

    public ColumnBlockPosition getFirstPosition() {
        return firstPosition;
    }

    public ColumnBlockPosition getSecondPosition() {
        return secondPosition;
    }

    public BlockPosition getTeleportPosition() {
        return teleportPosition;
    }

    // Detect if player is within bounds
    public List<Player> detectPlayer(World lobbyWorld) {
        List<Player> lobbyPlayers = lobbyWorld.getPlayers();
        List<Player> detectedPlayers = new ArrayList<>();

        int xOne = firstPosition.blockX();
        int xTwo = secondPosition.blockX();
        int zOne = firstPosition.blockZ();
        int zTwo = secondPosition.blockZ();

        for(Player player : lobbyPlayers) {

            // Check player position
            if(!(player.getY() <= yLevel)) {
                continue;
            }

            if(!(player.getX() >= Math.min(xOne, xTwo) && player.getX() <= Math.max(xOne, xTwo))) {
                continue;
            }

            if(!(player.getZ() >= Math.min(zOne, zTwo) && player.getZ() <= Math.max(zOne, zTwo))) {
                continue;
            }

            detectedPlayers.add(player);
        }

        return detectedPlayers;
    }

    // Teleport all players within bounds
    public void teleportPlayers(List<Player> detectedPlayers, World lobbyWorld) {
        Location centeredLocation = teleportPosition.toCenter().toLocation(lobbyWorld);

        for(Player player: detectedPlayers) {
            player.teleportAsync(centeredLocation);
            player.setFallDistance(0.0f);
        }
    }

    // Create safety net
    public static void createSafetyNet(String name, int yLevel, ColumnBlockPosition firstPosition, ColumnBlockPosition secondPosition, BlockPosition teleportPosition, LobmanPlugin plugin) {
        SafetyNet safetyNet = new SafetyNet(name, yLevel, firstPosition, secondPosition, teleportPosition);
        plugin.getSafetyNets().add(safetyNet);
    }

    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("name", this.name);
        data.put("y-level", this.yLevel);

        data.put("first-position-x", this.firstPosition.blockX());
        data.put("first-position-z", this.firstPosition.blockZ());

        data.put("second-position-x", this.secondPosition.blockX());
        data.put("second-position-z", this.secondPosition.blockZ());

        data.put("teleport-position-x", this.teleportPosition.blockX());
        data.put("teleport-position-y", this.teleportPosition.blockY());
        data.put("teleport-position-z", this.teleportPosition.blockZ());

        return data;
    }

    public static SafetyNet deserialize(Map<String, Object> args) {

        // Messy ahh code, fix later
        ColumnBlockPosition firstPosition = new ColumnBlockPosition() {
            @Override
            public int blockX() {
                return (int) args.get("first-position-x");
            }

            @Override
            public int blockZ() {
                return (int) args.get("first-position-z");
            }
        };

        ColumnBlockPosition secondPosition = new ColumnBlockPosition() {
            @Override
            public int blockX() {
                return (int) args.get("second-position-x");
            }

            @Override
            public int blockZ() {
                return (int) args.get("second-position-z");
            }
        };

        BlockPosition teleportPosition = new BlockPosition() {
            @Override
            public int blockX() {
                return (int) args.get("teleport-position-x");
            }

            @Override
            public int blockY() {
                return (int) args.get("teleport-position-y");
            }

            @Override
            public int blockZ() {
                return (int) args.get("teleport-position-z");
            }
        };


        return new SafetyNet(
                (String) args.get("name"),
                (int) args.get("y-level"),
                firstPosition,
                secondPosition,
                teleportPosition
        );

    }
}
