package top.magstar.residence.datamanager.statics;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.magstar.residence.Residence;
import top.magstar.residence.datamanager.runtime.RuntimeDataManager;
import top.magstar.residence.objects.Resident;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unchecked")
public final class FileManager extends AbstractDataManager{
    private static File f = new File(Residence.getInstance().getDataFolder() + "/data.yml");
    @Override
    public void saveData() {
        List<Resident> residents = RuntimeDataManager.getResidents();
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        for (Resident res : residents) {
            config.set(res.getUniqueId().toString(), res.serialize());
        }
        try {
            config.save(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reload() {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        f = new File(Residence.getInstance().getDataFolder() + "/data.yml");
    }

    @Override
    public void loadData() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        RuntimeDataManager.removeAll();
        for (String s : config.getKeys(false)) {
            RuntimeDataManager.loadData(Resident.deserialize(Objects.requireNonNull(config.getConfigurationSection(s).getValues(true))));
        }
    }

    @Override
    public void deleteData(UUID uuid) {
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        for (String s : cfg.getKeys(false)) {
            Resident res = Resident.deserialize((Map<String, Object>) Objects.requireNonNull(cfg.get(s)));
            if (res.getUniqueId().toString().equals(uuid.toString())) {
                cfg.set(s, null);
                return;
            }
        }
    }
}
