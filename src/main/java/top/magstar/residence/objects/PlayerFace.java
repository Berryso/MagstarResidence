package top.magstar.residence.objects;

public enum PlayerFace {
    NORTH, SOUTH, WEST, EAST, UP, DOWN;
    public static PlayerFace getFace(float yaw, float pitch) {
        if (pitch >= -45 && pitch <= 45) {
            if (yaw > -45 && yaw <= 45) {
                return PlayerFace.SOUTH;
            }
            else if (yaw > 45 && yaw <= 135) {
                return PlayerFace.WEST;
            }
            else if ((yaw > 135 && yaw <= 180) || (yaw > -180 && yaw < -135)) {
                return PlayerFace.NORTH;
            }
            else if ((yaw >= -135 && yaw < -45) || yaw == -180) {
                return PlayerFace.EAST;
            }
            else {
                throw new IllegalArgumentException("Yaw can only be larger than -180 and smaller than 180!");
            }
        } else if (pitch > 45 && pitch <= 90) {
            return PlayerFace.DOWN;
        } else if (pitch < -45 && pitch >= -90) {
            return PlayerFace.UP;
        } else {
            throw new IllegalArgumentException("Pitch can only be larger than -90 and smaller than 90!");
        }
    }
}
