package top.magstar.residence.objects;

import org.bukkit.World;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ResidentFactory {
    private ResidentFactory() {}
    public static Resident buildResident(UUID owner, World world, Loc firstLocation, Loc secondLocation) {
        return new MagstarResident(owner, world, firstLocation, secondLocation);
    }
    public static Resident buildResident(UUID owner, Map<UUID, Map<Permission, Boolean>> permMap, List<UUID> admins, World world, Loc firstLocation, Loc secondLocation, Loc teleportLocation, List<UUID> blackList) {
        return new MagstarResident(owner, permMap, admins, world, firstLocation, secondLocation, teleportLocation, blackList);
    }
    public static Resident buildResident(UUID id, UUID owner, Map<UUID, Map<Permission, Boolean>> permMap, List<UUID> admins, World world, Loc firstLocation, Loc secondLocation, Loc teleportLocation, List<UUID> blackList) {
        return new MagstarResident(id, owner, permMap, admins, world, firstLocation, secondLocation, teleportLocation, blackList);
    }
    public static Resident buildResident(Resident res) {
        return new MagstarResident(res);
    }
}
