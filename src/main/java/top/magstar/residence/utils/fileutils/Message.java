package top.magstar.residence.utils.fileutils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import top.magstar.residence.Residence;

import java.io.File;

public enum Message {
    position,
    creating,
    resident,
    confirm,
    accepted,
    denied,
    notEnough {
        @Override
        public String toString() {
            return "not_enough";
        }
    },
    changeWorld {
        @Override
        public String toString() {
            return "change_world";
        }
    },
    alreadyIs {
        @Override
        public String toString() {
            return "already_is";
        }
    },
    noticeIn {
        @Override
        public String toString() {
            return "notice_in";
        }
    },
    noticeLeave {
        @Override
        public String toString() {
            return "notice_leave";
        }
    },
    residentDenied {
        @Override
        public String toString() {
            return "resident_denied";
        }
    },
    residentOverlap {
        @Override
        public String toString() {
            return "resident_overlap";
        }
    },
    expandTooHigh {
        @Override
        public String toString() {
            return "expand_too_high";
        }
    },
    expandTooLow {
        @Override
        public String toString() {
            return "expand_too_low";
        }
    },
    noResident {
        @Override
        public String toString() {
            return "no_resident";
        }
    },
    notOwner {
        @Override
        public String toString() {
            return "not_owner";
        }
    },
    expandConfirm {
        @Override
        public String toString() {
            return "expand_confirm";
        }
    },
    expandAccepted {
        @Override
        public String toString() {
            return "expand_accepted";
        }
    },
    expandDenied {
        @Override
        public String toString() {
            return "expand_denied";
        }
    },
    onlyPlayer {
        @Override
        public String toString() {
            return "only_player";
        }
    },
    noPerm {
        @Override
        public String toString() {
            return "no_perm";
        }
    },
    blacklisted,
    wrongSyntax {
        @Override
        public String toString() {
            return "wrong_syntax";
        }
    },
    residentList {
        @Override
        public String toString() {
            return "resident_list";
        }
    },
    removeConfirm {
        @Override
        public String toString() {
            return "remove_confirm";
        }
    },
    removeAllConfirm {
        @Override
        public String toString() {
            return "removeall_confirm";
        }
    },
    removeAccepted {
        @Override
        public String toString() {
            return "remove_accepted";
        }
    },
    removeDenied {
        @Override
        public String toString() {
            return "remove_denied";
        }
    },
    unsafeTeleport {
        @Override
        public String toString() {
            return "unsafe_teleport";
        }
    },
    teleportSet {
        @Override
        public String toString() {
            return "teleport_set";
        }
    },
    notAllowed {
        @Override
        public String toString() {
            return "not_allowed";
        }
    },
    teleport,
    noPlayer {
        @Override
        public String toString() {
            return "no_player";
        }
    },
    userBlacklisted {
        @Override
        public String toString() {
            return "user_blacklisted";
        }
    },
    blacklist {
        @Override
        public String toString() {
            return "blacklist";
        }
    },
    whitelist {
        @Override
        public String toString() {
            return "whitelist";
        }
    },
    ownerSetAdmin {
        @Override
        public String toString() {
            return "owner_setadmin";
        }
    },
    blacklistedSetAdmin {
        @Override
        public String toString() {
            return "blacklisted_setadmin";
        }
    },
    adminSet {
        @Override
        public String toString() {
            return "admin_set";
        }
    },
    adminDisplace {
        @Override
        public String toString() {
            return "admin_displace";
        }
    },
    notOnline {
        @Override
        public String toString() {
            return "not_online";
        }
    },
    giveConfirm {
        @Override
        public String toString() {
            return "give_confirm";
        }
    },
    giveAccepted {
        @Override
        public String toString() {
            return "give_accepted";
        }
    },
    giveDenied {
        @Override
        public String toString() {
            return "give_denied";
        }
    },
    giveSuccess {
        @Override
        public String toString() {
            return "give_success";
        }
    },
    giveFailed {
        @Override
        public String toString() {
            return "give_failed";
        }
    },
    receiveConfirm {
        @Override
        public String toString() {
            return "receive_confirm";
        }
    },
    receiveCost {
        @Override
        public String toString() {
            return "receive_cost";
        }
    },
    receiveAccepted {
        @Override
        public String toString() {
            return "receive_accepted";
        }
    },
    receiveDenied {
        @Override
        public String toString() {
            return "receive_denied";
        }
    },
    maxSize {
        @Override
        public String toString() {
            return "max_size";
        }
    },
    sendNotEnough {
        @Override
        public String toString() {
            return "send_not_enough";
        }
    },
    residentChoose {
        @Override
        public String toString() {
            return "resident_choose";
        }
    },
    residentTeleport {
        @Override
        public String toString() {
            return "resident_teleport";
        }
    },
    residentUnsafe {
        @Override
        public String toString() {
            return "resident_unsafe";
        }
    },
    noTarget {
        @Override
        public String toString() {
            return "no_target";
        }
    },
    haveNoResidents {
        @Override
        public String toString() {
            return "have_no_residents";
        }
    },
    reload;
    private static File f = new File(Residence.getInstance().getDataFolder() + "/message.yml");
    public static void reload() {
        if (!f.exists()) {
            Residence.getInstance().saveResource("message.yml", false);
            f = new File(Residence.getInstance().getDataFolder() + "/message.yml");
        }
    }
    public static FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(f);
    }
    public String getTranslate() {
        return ConfigUtils.getConfig().getString("prefix") + getConfig().getString(this.toString()).replace("&", "§");
    }
}
