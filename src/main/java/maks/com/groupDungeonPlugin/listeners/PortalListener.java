package maks.com.groupDungeonPlugin.listeners;

import maks.com.groupDungeonPlugin.GroupDungeonPlugin;
import maks.com.groupDungeonPlugin.api.DungeonManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

/**
 * Listener for portal handling.
 */
public class PortalListener implements Listener {
    private final DungeonManager dungeonManager;
    private final GroupDungeonPlugin plugin;
    private final Map<String, PortalLocation> portalLocations;
    private static final int debuggingFlag = 1;

    /**
     * Creates a new portal listener.
     *
     * @param plugin The plugin instance
     * @param dungeonManager The dungeon manager
     */
    public PortalListener(GroupDungeonPlugin plugin, DungeonManager dungeonManager) {
        this.plugin = plugin;
        this.dungeonManager = dungeonManager;
        this.portalLocations = new HashMap<>();
        loadPortalLocations();
    }

    /**
     * Loads portal locations from config or creates defaults.
     */
    private void loadPortalLocations() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection portalsSection = config.getConfigurationSection("portals");

        if (portalsSection == null) {
            // Create default portals if none exist
            createDefaultPortalLocations();
            savePortalLocations();
            return;
        }

        for (String dungeonId : portalsSection.getKeys(false)) {
            ConfigurationSection portalSection = portalsSection.getConfigurationSection(dungeonId);
            if (portalSection != null) {
                int x = portalSection.getInt("x");
                int y = portalSection.getInt("y");
                int z = portalSection.getInt("z");
                String world = portalSection.getString("world", "world");

                portalLocations.put(dungeonId, new PortalLocation(world, x, y, z));

                if (debuggingFlag == 1) {
                    plugin.getLogger().info("Loaded portal for dungeon: " + dungeonId + " at " + world + "," + x + "," + y + "," + z);
                }
            }
        }

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Loaded " + portalLocations.size() + " portal locations");
        }
    }

    /**
     * Creates default portal locations for each dungeon.
     */
    private void createDefaultPortalLocations() {
        // Mythology dungeons
        portalLocations.put("mythology_odyssey", new PortalLocation("world", -625, -58, -939));
        portalLocations.put("mythology_poseidon", new PortalLocation("world", -620, -58, -939));
        portalLocations.put("mythology_zeus", new PortalLocation("world", -615, -58, -939));
        portalLocations.put("mythology_daedalus", new PortalLocation("world", -610, -58, -939));
        portalLocations.put("mythology_souls", new PortalLocation("world", -605, -58, -939));

        // Ancient Civilizations dungeons
        portalLocations.put("ancient_atlantis", new PortalLocation("world", -625, -58, -934));
        portalLocations.put("ancient_pyramid", new PortalLocation("world", -620, -58, -934));
        portalLocations.put("ancient_gardens", new PortalLocation("world", -615, -58, -934));
        portalLocations.put("ancient_colosseum", new PortalLocation("world", -610, -58, -934));
        portalLocations.put("ancient_wall", new PortalLocation("world", -605, -58, -934));

        // Fantasy Realms dungeons
        portalLocations.put("fantasy_drakonis", new PortalLocation("world", -625, -58, -929));
        portalLocations.put("fantasy_woods", new PortalLocation("world", -620, -58, -929));
        portalLocations.put("fantasy_caverns", new PortalLocation("world", -615, -58, -929));
        portalLocations.put("fantasy_spire", new PortalLocation("world", -610, -58, -929));
        portalLocations.put("fantasy_citadel", new PortalLocation("world", -605, -58, -929));

        // Elemental Challenges dungeons
        portalLocations.put("elemental_inferno", new PortalLocation("world", -625, -58, -924));
        portalLocations.put("elemental_trench", new PortalLocation("world", -620, -58, -924));
        portalLocations.put("elemental_peaks", new PortalLocation("world", -615, -58, -924));
        portalLocations.put("elemental_core", new PortalLocation("world", -610, -58, -924));
        portalLocations.put("elemental_sanctum", new PortalLocation("world", -605, -58, -924));

        // Cosmic Adventures dungeons
        portalLocations.put("cosmic_belt", new PortalLocation("world", -625, -58, -919));
        portalLocations.put("cosmic_xeno", new PortalLocation("world", -620, -58, -919));
        portalLocations.put("cosmic_corridor", new PortalLocation("world", -615, -58, -919));
        portalLocations.put("cosmic_nexus", new PortalLocation("world", -610, -58, -919));
        portalLocations.put("cosmic_horizon", new PortalLocation("world", -605, -58, -919));

        // Horror dungeons
        portalLocations.put("horror_asylum", new PortalLocation("world", -625, -58, -914));
        portalLocations.put("horror_manor", new PortalLocation("world", -620, -58, -914));
        portalLocations.put("horror_cemetery", new PortalLocation("world", -615, -58, -914));
        portalLocations.put("horror_dimension", new PortalLocation("world", -610, -58, -914));
        portalLocations.put("horror_torment", new PortalLocation("world", -605, -58, -914));

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Created " + portalLocations.size() + " default portal locations");
        }
    }

    /**
     * Saves portal locations to config.
     */
    private void savePortalLocations() {
        FileConfiguration config = plugin.getConfig();

        // Clear existing portals section
        config.set("portals", null);

        // Save each portal location
        for (Map.Entry<String, PortalLocation> entry : portalLocations.entrySet()) {
            String dungeonId = entry.getKey();
            PortalLocation location = entry.getValue();

            config.set("portals." + dungeonId + ".world", location.world);
            config.set("portals." + dungeonId + ".x", location.x);
            config.set("portals." + dungeonId + ".y", location.y);
            config.set("portals." + dungeonId + ".z", location.z);
        }

        plugin.saveConfig();

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Saved " + portalLocations.size() + " portal locations to config");
        }
    }

    /**
     * Handles entering portals.
     *
     * @param event The player interact event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Ignore off-hand interactions
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) return;

        // Check if the block is a portal (End Portal Frame)
        if (block.getType() == Material.END_PORTAL_FRAME) {
            event.setCancelled(true);

            // Check which dungeon this portal is for
            String dungeonId = getDungeonIdFromLocation(block.getLocation());

            if (dungeonId != null) {
                if (debuggingFlag == 1) {
                    plugin.getLogger().info("Player " + player.getName() + " is interacting with portal for dungeon: " + dungeonId);
                }

                // Try to enter the dungeon
                dungeonManager.enterDungeon(player, dungeonId);
            } else {
                if (debuggingFlag == 1) {
                    plugin.getLogger().info("Player " + player.getName() + " clicked an End Portal Frame that isn't a dungeon portal");
                    plugin.getLogger().info("Location: " + block.getLocation().getWorld().getName() + ", " + 
                                           block.getLocation().getBlockX() + ", " + 
                                           block.getLocation().getBlockY() + ", " + 
                                           block.getLocation().getBlockZ());
                }

                player.sendMessage("§cThis doesn't seem to be a dungeon portal.");
            }
        }
    }

    /**
     * Gets the dungeon ID from a location.
     *
     * @param location The location to check
     * @return The dungeon ID, or null if no dungeon is associated with this location
     */
    private String getDungeonIdFromLocation(Location location) {
        String worldName = location.getWorld().getName();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        for (Map.Entry<String, PortalLocation> entry : portalLocations.entrySet()) {
            PortalLocation portalLocation = entry.getValue();

            if (portalLocation.world.equals(worldName) && 
                portalLocation.x == x && 
                portalLocation.y == y && 
                portalLocation.z == z) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Class representing a portal location.
     */
    private static class PortalLocation {
        private final String world;
        private final int x, y, z;

        public PortalLocation(String world, int x, int y, int z) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
