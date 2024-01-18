package top.magstar.residence.objects;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import xyz.magstar.lib.objects.MagstarSerializable;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public final class Loc implements MagstarSerializable {
    private int x, y, z;
    public Loc(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Loc(Location loc) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public byte @NotNull [] serializeAsBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 3);
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.putInt(z);
        return buffer.array();
    }
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        return map;
    }
    public static Loc deserialize(Map<String, Object> map) {
        int x = (int) map.get("x");
        int y = (int) map.get("y");
        int z = (int) map.get("z");
        return new Loc(x, y, z);
    }
    public static Loc deserializeAsBytes(byte[] b) {
        ByteBuffer buffer = ByteBuffer.wrap(b);
        int x = buffer.getInt();
        int y = buffer.getInt();
        int z = buffer.getInt();
        return new Loc(x, y, z);
    }
    public static Location toLocation(World world, Loc loc) {
        return new Location(world, loc.getX(), loc.getY(), loc.getZ());
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Loc obj)) {
            return false;
        } else {
            return obj.getX() == this.x && obj.getY() == this.y && obj.getZ() == this.z;
        }
    }
    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }
    public static Loc toLoc(Location location) {
        return new Loc(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
