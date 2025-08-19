package maks.com.groupDungeonPlugin.api;

import maks.com.groupDungeonPlugin.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * API to integrate with the party system from MyExperiencePlugin
 */
public class PartyIntegrationAPI {
    private static boolean initialized = false;
    private static final int debuggingFlag = 1;

    /**
     * Initializes the API
     */
    public static void initialize() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("MyExperiencePlugin");
        if (plugin != null && plugin.isEnabled()) {
            initialized = true;
            if (debuggingFlag == 1) {
                Bukkit.getLogger().info("[PartyIntegrationAPI] Successfully connected to MyExperiencePlugin");
            }
        } else {
            Bukkit.getLogger().warning("[PartyIntegrationAPI] MyExperiencePlugin not found or not running!");
        }
    }

    /**
     * Checks if a player is in a party
     */
    public static boolean isInParty(Player player) {
        if (!initialized) return false;
        // This would call the actual party API from MyExperiencePlugin
        // For now, we'll just return false as a placeholder
        return false;
    }

    /**
     * Gets the size of a player's party
     */
    public static int getPartySize(Player player) {
        if (!initialized) return 1;
        // This would call the actual party API from MyExperiencePlugin
        // For now, we'll just return 1 (solo player) as a placeholder
        return 1;
    }

    /**
     * Checks if a party has a valid size for a dungeon
     */
    public static boolean hasValidPartySize(Player player, int minSize, int maxSize) {
        if (!initialized) return player != null; // Solo player always meets requirements
        int partySize = getPartySize(player);
        return partySize >= minSize && partySize <= maxSize;
    }

    /**
     * Gets all members of a player's party
     */
    public static List<Player> getPartyMembers(Player player) {
        List<Player> soloList = new ArrayList<>();
        if (player != null) soloList.add(player);

        if (!initialized) {
            return soloList;
        }

        // This would call the actual party API from MyExperiencePlugin
        // For now, we'll just return a list with the player as a placeholder
        return soloList;
    }

    /**
     * Teleports all members of a party to a location
     */
    public static void teleportParty(Player player, Location location) {
        if (!initialized) {
            if (player != null) player.teleport(location);
            return;
        }

        // This would call the actual party API from MyExperiencePlugin
        // For now, we'll just teleport the player as a placeholder
        List<Player> members = getPartyMembers(player);
        for (Player member : members) {
            member.teleport(location);
            if (debuggingFlag == 1) {
                Bukkit.getLogger().info("[PartyIntegrationAPI] Teleported party member " + member.getName() + " to dungeon");
            }
        }
    }

    /**
     * Sends a message to all members of a party
     */
    public static void sendMessageToParty(Player player, String message) {
        if (!initialized) {
            if (player != null) player.sendMessage(message);
            return;
        }

        // This would call the actual party API from MyExperiencePlugin
        // For now, we'll just send the message to the player as a placeholder
        List<Player> members = getPartyMembers(player);
        for (Player member : members) {
            member.sendMessage(message);
        }
    }

    /**
     * Checks if a player is the leader of their party
     * 
     * @param player The player to check
     * @return True if the player is the party leader, or if they're not in a party (solo player)
     */
    public static boolean isPartyLeader(Player player) {
        if (!initialized || !isInParty(player)) return true; // Solo player is always the "leader"

        // This would call the actual party API from MyExperiencePlugin
        // For now, we'll just return true as a placeholder
        return true;
    }

}
