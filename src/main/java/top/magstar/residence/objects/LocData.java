package top.magstar.residence.objects;

import org.bukkit.Location;

public final class LocData {
    private Location loc1;
    private Location loc2;

    public LocData(Location loc1, Location loc2) {
        this.loc1 = loc1;
        this.loc2 = loc2;
    }

    public Location getFirst() {
        return loc1;
    }

    public void setFirst(Location loc1) {
        this.loc1 = loc1;
    }

    public Location getSecond() {
        return loc2;
    }

    public void setSecond(Location loc2) {
        this.loc2 = loc2;
    }
}
