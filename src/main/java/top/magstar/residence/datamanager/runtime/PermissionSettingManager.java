package top.magstar.residence.datamanager.runtime;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import top.magstar.residence.objects.InventoryData;
import xyz.magstar.lib.objects.MagstarRuntimeManager;

import java.util.ArrayList;
import java.util.List;

@MagstarRuntimeManager("MagstarResidence")
public final class PermissionSettingManager {
    private static final List<InventoryData> invList = new ArrayList<>();
    public synchronized static void addPlayer(InventoryData data) {
        invList.add(data);
    }
    public synchronized static Inventory getInventory(Player p) {
        for (InventoryData data : invList) {
            if (data.getCaller().getUniqueId().toString().equals(p.getUniqueId().toString())) {
                return data.getInventory();
            }
        }
        return null;
    }
    public synchronized static void removePlayer(Player p) {
        invList.removeIf(data -> data.getCaller().getUniqueId().toString().equals(p.getUniqueId().toString()));
    }
    public synchronized static void removeAll() {
        invList.clear();
    }
    public synchronized static boolean containsPlayer(Player p) {
        for (InventoryData data : invList) {
            if (data.getCaller().getUniqueId().toString().equals(p.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }
    public synchronized static InventoryData getInventoryData(Player p) {
        for (InventoryData data : invList) {
            if (data.getCaller().getUniqueId().toString().equals(p.getUniqueId().toString())) {
                return data;
            }
        }
        return null;
    }
}