package top.magstar.residence.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import top.magstar.residence.Residence;
import top.magstar.residence.datamanager.runtime.PermissionSettingManager;
import top.magstar.residence.datamanager.runtime.ResidentCreateManager;
import top.magstar.residence.datamanager.runtime.RuntimeDataManager;
import top.magstar.residence.objects.InventoryData;
import top.magstar.residence.objects.Loc;
import top.magstar.residence.objects.Permission;
import top.magstar.residence.objects.Resident;
import top.magstar.residence.utils.GeneralUtils;
import top.magstar.residence.utils.ResidentUtils;
import top.magstar.residence.utils.fileutils.ConfigUtils;
import top.magstar.residence.utils.fileutils.GuiUtils;
import top.magstar.residence.utils.fileutils.Message;
import top.magstar.residence.utils.fileutils.PermissionsUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class EventHandlers implements Listener {
    private static final UUID defUniqueId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        Player p = e.getPlayer();
        FileConfiguration cfg = ConfigUtils.getConfig();
        Material mat = Material.getMaterial(Objects.requireNonNull(cfg.getString("create_tools")).toUpperCase());
        if (mat == null) {
            throw new RuntimeException("create tool material not found!");
        }
        if (p.getInventory().getItemInMainHand().getType().equals(mat)) {
            if (b != null && GeneralUtils.getResident(b.getLocation()) == null) {
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (p.getInventory().getItemInMainHand().getType().equals(mat) && e.getHand() == EquipmentSlot.HAND) {
                        if (!ResidentCreateManager.containsPlayer(p)) {
                            ResidentCreateManager.addPlayer(p, false);
                            ResidentCreateManager.addPlayer(p, null, new Loc(b.getX(), b.getY(), b.getZ()));
                            p.sendMessage(Message.position.getTranslate()
                                    .replace("{order}", "2")
                                    .replace("{location_x}", String.valueOf(b.getX()))
                                    .replace("{location_y}", String.valueOf(b.getY()))
                                    .replace("{location_z}", String.valueOf(b.getZ())));
                        } else {
                            if (!ResidentCreateManager.isFirstBreak(p)) {
                                p.sendMessage(Message.position.getTranslate()
                                        .replace("{order}", "2")
                                        .replace("{location_x}", String.valueOf(b.getX()))
                                        .replace("{location_y}", String.valueOf(b.getY()))
                                        .replace("{location_z}", String.valueOf(b.getZ())));
                                ResidentCreateManager.setSecond(p, b.getLocation());
                            } else {
                                ResidentCreateManager.setSecond(p, b.getLocation());
                                if (!Objects.requireNonNull(ResidentCreateManager.getFirst(p)).getWorld().getName().equals(Objects.requireNonNull(ResidentCreateManager.getSecond(p)).getWorld().getName())) {
                                    p.sendMessage(Message.changeWorld.getTranslate());
                                    ResidentCreateManager.removePlayer(p);
                                    ResidentCreateManager.removeLocation(p);
                                } else {
                                    p.sendMessage(Message.position.getTranslate()
                                            .replace("{order}", "2")
                                            .replace("{location_x}", String.valueOf(b.getX()))
                                            .replace("{location_y}", String.valueOf(b.getY()))
                                            .replace("{location_z}", String.valueOf(b.getZ())));
                                    p.sendMessage(Message.resident.getTranslate()
                                            .replace("{first_location_x}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getFirst(p)).getX()))
                                            .replace("{first_location_y}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getFirst(p)).getY()))
                                            .replace("{first_location_z}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getFirst(p)).getZ()))
                                            .replace("{second_location_x}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getSecond(p)).getX()))
                                            .replace("{second_location_y}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getSecond(p)).getY()))
                                            .replace("{second_location_z}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getSecond(p)).getZ())));
                                    if (!ResidentUtils.isResidentOverlap(new Loc(ResidentCreateManager.getFirst(p)), new Loc(ResidentCreateManager.getSecond(p)), ResidentCreateManager.getFirst(p).getWorld())) {
                                        Conversation conv = new ConversationFactory(Residence.getInstance())
                                                .withFirstPrompt(new ConversationHandlers.ResidentCreation())
                                                .withTimeout(60)
                                                .buildConversation(p);
                                        conv.begin();
                                    } else {
                                        p.sendMessage(Message.residentOverlap.getTranslate());
                                    }
                                }
                            }
                        }
                        e.setCancelled(true);
                    }
                } else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (p.getInventory().getItemInMainHand().getType().equals(mat)) {
                        if (!ResidentCreateManager.containsPlayer(p)) {
                            ResidentCreateManager.addPlayer(p, true);
                            ResidentCreateManager.addPlayer(p, new Loc(b.getX(), b.getY(), b.getZ()), null);
                            p.sendMessage((Message.position.getTranslate()
                                    .replace("{order}", "1")
                                    .replace("{location_x}", String.valueOf(b.getX()))
                                    .replace("{location_y}", String.valueOf(b.getY()))
                                    .replace("{location_z}", String.valueOf(b.getZ()))));
                        } else {
                            if (ResidentCreateManager.isFirstBreak(p)) {
                                p.sendMessage(Message.position.getTranslate()
                                        .replace("{order}", "1")
                                        .replace("{location_x}", String.valueOf(b.getX()))
                                        .replace("{location_y}", String.valueOf(b.getY()))
                                        .replace("{location_z}", String.valueOf(b.getZ())));
                                ResidentCreateManager.setFirst(p, b.getLocation());
                            } else {
                                ResidentCreateManager.setFirst(p, b.getLocation());
                                if (!Objects.requireNonNull(ResidentCreateManager.getFirst(p)).getWorld().getName().equals(Objects.requireNonNull(ResidentCreateManager.getSecond(p)).getWorld().getName())) {
                                    p.sendMessage(Message.changeWorld.getTranslate());
                                    ResidentCreateManager.removePlayer(p);
                                    ResidentCreateManager.removeLocation(p);
                                } else {
                                    p.sendMessage(Message.position.getTranslate()
                                            .replace("{order}", "1")
                                            .replace("{location_x}", String.valueOf(b.getX()))
                                            .replace("{location_y}", String.valueOf(b.getY()))
                                            .replace("{location_z}", String.valueOf(b.getZ())));
                                    p.sendMessage(Message.resident.getTranslate()
                                            .replace("{first_location_x}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getFirst(p)).getX()))
                                            .replace("{first_location_y}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getFirst(p)).getY()))
                                            .replace("{first_location_z}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getFirst(p)).getZ()))
                                            .replace("{second_location_x}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getSecond(p)).getX()))
                                            .replace("{second_location_y}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getSecond(p)).getY()))
                                            .replace("{second_location_z}", String.valueOf(Objects.requireNonNull(ResidentCreateManager.getSecond(p)).getZ())));
                                    if (!ResidentUtils.isResidentOverlap(new Loc(ResidentCreateManager.getFirst(p)), new Loc(ResidentCreateManager.getSecond(p)), ResidentCreateManager.getFirst(p).getWorld())) {
                                        Conversation conv = new ConversationFactory(Residence.getInstance())
                                                .withFirstPrompt(new ConversationHandlers.ResidentCreation())
                                                .withTimeout(60)
                                                .buildConversation(p);
                                        conv.begin();
                                    } else {
                                        p.sendMessage(Message.residentOverlap.getTranslate());
                                    }
                                }
                            }
                        }
                        e.setCancelled(true);
                    }
                }
            } else {
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (e.getHand() == EquipmentSlot.HAND) {
                        p.sendMessage(Message.alreadyIs.getTranslate());
                    }
                    e.setCancelled(true);
                } else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    p.sendMessage(Message.alreadyIs.getTranslate());
                    e.setCancelled(true);
                }
            }
        }
    }
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location to = e.getTo();
        Location from = e.getFrom();
        if (GeneralUtils.getResident(to) != null && GeneralUtils.getResident(from) == null) {
            Resident resident = GeneralUtils.getResident(to);
            assert resident != null;
            e.setCancelled(true);
            if (!resident.isBlackListed(p.getUniqueId())) {
                if (resident.getPermission(defUniqueId, Permission.move)) {
                    if (Objects.equals(ConfigUtils.getConfig().getString("notice"), "actionbar")) {
                        p.sendActionBar(Objects.requireNonNull(Message.getConfig().getString("notice_in"))
                                .replace("&", "§")
                                .replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(resident.getOwner()).getName()))
                                .replace("{player}", p.getName()));
                    } else if (Objects.equals(ConfigUtils.getConfig().getString("notice"), "message")) {
                        p.sendMessage(Message.noticeIn.getTranslate()
                                .replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(resident.getOwner()).getName()))
                                .replace("{player}", p.getName()));
                    }
                    e.setCancelled(false);
                } else {
                    if (!PermissionHandlers.hasPermission(p, resident, Permission.move)) {
                        p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", "move"));
                    } else {
                        if (Objects.equals(ConfigUtils.getConfig().getString("notice"), "actionbar")) {
                            p.sendActionBar(Objects.requireNonNull(Message.getConfig().getString("notice_in"))
                                    .replace("&", "§")
                                    .replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(resident.getOwner()).getName()))
                                    .replace("{player}", p.getName()));
                        } else if (Objects.equals(ConfigUtils.getConfig().getString("notice"), "message")) {
                            p.sendMessage(Message.noticeIn.getTranslate()
                                    .replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(resident.getOwner()).getName()))
                                    .replace("{player}", p.getName()));
                        }
                        e.setCancelled(false);
                    }
                }
            } else {
                p.sendMessage(Message.blacklisted.getTranslate());
            }
        }
        if (GeneralUtils.getResident(from) != null && GeneralUtils.getResident(to) == null) {
            Resident resident = GeneralUtils.getResident(from);
            assert resident != null;
            if (Objects.equals(ConfigUtils.getConfig().getString("notice"), "actionbar")) {
                p.sendActionBar(Objects.requireNonNull(Message.getConfig().getString("notice_leave"))
                        .replace("&", "§")
                        .replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(resident.getOwner()).getName()))
                        .replace("{player}", p.getName()));
            } else if (Objects.equals(ConfigUtils.getConfig().getString("notice"), "message")) {
                p.sendMessage(Message.noticeLeave.getTranslate()
                        .replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(resident.getOwner()).getName()))
                        .replace("{player}", p.getName()));
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (PermissionSettingManager.containsPlayer(p)) {
            int slot = e.getRawSlot();
            List<Integer> sideList = GuiUtils.getSideList();
            if (!sideList.contains(slot) && slot <= 44) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
                    Permission perm = PermissionsUtils.getPermissionByMaterial(e.getCurrentItem().getType());
                    InventoryData data = PermissionSettingManager.getInventoryData(p);
                    if (data == null) {
                        throw new RuntimeException("InventoryData cannot be null!");
                    }
                    if (data.isPlayerMode()) {
                        UUID setter = data.getSetter();
                        if (data.getResident().getPermission(setter, perm)) {
                            Objects.requireNonNull(RuntimeDataManager.getResident(data.getResident().getUniqueId())).setPermission(setter, perm, false);
                            ItemMeta meta = e.getCurrentItem().getItemMeta();
                            meta.removeEnchant(Enchantment.DURABILITY);
                            e.getCurrentItem().setItemMeta(meta);
                            p.updateInventory();
                        } else {
                            Objects.requireNonNull(RuntimeDataManager.getResident(data.getResident().getUniqueId())).setPermission(setter, perm, true);
                            ItemMeta meta = e.getCurrentItem().getItemMeta();
                            meta.addEnchant(Enchantment.DURABILITY, 1, false);
                            e.getCurrentItem().setItemMeta(meta);
                            p.updateInventory();
                        }
                    } else {
                        if (data.getResident().getPermission(defUniqueId, perm)) {
                            Objects.requireNonNull(RuntimeDataManager.getResident(data.getResident().getUniqueId())).setPermission(defUniqueId, perm, false);
                            ItemMeta meta = e.getCurrentItem().getItemMeta();
                            meta.removeEnchant(Enchantment.DURABILITY);
                            e.getCurrentItem().setItemMeta(meta);
                            p.updateInventory();
                        } else {
                            Objects.requireNonNull(RuntimeDataManager.getResident(data.getResident().getUniqueId())).setPermission(defUniqueId, perm, true);
                            ItemMeta meta = e.getCurrentItem().getItemMeta();
                            meta.addEnchant(Enchantment.DURABILITY, 1, false);
                            e.getCurrentItem().setItemMeta(meta);
                            p.updateInventory();
                        }
                    }
                }
            }
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (PermissionSettingManager.containsPlayer(p)) {
            PermissionSettingManager.removePlayer(p);
        }
    }
}
