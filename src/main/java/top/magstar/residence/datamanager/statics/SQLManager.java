package top.magstar.residence.datamanager.statics;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import top.magstar.residence.Residence;
import top.magstar.residence.datamanager.runtime.RuntimeDataManager;
import top.magstar.residence.objects.Resident;
import top.magstar.residence.utils.fileutils.ConfigUtils;

import java.security.InvalidParameterException;
import java.sql.*;
import java.util.List;
import java.util.UUID;

public final class SQLManager extends AbstractDataManager{
    private static String dbUrl, username, password;
    @Override
    public void saveData() {
        List<Resident> residents = RuntimeDataManager.getResidents();
        Connection con = null;
        try {
            con = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO magstar_residence (uuid, res) VALUES (?, ?) ON DUPLICATE KEY UPDATE res=VALUES(res)"
            );
            for (Resident res : residents) {
                String uuid = res.getUniqueId().toString();
                byte[] data = res.serializeAsBytes();
                ps.setString(1, uuid);
                Blob blob = con.createBlob();
                blob.setBytes(1, data);
                ps.setBlob(2, blob);
                ps.executeUpdate();
                blob.free();
            }
            ps.close();
            con.close();
        } catch (SQLException e) {
            putError(e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    putError(e);
                }
            }
        }
    }

    @Override
    public void reload() {
        Configuration cfg = ConfigUtils.getConfig();
        String host = cfg.getString("mysql.host");
        String dbName = cfg.getString("mysql.db-name");
        username = cfg.getString("mysql.username");
        password = cfg.getString("mysql.password");
        String port = cfg.getString("mysql.port");
        if (host == null || dbName == null || port == null) {
            Residence.dbAvailable = false;
            throw new InvalidParameterException("MySQL configuration not complete!");
        }
        dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement ps = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS magstar_residence (" +
                            "uuid VARCHAR(255) PRIMARY KEY NOT NULL, " +
                            "res BLOB NOT NULL)"
            );
            ps.execute();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            putError(e);
        } catch (ClassNotFoundException e) {
            Residence.dbAvailable = false;
            Residence.getInstance().getLogger().info("未发现JDBC前置库。请重装本插件。");
            Bukkit.getPluginManager().disablePlugin(Residence.getInstance());
            throw new RuntimeException(e);
        }
    }
    private void putError(SQLException e){
        Residence.getInstance().getLogger().info("数据库操作失败。将采用备用数据存储方法。");
        Residence.dbAvailable = false;
        e.printStackTrace();
    }
    @Override
    public void loadData() {
        try {
            Connection con = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement ps = con.prepareStatement(
                    "SELECT res FROM magstar_residence"
            );
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Resident res = Resident.deserializeBytes(rs.getBytes(1));
                RuntimeDataManager.loadData(res);
            }
            ps.close();
            con.close();
        } catch (SQLException e) {
            putError(e);
        }
    }

    @Override
    public void deleteData(UUID uuid) {
        try {
            Connection con = DriverManager.getConnection(dbUrl, username, password);
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM magstar_residence WHERE uuid=?"
            );
            ps.setString(1, uuid.toString());
            ps.execute();
            ps.close();
            con.close();
        } catch (SQLException e) {
            putError(e);
        }
    }
}
