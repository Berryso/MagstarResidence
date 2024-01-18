package top.magstar.residence.objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public final class InventoryData {
    private final Inventory inv;
    private final boolean b;
    private final Player caller;
    private final UUID setter;
    private final Resident res;
    public InventoryData(Inventory inv, boolean b, Player caller, UUID setter, Resident res) {
        this.inv = inv;
        this.b = b;
        this.caller = caller;
        this.setter = setter;
        this.res = res;
    }

    public Inventory getInventory() {
        return inv;
    }

    public boolean isPlayerMode() {
        return b;
    }

    public Player getCaller() {
        return caller;
    }

    public UUID getSetter() {
        return setter;
    }

    public Resident getResident() {
        return res;
    }
}
