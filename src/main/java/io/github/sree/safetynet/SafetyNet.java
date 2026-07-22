package io.github.sree.safetynet;

import io.papermc.paper.command.brigadier.argument.position.ColumnBlockPosition;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SafetyNet {
    private String name;
    private int yLevel;
    private ColumnBlockPosition firstPosition;
    private ColumnBlockPosition secondPosition;
    private BlockPosition teleportPosition;

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setYLevel(int yLevel) {
        this.yLevel = yLevel;
    }

    public void setFirstPosition(ColumnBlockPosition firstPosition) {
        this.firstPosition = firstPosition;
    }

    public void setSecondPosition(ColumnBlockPosition secondPosition) {
        this.secondPosition = secondPosition;
    }

    public void setTeleportPosition(BlockPosition teleportPosition) {
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


}
