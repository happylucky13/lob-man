package io.github.sree.safetynet;

import io.papermc.paper.command.brigadier.argument.position.ColumnBlockPosition;

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


}
