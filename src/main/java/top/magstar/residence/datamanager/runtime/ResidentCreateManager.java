package top.magstar.residence.datamanager.runtime;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import top.magstar.residence.Residence;
import top.magstar.residence.objects.Loc;
import top.magstar.residence.objects.LocData;
import top.magstar.residence.utils.fileutils.ConfigUtils;
import top.magstar.residence.utils.fileutils.Message;
import xyz.magstar.lib.objects.MagstarRuntimeManager;

import java.util.HashMap;
import java.util.Map;

@MagstarRuntimeManager("MagstarResidence")
public final class ResidentCreateManager {
    //Player handler
    private final static Map<Player, Boolean> RESIDENT_CREATING = new HashMap<>();
    public synchronized static void addPlayer(Player p, boolean b) {
        if (!containsPlayer(p)) {
            RESIDENT_CREATING.put(p, b);
            p.sendMessage(Message.creating.getTranslate().replace("{time}", String.valueOf(ConfigUtils.getConfig().getInt("wait_creation"))));
            new BukkitRunnable() {
                @Override
                public void run() {
                    RESIDENT_CREATING.remove(p);
                }
            }.runTaskLater(Residence.getInstance(), ConfigUtils.getConfig().getInt("wait_creation") * 20L);
        }
    }
    public synchronized static void removePlayer(Player p) {
        RESIDENT_CREATING.remove(p);
    }
    public synchronized static boolean containsPlayer(Player p) {
        return RESIDENT_CREATING.containsKey(p);
    }
    public synchronized static void removeAll() {
        RESIDENT_CREATING.clear();
    }
    public synchronized static boolean isFirstBreak(Player p) {
        return RESIDENT_CREATING.get(p);
    }

    //Loc handler
    private final static Map<Player, LocData> LOC_MAP = new HashMap<>();
    public synchronized static void addPlayer(Player p, Loc first, Loc second) {
        if (first == null) {
            LOC_MAP.put(p, new LocData(null, new Location(p.getWorld(), second.getX(), second.getY(), second.getZ())));
        }
        else if (second == null) {
            LOC_MAP.put(p, new LocData(new Location(p.getWorld(), first.getX(), first.getY(), first.getZ()), null));
        }
    }
    public synchronized static void removeLocation(Player p) {
        LOC_MAP.remove(p);
    }
    public synchronized static Location getFirst(Player p) {
        if (LOC_MAP.containsKey(p)) {
            return LOC_MAP.get(p).getFirst();
        }
        return null;
    }
    public synchronized static Location getSecond(Player p) {
        if (LOC_MAP.containsKey(p)) {
            return LOC_MAP.get(p).getSecond();
        }
        return null;
    }
    public synchronized static void setSecond(Player p, Location second) {
        if (LOC_MAP.containsKey(p)) {
            LOC_MAP.get(p).setSecond(second);
        }
    }
    public synchronized static void setFirst(Player p, Location first) {
        if (LOC_MAP.containsKey(p)) {
            LOC_MAP.get(p).setFirst(first);
        }
    }
    public synchronized static void removeLocations() {
        LOC_MAP.clear();
    }
}
