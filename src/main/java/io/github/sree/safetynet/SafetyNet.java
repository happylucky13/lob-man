package io.github.sree.safetynet;

import io.papermc.paper.command.brigadier.argument.position.ColumnBlockPosition;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class SafetyNet {
    private String name;
    private int yLevel;
    private ColumnBlockPosition firstPosition;
    private ColumnBlockPosition secondPosition;

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

    // Detect if player is within bounds
    @Nullable
    public Player detectPlayer(World lobbyWorld) {
        List<Player> lobbyPlayers = lobbyWorld.getPlayers();

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

            return player;
        }

        return null;
    }


}
