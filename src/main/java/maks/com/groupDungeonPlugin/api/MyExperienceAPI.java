package maks.com.groupDungeonPlugin.api;

import org.bukkit.entity.Player;

/**
 * Provides access to player experience levels from the MyExperiencePlugin.
 */
public interface MyExperienceAPI {
    /**
     * Gets the level of the given player.
     *
     * @param player the player whose level to fetch
     * @return the player's level
     */
    int getPlayerLevel(Player player);
}

