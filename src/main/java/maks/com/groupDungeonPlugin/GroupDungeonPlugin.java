package maks.com.groupDungeonPlugin;

import maks.com.groupDungeonPlugin.api.DungeonManager;
import maks.com.groupDungeonPlugin.api.GUIManager;
import maks.com.groupDungeonPlugin.api.PartyIntegrationAPI;
import maks.com.groupDungeonPlugin.api.PartyManager;
import maks.com.groupDungeonPlugin.database.DatabaseManager;
import maks.com.groupDungeonPlugin.commands.DungeonCommand;
import maks.com.groupDungeonPlugin.commands.PreviewEditCommand;
import maks.com.groupDungeonPlugin.commands.PartyDungeonCommand;
import maks.com.groupDungeonPlugin.listeners.GUIListener;
import maks.com.groupDungeonPlugin.listeners.DungeonMobListener;
import maks.com.groupDungeonPlugin.listeners.PortalListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class GroupDungeonPlugin extends JavaPlugin {
    private DungeonManager dungeonManager;
    private GUIManager guiManager;
    private PartyManager partyManager;
    private DatabaseManager databaseManager;

    /**
     * Gets the dungeon manager.
     * 
     * @return The dungeon manager
     */
    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    /**
     * Gets the GUI manager.
     * 
     * @return The GUI manager
     */
    public GUIManager getGuiManager() {
        return guiManager;
    }

    /**
     * Gets the party manager.
     * 
     * @return The party manager
     */
    public PartyManager getPartyManager() {
        return partyManager;
    }

    /**
     * Gets the MySQL manager.
     *
     * @return The database manager
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Set debugging flag in config if not present
        if (!getConfig().contains("debug")) {
            getConfig().set("debug", 1);
            saveConfig();
        }

        // Initialize managers
        this.databaseManager = new DatabaseManager(this);

        // Save default dungeon configuration
        saveResource("dungeons.yml", false);

        // Initialize party API
        PartyIntegrationAPI.initialize();

        this.partyManager = new PartyManager(this);
        this.dungeonManager = new DungeonManager(this, databaseManager);
        this.guiManager = new GUIManager(this, dungeonManager);

        // Register commands
        getCommand("party_dungeon").setExecutor(new PartyDungeonCommand(dungeonManager));
        getCommand("dungeon").setExecutor(new DungeonCommand(dungeonManager, guiManager));
        getCommand("edit_preview").setExecutor(new PreviewEditCommand(dungeonManager, guiManager));

        // Register listeners
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new PortalListener(this, dungeonManager), this);
        getServer().getPluginManager().registerEvents(new DungeonMobListener(dungeonManager), this);

        getLogger().info("GroupDungeonPlugin has been enabled!");
        getLogger().info("Debug mode: " + (getConfig().getInt("debug") == 1 ? "ON" : "OFF"));
        getLogger().info("Loaded " + dungeonManager.getCategories().size() + " categories and " + 
                        dungeonManager.getDungeons().size() + " dungeons");
    }

    @Override
    public void onDisable() {
        // Close database connection
        if (databaseManager != null) {
            databaseManager.close();
        }

        getLogger().info("GroupDungeonPlugin has been disabled!");
    }
}
