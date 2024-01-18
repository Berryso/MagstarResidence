package top.magstar.residence.objects;

import java.io.Serializable;

public enum Permission implements Serializable {

    move,
    breek {
        @Override
        public String toString() {
            return "break";
        }
    },
    place,
    use,
    container,
    bucket,
    hopper,
    explode,
    water,
    lava,
    burn,
    grow,
    spread,
    bonemeal,
    interact,
    pick,
    attack,
    pvp,
    fish,
    threw {
        @Override
        public String toString() {
            return "throw";
        }
    },
    info,
    fade,
    fall,
    piston,
    form,
    tp,
    shop;

    public static Permission getPermission(String s) {
        for (Permission perm : Permission.values()) {
            if (perm.toString().equals(s)) {
                return perm;
            }
        }
        return null;
    }
}
