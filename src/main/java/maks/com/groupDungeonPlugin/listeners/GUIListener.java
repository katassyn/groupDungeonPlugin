package maks.com.groupDungeonPlugin.listeners;

import maks.com.groupDungeonPlugin.GroupDungeonPlugin;
import maks.com.groupDungeonPlugin.api.DungeonManager;
import maks.com.groupDungeonPlugin.api.GUIManager;
import maks.com.groupDungeonPlugin.models.Dungeon;
import maks.com.groupDungeonPlugin.models.DungeonCategory;
import maks.com.groupDungeonPlugin.models.DungeonDrop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
            event.setCancelled(true);

            // Handle category selection GUI
            if (title.equals("§8Select Dungeon Category")) {
                handleCategorySelection(event);
            }
            // Handle dungeon selection GUI
            else if (title.startsWith("§8Dungeons - ")) {
                handleDungeonSelection(event);
            }
            // Handle drop preview GUI
            else if (title.startsWith("§8Possible Drops - ")) {
                handleDropPreview(event);
            }
            // Handle drop edit GUI
            else if (title.startsWith("§8Edit Drops - ")) {
                handleDropEdit(event);
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
                    guiManager.openDropPreviewGUI(player, dungeon.getId());
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

    /**
     * Handles clicks in the drop preview GUI.
     *
     * @param event The inventory click event
     */
    private void handleDropPreview(InventoryClickEvent event) {
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

            // Extract the dungeon name from the title
            String title = event.getView().getTitle();
            String dungeonName = title.substring(16); // Remove "§8Possible Drops - "

            // Find the dungeon and its category
            for (Dungeon dungeon : dungeonManager.getDungeons().values()) {
                if (dungeon.getName().equals(dungeonName)) {
                    guiManager.openDungeonGUI(player, dungeon.getCategoryId());
                    return;
                }
            }

            // If dungeon not found, go back to category selection
            guiManager.openCategoryGUI(player);
            return;
        }
    }

    /**
     * Finds a dungeon by name.
     *
     * @param name The name of the dungeon
     * @return The dungeon, or null if not found
     */
    private Dungeon findDungeonByName(String name) {
        for (Dungeon dungeon : dungeonManager.getDungeons().values()) {
            if (dungeon.getName().equals(name)) {
                return dungeon;
            }
        }
        return null;
    }

    /**
     * Handles clicks in the drop edit GUI.
     *
     * @param event The inventory click event
     */
    private void handleDropEdit(InventoryClickEvent event) {
        int slot = event.getSlot();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR || 
            clickedItem.getType() == Material.WHITE_STAINED_GLASS_PANE) {
            return;
        }

        // Extract the dungeon name from the title
        String title = event.getView().getTitle();
        String dungeonName = title.substring(13); // Remove "§8Edit Drops - "

        // Find the dungeon
        Dungeon dungeon = findDungeonByName(dungeonName);
        if (dungeon == null) {
            player.closeInventory();
            player.sendMessage("§cError: Dungeon not found.");
            return;
        }

        // Back button
        if (slot == 45 && clickedItem.getType() == Material.ARROW) {
            player.closeInventory();
            guiManager.openDungeonGUI(player, dungeon.getCategoryId());
            return;
        }

        // Add item from hand button
        if (slot == 47 && clickedItem.getType() == Material.EMERALD) {
            // Get the item in the player's hand
            ItemStack handItem = player.getInventory().getItemInMainHand();
            
            if (handItem == null || handItem.getType() == Material.AIR) {
                player.sendMessage("§cYou need to hold an item in your main hand.");
                return;
            }
            
            // Create a new drop from the item in hand
            DungeonDrop drop = new DungeonDrop(handItem);
            
            // Add the drop to the dungeon
            dungeon.addDrop(drop);
            
            player.sendMessage("§aAdded the item in your hand as a possible drop.");
            
            // Refresh the GUI
            guiManager.openDropEditGUI(player, dungeon.getId());
            return;
        }

        // Save button
        if (slot == 49 && clickedItem.getType() == Material.WRITABLE_BOOK) {
            player.closeInventory();

            // Save drops to database
            dungeonManager.saveDrops(dungeon.getId());

            player.sendMessage("§aSaved all changes to the database.");
            guiManager.openDungeonGUI(player, dungeon.getCategoryId());
            return;
        }

        // Check if the clicked item is a drop (has the remove lore)
        if (clickedItem.getItemMeta() != null && 
            clickedItem.getItemMeta().getLore() != null && 
            clickedItem.getItemMeta().getLore().contains("§cShift + Right Click to remove")) {

            // Get the drop
            String displayName = clickedItem.getItemMeta().getDisplayName().replace("§", "&");
            Material material = clickedItem.getType();

            // Find the drop in the dungeon
            for (DungeonDrop drop : dungeon.getPossibleDrops()) {
                if (drop.getMaterial() == material && drop.getDisplayName().equals(displayName)) {
                    // Shift + Right Click to remove
                    if (event.getClick() == ClickType.SHIFT_RIGHT) {
                        player.closeInventory();

                        // Remove the drop
                        dungeon.getPossibleDrops().remove(drop);

                        player.sendMessage("§aRemoved drop from the dungeon.");
                        guiManager.openDropEditGUI(player, dungeon.getId());
                        return;
                    }
                }
            }
        }
    }
}