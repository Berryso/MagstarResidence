package top.magstar.residence.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import top.magstar.residence.Residence;
import top.magstar.residence.datamanager.runtime.PermissionSettingManager;
import top.magstar.residence.datamanager.runtime.ResidentCreateManager;
import top.magstar.residence.datamanager.runtime.RuntimeDataManager;
import top.magstar.residence.datamanager.statics.AbstractDataManager;
import top.magstar.residence.handlers.CommandHandlers;
import top.magstar.residence.handlers.PermissionHandlers;
import top.magstar.residence.objects.Loc;
import top.magstar.residence.objects.Resident;
import top.magstar.residence.utils.fileutils.ConfigUtils;
import top.magstar.residence.utils.fileutils.GuiUtils;
import top.magstar.residence.utils.fileutils.Message;
import top.magstar.residence.utils.fileutils.PermissionsUtils;
import xyz.magstar.lib.api.IDataManager;

import java.util.*;

public final class GeneralUtils {
    private static final List<Material> notFootBlock = Arrays.asList(
        Material.OAK_FENCE, Material.OAK_FENCE_GATE, Material.OAK_DOOR, Material.OAK_TRAPDOOR, Material.OAK_PRESSURE_PLATE, Material.OAK_BUTTON, Material.OAK_SAPLING, Material.OAK_SIGN,
        Material.SPRUCE_FENCE, Material.SPRUCE_FENCE_GATE, Material.SPRUCE_DOOR, Material.SPRUCE_TRAPDOOR, Material.SPRUCE_PRESSURE_PLATE, Material.SPRUCE_BUTTON, Material.SPRUCE_SAPLING, Material.SPRUCE_SIGN,
        Material.BIRCH_FENCE, Material.BIRCH_FENCE_GATE, Material.BIRCH_DOOR, Material.BIRCH_TRAPDOOR, Material.BIRCH_PRESSURE_PLATE, Material.BIRCH_BUTTON, Material.BIRCH_SAPLING, Material.BIRCH_SIGN,
        Material.JUNGLE_FENCE, Material.JUNGLE_FENCE_GATE, Material.JUNGLE_DOOR, Material.JUNGLE_TRAPDOOR, Material.JUNGLE_PRESSURE_PLATE, Material.JUNGLE_BUTTON, Material.JUNGLE_SAPLING, Material.JUNGLE_SIGN,
        Material.ACACIA_FENCE, Material.ACACIA_FENCE_GATE, Material.ACACIA_DOOR, Material.ACACIA_TRAPDOOR, Material.ACACIA_PRESSURE_PLATE, Material.ACACIA_BUTTON, Material.ACACIA_SAPLING, Material.ACACIA_SIGN,
        Material.DARK_OAK_FENCE, Material.DARK_OAK_FENCE_GATE, Material.DARK_OAK_DOOR, Material.DARK_OAK_TRAPDOOR, Material.DARK_OAK_PRESSURE_PLATE, Material.DARK_OAK_BUTTON, Material.DARK_OAK_SAPLING, Material.DARK_OAK_SIGN,
        Material.MANGROVE_FENCE, Material.MANGROVE_FENCE_GATE, Material.MANGROVE_DOOR, Material.MANGROVE_TRAPDOOR, Material.MANGROVE_PRESSURE_PLATE, Material.MANGROVE_BUTTON, Material.MANGROVE_PROPAGULE, Material.MANGROVE_SIGN,
        Material.CRIMSON_FENCE, Material.CRIMSON_FENCE_GATE, Material.CRIMSON_DOOR, Material.CRIMSON_TRAPDOOR, Material.CRIMSON_PRESSURE_PLATE, Material.CRIMSON_BUTTON, Material.CRIMSON_FUNGUS, Material.CRIMSON_ROOTS, Material.WEEPING_VINES, Material.CRIMSON_SIGN,
        Material.WARPED_FENCE, Material.WARPED_FENCE_GATE, Material.WARPED_DOOR, Material.WARPED_TRAPDOOR, Material.WARPED_PRESSURE_PLATE, Material.WARPED_BUTTON, Material.WARPED_FUNGUS, Material.WARPED_ROOTS, Material.NETHER_SPROUTS, Material.TWISTING_VINES, Material.WARPED_SIGN,
        Material.STONE_PRESSURE_PLATE, Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL, Material.STONE_BRICK_WALL, Material.MOSSY_STONE_BRICK_WALL, Material.GRANITE_WALL,
        Material.DIORITE_WALL, Material.ANDESITE_WALL, Material.COBBLED_DEEPSLATE_WALL, Material.POLISHED_DEEPSLATE_WALL, Material.DEEPSLATE_BRICK_WALL, Material.BRICK_WALL,
        Material.MUD_BRICK_WALL, Material.SANDSTONE_WALL, Material.RED_SANDSTONE_WALL, Material.PRISMARINE_WALL, Material.NETHER_BRICK_WALL, Material.NETHER_BRICK_FENCE,
        Material.RED_NETHER_BRICK_WALL, Material.BLACKSTONE_WALL, Material.POLISHED_BLACKSTONE_WALL, Material.POLISHED_BLACKSTONE_BUTTON, Material.POLISHED_BLACKSTONE_WALL,
        Material.IRON_TRAPDOOR, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
        Material.WHITE_BANNER, Material.LIGHT_GRAY_BANNER, Material.GRAY_BANNER, Material.BLACK_BANNER, Material.BROWN_BANNER, Material.RED_BANNER, Material.ORANGE_BANNER,
        Material.YELLOW_BANNER, Material.LIME_BANNER, Material.GREEN_BANNER, Material.CYAN_BANNER, Material.LIGHT_BLUE_BANNER, Material.BLUE_BANNER, Material.PURPLE_BANNER,
        Material.MAGENTA_BANNER, Material.PINK_BANNER, Material.SNOW, Material.MOSS_CARPET, Material.POINTED_DRIPSTONE, Material.SMALL_AMETHYST_BUD, Material.MEDIUM_AMETHYST_BUD,
        Material.LARGE_AMETHYST_BUD, Material.AMETHYST_CLUSTER,
        Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.GRASS, Material.FERN, Material.DEAD_BUSH, Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET,
        Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY, Material.SPORE_BLOSSOM, Material.BAMBOO,
        Material.SUGAR_CANE, Material.CACTUS, Material.WITHER_ROSE, Material.VINE, Material.TALL_GRASS, Material.LARGE_FERN, Material.SUNFLOWER, Material.LILAC, Material.ROSE_BUSH, Material.PEONY, Material.BIG_DRIPLEAF,
        Material.SMALL_DRIPLEAF, Material.GLOW_LICHEN, Material.HANGING_ROOTS, Material.WHEAT, Material.COCOA, Material.PUMPKIN_STEM, Material.MELON_STEM, Material.BEETROOT, Material.GLOW_BERRIES, Material.SWEET_BERRIES,
        Material.NETHER_WART, Material.SEAGRASS, Material.TALL_SEAGRASS, Material.SEA_PICKLE, Material.KELP_PLANT, Material.TUBE_CORAL, Material.BRAIN_CORAL, Material.BUBBLE_CORAL, Material.FIRE_CORAL, Material.HORN_CORAL,
        Material.DEAD_BRAIN_CORAL, Material.DEAD_BUBBLE_CORAL, Material.DEAD_FIRE_CORAL, Material.DEAD_HORN_CORAL, Material.DEAD_TUBE_CORAL, Material.TUBE_CORAL_FAN, Material.BRAIN_CORAL_FAN, Material.BUBBLE_CORAL_FAN,
        Material.FIRE_CORAL_FAN, Material.HORN_CORAL_FAN, Material.DEAD_BRAIN_CORAL_FAN, Material.DEAD_BUBBLE_CORAL_FAN, Material.DEAD_FIRE_CORAL_FAN, Material.DEAD_HORN_CORAL_FAN, Material.DEAD_TUBE_CORAL_FAN,
        Material.COBWEB, Material.TORCH, Material.SOUL_TORCH, Material.REDSTONE_TORCH, Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.LADDER, Material.SCAFFOLDING,
        Material.REDSTONE_TORCH, Material.REDSTONE_WIRE, Material.REPEATER, Material.COMPARATOR, Material.LEVER, Material.TRIPWIRE_HOOK, Material.STRING, Material.RAIL, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL,
        Material.POWDER_SNOW, Material.WATER, Material.LAVA, Material.AIR, Material.CAVE_AIR
    );
    public static void setTeleportLocation(Resident res, Player p) {
        World world = res.getWorld();
        Location loc1 = new Location(world, p.getLocation().getBlockX(), p.getLocation().getBlockY() + 1, p.getLocation().getBlockZ());
        Location loc2 = p.getLocation();
        Location loc3 = new Location(world, p.getLocation().getBlockX(), p.getLocation().getBlockY() - 1, p.getLocation().getBlockZ());
        Block b1 = world.getBlockAt(loc1);
        Block b2 = world.getBlockAt(loc2);
        Block b3 = world.getBlockAt(loc3);
        if (!notFootBlock.contains(b3.getType()) && (b1.getType() == Material.AIR || b1.getType() == Material.CAVE_AIR) && (b2.getType() == Material.AIR || b2.getType() == Material.CAVE_AIR)) {
            res.setTeleportLocation(Loc.toLoc(p.getLocation()));
            p.sendMessage(Message.teleportSet.getTranslate());
        } else {
            p.sendMessage(Message.unsafeTeleport.getTranslate());
        }
    }
    public static boolean isPrePluginLoaded() {
        return Bukkit.getPluginManager().getPlugin("MagstarLib") != null && Bukkit.getPluginManager().getPlugin("MagstarEconomy") != null;
    }
    public static boolean isLoginSuccessful() {
        IDataManager iDataManager = Bukkit.getServicesManager().load(IDataManager.class);
        assert iDataManager != null;
        return iDataManager.check(Residence.getSerialNumber(), Residence.getUserVersion());
    }
    public static void init() {
        Residence.dbAvailable = ConfigUtils.getConfig().getBoolean("mysql.enabled");
        ConfigUtils.reload();
        Message.reload();
        PermissionsUtils.reload();
        GuiUtils.reload();
        AbstractDataManager.getInstance().reload();
        AbstractDataManager.getInstance().loadData();
        Objects.requireNonNull(Bukkit.getPluginCommand("MagstarResidence")).setExecutor(new CommandHandlers());
        Objects.requireNonNull(Bukkit.getPluginCommand("MagstarResidenceAdmin")).setExecutor(new CommandHandlers());
        Objects.requireNonNull(Bukkit.getPluginCommand("MagstarResidence")).setTabCompleter(new CommandHandlers());
        Objects.requireNonNull(Bukkit.getPluginCommand("MagstarResidenceAdmin")).setTabCompleter(new CommandHandlers());
    }
    public static UUID generateUniqueId() {
        return UUID.randomUUID();
    }
    public static int getAllSize(Location loc1, Location loc2) {
        int $x = Math.abs(loc1.getBlockX() - loc2.getBlockX() + 1);
        int $y = Math.abs(loc1.getBlockY() - loc2.getBlockY() + 1);
        int $z = Math.abs(loc1.getBlockZ() - loc2.getBlockZ() + 1);
        return $x * $y * $z;
    }
    public static int getAllSize(Loc loc1, Loc loc2) {
        int $x = Math.abs(loc1.getX() - loc2.getX() + 1);
        int $y = Math.abs(loc1.getY() - loc2.getY() + 1);
        int $z = Math.abs(loc1.getZ() - loc2.getZ() + 1);
        return $x * $y * $z;
    }
    public static int getBuyPrice(Player p, Location loc1, Location loc2) {
        String group = PermissionHandlers.getPlayerGroup(p);
        int allSize = getAllSize(loc1, loc2);
        int price = ConfigUtils.getConfig().getInt("buy_price." + group);
        return allSize * price;
    }
    public static int getBuyPriceByBlock(Player p) {
        String group = PermissionHandlers.getPlayerGroup(p);
        return ConfigUtils.getConfig().getInt("buy_price." + group);
    }
    public static int getSellPrice(Resident res) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(res.getOwner());
        if (op.isOnline()) {
            int price = getSellPriceByBlock(res);
            int allSize = getAllSize(res.getFirstLocation(), res.getSecondLocation());
            return allSize * price;
        } else {
            throw new RuntimeException("Player not online!");
        }
    }
    public static int getSellPriceByBlock(Resident res) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(res.getOwner());
        if (op.isOnline()) {
            Player p = Bukkit.getPlayer(res.getOwner());
            String group = PermissionHandlers.getPlayerGroup(p);
            return ConfigUtils.getConfig().getInt("sell_price." + group);
        } else {
            throw new RuntimeException("Player not online!");
        }
    }
    public static void flushRuntimeData() {
        RuntimeDataManager.removeAll();
        ResidentCreateManager.removeAll();
        ResidentCreateManager.removeLocations();
        PermissionSettingManager.removeAll();
    }
    public static boolean isIntegerBetween(int a, int b, int x) {
        return a >= b ? (x >= b && x <= a) : (x >= a && x <= b);
    }
    public static Resident getResident(Location loc) {
        for (Resident res : RuntimeDataManager.getResidents()) {
            if (res.getWorld().getName().equals(loc.getWorld().getName())) {
                if (isIntegerBetween(res.getFirstLocation().getX(), res.getSecondLocation().getX(), loc.getBlockX())
                 && isIntegerBetween(res.getFirstLocation().getY(), res.getSecondLocation().getY(), loc.getBlockY())
                 && isIntegerBetween(res.getFirstLocation().getZ(), res.getSecondLocation().getZ(), loc.getBlockZ())) {
                    return res;
                }
            }
        }
        return null;
    }
    public static boolean isLocationSafe(Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        World world = loc.getWorld();
        Block block = world.getBlockAt(x, y - 1, z);
        Block block1 = world.getBlockAt(x, y, z);
        Block block2 = world.getBlockAt(x, y + 1, z);
        return !notFootBlock.contains(block.getType()) && (block1.getType() == Material.AIR || block1.getType() == Material.CAVE_AIR) && (block2.getType() == Material.AIR || block2.getType() == Material.CAVE_AIR);
    }
    public static boolean isLocationSafe(Loc loc, World world) {
        int x = loc.getX();
        int y = loc.getY();
        int z = loc.getZ();
        Block block = world.getBlockAt(x, y - 1, z);
        Block block1 = world.getBlockAt(x, y, z);
        Block block2 = world.getBlockAt(x, y + 1, z);
        return !notFootBlock.contains(block.getType()) && (block1.getType() == Material.AIR || block1.getType() == Material.CAVE_AIR) && (block2.getType() == Material.AIR || block2.getType() == Material.CAVE_AIR);
    }
    public static Loc generateDefaultTeleportLocation(Loc first, Loc second, World world) {
        Loc minLoc = new Loc(Math.min(first.getX(), second.getX()), Math.min(first.getY(), second.getY()), Math.min(first.getZ(), second.getZ()));
        Loc maxLoc = new Loc(Math.max(first.getX(), second.getX()), Math.max(first.getY(), second.getY()), Math.max(first.getZ(), second.getZ()));
        List<Loc> availableLocations = new ArrayList<>();
        for (int x = minLoc.getX() - 3; x <= maxLoc.getX() + 3; x++) {
            for (int y = minLoc.getY() - 3; y <= maxLoc.getY() + 3; y++) {
                for (int z = minLoc.getZ() - 3; z <= maxLoc.getZ() + 3; z++) {
                    if (isLocationSafe(new Location(world, x, y, z))) {
                        availableLocations.add(new Loc(x, y, z));
                    }
                }
            }
        }
        Loc result;
        if (availableLocations.size() == 0) {
            return null;
        } else {
            result = availableLocations.get(0);
        }
        int halfX = (minLoc.getX() + maxLoc.getX()) / 2;
        int halfY = (minLoc.getY() + maxLoc.getY()) / 2;
        int halfZ = (minLoc.getZ() + maxLoc.getZ()) / 2;
        for (Loc loc : availableLocations) {
            double distance = Math.sqrt(Math.pow(loc.getX() - halfX, 2) + Math.pow(loc.getY() - halfY, 2) + Math.pow(loc.getZ() - halfZ, 2));
            double distance0 = Math.sqrt(Math.pow(result.getX() - halfX, 2) + Math.pow(result.getY() - halfY, 2) + Math.pow(result.getZ() - halfZ, 2));
            if (distance < distance0) {
                result = loc;
            }
        }
        result.setY(result.getY());
        return result;
    }
}
