package io.github.sree.safetynet;

import io.github.sree.LobmanPlugin;
import io.papermc.paper.command.brigadier.argument.position.ColumnBlockPosition;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SafetyNet {
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
    public static void createSafetyNet(String name, int yLevel, ColumnBlockPosition firstPosition, ColumnBlockPosition secondPosition, BlockPosition teleportPosition) {
        SafetyNet safetyNet = new SafetyNet(name, yLevel, firstPosition, secondPosition, teleportPosition);
        var plugin = LobmanPlugin.getInstance();
        plugin.getSafetyNets().add(safetyNet);
    }
}
