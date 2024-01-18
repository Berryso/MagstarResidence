package top.magstar.residence.datamanager.statics;

import top.magstar.residence.Residence;

import java.util.UUID;

public abstract class AbstractDataManager {
    public abstract void saveData();
    public abstract void reload();
    public abstract void loadData();
    public abstract void deleteData(UUID uuid);
    public static AbstractDataManager getInstance() {
        if (Residence.dbAvailable) {
            return new SQLManager();
        } else {
            return new FileManager();
        }
    }
}
