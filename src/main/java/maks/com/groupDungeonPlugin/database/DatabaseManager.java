package maks.com.groupDungeonPlugin.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import maks.com.groupDungeonPlugin.utils.ItemSerializationUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages MySQL operations for preview items.
 */
public class DatabaseManager {

    private final JavaPlugin plugin;
    private HikariDataSource dataSource;
    private final String tablePrefix;

    /**
     * Creates and initializes the MySQL manager using configuration values.
     *
     * @param plugin owning plugin
     */
    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("database");
        String host = section.getString("host", "localhost");
        int port = section.getInt("port", 3306);
        String dbName = section.getString("dbname", "groupdungeon");
        String user = section.getString("user", "root");
        String password = section.getString("password", "");
        int poolSize = section.getInt("pool-size", 10);
        this.tablePrefix = section.getString("table-prefix", "");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + dbName);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(poolSize);
        config.setPoolName("GroupDungeonPool");

        this.dataSource = new HikariDataSource(config);

        createTables();
    }

    /**
     * Creates required tables if they don't already exist.
     */
    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tablePrefix +
                "preview_items (" +
                "dungeon_id VARCHAR(64) NOT NULL, " +
                "slot INT NOT NULL, " +
                "item_blob MEDIUMTEXT NOT NULL, " +
                "PRIMARY KEY (dungeon_id, slot))";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not create preview_items table: " + e.getMessage());
        }
    }

    /**
     * Loads preview items for a dungeon.
     *
     * @param dungeonId id of the dungeon
     * @return map of slot -> ItemStack
     */
    public Map<Integer, ItemStack> loadPreviewItems(String dungeonId) {
        Map<Integer, ItemStack> result = new HashMap<>();
        String sql = "SELECT slot, item_blob FROM " + tablePrefix + "preview_items WHERE dungeon_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dungeonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int slot = rs.getInt("slot");
                    String blob = rs.getString("item_blob");
                    ItemStack item = ItemSerializationUtils.deserializeItem(blob);
                    if (item != null) {
                        result.put(slot, item);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error loading preview items for " + dungeonId + ": " + e.getMessage());
        }

        return result;
    }

    /**
     * Saves preview items for a dungeon.
     *
     * @param dungeonId id of the dungeon
     * @param items     map of slot -> ItemStack to save
     */
    public void savePreviewItems(String dungeonId, Map<Integer, ItemStack> items) {
        String deleteSql = "DELETE FROM " + tablePrefix + "preview_items WHERE dungeon_id = ?";
        String insertSql = "REPLACE INTO " + tablePrefix +
                "preview_items (dungeon_id, slot, item_blob) VALUES (?,?,?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement delete = conn.prepareStatement(deleteSql);
             PreparedStatement insert = conn.prepareStatement(insertSql)) {

            delete.setString(1, dungeonId);
            delete.executeUpdate();

            for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                insert.setString(1, dungeonId);
                insert.setInt(2, entry.getKey());
                insert.setString(3, ItemSerializationUtils.serializeItem(entry.getValue()));
                insert.addBatch();
            }
            insert.executeBatch();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error saving preview items for " + dungeonId + ": " + e.getMessage());
        }
    }

    /**
     * Closes the datasource.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}

