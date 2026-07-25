package io.github.sree.selection;

import org.bukkit.Location;

public class RegionSelection {

    private Location firstCorner;
    private Location secondCorner;

    public void setFirstCorner(Location location) {
        this.firstCorner = location;
    }

    public void setSecondCorner(Location location) {
        this.secondCorner = location;
    }

    public boolean isComplete() {
        return firstCorner != null && secondCorner != null;
    }
}
