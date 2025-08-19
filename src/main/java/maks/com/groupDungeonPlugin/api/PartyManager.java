package maks.com.groupDungeonPlugin.api;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages party integration for the dungeon plugin.
 */
public class PartyManager {
    private final JavaPlugin plugin;
    private static final int debuggingFlag = 1;

    /**
     * Creates a new party manager.
     *
     * @param plugin The plugin instance
     */
    public PartyManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks if a player is in a party.
     *
     * @param player The player to check
     * @return True if the player is in a party, false otherwise
     */
    public boolean isInParty(Player player) {
        // Check using PartyIntegrationAPI
        boolean result = PartyIntegrationAPI.isInParty(player);

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Checking if player " + player.getName() + " is in party: " + result);
        }

        return result;
    }

    /**
     * Checks if a player is the leader of their party.
     *
     * @param player The player to check
     * @return True if the player is the party leader, false otherwise
     */
    public boolean isPartyLeader(Player player) {
        // Check using PartyIntegrationAPI
        boolean result = PartyIntegrationAPI.isPartyLeader(player);

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Checking if player " + player.getName() + " is party leader: " + result);
        }

        return result;
    }

    /**
     * Gets the size of a player's party.
     *
     * @param player The player whose party to check
     * @return The party size, or 1 if the player is not in a party
     */
    public int getPartySize(Player player) {
        // Get from PartyIntegrationAPI
        int size = PartyIntegrationAPI.getPartySize(player);

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Getting party size for player " + player.getName() + ": " + size);
        }

        return size;
    }

    /**
     * Gets all members of a player's party.
     *
     * @param player The player whose party to check
     * @return A list of party members, or a list containing only the player if they are not in a party
     */
    public List<Player> getPartyMembers(Player player) {
        // Get from PartyIntegrationAPI
        List<Player> members = PartyIntegrationAPI.getPartyMembers(player);

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Getting party members for player " + player.getName() + ": " + members.size() + " members");
            for (Player member : members) {
                plugin.getLogger().info("- " + member.getName());
            }
        }

        return members;
    }

    /**
     * Sends a message to all members of a player's party.
     *
     * @param player The player whose party to send the message to
     * @param message The message to send
     */
    public void sendMessageToParty(Player player, String message) {
        // Send using PartyIntegrationAPI
        PartyIntegrationAPI.sendMessageToParty(player, message);

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Sending message to " + player.getName() + "'s party: " + message);
        }
    }

    /**
     * Checks if a party has a valid size for a dungeon.
     *
     * @param player The party leader
     * @param minSize The minimum size
     * @param maxSize The maximum size
     * @return True if the party size is valid, false otherwise
     */
    public boolean hasValidPartySize(Player player, int minSize, int maxSize) {
        // Check using PartyIntegrationAPI
        boolean result = PartyIntegrationAPI.hasValidPartySize(player, minSize, maxSize);

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Checking if player " + player.getName() + "'s party has valid size (" + 
                                   minSize + "-" + maxSize + "): " + result);
        }

        return result;
    }

    /**
     * Teleports all members of a party to a location.
     *
     * @param player The party leader
     * @param location The location to teleport to
     */
    public void teleportParty(Player player, org.bukkit.Location location) {
        // Teleport using PartyIntegrationAPI
        PartyIntegrationAPI.teleportParty(player, location);

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Teleporting " + player.getName() + "'s party to location: " + 
                                   location.getWorld().getName() + ", " + 
                                   location.getX() + ", " + 
                                   location.getY() + ", " + 
                                   location.getZ());
        }
    }
}
