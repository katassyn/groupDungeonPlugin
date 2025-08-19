package maks.com.groupDungeonPlugin.api;

import maks.com.groupDungeonPlugin.models.DungeonDrop;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages database operations for the dungeon plugin.
 */
public class DatabaseManager {
    private final JavaPlugin plugin;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String tablePrefix;
    private Connection connection;
    private static final int debuggingFlag = 1;

    /**
     * Creates a new database manager.
     *
     * @param plugin The plugin instance
     */
    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;

        // Load database config from config.yml
        FileConfiguration config = plugin.getConfig();
        this.host = config.getString("database.host", "localhost");
        this.port = config.getInt("database.port", 3306);
        this.database = config.getString("database.name", "minecraft");
        this.username = config.getString("database.username", "root");
        this.password = config.getString("database.password", "");
        this.tablePrefix = config.getString("database.table_prefix", "gd_");

        connect();
        createTables();
    }

    /**
     * Connects to the database.
     */
    private void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database, username, password);

                plugin.getLogger().info("Connected to database!");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to database: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("JDBC driver not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates the necessary tables if they don't exist.
     */
    private void createTables() {
        try (Connection conn = getConnection()) {
            // Create drops table with simplified schema
            try (PreparedStatement stmt = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + tablePrefix + "dungeon_drops (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "dungeon_id VARCHAR(64) NOT NULL, " +
                    "material VARCHAR(64) NOT NULL, " +
                    "display_name VARCHAR(128) NOT NULL, " +
                    "INDEX (dungeon_id)" +
                    ")")) {
                stmt.executeUpdate();
            }

            // Check if old columns exist and remove them if they do
            try {
                // Check if the rarity column exists
                try (PreparedStatement stmt = conn.prepareStatement(
                        "SHOW COLUMNS FROM " + tablePrefix + "dungeon_drops LIKE 'rarity'");
                     ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {
                        // Column exists, so alter table to remove it
                        try (PreparedStatement alterStmt = conn.prepareStatement(
                                "ALTER TABLE " + tablePrefix + "dungeon_drops " +
                                "DROP COLUMN rarity, " +
                                "DROP COLUMN drop_chance, " +
                                "DROP COLUMN drop_type")) {
                            alterStmt.executeUpdate();
                            plugin.getLogger().info("Removed old columns from dungeon_drops table");
                        }
                    }
                }
            } catch (SQLException ex) {
                // If this fails, it's likely because the table doesn't exist yet or columns are already gone
                plugin.getLogger().info("No need to update table schema: " + ex.getMessage());
            }

            plugin.getLogger().info("Database tables created or already exist!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not create tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection to the database.
     *
     * @return The connection
     * @throws SQLException If a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }

        return connection;
    }

    /**
     * Loads all drops from the database.
     *
     * @return A map of drops, keyed by dungeon ID
     */
    public Map<String, List<DungeonDrop>> loadAllDrops() {
        Map<String, List<DungeonDrop>> result = new HashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM " + tablePrefix + "dungeon_drops");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String dungeonId = rs.getString("dungeon_id");
                Material material = Material.getMaterial(rs.getString("material"));
                String displayName = rs.getString("display_name");

                // Create simplified drop with just material and display name
                DungeonDrop drop = new DungeonDrop(material, displayName);

                result.computeIfAbsent(dungeonId, k -> new ArrayList<>()).add(drop);
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Error loading drops from database: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Saves drops for a specific dungeon.
     *
     * @param dungeonId The ID of the dungeon
     * @param drops The drops to save
     */
    public void saveDrops(String dungeonId, List<DungeonDrop> drops) {
        Connection conn = null;
        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;

        try {
            conn = getConnection();

            // Start transaction
            conn.setAutoCommit(false);

            // First delete existing drops
            deleteStmt = conn.prepareStatement(
                    "DELETE FROM " + tablePrefix + "dungeon_drops WHERE dungeon_id = ?");
            deleteStmt.setString(1, dungeonId);
            deleteStmt.executeUpdate();

            // Then insert new drops with simplified schema
            insertStmt = conn.prepareStatement(
                    "INSERT INTO " + tablePrefix + "dungeon_drops " +
                    "(dungeon_id, material, display_name) " +
                    "VALUES (?, ?, ?)");

            for (DungeonDrop drop : drops) {
                insertStmt.setString(1, dungeonId);
                insertStmt.setString(2, drop.getMaterial().name());
                insertStmt.setString(3, drop.getDisplayName());

                insertStmt.addBatch();
            }

            insertStmt.executeBatch();

            // Commit transaction
            conn.commit();

            if (debuggingFlag == 1) {
                plugin.getLogger().info("Successfully saved " + drops.size() + 
                                      " drops for dungeon " + dungeonId + " to database");
            }
        } catch (SQLException e) {
            // Try to rollback transaction in case of error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    plugin.getLogger().severe("Error rolling back transaction: " + ex.getMessage());
                }
            }

            plugin.getLogger().severe("Error saving drops to database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (insertStmt != null) insertStmt.close();
                if (deleteStmt != null) deleteStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    // Don't close connection, just return to pool
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Error closing database resources: " + e.getMessage());
            }
        }
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
