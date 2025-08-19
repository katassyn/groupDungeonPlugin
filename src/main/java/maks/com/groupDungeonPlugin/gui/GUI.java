package maks.com.groupDungeonPlugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Base class for all GUIs in the plugin.
 */
public abstract class GUI implements InventoryHolder {
    protected final Inventory inventory;
    protected final Player player;

    public GUI(Player player, String title, int size) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, size, title);
        initializeItems();
    }

    /**
     * Initialize the items in the inventory.
     */
    protected abstract void initializeItems();

    /**
     * Handle a click in the inventory.
     *
     * @param event The click event
     */
    public abstract void handleClick(InventoryClickEvent event);

    /**
     * Open the inventory for the player.
     */
    public void open() {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Set an item in the inventory.
     *
     * @param slot The slot to set the item in
     * @param item The item to set
     */
    protected void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }
}