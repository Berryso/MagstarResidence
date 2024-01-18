package top.magstar.residence.handlers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.magstar.residence.Residence;
import top.magstar.residence.datamanager.runtime.RuntimeDataManager;
import top.magstar.residence.objects.Permission;
import top.magstar.residence.objects.Resident;
import top.magstar.residence.utils.GeneralUtils;
import top.magstar.residence.utils.ResidentUtils;
import top.magstar.residence.utils.fileutils.ConfigUtils;
import top.magstar.residence.utils.fileutils.GuiUtils;
import top.magstar.residence.utils.fileutils.Message;
import top.magstar.residence.utils.fileutils.PermissionsUtils;

import java.util.*;
import java.util.regex.Pattern;

public final class CommandHandlers implements TabExecutor {
    private final static UUID defUniqueId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (cmd.getName().equals("MagstarResidence")) {
            switch (args.length) {
                case 0 -> {
                    sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                    sender.sendMessage("§b作者: §dBerry_so 浆果");
                    sender.sendMessage(" ");
                    sender.sendMessage("§7指令缩写: res;msres");
                    sender.sendMessage(" ");
                    sender.sendMessage("§e/res help <页数>     §d| §b打开指令帮助页面");
                    sender.sendMessage("§e/res info           §d| §b查询你所在的领地信息");
                    sender.sendMessage("§e/res set            §d| §b打开领地通用权限设置菜单");
                    sender.sendMessage("§e/res pset <玩家>     §d| §b打开玩家权限设置菜单");
                    sender.sendMessage("§e/res expand <格数>   §d| §b向你面朝的方向扩建领地");
                    sender.sendMessage("§e/res list           §d| §b查看你的领地列表");
                    sender.sendMessage("§e/res remove         §d| §b选择移除你所在的领地");
                    sender.sendMessage("§e/res removeall      §d| §b移除你的所有领地");
                    sender.sendMessage("§f页数: §b1 §f/ §b2");
                    sender.sendMessage(" ");
                    sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                }
                case 1 -> {
                    switch (args[0]) {
                        case "help" -> {
                            sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                            sender.sendMessage("§b作者: §dBerry_so 浆果");
                            sender.sendMessage(" ");
                            sender.sendMessage("§7指令缩写: res;msres");
                            sender.sendMessage(" ");
                            sender.sendMessage("§e/res help [页数]     §d| §b打开指令帮助页面");
                            sender.sendMessage("§e/res info           §d| §b查询你所在的领地信息");
                            sender.sendMessage("§e/res set            §d| §b打开领地通用权限设置菜单");
                            sender.sendMessage("§e/res pset <玩家>     §d| §b打开玩家权限设置菜单");
                            sender.sendMessage("§e/res expand <格数>   §d| §b向你面朝的方向扩建领地");
                            sender.sendMessage("§e/res list [玩家]     §d| §b查看你的领地列表");
                            sender.sendMessage("§e/res remove         §d| §b选择移除你所在的领地");
                            sender.sendMessage("§e/res removeall      §d| §b移除你的所有领地");
                            sender.sendMessage(" ");
                            sender.sendMessage("§f页数: §b1 §f/ §b2");
                            sender.sendMessage(" ");
                            sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                        }
                        case "info" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.info")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    if (GeneralUtils.getResident(p.getLocation()) == null) {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    } else {
                                        Resident res = GeneralUtils.getResident(p.getLocation());
                                        assert res != null;
                                        boolean b1 = res.getPermission(defUniqueId, Permission.info); //Check if the resident has default permission
                                        boolean b2 = PermissionHandlers.hasPermission(p, res, Permission.info); //Check if the player has permission
                                        if (b1) {
                                            if (!res.isBlackListed(p.getUniqueId())) {
                                                p.sendMessage(Message.blacklisted.getTranslate());
                                            } else {
                                                ResidentUtils.info(p);
                                            }
                                        } else {
                                            if (b2) {
                                                ResidentUtils.info(p);
                                            } else {
                                                p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", "info"));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        case "set" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.set")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res == null) {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    } else {
                                        if (res.isOwner(p.getUniqueId()) || res.isAdmin(p.getUniqueId())) {
                                            GuiUtils.createPermissionGui(res, false, null, p);
                                        } else {
                                            p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", "set"));
                                        }
                                    }
                                }
                            }
                        }
                        case "pset" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.pset")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "expand" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.expand")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "list" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.list")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    for (Map.Entry<Integer, Resident> entry : RuntimeDataManager.getPlayerResidents(p.getUniqueId()).entrySet()) {
                                        Resident res = entry.getValue();
                                        p.sendMessage(Message.residentList.getTranslate()
                                                .replace("{index}", String.valueOf(entry.getKey()))
                                                .replace("{world}", res.getWorld().getName())
                                                .replace("{first_location_x}", String.valueOf(res.getFirstLocation().getX()))
                                                .replace("{first_location_y}", String.valueOf(res.getFirstLocation().getY()))
                                                .replace("{first_location_z}", String.valueOf(res.getFirstLocation().getZ()))
                                                .replace("{second_location_x}", String.valueOf(res.getSecondLocation().getX()))
                                                .replace("{second_location_y}", String.valueOf(res.getSecondLocation().getY()))
                                                .replace("{second_location_z}", String.valueOf(res.getSecondLocation().getZ()))
                                        );
                                    }
                                }
                            }
                        }
                        case "remove" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.remove")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res == null) {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    } else {
                                        if (!res.isOwner(p.getUniqueId())) {
                                            p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", "remove"));
                                        } else {
                                            Conversation conv = new ConversationFactory(Residence.getInstance())
                                                    .withFirstPrompt(new ConversationHandlers.ResidentRemove(res))
                                                    .withTimeout(60)
                                                    .buildConversation(p);
                                            conv.begin();
                                        }
                                    }
                                }
                            }
                        }
                        case "removeall" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.removeall")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    if (RuntimeDataManager.getPlayerResidents(p.getUniqueId()).size() == 0) {
                                        p.sendMessage(Message.haveNoResidents.getTranslate());
                                    } else {
                                        Conversation conv = new ConversationFactory(Residence.getInstance())
                                                .withFirstPrompt(new ConversationHandlers.ResidentRemoveAll())
                                                .withTimeout(60)
                                                .buildConversation(p);
                                        conv.begin();
                                    }
                                }
                            }
                        }
                        case "tpset" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.tpset")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res == null) {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    } else {
                                        if (!res.isOwner(p.getUniqueId()) && !res.isAdmin(p.getUniqueId())) {
                                            p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", "tpset"));
                                        } else {
                                            GeneralUtils.setTeleportLocation(res, p);
                                        }
                                    }
                                }
                            }
                        }
                        case "tp" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.tpset")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "blacklist" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.blacklist")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "admin" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.admin")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "give" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.give")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "rt" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.rt")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    ResidentUtils.randomTeleport(p);
                                }
                            }
                        }
                        default -> sender.sendMessage(Message.wrongSyntax.getTranslate());
                    }
                }
                case 2 -> {
                    switch (args[0]) {
                        case "help" -> {
                            switch (args[1]) {
                                case "1" -> {
                                    sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                                    sender.sendMessage("§b作者: §dBerry_so 浆果");
                                    sender.sendMessage(" ");
                                    sender.sendMessage("§7指令缩写: res;msres");
                                    sender.sendMessage(" ");
                                    sender.sendMessage("§e/res help [页数]      §d| §b打开指令帮助页面");
                                    sender.sendMessage("§e/res info            §d| §b查询你所在的领地信息");
                                    sender.sendMessage("§e/res set             §d| §b打开领地通用权限设置菜单");
                                    sender.sendMessage("§e/res pset <玩家>      §d| §b打开玩家权限设置菜单");
                                    sender.sendMessage("§e/res expand <格数>    §d| §b向你面朝的方向扩建领地");
                                    sender.sendMessage("§e/res list            §d| §b查看你的领地列表");
                                    sender.sendMessage("§e/res remove          §d| §b选择移除你所在的领地");
                                    sender.sendMessage("§e/res removeall       §d| §b移除你的所有领地");
                                    sender.sendMessage(" ");
                                    sender.sendMessage("§f页数: §b1 §f/ §b2");
                                    sender.sendMessage(" ");
                                    sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                                }
                                case "2" -> {
                                    sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                                    sender.sendMessage("§b作者: §dBerry_so 浆果");
                                    sender.sendMessage(" ");
                                    sender.sendMessage("§7指令缩写: res;msres");
                                    sender.sendMessage(" ");
                                    sender.sendMessage("§e/res tpset           §d| §b设置领地传送点");
                                    sender.sendMessage("§e/res tp <玩家> <序号>  §d| §b传送到玩家的某个领地");
                                    sender.sendMessage("§e/res blacklist <玩家> §d| §b将某个玩家设为黑名单");
                                    sender.sendMessage("§e/res admin <玩家>     §d| §b将某个玩家设为管理员");
                                    sender.sendMessage("§e/res give <玩家>      §d| §b将您所处的领地给予某玩家");
                                    sender.sendMessage("§e/res rt              §d| §b随机传送至你所在世界的某个位置");
                                    sender.sendMessage(" ");
                                    sender.sendMessage("§f页数: §b2 §f/ §b2");
                                    sender.sendMessage(" ");
                                    sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                                }
                                default -> sender.sendMessage(Message.wrongSyntax.getTranslate());
                            }
                        }
                        case "info" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.info")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "set" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.set")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "list" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.list")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "remove" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.remove")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "removeall" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.removeall")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "tpset" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.tpset")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "pset" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.pset")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res != null) {
                                        if (res.isOwner(p.getUniqueId()) || res.isAdmin(p.getUniqueId())) {
                                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                            if (!player.hasPlayedBefore()) {
                                                p.sendMessage(Message.noPlayer.getTranslate());
                                            } else {
                                                GuiUtils.createPermissionGui(res, true, player, p);
                                            }
                                        } else {
                                            p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", "set"));
                                        }
                                    } else {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    }
                                }
                            }
                        }
                        case "expand" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.expand")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res != null) {
                                        if (Pattern.compile("^[0-9]*").matcher(args[1]).matches() && Integer.parseInt(args[1]) > 0) {
                                            ResidentUtils.expand(p, Integer.parseInt(args[1]));
                                        } else {
                                            p.sendMessage(Message.wrongSyntax.getTranslate());
                                        }
                                    } else {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    }
                                }
                            }
                        }
                        case "blacklist" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.blacklist")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res != null) {
                                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                        if (!player.hasPlayedBefore()) {
                                            p.sendMessage(Message.noPlayer.getTranslate());
                                        } else {
                                            if (res.isOwner(player.getUniqueId()) || res.isAdmin(player.getUniqueId())) {
                                                p.sendMessage(Message.userBlacklisted.getTranslate());
                                            } else {
                                                if (!res.isBlackListed(player.getUniqueId())) {
                                                    res.setBlackListed(player.getUniqueId());
                                                    p.sendMessage(Message.blacklist.getTranslate());
                                                } else {
                                                    res.setWhiteListed(player.getUniqueId());
                                                    p.sendMessage(Message.whitelist.getTranslate());
                                                }
                                            }
                                        }
                                    } else {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    }
                                }
                            }
                        }
                        case "admin" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.admin")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res != null) {
                                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                        if (!player.hasPlayedBefore()) {
                                            p.sendMessage(Message.noPlayer.getTranslate());
                                        } else {
                                            if (!res.isOwner(p.getUniqueId())) {
                                                p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", "admin"));
                                            } else {
                                                if (res.isOwner(player.getUniqueId())) {
                                                    p.sendMessage(Message.ownerSetAdmin.getTranslate());
                                                } else {
                                                    if (res.isBlackListed(player.getUniqueId())) {
                                                        p.sendMessage(Message.blacklistedSetAdmin.getTranslate());
                                                    } else {
                                                        if (res.isAdmin(player.getUniqueId())) {
                                                            p.sendMessage(Message.adminDisplace.getTranslate());
                                                        } else {
                                                            p.sendMessage(Message.adminSet.getTranslate());
                                                        }
                                                        res.setAdmin(player.getUniqueId());
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    }
                                }
                            }
                        }
                        case "give" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (p.hasPermission("magstarresidence.give")) {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res != null) {
                                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                        if (!player.hasPlayedBefore()) {
                                            p.sendMessage(Message.noPlayer.getTranslate());
                                        } else {
                                            if (!player.isOnline()) {
                                                p.sendMessage(Message.notOnline.getTranslate());
                                            } else {
                                                Player to = Bukkit.getPlayer(args[1]);
                                                if (!res.isOwner(p.getUniqueId())) {
                                                    p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", "give"));
                                                } else {
                                                    ResidentUtils.give(res, to);
                                                }
                                            }
                                        }
                                    } else {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    }
                                } else {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                }
                            }
                        }
                        case "tp" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.tp")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        case "rt" -> {
                            if (!(sender instanceof Player p)) {
                                sender.sendMessage(Message.onlyPlayer.getTranslate());
                            } else {
                                if (!p.hasPermission("magstarresidence.rt")) {
                                    p.sendMessage(Message.noPerm.getTranslate());
                                } else {
                                    p.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                        }
                        default -> sender.sendMessage(Message.wrongSyntax.getTranslate());
                    }
                }
                case 3 -> {
                    if (args[0].equals("tp")) {
                        if (!(sender instanceof Player p)) {
                            sender.sendMessage(Message.onlyPlayer.getTranslate());
                        } else {
                            if (!p.hasPermission("magstarresidence.tp")) {
                                p.sendMessage(Message.noPerm.getTranslate());
                            } else {
                                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                if (!player.hasPlayedBefore()) {
                                    p.sendMessage(Message.noPlayer.getTranslate());
                                } else {
                                    Map<Integer, Resident> residents = RuntimeDataManager.getPlayerResidents(player.getUniqueId());
                                    if (!(Pattern.compile("^[0-9]*").matcher(args[2]).matches() && Integer.parseInt(args[2]) > 0)) {
                                        p.sendMessage(Message.wrongSyntax.getTranslate());
                                    } else {
                                        int x = Integer.parseInt(args[2]);
                                        if (x > residents.size()) {
                                            p.sendMessage(Message.noTarget.getTranslate());
                                        } else {
                                            ResidentUtils.tp(p, residents.get(x));
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        sender.sendMessage(Message.wrongSyntax.getTranslate());
                    }
                }
                default -> sender.sendMessage(Message.wrongSyntax.getTranslate());
            }
        } else if (cmd.getName().equals("MagstarResidenceAdmin")) {
            if (!sender.hasPermission("residence.admin")) {
                sender.sendMessage(Message.noPerm.getTranslate());
            } else {
                switch (args.length) {
                    case 0 -> {
                        sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                        sender.sendMessage("§b作者: §dBerry_so 浆果");
                        sender.sendMessage(" ");
                        sender.sendMessage("§7指令缩写: resadmin;msresadmin");
                        sender.sendMessage(" ");
                        sender.sendMessage("§e/resadmin help [页数]      §d| §b打开指令帮助页面");
                        sender.sendMessage("§e/resadmin info            §d| §b查询你所在的领地信息");
                        sender.sendMessage("§e/resadmin set             §d| §b打开领地通用权限设置菜单");
                        sender.sendMessage("§e/resadmin pset <玩家>      §d| §b打开玩家权限设置菜单");
                        sender.sendMessage("§e/resadmin expand <格数>    §d| §b向你面朝的方向扩建领地");
                        sender.sendMessage("§e/resadmin list            §d| §b查看你的领地列表");
                        sender.sendMessage("§e/resadmin remove          §d| §b选择移除你所在的领地");
                        sender.sendMessage("§e/resadmin removeall       §d| §b移除你的所有领地");
                        sender.sendMessage(" ");
                        sender.sendMessage("§f页数: §b1 §f/ §b2");
                        sender.sendMessage(" ");
                        sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                    }
                    case 1 -> {
                        switch (args[0]) {
                            case "help" -> {
                                sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                                sender.sendMessage("§b作者: §dBerry_so 浆果");
                                sender.sendMessage(" ");
                                sender.sendMessage("§7指令缩写: resadmin;msresadmin");
                                sender.sendMessage(" ");
                                sender.sendMessage("§e/resadmin help [页数]      §d| §b打开指令帮助页面");
                                sender.sendMessage("§e/resadmin info            §d| §b查询你所在的领地信息");
                                sender.sendMessage("§e/resadmin set             §d| §b打开领地通用权限设置菜单");
                                sender.sendMessage("§e/resadmin pset <玩家>      §d| §b打开玩家权限设置菜单");
                                sender.sendMessage("§e/resadmin expand <格数>    §d| §b向你面朝的方向扩建领地");
                                sender.sendMessage("§e/resadmin list [玩家]      §d| §b查看你的领地列表");
                                sender.sendMessage("§e/resadmin remove          §d| §b选择移除你所在的领地");
                                sender.sendMessage("§e/resadmin removeall [玩家] §d| §b移除你的所有领地");
                                sender.sendMessage(" ");
                                sender.sendMessage("§f页数: §b1 §f/ §b2");
                                sender.sendMessage(" ");
                                sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                            }
                            case "info" -> {
                                if (!(sender instanceof Player p)) {
                                    sender.sendMessage(Message.onlyPlayer.getTranslate());
                                } else {
                                    ResidentUtils.info(p);
                                }
                            }
                            case "set" -> {
                                if (!(sender instanceof Player p)) {
                                    sender.sendMessage(Message.onlyPlayer.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res == null) {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    } else {
                                        if (res.isOwner(p.getUniqueId()) || res.isAdmin(p.getUniqueId())) {
                                            GuiUtils.createPermissionGui(res, false, null, p);
                                        } else {
                                            p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", "set"));
                                        }
                                    }
                                }
                            }
                            case "list" -> {
                                if (!(sender instanceof Player p)) {
                                    sender.sendMessage(Message.onlyPlayer.getTranslate());
                                } else {
                                    for (Map.Entry<Integer, Resident> entry : RuntimeDataManager.getPlayerResidents(p.getUniqueId()).entrySet()) {
                                        Resident res = entry.getValue();
                                        sender.sendMessage(Message.residentList.getTranslate()
                                                .replace("{index}", String.valueOf(entry.getKey()))
                                                .replace("{world}", res.getWorld().getName())
                                                .replace("{first_location_x}", String.valueOf(res.getFirstLocation().getX()))
                                                .replace("{first_location_y}", String.valueOf(res.getFirstLocation().getY()))
                                                .replace("{first_location_z}", String.valueOf(res.getFirstLocation().getZ()))
                                                .replace("{second_location_x}", String.valueOf(res.getSecondLocation().getX()))
                                                .replace("{second_location_y}", String.valueOf(res.getSecondLocation().getY()))
                                                .replace("{second_location_z}", String.valueOf(res.getSecondLocation().getZ()))
                                        );
                                    }
                                }
                            }
                            case "remove" -> {
                                if (!(sender instanceof Player p)) {
                                    sender.sendMessage(Message.onlyPlayer.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res == null) {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    } else {
                                        Conversation conv = new ConversationFactory(Residence.getInstance())
                                                .withFirstPrompt(new ConversationHandlers.ResidentRemove(res))
                                                .withTimeout(60)
                                                .buildConversation(p);
                                        conv.begin();
                                    }
                                }
                            }
                            case "removeall" -> {
                                if (!(sender instanceof Player p)) {
                                    sender.sendMessage(Message.onlyPlayer.getTranslate());
                                } else {
                                    if (RuntimeDataManager.getPlayerResidents(p.getUniqueId()).size() == 0) {
                                        p.sendMessage(Message.haveNoResidents.getTranslate());
                                    } else {
                                        Conversation conv = new ConversationFactory(Residence.getInstance())
                                                .withFirstPrompt(new ConversationHandlers.ResidentRemoveAll())
                                                .withTimeout(60)
                                                .buildConversation(p);
                                        conv.begin();
                                    }
                                }
                            }
                            case "tpset" -> {
                                if (!(sender instanceof Player p)) {
                                    sender.sendMessage(Message.onlyPlayer.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res == null) {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    } else {
                                        GeneralUtils.setTeleportLocation(res, p);
                                    }
                                }
                            }
                            case "rt" -> {
                                if (!(sender instanceof Player p)) {
                                    sender.sendMessage(Message.onlyPlayer.getTranslate());
                                } else {
                                    ResidentUtils.randomTeleport(p);
                                }
                            }
                            default -> sender.sendMessage(Message.wrongSyntax.getTranslate());
                        }
                    }
                    case 2 -> {
                        switch (args[0]) {
                            case "help" -> {
                                switch (args[1]) {
                                    case "1" -> {
                                        sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                                        sender.sendMessage("§b作者: §dBerry_so 浆果");
                                        sender.sendMessage(" ");
                                        sender.sendMessage("§7指令缩写: resadmin;msresadmin");
                                        sender.sendMessage(" ");
                                        sender.sendMessage("§e/resadmin help [页数]      §d| §b打开指令帮助页面");
                                        sender.sendMessage("§e/resadmin info            §d| §b查询你所在的领地信息");
                                        sender.sendMessage("§e/resadmin set             §d| §b打开领地通用权限设置菜单");
                                        sender.sendMessage("§e/resadmin pset <玩家>      §d| §b打开玩家权限设置菜单");
                                        sender.sendMessage("§e/resadmin reload <文件>    §d| §b重置某个配置文件");
                                        sender.sendMessage("§e/resadmin list            §d| §b查看你的领地列表");
                                        sender.sendMessage("§e/resadmin remove          §d| §b选择移除你所在的领地");
                                        sender.sendMessage("§e/resadmin removeall       §d| §b移除你的所有领地");
                                        sender.sendMessage(" ");
                                        sender.sendMessage("§f页数: §b1 §f/ §b2");
                                        sender.sendMessage(" ");
                                        sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                                    }
                                    case "2" -> {
                                        sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                                        sender.sendMessage("§b作者: §dBerry_so 浆果");
                                        sender.sendMessage(" ");
                                        sender.sendMessage("§7指令缩写: resadmin;msresadmin");
                                        sender.sendMessage(" ");
                                        sender.sendMessage("§e/resadmin tpset           §d| §b设置领地传送点");
                                        sender.sendMessage("§e/resadmin tp <玩家> <序号>  §d| §b传送到玩家的某个领地");
                                        sender.sendMessage("§e/resadmin blacklist <玩家> §d| §b将某个玩家设为黑名单");
                                        sender.sendMessage("§e/resadmin admin <玩家>     §d| §b将某个玩家设为管理员");
                                        sender.sendMessage("§e/resadmin give <玩家>      §d| §b将您所处的领地给予某玩家");
                                        sender.sendMessage("§e/resadmin rt              §d| §b随机传送至你所在世界的某个位置");
                                        sender.sendMessage(" ");
                                        sender.sendMessage("§f页数: §b2 §f/ §b2");
                                        sender.sendMessage(" ");
                                        sender.sendMessage("§e========== §f[ §b§lMagStar§3§lResidence §f] §e==========");
                                    }
                                    default -> sender.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                            case "pset" -> {
                                if (!(sender instanceof Player p)) {
                                    sender.sendMessage(Message.onlyPlayer.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res != null) {
                                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                        if (!player.hasPlayedBefore()) {
                                            p.sendMessage(Message.noPlayer.getTranslate());
                                        } else {
                                            GuiUtils.createPermissionGui(res, true, player, p);
                                        }
                                    } else {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    }
                                }
                            }
                            case "reload" -> {
                                switch (args[1]) {
                                    case "config" -> {
                                        ConfigUtils.reload();
                                        sender.sendMessage(Message.reload.getTranslate().replace("{config}", "config.yml"));
                                    }
                                    case "gui" -> {
                                        GuiUtils.reload();
                                        sender.sendMessage(Message.reload.getTranslate().replace("{config}", "gui.yml"));
                                    }
                                    case "message" -> {
                                        Message.reload();
                                        sender.sendMessage(Message.reload.getTranslate().replace("{config}", "message.yml"));
                                    }
                                    case "permissions" -> {
                                        PermissionsUtils.reload();
                                        sender.sendMessage(Message.reload.getTranslate().replace("{config}", "permissions.yml"));
                                    }
                                    case "all" -> {
                                        ConfigUtils.reload();
                                        GuiUtils.reload();
                                        Message.reload();
                                        PermissionsUtils.reload();
                                        sender.sendMessage(Message.reload.getTranslate().replace("{config}", "all"));
                                    }
                                    default -> sender.sendMessage(Message.wrongSyntax.getTranslate());
                                }
                            }
                            case "blacklist" -> {
                                if (!(sender instanceof Player p)) {
                                    sender.sendMessage(Message.onlyPlayer.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res != null) {
                                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                        if (!player.hasPlayedBefore()) {
                                            p.sendMessage(Message.noPlayer.getTranslate());
                                        } else {
                                            if (res.isOwner(player.getUniqueId()) || res.isAdmin(player.getUniqueId())) {
                                                p.sendMessage(Message.userBlacklisted.getTranslate());
                                            } else {
                                                if (!res.isBlackListed(player.getUniqueId())) {
                                                    res.setBlackListed(player.getUniqueId());
                                                    p.sendMessage(Message.blacklist.getTranslate());
                                                } else {
                                                    res.setWhiteListed(player.getUniqueId());
                                                    p.sendMessage(Message.whitelist.getTranslate());
                                                }
                                            }
                                        }
                                    } else {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    }
                                }
                            }
                            case "give" -> {
                                if (!(sender instanceof Player p)) {
                                    sender.sendMessage(Message.onlyPlayer.getTranslate());
                                } else {
                                    Resident res = GeneralUtils.getResident(p.getLocation());
                                    if (res != null) {
                                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                        if (!player.hasPlayedBefore()) {
                                            p.sendMessage(Message.noPlayer.getTranslate());
                                        } else {
                                            if (!player.isOnline()) {
                                                p.sendMessage(Message.notOnline.getTranslate());
                                            } else {
                                                Player to = Bukkit.getPlayer(args[1]);
                                                ResidentUtils.give(res, to);
                                            }
                                        }
                                    } else {
                                        p.sendMessage(Message.noResident.getTranslate());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player p) {
            if (cmd.getName().equals("MagstarResidence")) {
                switch (args.length) {
                    case 1 -> {
                        List<String> str = new ArrayList<>(Arrays.asList("help", "list", "removeall", "tp", "rt"));
                        Resident res = GeneralUtils.getResident(p.getLocation());
                        if (res != null) {
                            if (PermissionHandlers.hasPermission(p, res, Permission.info)) {
                                str.add("info");
                            }
                            if (res.isAdmin(p.getUniqueId()) || res.isOwner(p.getUniqueId())) {
                                str.add("set");
                                str.add("pset");
                                str.add("blacklist");
                                str.add("tpset");
                            }
                            if (res.isOwner(p.getUniqueId())) {
                                str.add("expand");
                                str.add("give");
                                str.add("admin");
                                str.add("remove");
                            }
                        }
                        switch (args[0]) {
                            case "" -> {
                                return str;
                            }
                            case "h", "he", "hel" -> {
                                return Collections.singletonList("help");
                            }
                            case "l", "li", "lis" -> {
                                return Collections.singletonList("list");
                            }
                            case "r" -> {
                                if (str.contains("remove")) {
                                    return Arrays.asList("rt", "remove", "removeall");
                                }
                                else {
                                    return Arrays.asList("rt", "removeall");
                                }
                            }
                            case "re", "rem", "remo", "remov" -> {
                                if (str.contains("remove")) {
                                    return Arrays.asList("remove", "removeall");
                                } else {
                                    return Collections.singletonList("removeall");
                                }
                            }
                            case "remove", "removea", "removeal" -> {
                                return Collections.singletonList("removeall");
                            }
                            case "t" -> {
                                return str.contains("tpset") ? Arrays.asList("tp", "tpset") : Collections.singletonList("tp");
                            }
                            case "tp", "tps", "tpse" -> {
                                return str.contains("tpset") ? Collections.singletonList("tpset") : null;
                            }
                            case "i", "in", "inf" -> {
                                return str.contains("info") ? Collections.singletonList("info") : null;
                            }
                            case "s", "se" -> {
                                return str.contains("set") ? Collections.singletonList("set") : null;
                            }
                            case "p", "ps", "pse" -> {
                                return str.contains("pset") ? Collections.singletonList("pset") : null;
                            }
                            case "e", "ex", "exp", "expa", "expan" -> {
                                return str.contains("expand") ? Collections.singletonList("expand") : null;
                            }
                            case "g", "gi", "giv" -> {
                                return str.contains("give") ? Collections.singletonList("give") : null;
                            }
                            case "a", "ad", "adm", "admi" -> {
                                return str.contains("admin") ? Collections.singletonList("admin") : null;
                            }
                            case "b", "bl", "bla", "blac", "black", "blackl", "blackli", "blacklis" -> {
                                return str.contains("blacklist") ? Collections.singletonList("blacklist") : null;
                            }
                            default -> {
                                return null;
                            }
                        }
                    }
                    case 2 -> {
                        switch (args[0]) {
                            case "help" -> {
                                return Arrays.asList("1", "2");
                            }
                            case "pset", "blacklist" -> {
                                Resident res = GeneralUtils.getResident(p.getLocation());
                                if (res == null) {
                                    return null;
                                } else {
                                    if (res.isAdmin(p.getUniqueId()) || res.isOwner(p.getUniqueId())) {
                                        List<String> pls = new ArrayList<>();
                                        for (Player player : Bukkit.getOnlinePlayers()) {
                                            if (player.getName().toUpperCase().startsWith(args[1]) || player.getName().toLowerCase().startsWith(args[1])) {
                                                pls.add(player.getName());
                                            }
                                        }
                                        return pls;
                                    } else {
                                        return null;
                                    }
                                }
                            }
                            case "expand" -> {
                                return Collections.singletonList("格数");
                            }
                            case "tp" -> {
                                List<String> pls = new ArrayList<>();
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (RuntimeDataManager.getPlayerResidents(player.getUniqueId()).size() != 0 && (player.getName().toUpperCase().startsWith(args[1]) || player.getName().toLowerCase().startsWith(args[1]))) {
                                        pls.add(player.getName());
                                    }
                                }
                                return pls;
                            }
                            case "admin", "give" -> {
                                Resident res = GeneralUtils.getResident(p.getLocation());
                                if (res == null) {
                                    return null;
                                } else {
                                    if (res.isOwner(p.getUniqueId())) {
                                        List<String> pls = new ArrayList<>();
                                        for (Player player : Bukkit.getOnlinePlayers()) {
                                            if (player.getName().toUpperCase().startsWith(args[1]) || player.getName().toLowerCase().startsWith(args[1])) {
                                                pls.add(player.getName());
                                            }
                                        }
                                        return pls;
                                    } else {
                                        return null;
                                    }
                                }
                            }
                            default -> {
                                return null;
                            }
                        }
                    }
                    case 3 -> {
                        if (args[0].equals("tp")) {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player == null) {
                                return null;
                            } else {
                                if (RuntimeDataManager.getPlayerResidents(player.getUniqueId()).size() == 0) {
                                    return null;
                                } else {
                                    List<String> pls = new ArrayList<>();
                                    for (int i = 1; i <= RuntimeDataManager.getPlayerResidents(player.getUniqueId()).size(); i++) {
                                        pls.add(String.valueOf(i));
                                    }
                                    return pls;
                                }
                            }
                        } else {
                            return null;
                        }
                    }
                    default -> {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
