package maks.com.groupDungeonPlugin;

import maks.com.groupDungeonPlugin.api.DatabaseManager;
import maks.com.groupDungeonPlugin.api.DungeonManager;
import maks.com.groupDungeonPlugin.api.GUIManager;
import maks.com.groupDungeonPlugin.api.PartyIntegrationAPI;
import maks.com.groupDungeonPlugin.api.PartyManager;
import maks.com.groupDungeonPlugin.commands.DungeonCommand;
import maks.com.groupDungeonPlugin.commands.DropEditCommand;
import maks.com.groupDungeonPlugin.commands.PartyDungeonCommand;
import maks.com.groupDungeonPlugin.listeners.GUIListener;
import maks.com.groupDungeonPlugin.listeners.PortalListener;
import maks.com.groupDungeonPlugin.models.DungeonKey;
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
     * Gets the database manager.
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

        // Initialize party API
        PartyIntegrationAPI.initialize();

        this.partyManager = new PartyManager(this);
        this.dungeonManager = new DungeonManager(this, databaseManager);
        this.guiManager = new GUIManager(this, dungeonManager);

        // Set plugin instance for DungeonKey
        DungeonKey.setPlugin(this);

        // Register commands
        getCommand("party_dungeon").setExecutor(new PartyDungeonCommand(dungeonManager));
        getCommand("dungeon").setExecutor(new DungeonCommand(dungeonManager, guiManager));
        getCommand("edit_drops").setExecutor(new DropEditCommand(dungeonManager, guiManager));

        // Register listeners
        getServer().getPluginManager().registerEvents(new GUIListener(this, guiManager, dungeonManager), this);
        getServer().getPluginManager().registerEvents(new PortalListener(this, dungeonManager), this);

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
