package top.magstar.residence.handlers;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import top.magstar.residence.objects.Permission;
import top.magstar.residence.objects.Resident;
import top.magstar.residence.utils.GeneralUtils;
import top.magstar.residence.utils.fileutils.ConfigUtils;
import top.magstar.residence.utils.fileutils.Message;
import top.magstar.shop.objects.PlayerOpenShopEvent;

import java.util.*;

public final class PermissionHandlers implements Listener {
    private final static List<Material> USE_LIST = Arrays.asList(
            Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.MANGROVE_DOOR, Material.CRIMSON_DOOR, Material.WARPED_DOOR, //Doors
            Material.OAK_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.MANGROVE_FENCE_GATE, Material.CRIMSON_FENCE_GATE, Material.WARPED_FENCE_GATE, //Fence gates
            Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.MANGROVE_TRAPDOOR, Material.CRIMSON_TRAPDOOR, Material.WARPED_TRAPDOOR, //Trapdoors
            Material.WHITE_BED, Material.LIGHT_GRAY_BED, Material.GRAY_BED, Material.BLACK_BED, Material.BROWN_BED, Material.RED_BED, Material.ORANGE_BED, Material.YELLOW_BED, Material.LIME_BED, Material.GREEN_BED, Material.CYAN_BED, Material.LIGHT_BLUE_BED, Material.BLUE_BED, Material.PURPLE_BED, Material.MAGENTA_BED, Material.PINK_BED, //Beds
            Material.WHITE_CANDLE, Material.LIGHT_GRAY_CANDLE, Material.GRAY_CANDLE, Material.BLACK_CANDLE, Material.BROWN_CANDLE, Material.RED_CANDLE, Material.ORANGE_CANDLE, Material.YELLOW_CANDLE, Material.LIME_CANDLE, Material.GREEN_CANDLE, Material.CYAN_CANDLE, Material.LIGHT_BLUE_CANDLE, Material.BLUE_CANDLE, Material.PURPLE_CANDLE, Material.MAGENTA_CANDLE, Material.PINK_CANDLE, //Candles
            Material.CRAFTING_TABLE, Material.STONECUTTER, Material.CARTOGRAPHY_TABLE, Material.FLETCHING_TABLE, Material.SMITHING_TABLE, Material.GRINDSTONE, Material.LOOM, Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL, Material.COMPOSTER, Material.NOTE_BLOCK, Material.JUKEBOX, Material.CAULDRON, Material.LECTERN, Material.BEACON, Material.BEE_NEST, Material.BEEHIVE,  Material.FLOWER_POT, ///Work stations
            Material.REPEATER, Material.COMPARATOR, Material.LEVER, Material.OAK_BUTTON, Material.STONE_BUTTON, Material.BELL, Material.DAYLIGHT_DETECTOR, Material.POLISHED_BLACKSTONE_BUTTON, Material.REDSTONE_ORE//About redstone
    );
    private final static List<Material> CONTAINER_LIST = Arrays.asList(
            Material.SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.BLACK_SHULKER_BOX, Material.BROWN_SHULKER_BOX,
            Material.RED_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.CYAN_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.PINK_SHULKER_BOX,
            Material.FURNACE, Material.SMOKER, Material.BLAST_FURNACE, Material.BREWING_STAND, Material.CHEST, Material.ENDER_CHEST, Material.DISPENSER, Material.DROPPER,
            Material.HOPPER, Material.BARREL, Material.TRAPPED_CHEST
    );
    private final static List<Material> PHYSICAL_LIST = Arrays.asList(
            Material.OAK_PRESSURE_PLATE, Material.SPRUCE_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE, Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.MANGROVE_PRESSURE_PLATE, Material.CRIMSON_PRESSURE_PLATE, Material.WARPED_PRESSURE_PLATE,
            Material.STONE_PRESSURE_PLATE, Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE
    );
    private final static List<Material> CROPS = Arrays.asList(
            Material.ACACIA_SAPLING, Material.BIRCH_SAPLING, Material.DARK_OAK_SAPLING, Material.JUNGLE_SAPLING, Material.OAK_SAPLING, Material.SPRUCE_SAPLING, Material.GRASS_BLOCK, Material.FERN, Material.AZALEA, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS, Material.NETHER_SPROUTS,
            Material.WARPED_NYLIUM, Material.CRIMSON_NYLIUM, Material.BEETROOT
    );
    private final static List<Material> FALLABLE_LIST = Arrays.asList(
            Material.SAND, Material.RED_SAND, Material.POINTED_DRIPSTONE, Material.ANVIL, Material.DAMAGED_ANVIL, Material.CHIPPED_ANVIL, Material.GRAVEL
    );
    private final static UUID defUniqueId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static boolean hasPermission(Player p, Resident res, Permission perm) {
        return res.isOwner(p.getUniqueId()) || res.isAdmin(p.getUniqueId()) || res.getPermission(p.getUniqueId(), perm) || res.getPermission(defUniqueId, perm);
    }

    private static void executePermission(Cancellable e, Resident res, Permission perm, Player p) {
        e.setCancelled(true);
        if (!res.isBlackListed(p.getUniqueId())) {
            if (res.getPermission(defUniqueId, perm)) {
                e.setCancelled(false);
            } else {
                if (!hasPermission(p, res, perm)) {
                    p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", perm.toString()));
                } else {
                    e.setCancelled(false);
                }
            }
        } else {
            p.sendMessage(Message.blacklisted.getTranslate());
        }
    }
    public static String getPlayerGroup(Player p) {
        Set<String> groups = Objects.requireNonNull(ConfigUtils.getConfig().getConfigurationSection("buy_price")).getKeys(false);
        for (String s : groups) {
            if (p.hasPermission("group." + s)) {
                return s;
            }
        }
        return "default";
    }
    /*public static String getPlayerGroup(OfflinePlayer p) {
    }*/

    @EventHandler
    public void onPlayerDig(BlockBreakEvent e) {
        if (GeneralUtils.getResident(e.getBlock().getLocation()) != null) {
            Resident res = GeneralUtils.getResident(e.getBlock().getLocation());
            assert res != null;
            executePermission(e, res, Permission.breek, e.getPlayer());
        }
    }
    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
        if (GeneralUtils.getResident(e.getBlock().getLocation()) != null) {
            Resident res = GeneralUtils.getResident(e.getBlock().getLocation());
            assert res != null;
            executePermission(e, res, Permission.place, e.getPlayer());
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (GeneralUtils.getResident(e.getFrom()) != null && GeneralUtils.getResident(e.getTo()) != null) {
            Resident res1 = GeneralUtils.getResident(e.getFrom());
            Resident res2 = GeneralUtils.getResident(e.getTo());
            assert res1 != null;
            assert res2 != null;
            if (res1.getUniqueId().toString().equals(res2.getUniqueId().toString())) {
                executePermission(e, res1, Permission.move, e.getPlayer());
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = e.getClickedBlock();
            assert b != null;
            if (GeneralUtils.getResident(b.getLocation()) != null && e.getHand() == EquipmentSlot.HAND) {
                Player p = e.getPlayer();
                Resident res = GeneralUtils.getResident(b.getLocation());
                assert res != null;
                if (USE_LIST.contains(b.getType())) {
                    executePermission(e, res, Permission.use, e.getPlayer());
                }
                if (CONTAINER_LIST.contains(b.getType())) {
                    executePermission(e, res, Permission.container, p);
                }
            }
            if (GeneralUtils.getResident(b.getLocation()) != null) {
                if (e.getItem() != null && e.getItem().getType() == Material.BONE_MEAL) {
                    Player p = e.getPlayer();
                    Resident res = GeneralUtils.getResident(b.getLocation());
                    assert res != null;
                    if (CROPS.contains(e.getClickedBlock().getType())) {
                        executePermission(e, res, Permission.bonemeal, p);
                    }
                }
            }
        }
        if (e.getAction() == Action.PHYSICAL) {
            Block b = e.getClickedBlock();
            assert b != null;
            if (GeneralUtils.getResident(b.getLocation()) != null) {
                Resident res = GeneralUtils.getResident(b.getLocation());
                Player p = e.getPlayer();
                assert res != null;
                if (PHYSICAL_LIST.contains(b.getType())) {
                    executePermission(e, res, Permission.use, p);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerFillBucket(PlayerBucketFillEvent e) {
        if (GeneralUtils.getResident(e.getBlock().getLocation()) != null) {
            Resident res = GeneralUtils.getResident(e.getBlock().getLocation());
            Player p = e.getPlayer();
            assert res != null;
            executePermission(e, res, Permission.bucket, p);
        }
    }
    @EventHandler
    public void onPlayerEmptyBucket(PlayerBucketEmptyEvent e) {
        if (GeneralUtils.getResident(e.getBlock().getLocation()) != null) {
            Resident res = GeneralUtils.getResident(e.getBlock().getLocation());
            Player p = e.getPlayer();
            assert res != null;
            executePermission(e, res, Permission.bucket, p);
        }
    }
    @EventHandler
    public void onPlayerBucketEntity(PlayerBucketEntityEvent e) {
        if (GeneralUtils.getResident(e.getEntity().getLocation()) != null) {
            Resident res = GeneralUtils.getResident(e.getEntity().getLocation());
            Player p = e.getPlayer();
            assert res != null;
            executePermission(e, res, Permission.bucket, p);
        }
    }
    @EventHandler
    public void onHopperPickUpItem(InventoryPickupItemEvent e) {
        Item item = e.getItem();
        if (GeneralUtils.getResident(item.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(item.getLocation());
            assert res != null;
            if (GeneralUtils.getResident(e.getInventory().getLocation()) == null) {
                if (!res.getPermission(defUniqueId, Permission.hopper)) {
                    e.setCancelled(true);
                }
            } else {
                Resident res2 = GeneralUtils.getResident(e.getInventory().getLocation());
                assert res2 != null;
                if (!res.getUniqueId().toString().equals(res2.getUniqueId().toString())) {
                    if (!res.getPermission(defUniqueId, Permission.hopper)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent e) {
        Resident res1 = GeneralUtils.getResident(e.getInitiator().getLocation());
        Resident res2 = GeneralUtils.getResident(e.getSource().getLocation());
        if (res2 != null) {
            if (res1 == null) {
                if (!res2.getPermission(defUniqueId, Permission.hopper)) {
                    e.setCancelled(true);
                }
            } else {
                if (!res1.getUniqueId().toString().equals(res2.getUniqueId().toString())) {
                    if (!res2.getPermission(defUniqueId, Permission.hopper)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        boolean proof = ConfigUtils.getConfig().getBoolean("explosion_proof");
        Iterator<Block> it = e.blockList().iterator();
        while(it.hasNext()) {
            Block b = it.next();
            if (GeneralUtils.getResident(b.getLocation()) != null) {
                Resident res = GeneralUtils.getResident(b.getLocation());
                assert res != null;
                if (!res.getPermission(defUniqueId, Permission.explode)) {
                    it.remove();
                }
            } else {
                if (proof) {
                    it.remove();
                }
            }
        }
    }
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        boolean proof = ConfigUtils.getConfig().getBoolean("explosion_proof");
        Iterator<Block> it = e.blockList().iterator();
        while(it.hasNext()) {
            Block b = it.next();
            if (GeneralUtils.getResident(b.getLocation()) != null) {
                Resident res = GeneralUtils.getResident(b.getLocation());
                assert res != null;
                if (!res.getPermission(defUniqueId, Permission.explode)) {
                    it.remove();
                }
            } else {
                if (proof) {
                    it.remove();
                }
            }
        }
    }
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        Block b = e.getBlock();
        if (b.getType() == Material.WATER) {
            if (GeneralUtils.getResident(b.getLocation()) != null) {
                Resident res = GeneralUtils.getResident(b.getLocation());
                assert res != null;
                if (!res.getPermission(defUniqueId, Permission.water)) {
                    e.setCancelled(true);
                }
            }
        }
        if (b.getType() == Material.LAVA) {
            if (GeneralUtils.getResident(b.getLocation()) != null) {
                Resident res = GeneralUtils.getResident(b.getLocation());
                assert res != null;
                if (!res.getPermission(defUniqueId, Permission.lava)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onBlockGrow(BlockGrowEvent e) {
        Block b = e.getBlock();
        if (GeneralUtils.getResident(b.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(b.getLocation());
            assert res != null;
            if (!res.getPermission(defUniqueId, Permission.grow) && CROPS.contains(b.getType())) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        Block b = e.getBlock();
        if (GeneralUtils.getResident(b.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(b.getLocation());
            assert res != null;
            if (!res.getPermission(defUniqueId, Permission.spread)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBlockFertilize(BlockFertilizeEvent e) {
        Block b = e.getBlock();
        if (GeneralUtils.getResident(b.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(b.getLocation());
            assert res != null;
            if (!res.getPermission(defUniqueId, Permission.bonemeal)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (GeneralUtils.getResident(e.getRightClicked().getLocation()) != null) {
            Resident res = GeneralUtils.getResident(e.getRightClicked().getLocation());
            assert res != null;
            executePermission(e, res, Permission.interact, p);
        }
    }
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (GeneralUtils.getResident(e.getItem().getLocation()) != null) {
                Resident res = GeneralUtils.getResident(e.getItem().getLocation());
                assert res != null;
                executePermission(e, res, Permission.pick, p);
            }
        }
    }
    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent e) {
        Player p = e.getPlayer();
        if (GeneralUtils.getResident(e.getItem().getLocation()) != null) {
            Resident res = GeneralUtils.getResident(e.getItem().getLocation());
            assert res != null;
            executePermission(e, res, Permission.pick, p);
        }
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if (GeneralUtils.getResident(entity.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(entity.getLocation());
            assert res != null;
            if (damager instanceof Player p) {
                if (entity instanceof Player) {
                    if (!res.getPermission(defUniqueId, Permission.pvp)) {
                        p.sendMessage(Message.residentDenied.getTranslate().replace("{permission}", "pvp"));
                        e.setCancelled(true);
                    }
                } else {
                    executePermission(e, res, Permission.attack, p);
                }
            } else {
                if (!res.getPermission(defUniqueId, Permission.attack)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerPickupExperience(PlayerPickupExperienceEvent e) {
        Player p = e.getPlayer();
        if (GeneralUtils.getResident(e.getExperienceOrb().getLocation()) != null) {
            Resident res = GeneralUtils.getResident(e.getExperienceOrb().getLocation());
            assert res != null;
            executePermission(e, res, Permission.pick, p);
        }
    }
    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        Player p = e.getPlayer();
        if (GeneralUtils.getResident(e.getHook().getLocation()) != null) {
            Resident res = GeneralUtils.getResident(e.getHook().getLocation());
            assert res != null;
            executePermission(e, res, Permission.fish, p);
        }
    }
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        Projectile proj = e.getEntity();
        if (GeneralUtils.getResident(proj.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(proj.getLocation());
            assert res != null;
            if (proj.getShooter() instanceof Entity shooter) {
                if (shooter instanceof Player p) {
                    executePermission(e, res, Permission.threw, p);
                } else {
                    if (!res.getPermission(defUniqueId, Permission.threw)) {
                        e.setCancelled(true);
                    }
                }
            } else {
                if (!res.getPermission(defUniqueId, Permission.threw)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        Block b = e.getIgnitingBlock();
        if (b != null && GeneralUtils.getResident(b.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(b.getLocation());
            assert res != null;
            if (!res.getPermission(defUniqueId, Permission.burn)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onEntityInteractBlock(EntityInteractEvent e) {
        Block b = e.getBlock();
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            Resident res = GeneralUtils.getResident(b.getLocation());
            if (res != null) {
                if (!res.getPermission(defUniqueId, Permission.interact)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        Block b = e.getHitBlock();
        if (b != null && GeneralUtils.getResident(b.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(b.getLocation());
            assert res != null;
            if (!res.getPermission(defUniqueId, Permission.interact)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if (e.getEntity() instanceof FallingBlock fb) {
            Resident res;
            if ((res = GeneralUtils.getResident(fb.getLocation())) != null && !res.getPermission(defUniqueId, Permission.fall)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        Block b = e.getBlock();
        if (GeneralUtils.getResident(b.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(b.getLocation());
            assert res != null;
            if (!res.getPermission(defUniqueId, Permission.fade)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        List<Block> blocks = e.getBlocks();
        Block piston = e.getBlock();
        if (GeneralUtils.getResident(piston.getLocation()) == null) {
            for (Block b : blocks) {
                Resident res = GeneralUtils.getResident(b.getLocation());
                if (res != null && !res.getPermission(defUniqueId, Permission.piston)) {
                    e.setCancelled(true);
                    break;
                }
                BlockFace face = e.getDirection();
                Location loc1;
                switch (face) {
                    case DOWN -> loc1 = new Location(b.getWorld(), b.getX(), b.getY() - 1, b.getZ());
                    case UP -> loc1 = new Location(b.getWorld(), b.getX(), b.getY() + 1, b.getZ());
                    case EAST -> loc1 = new Location(b.getWorld(), b.getX() + 1, b.getY(), b.getZ());
                    case WEST -> loc1 = new Location(b.getWorld(), b.getX() - 1, b.getY(), b.getZ());
                    case NORTH -> loc1 = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ() - 1);
                    case SOUTH -> loc1 = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ() + 1);
                    default -> loc1 = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
                }
                Resident res1 = GeneralUtils.getResident(loc1);
                if (res1 != null && !res1.getPermission(defUniqueId, Permission.piston)) {
                    e.setCancelled(true);
                    break;
                }
            }
        } else {
            Resident origin = GeneralUtils.getResident(piston.getLocation());
            assert origin != null;
            for (Block b : blocks) {
                Resident res = GeneralUtils.getResident(b.getLocation());
                if (res != null && !res.getUniqueId().toString().equals(origin.getUniqueId().toString()) && !res.getPermission(defUniqueId, Permission.piston)) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e) {
        List<Block> blocks = e.getBlocks();
        Block piston = e.getBlock();
        if (GeneralUtils.getResident(piston.getLocation()) == null) {
            for (Block b : blocks) {
                Resident res = GeneralUtils.getResident(b.getLocation());
                if (res != null && !res.getPermission(defUniqueId, Permission.piston)) {
                    e.setCancelled(true);
                    break;
                }
            }
        } else {
            Resident origin = GeneralUtils.getResident(piston.getLocation());
            assert origin != null;
            for (Block b : blocks) {
                Resident res = GeneralUtils.getResident(b.getLocation());
                if (res != null && !res.getUniqueId().toString().equals(origin.getUniqueId().toString()) && !res.getPermission(defUniqueId, Permission.piston)) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }
    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        Block block = e.getBlock();
        if (GeneralUtils.getResident(block.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(block.getLocation());
            assert res != null;
            if (!res.getPermission(defUniqueId, Permission.form)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onEntityBlockForm(EntityBlockFormEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof FallingBlock && GeneralUtils.getResident(entity.getLocation()) != null) {
            Resident res = GeneralUtils.getResident(entity.getLocation());
            assert res != null;
            if (!res.getPermission(defUniqueId, Permission.fall)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlayerOpenShop(PlayerOpenShopEvent e) {
        Player p = e.getPlayer();
        Resident res = GeneralUtils.getResident(e.getClickedBlock().getLocation());
        if (res != null) {
            executePermission(e, res, Permission.shop, p);
        }
    }
    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (e.getMessage().equals("call back speed")) {
            e.getPlayer().setWalkSpeed(0.2f);
        }
    }
}