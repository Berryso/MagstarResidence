package top.magstar.residence.utils.fileutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.magstar.residence.Residence;

import java.io.File;

public final class ConfigUtils {
    private static File f = new File(Residence.getInstance().getDataFolder() + "/config.yml");
    public static void reload() {
        if (!f.exists()) {
            Residence.getInstance().saveDefaultConfig();
        }
        f = new File(Residence.getInstance().getDataFolder() + "/config.yml");
    }
    public static FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(f);
    }
}
