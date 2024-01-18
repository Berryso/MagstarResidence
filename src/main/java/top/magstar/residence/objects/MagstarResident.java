package top.magstar.residence.objects;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import top.magstar.residence.utils.GeneralUtils;
import xyz.magstar.lib.objects.MagstarSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
class MagstarResident implements Resident, MagstarSerializable {

    private UUID uniqueId;
    private UUID owner;
    private Map<UUID, Map<Permission, Boolean>> permMap;
    private List<UUID> admins;
    private String world;
    private Loc firstLocation;
    private Loc secondLocation;
    private Loc teleportLocation;
    List<UUID> blackList;
    public MagstarResident() {}
    public MagstarResident(UUID owner, World world, Loc firstLocation, Loc secondLocation) {
        this.owner = owner;
        this.world = world.getName();
        this.firstLocation = firstLocation;
        this.secondLocation = secondLocation;
        this.teleportLocation = GeneralUtils.generateDefaultTeleportLocation(firstLocation, secondLocation, world);
        this.admins = new ArrayList<>();
        this.permMap = new HashMap<>();
        permMap.put(UUID.fromString("00000000-0000-0000-0000-000000000000"), new HashMap<>());
        this.uniqueId = GeneralUtils.generateUniqueId();
        this.blackList = new ArrayList<>();
    }
    public MagstarResident(UUID owner, Map<UUID, Map<Permission, Boolean>> permMap, List<UUID> admins, World world, Loc firstLocation, Loc secondLocation, Loc teleportLocation, List<UUID> blackList) {
        this.owner = owner;
        this.world = world.getName();
        this.firstLocation = firstLocation;
        this.secondLocation = secondLocation;
        this.teleportLocation = teleportLocation;
        this.admins = admins;
        this.permMap = permMap;
        this.uniqueId = GeneralUtils.generateUniqueId();
        this.blackList = blackList;
    }
    public MagstarResident(UUID id, UUID owner, Map<UUID, Map<Permission, Boolean>> permMap, List<UUID> admins, World world, Loc firstLocation, Loc secondLocation, Loc teleportLocation, List<UUID> blackList) {
        this.owner = owner;
        this.world = world.getName();
        this.firstLocation = firstLocation;
        this.secondLocation = secondLocation;
        this.teleportLocation = teleportLocation;
        this.admins = admins;
        this.permMap = permMap;
        this.uniqueId = id;
        this.blackList = blackList;
    }
    public MagstarResident(Resident res) {
        this.owner = res.getOwner();
        this.world = res.getWorld().getName();
        this.firstLocation = res.getFirstLocation();
        this.secondLocation = res.getSecondLocation();
        this.teleportLocation = res.getTeleportLocation();
        this.admins = res.getAdmins();
        this.permMap = res.getPermMap();
        this.uniqueId = res.getUniqueId();
        this.blackList = res.getBlacklist();
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    @Nonnull
    public UUID getOwner() {
        return owner;
    }

    @Override
    @Nonnull
    public World getWorld() {
        return Objects.requireNonNull(Bukkit.getWorld(world));
    }

    @Override
    @Nonnull
    public Loc getFirstLocation() {
        return firstLocation;
    }

    @Override
    @Nonnull
    public Loc getSecondLocation() {
        return secondLocation;
    }

    @Override
    public Loc getTeleportLocation() {
        return teleportLocation;
    }

    @Override
    public void setFirstLocation(Loc firstLocation) {
        this.firstLocation = firstLocation;
    }

    @Override
    public void setSecondLocation(Loc secondLocation) {
        this.secondLocation = secondLocation;
    }

    @Override
    public void setTeleportLocation(Loc loc) {
        this.teleportLocation = loc;
    }

    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    @Override
    @Nullable
    public Map<Permission, Boolean> getPermissions(UUID uuid) {
        return permMap.get(uuid);
    }

    @Override
    public boolean getPermission(UUID uuid, Permission permission) {
        if (permMap.get(uuid) != null) {
            if (permMap.get(uuid).get(permission) == null) {
                return false;
            }
            return permMap.get(uuid).get(permission);
        } else {
            return false;
        }
    }

    @Override
    public List<UUID> getAdmins() {
        return admins;
    }

    @Override
    public boolean isAdmin(UUID uuid) {
        return admins.contains(uuid);
    }

    @Override
    public void setAdmin(UUID uuid) {
        if (admins.contains(uuid)) {
            admins.remove(uuid);
        } else {
            admins.add(uuid);
        }
    }

    @Override
    public boolean isOwner(UUID uuid) {
        return uuid.equals(owner);
    }

    @Override
    public void setPermission(UUID uuid, Permission permission, boolean b) {
        if (permMap.get(uuid) != null) {
            permMap.get(uuid).put(permission, b);
        } else {
            Map<Permission, Boolean> map = new HashMap<>();
            map.put(permission, b);
            permMap.put(uuid, map);
        }
    }

    @Override
    public List<UUID> getBlacklist() {
        return blackList;
    }

    @Override
    public boolean isBlackListed(UUID uuid) {
        return blackList.contains(uuid);
    }

    @Override
    public void setBlackListed(UUID uuid) {
        blackList.add(uuid);
    }

    @Override
    public void setWhiteListed(UUID uuid) {
        blackList.remove(uuid);
    }

    @Override
    public Map<UUID, Map<Permission, Boolean>> getPermMap() {
        return permMap;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uniqueId.toString());
        map.put("owner", owner.toString());
        List<String> admin = new ArrayList<>();
        for (UUID uuid : admins) {
            admin.add(uuid.toString());
        }
        map.put("admins", admin);
        map.put("perm", permMap);
        map.put("world", world);
        map.put("first", firstLocation.serialize());
        map.put("second", secondLocation.serialize());
        map.put("blacklist", blackList);
        return map;
    }

    @Override
    @Nonnull
    public byte[] serializeAsBytes() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(stream);
            output.writeObject(this);
            output.flush();
            output.close();
            return stream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
