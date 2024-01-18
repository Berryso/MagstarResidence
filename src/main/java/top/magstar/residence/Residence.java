package top.magstar.residence;

import org.bukkit.Bukkit;
import top.magstar.residence.datamanager.statics.AbstractDataManager;
import top.magstar.residence.handlers.EventHandlers;
import top.magstar.residence.handlers.PermissionHandlers;
import top.magstar.residence.utils.GeneralUtils;
import xyz.magstar.lib.objects.MagstarPlugin;

import javax.annotation.Nonnull;

public class Residence extends MagstarPlugin {
    private static final int SERIAL_NUMBER = 7;
    private static final String USER_VERSION = "basic";
    private static final String UPDATED_VERSION = "1.0.0";
    private static Residence instance;
    public static boolean dbAvailable;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("=================================");
        getLogger().info(" ");
        getLogger().info("§b插件名称: §fMagStarResidence");
        getLogger().info("§b插件作者: §fBerry_so");
        getLogger().info("§b工作室: §f星星逹 Stars Creation");
        getLogger().info("§b插件版本: §fv " + getUpdatedVersion());
        getLogger().info(" ");
        getLogger().info("=================================");
        getLogger().info("          §a>>> 注册 <<<");
        instance = this;
        Bukkit.getPluginManager().registerEvents(new EventHandlers(), this);
        Bukkit.getPluginManager().registerEvents(new PermissionHandlers(), this);
        if (!GeneralUtils.isPrePluginLoaded()) {
            getLogger().warning("未安装前置插件。");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        else {
            getLogger().info("已经安装前置插件！正在检查Magstar账户……");
            if (GeneralUtils.isLoginSuccessful()) {
                getLogger().info("检查成功！插件已经开始加载，正在初始化……");
                GeneralUtils.init();
            } else {
                getLogger().warning("检查失败！请查看你的Magstar账户。");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
    }
    @Override
    public void onDisable() {
        getLogger().info("=================================");
        getLogger().info(" ");
        getLogger().info("§b插件名称: §fMagStarResidence");
        getLogger().info("§b插件作者: §fBerry_so");
        getLogger().info("§b工作室: §f星星逹 Stars Creation");
        getLogger().info("§b插件版本: §fv " + getUpdatedVersion());
        getLogger().info(" ");
        getLogger().info("=================================");
        getLogger().info("§e感谢您的使用！");
        AbstractDataManager.getInstance().saveData();
        GeneralUtils.flushRuntimeData();
    }
    @Nonnull
    public static MagstarPlugin getInstance() {
        return instance;
    }
    public static int getSerialNumber() {
        return SERIAL_NUMBER;
    }
    public static String getUserVersion() {
        return USER_VERSION;
    }
    public static String getUpdatedVersion() {
        return UPDATED_VERSION;
    }
}
