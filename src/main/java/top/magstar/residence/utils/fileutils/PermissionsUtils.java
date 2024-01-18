package top.magstar.residence.utils.fileutils;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.magstar.residence.Residence;
import top.magstar.residence.objects.Permission;

import java.io.File;
import java.util.Objects;

public final class PermissionsUtils {
    private static File f = new File(Residence.getInstance().getDataFolder() + "/permissions.yml");
    public static void reload() {
        if (!f.exists()) {
            Residence.getInstance().saveResource("permissions.yml", false);
        }
        f = new File(Residence.getInstance().getDataFolder() + "/permissions.yml");
    }
    public static FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(f);
    }
    public static String getName(Permission perm) {
        return getConfig().getString(perm.toString() + ".name");
    }
    public static String getDescription(Permission perm) {
        return getConfig().getString(perm.toString() + ".description");
    }
    public static Material getMaterial(Permission perm) {
        return Material.getMaterial(Objects.requireNonNull(Objects.requireNonNull(getConfig().getString(perm.toString() + ".material")).toUpperCase()));
    }
    public static Permission getPermissionByMaterial(Material material) {
        for (String s : getConfig().getKeys(false)) {
            if (Objects.requireNonNull(getConfig().getString(s + ".material")).toUpperCase().equals(material.toString())) {
                return Permission.getPermission(s);
            }
        }
        return null;
    }
}
