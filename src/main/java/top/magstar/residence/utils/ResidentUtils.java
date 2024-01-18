package top.magstar.residence.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import top.magstar.economy.objects.Currency;
import top.magstar.economy.objects.CurrencyNotFoundException;
import top.magstar.residence.Residence;
import top.magstar.residence.datamanager.runtime.RuntimeDataManager;
import top.magstar.residence.handlers.ConversationHandlers;
import top.magstar.residence.handlers.CurrencyHandlers;
import top.magstar.residence.handlers.PermissionHandlers;
import top.magstar.residence.objects.Loc;
import top.magstar.residence.objects.PlayerFace;
import top.magstar.residence.objects.Resident;
import top.magstar.residence.utils.fileutils.ConfigUtils;
import top.magstar.residence.utils.fileutils.Message;
import top.magstar.residence.utils.fileutils.PermissionsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class ResidentUtils {
    public static void expand(Player p, int blocks) {
        if (GeneralUtils.getResident(p.getLocation()) == null) {
            p.sendMessage(Message.noResident.getTranslate());
        } else {
            Resident res = GeneralUtils.getResident(p.getLocation());
            assert res != null;
            if (!res.isOwner(p.getUniqueId())) {
                p.sendMessage(Message.notOwner.getTranslate());
            } else {
                Loc first = res.getFirstLocation();
                Loc second = res.getSecondLocation();
                Loc minLoc = new Loc(Math.min(first.getX(), second.getX()), Math.min(first.getY(), second.getY()), Math.min(first.getZ(), second.getZ()));
                Loc maxLoc = new Loc(Math.max(first.getX(), second.getX()), Math.max(first.getY(), second.getY()), Math.max(first.getZ(), second.getZ()));
                float yaw = p.getLocation().getYaw();
                float pitch = p.getLocation().getPitch();
                PlayerFace face = PlayerFace.getFace(yaw, pitch);
                Currency currency = CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency"));
                if (currency == null) {
                    throw new CurrencyNotFoundException("Currency not found!");
                } else {
                    switch (face) {
                        case UP -> {
                            if (maxLoc.getY() + blocks >= res.getWorld().getMaxHeight()) {
                                p.sendMessage(Message.expandTooHigh.getTranslate());
                            } else {
                                int balance = CurrencyHandlers.getCurrency(p.getUniqueId(), currency);
                                int cost = (maxLoc.getX() - minLoc.getX() + 1) * (maxLoc.getZ() - minLoc.getZ() + 1) * blocks * GeneralUtils.getBuyPriceByBlock(p);
                                if (balance < cost) {
                                    p.sendMessage(Message.notEnough.getTranslate());
                                } else {
                                    Location loc1 = new Location(res.getWorld(), maxLoc.getX(), maxLoc.getY() + 1, maxLoc.getZ());
                                    Location loc2 = new Location(res.getWorld(), minLoc.getX(), maxLoc.getY() + blocks, minLoc.getZ());
                                    if (isResidentOverlap(Loc.toLoc(loc1), Loc.toLoc(loc2), res.getWorld())) {
                                        p.sendMessage(Message.residentOverlap.getTranslate());
                                    } else {
                                        ConversationHandlers.ResidentExpand expand = new ConversationHandlers.ResidentExpand(face, blocks, currency, cost, p, res);
                                        Conversation conv = new ConversationFactory(Residence.getInstance())
                                                .withFirstPrompt(expand)
                                                .withTimeout(60)
                                                .buildConversation(p);
                                        conv.begin();
                                    }
                                }
                            }
                        }
                        case DOWN -> {
                            if (minLoc.getY() - blocks <= res.getWorld().getMinHeight()) {
                                p.sendMessage(Message.expandTooLow.getTranslate());
                            } else {
                                int balance = CurrencyHandlers.getCurrency(p.getUniqueId(), currency);
                                int cost = (maxLoc.getX() - minLoc.getX() + 1) * (maxLoc.getZ() - minLoc.getZ() + 1) * blocks * GeneralUtils.getBuyPriceByBlock(p);
                                if (balance < cost) {
                                    p.sendMessage(Message.notEnough.getTranslate());
                                } else {
                                    Location loc1 = new Location(res.getWorld(), maxLoc.getX(), minLoc.getY() - 1, maxLoc.getZ());
                                    Location loc2 = new Location(res.getWorld(), minLoc.getX(), minLoc.getY() - blocks, minLoc.getZ());
                                    if (isResidentOverlap(Loc.toLoc(loc1), Loc.toLoc(loc2), res.getWorld())) {
                                        p.sendMessage(Message.residentOverlap.getTranslate());
                                    } else {
                                        ConversationHandlers.ResidentExpand expand = new ConversationHandlers.ResidentExpand(face, blocks, currency, cost, p, res);
                                        Conversation conv = new ConversationFactory(Residence.getInstance())
                                                .withFirstPrompt(expand)
                                                .withTimeout(60)
                                                .buildConversation(p);
                                        conv.begin();
                                    }
                                }
                            }
                        }
                        case NORTH -> {
                            int balance = CurrencyHandlers.getCurrency(p.getUniqueId(), currency);
                            int cost = (maxLoc.getX() - minLoc.getX() + 1) * (maxLoc.getY() - minLoc.getY() + 1) * blocks * GeneralUtils.getBuyPriceByBlock(p);
                            if (balance < cost) {
                                p.sendMessage(Message.notEnough.getTranslate());
                            } else {
                                Location loc1 = new Location(res.getWorld(), maxLoc.getX(), maxLoc.getY(), minLoc.getZ() - 1);
                                Location loc2 = new Location(res.getWorld(), minLoc.getX(), minLoc.getY(), minLoc.getZ() - blocks);
                                if (isResidentOverlap(Loc.toLoc(loc1), Loc.toLoc(loc2), res.getWorld())) {
                                    p.sendMessage(Message.residentOverlap.getTranslate());
                                } else {
                                    ConversationHandlers.ResidentExpand expand = new ConversationHandlers.ResidentExpand(face, blocks, currency, cost, p, res);
                                    Conversation conv = new ConversationFactory(Residence.getInstance())
                                            .withFirstPrompt(expand)
                                            .withTimeout(60)
                                            .buildConversation(p);
                                    conv.begin();
                                }
                            }
                        }
                        case SOUTH -> {
                            int balance = CurrencyHandlers.getCurrency(p.getUniqueId(), currency);
                            int cost = (maxLoc.getX() - minLoc.getX() + 1) * (maxLoc.getY() - minLoc.getY() + 1) * blocks * GeneralUtils.getBuyPriceByBlock(p);
                            if (balance < cost) {
                                p.sendMessage(Message.notEnough.getTranslate());
                            } else {
                                Location loc1 = new Location(res.getWorld(), maxLoc.getX(), maxLoc.getY(), maxLoc.getZ() + 1);
                                Location loc2 = new Location(res.getWorld(), minLoc.getX(), minLoc.getY(), maxLoc.getZ() + blocks);
                                if (isResidentOverlap(Loc.toLoc(loc1), Loc.toLoc(loc2), res.getWorld())) {
                                    p.sendMessage(Message.residentOverlap.getTranslate());
                                } else {
                                    ConversationHandlers.ResidentExpand expand = new ConversationHandlers.ResidentExpand(face, blocks, currency, cost, p, res);
                                    Conversation conv = new ConversationFactory(Residence.getInstance())
                                            .withFirstPrompt(expand)
                                            .withTimeout(60)
                                            .buildConversation(p);
                                    conv.begin();
                                }
                            }
                        }
                        case EAST -> {
                            Location loc1 = new Location(res.getWorld(), maxLoc.getX() + 1, maxLoc.getY(), maxLoc.getZ());
                            Location loc2 = new Location(res.getWorld(), maxLoc.getX() + blocks, minLoc.getY(), minLoc.getZ());
                            int balance = CurrencyHandlers.getCurrency(p.getUniqueId(), currency);
                            int cost = (maxLoc.getZ() - minLoc.getZ() + 1) * (maxLoc.getY() - minLoc.getY() + 1) * blocks * GeneralUtils.getBuyPriceByBlock(p);
                            if (balance < cost) {
                                p.sendMessage(Message.notEnough.getTranslate());
                            } else {
                                if (isResidentOverlap(Loc.toLoc(loc1), Loc.toLoc(loc2), res.getWorld())) {
                                    p.sendMessage(Message.residentOverlap.getTranslate());
                                } else {
                                    ConversationHandlers.ResidentExpand expand = new ConversationHandlers.ResidentExpand(face, blocks, currency, cost, p, res);
                                    Conversation conv = new ConversationFactory(Residence.getInstance())
                                            .withFirstPrompt(expand)
                                            .withTimeout(60)
                                            .buildConversation(p);
                                    conv.begin();
                                }
                            }
                        }
                        case WEST -> {
                            Location loc1 = new Location(res.getWorld(), minLoc.getX() - 1, maxLoc.getY(), maxLoc.getZ());
                            Location loc2 = new Location(res.getWorld(), minLoc.getX() - blocks, minLoc.getY(), minLoc.getZ());
                            int balance = CurrencyHandlers.getCurrency(p.getUniqueId(), currency);
                            int cost = (maxLoc.getZ() - minLoc.getZ() + 1) * (maxLoc.getY() - minLoc.getY() + 1) * blocks * GeneralUtils.getBuyPriceByBlock(p);
                            if (balance < cost) {
                                p.sendMessage(Message.notEnough.getTranslate());
                            } else {
                                if (isResidentOverlap(Loc.toLoc(loc1), Loc.toLoc(loc2), res.getWorld())) {
                                    p.sendMessage(Message.residentOverlap.getTranslate());
                                } else {
                                    ConversationHandlers.ResidentExpand expand = new ConversationHandlers.ResidentExpand(face, blocks, currency, cost, p, res);
                                    Conversation conv = new ConversationFactory(Residence.getInstance())
                                            .withFirstPrompt(expand)
                                            .withTimeout(60)
                                            .buildConversation(p);
                                    conv.begin();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public static void info(Player p) {
        Resident res = GeneralUtils.getResident(p.getLocation());
        assert res != null;
        List<String> originMessage = Message.getConfig().getStringList("resident_info");
        List<String> admins = new ArrayList<>();
        for (UUID admin : res.getAdmins()) {
            admins.add(Bukkit.getOfflinePlayer(admin).getName());
        }
        List<String> blackLists = new ArrayList<>();
        for (UUID blacklist : res.getBlacklist()) {
            blackLists.add(Bukkit.getOfflinePlayer(blacklist).getName());
        }
        for (String s : originMessage) {
            p.sendMessage(
                    s.replace("{player}", Objects.requireNonNull(Bukkit.getOfflinePlayer(res.getOwner()).getName()))
                            .replace("{admin}", admins.toString().replace("[", "").replace("]", ""))
                            .replace("{blacklist}", blackLists.toString().replace("[", "").replace("]", ""))
                            .replace("{first_location_x}", String.valueOf(res.getFirstLocation().getX()))
                            .replace("{first_location_y}", String.valueOf(res.getFirstLocation().getY()))
                            .replace("{first_location_z}", String.valueOf(res.getFirstLocation().getZ()))
                            .replace("{second_location_x}", String.valueOf(res.getSecondLocation().getX()))
                            .replace("{second_location_y}", String.valueOf(res.getSecondLocation().getY()))
                            .replace("{second_location_z}", String.valueOf(res.getSecondLocation().getZ()))
                            .replace("{tp_location_x}", String.valueOf(res.getTeleportLocation().getX()))
                            .replace("{tp_location_y}", String.valueOf(res.getTeleportLocation().getY()))
                            .replace("{tp_location_z}", String.valueOf(res.getTeleportLocation().getZ()))
                            .replace("&", "§")
            );
        }
    }
    public static boolean isResidentOverlap(Loc first, Loc second, World world) {
        boolean b = false;
        Loc minLoc = new Loc(Math.min(first.getX(), second.getX()), Math.min(first.getY(), second.getY()), Math.min(first.getZ(), second.getZ()));
        Loc maxLoc = new Loc(Math.max(first.getX(), second.getX()), Math.max(first.getY(), second.getY()), Math.max(first.getZ(), second.getZ()));
        for (int x = minLoc.getX(); x <= maxLoc.getX(); x++) {
            for (int y = minLoc.getY(); y <= maxLoc.getY(); y++) {
                for (int z = minLoc.getZ(); z <= maxLoc.getZ(); z++) {
                    if (GeneralUtils.getResident(new Location(world, x, y, z)) != null) {
                        b = true;
                        break;
                    }
                }
            }
        }
        return b;
    }
    public static void give(Resident res, Player to) {
        Player owner = Bukkit.getPlayer(res.getOwner());
        if (owner == null) {
            throw new IllegalArgumentException("Resident owner is not online or has not existed!");
        }
        if (getMaxResidents(to) == RuntimeDataManager.getPlayerResidents(to.getUniqueId()).size()) {
            owner.sendMessage(Message.maxSize.getTranslate());
        } else {
            int from = GeneralUtils.getBuyPriceByBlock(owner);
            int too = GeneralUtils.getBuyPriceByBlock(to);
            if (from < too) {
                Currency c = Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency")));
                int price = GeneralUtils.getAllSize(res.getFirstLocation(), res.getSecondLocation());
                int money = CurrencyHandlers.getCurrency(to.getUniqueId(), c);
                if (money < price) {
                    owner.sendMessage(Message.sendNotEnough.getTranslate());
                } else {
                    Conversation conv = new ConversationFactory(Residence.getInstance())
                            .withFirstPrompt(new ConversationHandlers.ResidentGive(res, to, price))
                            .withTimeout(60)
                            .buildConversation(owner);
                    conv.begin();
                }
            } else {
                Conversation conv = new ConversationFactory(Residence.getInstance())
                        .withFirstPrompt(new ConversationHandlers.ResidentGive(res, to, 0))
                        .withTimeout(60)
                        .buildConversation(owner);
                conv.begin();
            }
        }
    }
    /*public static void give(Resident res, Player to, Player admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin is not online or has not existed!");
        }
        if (getMaxResidents(to) == RuntimeDataManager.getPlayerResidents(to.getUniqueId()).size()) {
            admin.sendMessage(Message.maxSize.getTranslate());
        } else {
            int from = GeneralUtils.getBuyPriceByBlock(owner);
            int too = GeneralUtils.getBuyPriceByBlock(to);
            if (from < too) {
                Currency c = Objects.requireNonNull(CurrencyHandlers.getCurrency(ConfigUtils.getConfig().getString("cost_currency")));
                int price = GeneralUtils.getAllSize(res.getFirstLocation(), res.getSecondLocation());
                int money = CurrencyHandlers.getCurrency(to.getUniqueId(), c);
                if (money < price) {
                    owner.sendMessage(Message.sendNotEnough.getTranslate());
                } else {
                    Conversation conv = new ConversationFactory(Residence.getInstance())
                            .withFirstPrompt(new ConversationHandlers.ResidentGive(res, to, price))
                            .withTimeout(60)
                            .buildConversation(owner);
                    conv.begin();
                }
            } else {
                Conversation conv = new ConversationFactory(Residence.getInstance())
                        .withFirstPrompt(new ConversationHandlers.ResidentGive(res, to, 0))
                        .withTimeout(60)
                        .buildConversation(owner);
                conv.begin();
            }
        }
    }*/
    public static void randomTeleport(Player p) {
        World world = p.getWorld();
        if (ConfigUtils.getConfig().getStringList("available_world").contains(world.getName())) {
            Location loc = generateRandomLoc(world);
            p.teleport(loc);
            p.sendMessage(Message.teleport.getTranslate()
                    .replace("{location_x}", String.valueOf(loc.getBlockX()))
                    .replace("{location_y}", String.valueOf(loc.getBlockY()))
                    .replace("{location_z}", String.valueOf(loc.getBlockZ())));
        } else {
            p.sendMessage(Message.notAllowed.getTranslate());
        }
    }
    public static Location generateRandomLoc(World world) {
        int range = ConfigUtils.getConfig().getInt("random_range");
        Location loc;
        do {
            int x = (int) Math.round((Math.random() >= 0.5 ? 1 : -1) * range * Math.random());
            int z = (int) Math.round((Math.random() >= 0.5 ? 1 : -1) * range * Math.random());
            int y = world.getHighestBlockYAt(x, z) + 1;
            loc = new Location(world, x, y, z);
        } while (GeneralUtils.getResident(loc) != null);
        return loc;
    }
    public static int getMaxResidents(Player p) {
        if (p.hasPermission("magstarresidence.max.*")) {
            return -1;
        }
        else {
            int max = ConfigUtils.getConfig().getInt("max_residents." + PermissionHandlers.getPlayerGroup(p));
            for (PermissionAttachmentInfo perm : p.getEffectivePermissions()) {
                String name = perm.getPermission();
                if (name.startsWith("magstarresidence.max.")) {
                    String[] args = name.split("\\.");
                    if (args.length == 3 && Pattern.compile("^[0-9]*").matcher(args[2]).matches() && Integer.parseInt(args[2]) > 0) {
                        max = Math.max(max, Integer.parseInt(args[2]));
                    } else {
                        throw new IllegalArgumentException("Permission error!");
                    }
                }
            }
            return max;
        }
    }
    public static void tp(Player p, Resident res) {
        Loc loc = res.getTeleportLocation();
        World world = res.getWorld();
        if (GeneralUtils.isLocationSafe(loc, world)) {
            p.teleport(Loc.toLocation(world, loc));
            p.sendMessage(Message.residentTeleport.getTranslate());
        } else {
            p.sendMessage(Message.residentChoose.getTranslate());
            Loc tp = GeneralUtils.generateDefaultTeleportLocation(res.getFirstLocation(), res.getSecondLocation(), world);
            if (tp == null) {
                p.sendMessage(Message.residentUnsafe.getTranslate());
            } else {
                res.setTeleportLocation(tp);
                p.teleport(Loc.toLocation(world, res.getTeleportLocation()));
                p.sendMessage(Message.residentTeleport.getTranslate());
            }
        }
    }
}
