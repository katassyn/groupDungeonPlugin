package maks.com.groupDungeonPlugin.listeners;

import maks.com.groupDungeonPlugin.api.DungeonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DungeonMobListener implements Listener {
    private final DungeonManager dungeonManager;

    public DungeonMobListener(DungeonManager dungeonManager) {
        this.dungeonManager = dungeonManager;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        String name = event.getEntity().getName();
        dungeonManager.handleMobDeath(killer, name);
    }
}
