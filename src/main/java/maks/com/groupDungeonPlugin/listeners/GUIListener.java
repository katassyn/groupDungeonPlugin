package maks.com.groupDungeonPlugin.listeners;

import maks.com.groupDungeonPlugin.GroupDungeonPlugin;
import maks.com.groupDungeonPlugin.api.DungeonManager;
import maks.com.groupDungeonPlugin.api.GUIManager;
import maks.com.groupDungeonPlugin.models.Dungeon;
import maks.com.groupDungeonPlugin.models.DungeonCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

/**
 * Listener for GUI handling.
 */
public class GUIListener implements Listener {
    private final GUIManager guiManager;
    private final DungeonManager dungeonManager;
    private final GroupDungeonPlugin plugin;
    private static final int debuggingFlag = 1;

    /**
     * Creates a new GUI listener.
     *
     * @param plugin The plugin instance
     * @param guiManager The GUI manager
     * @param dungeonManager The dungeon manager
     */
    public GUIListener(GroupDungeonPlugin plugin, GUIManager guiManager, DungeonManager dungeonManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.dungeonManager = dungeonManager;
    }

    /**
     * Handles inventory clicks.
     *
     * @param event The inventory click event
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle() == null) return;

        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();

        // Cancel all clicks in our GUIs
        if (title.startsWith("§8")) {
            boolean editing = title.startsWith("§8Edit Drops - ");
            if (!editing) {
                event.setCancelled(true);
            }

            if (title.equals("§8Select Dungeon Category")) {
                handleCategorySelection(event);
            } else if (title.startsWith("§8Dungeons - ")) {
                handleDungeonSelection(event);
            }
        }
    }

    /**
     * Handles clicks in the category selection GUI.
     *
     * @param event The inventory click event
     */
    private void handleCategorySelection(InventoryClickEvent event) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR || 
            clickedItem.getType() == Material.WHITE_STAINED_GLASS_PANE) {
            return;
        }

        // Check if the clicked item is a category
        for (DungeonCategory category : dungeonManager.getCategories().values()) {
            if (clickedItem.getType() == category.getIcon() && 
                clickedItem.getItemMeta().getDisplayName().equals("§6" + category.getName())) {

                // Open the dungeon selection GUI for this category
                player.closeInventory();
                guiManager.openDungeonGUI(player, category.getId());
                return;
            }
        }
    }

    /**
     * Handles clicks in the dungeon selection GUI.
     *
     * @param event The inventory click event
     */
    private void handleDungeonSelection(InventoryClickEvent event) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR || 
            clickedItem.getType() == Material.WHITE_STAINED_GLASS_PANE) {
            return;
        }

        // Back button
        if (slot == 49 && clickedItem.getType() == Material.ARROW) {
            player.closeInventory();
            guiManager.openCategoryGUI(player);
            return;
        }

        // Check if the clicked item is a dungeon
        for (Dungeon dungeon : dungeonManager.getDungeons().values()) {
            if (clickedItem.getType() == dungeon.getIcon() && 
                clickedItem.getItemMeta().getDisplayName().equals("§e" + dungeon.getName())) {

                // Shift + Right Click to view drops
                if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    player.closeInventory();
                    guiManager.openDropPreviewGUI(player, dungeon.getId(), false);
                    return;
                }

                // Left Click to enter dungeon
                else if (event.getClick() == ClickType.LEFT) {
                    player.closeInventory();
                    dungeonManager.enterDungeon(player, dungeon.getId());
                    return;
                }
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String dungeonId = guiManager.getEditingDungeon(player.getUniqueId());
        if (dungeonId == null) return;

        Inventory inv = event.getInventory();
        Map<Integer, ItemStack> items = new HashMap<>();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                items.put(i, item);
            }
        }

        Dungeon dungeon = dungeonManager.getDungeon(dungeonId);
        dungeon.setPreviewItems(items);
        plugin.getMySQLManager().savePreviewItems(dungeonId, items);
        guiManager.clearEditing(player.getUniqueId());
        player.sendMessage("§aSaved dungeon drops.");
    }
}