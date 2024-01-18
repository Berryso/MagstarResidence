package top.magstar.residence.datamanager.runtime;

import top.magstar.residence.datamanager.statics.AbstractDataManager;
import top.magstar.residence.objects.Resident;
import xyz.magstar.lib.objects.MagstarRuntimeManager;

import java.util.*;

@MagstarRuntimeManager("MagstarResidence")
public final class RuntimeDataManager {
    private static final List<Resident> RES = new ArrayList<>();
    public synchronized static void loadData(Resident res) {
        RES.add(res);
    }
    public synchronized static void removeAll() {
        RES.clear();
    }
    public synchronized static List<Resident> getResidents() {
        return RES;
    }
    public synchronized static Map<Integer, Resident> getPlayerResidents(UUID uuid) {
        Map<Integer, Resident> res = new HashMap<>();
        int i = 1;
        for (Resident r : RES) {
            if (r.isOwner(uuid)) {
                res.put(i, r);
                i++;
            }
        }
        return res;
    }
    public synchronized static void removeResident(Resident res) {
        RES.removeIf(data -> data.getUniqueId().toString().equals(res.getUniqueId().toString()));
        AbstractDataManager.getInstance().deleteData(res.getUniqueId());
    }
    public synchronized static Resident getResident(UUID uuid) {
        for (Resident res : RES) {
            if (res.getUniqueId().toString().equals(uuid.toString())) {
                return res;
            }
        }
        return null;
    }
}
