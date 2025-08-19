package maks.com.groupDungeonPlugin.listeners;

import maks.com.groupDungeonPlugin.gui.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Generic listener that forwards inventory clicks to GUI instances.
 */
public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GUI) {
            ((GUI) holder).handleClick(event);
        }
    }
}
