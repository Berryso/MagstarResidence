package top.magstar.residence.objects;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import xyz.magstar.lib.objects.MagstarSerializable;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

@SuppressWarnings("unchecked")
public interface Resident extends MagstarSerializable {
    UUID getUniqueId();
    UUID getOwner();
    World getWorld();
    Loc getFirstLocation();
    Loc getSecondLocation();
    Loc getTeleportLocation();
    void setFirstLocation(Loc firstLocation);
    void setSecondLocation(Loc secondLocation);
    void setTeleportLocation(Loc loc);
    void setOwner(UUID owner);
    Map<Permission, Boolean> getPermissions(UUID uuid);
    Map<UUID, Map<Permission, Boolean>> getPermMap();
    boolean getPermission(UUID uuid, Permission permission);
    List<UUID> getAdmins();
    boolean isAdmin(UUID uuid);
    void setAdmin(UUID uuid);
    boolean isOwner(UUID uuid);
    void setPermission(UUID uuid, Permission permission, boolean b);
    List<UUID> getBlacklist();
    boolean isBlackListed(UUID uuid);
    void setBlackListed(UUID uuid);
    void setWhiteListed(UUID uuid);
    static Resident deserialize(Map<String, Object> data) {
        List<UUID> admins = new ArrayList<>();
        for (String uuid : (List<String>) data.get("admins")) {
            admins.add(UUID.fromString(uuid));
        }
        UUID uuid = UUID.fromString((String) data.get("uuid"));
        UUID owner = UUID.fromString((String) data.get("owner"));
        Map<String, Object> map = ((MemorySection) data.get("perm")).getValues(true);
        Map<UUID, Map<Permission, Boolean>> permMap = new HashMap<>();
        for (String u : map.keySet()) {
            UUID id = UUID.fromString(u);
            Map<String, Boolean> perms = (Map<String, Boolean>) map.get(u);
            Map<Permission, Boolean> finalMap = new HashMap<>();
            for (String permission : perms.keySet()) {
                Permission perm = Permission.getPermission(permission);
                finalMap.put(perm, perms.get(permission));
            }
            permMap.put(id, finalMap);
        }
        World world = Bukkit.getWorld((String) data.get("world"));
        Map<String, Object> firstMap = ((MemorySection) data.get("first")).getValues(true);
        Loc first = new Loc((Integer) firstMap.get("x"), (Integer) firstMap.get("y"), (Integer) firstMap.get("z"));
        Map<String, Object> secondMap = ((MemorySection) data.get("second")).getValues(true);
        Loc second = new Loc((Integer) secondMap.get("x"), (Integer) secondMap.get("y"), (Integer) secondMap.get("z"));
        Map<String, Object> randomMap = ((MemorySection) data.get("random")).getValues(true);
        Loc random = new Loc((Integer) randomMap.get("x"), (Integer) randomMap.get("y"), (Integer) randomMap.get("z"));
        List<String> uuids = (List<String>) data.get("blacklist");
        List<UUID> blackList = new ArrayList<>();
        for (String s : uuids) {
            blackList.add(UUID.fromString(s));
        }
        if (world == null) {
            return null;
        } else {
            return ResidentFactory.buildResident(uuid, owner, permMap, admins, world, first, second, random, blackList);
        }
    }
    @Nonnull
    static Resident deserializeBytes(byte[] data) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(data);
            ObjectInputStream input = new ObjectInputStream(stream);
            MagstarResident res = (MagstarResident) input.readObject();
            input.close();
            return res;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new MagstarResident();
        }
    }
}
