package io.github.sree;

import org.bukkit.Location;

public class DisplayFormatter {

    private DisplayFormatter() {

    }

    public static String format2DLocation(Location location) {
        return (location.getBlockX() + ", " + location.getBlockZ());
    }

    public static String formatLocation(Location location) {
        return ((int) location.getX() + ", " + (int) location.getY() + ", " + (int) location.getZ());
    }
}
