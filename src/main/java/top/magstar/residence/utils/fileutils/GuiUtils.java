package top.magstar.residence.utils.fileutils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import top.magstar.residence.Residence;
import top.magstar.residence.datamanager.runtime.PermissionSettingManager;
import top.magstar.residence.objects.InventoryData;
import top.magstar.residence.objects.Permission;
import top.magstar.residence.objects.Resident;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public final class GuiUtils {
    private static File f = new File(Residence.getInstance().getDataFolder() + "/gui.yml");
    private final static UUID defUniqueId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final List<Permission> generalList = Arrays.asList(
            Permission.values()
    );
    private static final List<Permission> playerList = Arrays.asList(
            Permission.move, Permission.breek, Permission.place, Permission.use, Permission.container, Permission.bucket, Permission.bonemeal, Permission.tp,
            Permission.interact, Permission.pick, Permission.attack, Permission.pvp, Permission.fish, Permission.threw, Permission.info, Permission.shop
    );
    public static void reload() {
        if (!f.exists()) {
            Residence.getInstance().saveResource("gui.yml", false);
        }
        f = new File(Residence.getInstance().getDataFolder() + "/gui.yml");
    }
    public static FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(f);
    }
    public static List<Integer> getSideList() {
        List<Integer> sideList = new ArrayList<>();
        for (String s : Objects.requireNonNull(getConfig().getConfigurationSection("Buttons")).getKeys(false)) {
            if (!s.equals("<available_locations>")) {
                sideList.addAll(getConfig().getIntegerList("Buttons." + s + ".Location"));
            }
        }
        return sideList;
    }
    /**
     * @param res the res to be queried;
     * @param b true represents command "pset" while false represents command "set";
     * @param p if b is true, p is not null;
     * @param caller the gui caller;
     */
    @SuppressWarnings("deprecation")
    public static void createPermissionGui(@NotNull Resident res, boolean b, @Nullable OfflinePlayer p, @NotNull Player caller) {
        Inventory inv;
        if (b) {
            assert p != null;
            inv = Bukkit.createInventory(caller, 45, Objects.requireNonNull(getConfig().getString("Title")).split("\\|")[1].replace("{player}", p.getName()));
        } else {
            inv = Bukkit.createInventory(caller, 45, Objects.requireNonNull(getConfig().getString("Title")).split("\\|")[0]);
        }
        for (String s : Objects.requireNonNull(getConfig().getConfigurationSection("Buttons")).getKeys(false)) {
            if (!s.equals("<available_locations>")) {
                Material mat = Material.getMaterial(s.toUpperCase());
                if (mat == null) {
                    throw new IllegalArgumentException("Material not found! " + s.toUpperCase());
                }
                String display = Objects.requireNonNull(getConfig().getString("Buttons." + s + ".Display")).replace("&", "§");
                List<String> originLores = getConfig().getStringList("Buttons." + s + ".Lore");
                List<String> finalLores = new ArrayList<>();
                for (String lore : originLores) {
                    finalLores.add(lore.replace("&", "§"));
                }
                ItemStack item = new ItemStack(mat, 1);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(display);
                meta.setLore(finalLores);
                item.setItemMeta(meta);
                for (int i : getConfig().getIntegerList("Buttons." + s + ".Location")) {
                    inv.setItem(i, item);
                }
            } else {
                FileConfiguration cfg = PermissionsUtils.getConfig();
                int index = 0;
                for (int i : getConfig().getIntegerList("Buttons." + s + ".Location")) {
                    if (b) {
                        String display = Objects.requireNonNull(getConfig().getString("Buttons." + s + ".Display")).replace("{permission_name}", PermissionsUtils.getName(playerList.get(index))).replace("&", "§");
                        List<String> originLore = getConfig().getStringList("Buttons." + s + ".Lore");
                        List<String> finalLore = new ArrayList<>();
                        for (String lore : originLore) {
                            finalLore.add(lore
                                    .replace("{permission_name}", PermissionsUtils.getName(playerList.get(index)))
                                    .replace("{permission_description}", PermissionsUtils.getDescription(playerList.get(index)))
                                    .replace("&", "§")
                            );
                        }
                        Material mat = PermissionsUtils.getMaterial(playerList.get(index));
                        if (mat == null) {
                            throw new IllegalArgumentException("Material not found! " + Objects.requireNonNull(cfg.getString(playerList.get(index) + ".material")));
                        }
                        ItemStack item = new ItemStack(mat, 1);
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(display);
                        meta.setLore(finalLore);
                        if (res.getPermission(p.getUniqueId(), playerList.get(index))) {
                            meta.addEnchant(Enchantment.DURABILITY, 1, false);
                        }
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DYE);
                        item.setItemMeta(meta);
                        inv.setItem(i, item);
                        index++;
                        if (index == playerList.size()) {
                            break;
                        }
                    } else {
                        String display = Objects.requireNonNull(getConfig().getString("Buttons." + s + ".Display")).replace("{permission_name}", PermissionsUtils.getName(generalList.get(index))).replace("&", "§");
                        List<String> originLore = getConfig().getStringList("Buttons." + s + ".Lore");
                        List<String> finalLore = new ArrayList<>();
                        for (String lore : originLore) {
                            finalLore.add(lore
                                    .replace("{permission_name}", PermissionsUtils.getName(generalList.get(index)))
                                    .replace("{permission_description}", PermissionsUtils.getDescription(generalList.get(index)))
                                    .replace("&", "§")
                            );
                        }
                        Material mat = PermissionsUtils.getMaterial(generalList.get(index));
                        if (mat == null) {
                            throw new IllegalArgumentException("Material not found! " + Objects.requireNonNull(cfg.getString(generalList.get(index) + ".material")));
                        }
                        ItemStack item = new ItemStack(mat, 1);
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(display);
                        meta.setLore(finalLore);
                        if (res.getPermission(defUniqueId, generalList.get(index))) {
                            meta.addEnchant(Enchantment.DURABILITY, 1, false);
                        }
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DYE);
                        item.setItemMeta(meta);
                        inv.setItem(i, item);
                        index++;
                        if (index == generalList.size()) {
                            break;
                        }
                    }
                }
            }
        }
        caller.openInventory(inv);
        PermissionSettingManager.addPlayer(new InventoryData(inv, b, caller, p == null ? null : p.getUniqueId(), res));
    }
}
