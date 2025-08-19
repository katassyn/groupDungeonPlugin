package maks.com.groupDungeonPlugin.api;

import maks.com.groupDungeonPlugin.models.Dungeon;
import maks.com.groupDungeonPlugin.models.DungeonCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages GUI interfaces for the dungeon plugin.
 */
public class GUIManager {
    private final JavaPlugin plugin;
    private final DungeonManager dungeonManager;

    /**
     * Creates a new GUI manager.
     *
     * @param plugin The plugin instance
     * @param dungeonManager The dungeon manager
     */
    public GUIManager(JavaPlugin plugin, DungeonManager dungeonManager) {
        this.plugin = plugin;
        this.dungeonManager = dungeonManager;
    }

    /**
     * Opens the category selection GUI for a player.
     *
     * @param player The player to show the GUI to
     */
    public void openCategoryGUI(Player player) {
        // Creating inventory
        Inventory inv = Bukkit.createInventory(null, 54, "§8Select Dungeon Category");

        // Filling background with white glass panes
        ItemStack background = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = background.getItemMeta();
        meta.setDisplayName(" ");
        background.setItemMeta(meta);

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, background);
        }

        // Adding category icons
        int[] slots = {10, 12, 14, 16, 28, 30, 32, 34}; // Positions for icons
        int index = 0;

        for (DungeonCategory category : dungeonManager.getCategories().values()) {
            if (index >= slots.length) break;

            ItemStack icon = new ItemStack(category.getIcon());
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName("§6" + category.getName());

            List<String> lore = new ArrayList<>();
            lore.add("§7" + category.getDescription());
            lore.add("");
            lore.add("§7Available Dungeons: §f" + category.getDungeons().size());
            lore.add("");
            lore.add("§eClick to view dungeons");

            iconMeta.setLore(lore);
            icon.setItemMeta(iconMeta);

            inv.setItem(slots[index], icon);
            index++;
        }

        // Adding information
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§6Information");

        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7Select a dungeon category");
        infoLore.add("§7Each dungeon has level and party requirements");

        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);

        inv.setItem(4, info);

        // Opening inventory
        player.openInventory(inv);
    }

    /**
     * Opens the dungeon selection GUI for a player.
     *
     * @param player The player to show the GUI to
     * @param categoryId The ID of the category to show dungeons for
     */
    public void openDungeonGUI(Player player, String categoryId) {
        DungeonCategory category = dungeonManager.getCategory(categoryId);
        if (category == null) return;

        // Creating inventory
        Inventory inv = Bukkit.createInventory(null, 54, "§8Dungeons - " + category.getName());

        // Filling background with white glass panes
        ItemStack background = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = background.getItemMeta();
        meta.setDisplayName(" ");
        background.setItemMeta(meta);

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, background);
        }

        // Adding category information
        ItemStack categoryItem = new ItemStack(category.getIcon());
        ItemMeta categoryMeta = categoryItem.getItemMeta();
        categoryMeta.setDisplayName("§6" + category.getName());

        List<String> categoryLore = new ArrayList<>();
        categoryLore.add("§7" + category.getDescription());
        categoryLore.add("");
        categoryLore.add("§7Select a dungeon to view details");

        categoryMeta.setLore(categoryLore);
        categoryItem.setItemMeta(categoryMeta);

        inv.setItem(4, categoryItem);

        // Adding dungeons
        List<Dungeon> dungeons = category.getDungeons();
        int[] slots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34}; // Positions for dungeons
        int index = 0;

        for (Dungeon dungeon : dungeons) {
            if (index >= slots.length) break;

            ItemStack icon = new ItemStack(dungeon.getIcon());
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName("§e" + dungeon.getName());

            List<String> lore = new ArrayList<>();
            lore.add("§7" + dungeon.getDescription());
            lore.add("");
            lore.add("§7Tier: §f" + dungeon.getTier());
            lore.add("§7Required Level: §f" + dungeon.getRequiredLevel() + "+");
            lore.add("§7Party Size: §f" + dungeon.getMinPartySize() + "-" + dungeon.getMaxPartySize());
            lore.add("");
            lore.add("§eClick to enter dungeon");
            lore.add("§eShift + Right Click to view possible drops");

            iconMeta.setLore(lore);
            icon.setItemMeta(iconMeta);

            inv.setItem(slots[index], icon);
            index++;
        }

        // Adding back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cBack to Categories");
        back.setItemMeta(backMeta);

        inv.setItem(49, back);

        // Opening inventory
        player.openInventory(inv);
    }

    // Tracks which players are editing which dungeon's preview items
    private final Map<java.util.UUID, String> editingPlayers = new HashMap<>();

    /**
     * Opens the drop preview GUI for a player. When editable is true the player can
     * modify the inventory and changes will be saved on close.
     *
     * @param player the player
     * @param dungeonId dungeon identifier
     * @param editable whether the GUI is in edit mode
     */
    public void openDropPreviewGUI(Player player, String dungeonId, boolean editable) {
        Dungeon dungeon = dungeonManager.getDungeon(dungeonId);
        if (dungeon == null) return;

        String title = editable ? "§8Edit Drops - " + dungeon.getName()
                                : "§8Possible Drops - " + dungeon.getName();
        Inventory inv = Bukkit.createInventory(null, 54, title);

        // Populate with existing preview items
        for (Map.Entry<Integer, ItemStack> entry : dungeon.getPreviewItems().entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue());
        }

        player.openInventory(inv);

        if (editable) {
            editingPlayers.put(player.getUniqueId(), dungeonId);
        }
    }

    /**
     * Gets the dungeon id a player is editing, or null if not editing.
     */
    public String getEditingDungeon(java.util.UUID uuid) {
        return editingPlayers.get(uuid);
    }

    /**
     * Clears editing state for a player.
     */
    public void clearEditing(java.util.UUID uuid) {
        editingPlayers.remove(uuid);
    }
}
