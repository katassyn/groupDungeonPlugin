package maks.com.groupDungeonPlugin.api;

import maks.com.groupDungeonPlugin.models.Dungeon;
import maks.com.groupDungeonPlugin.models.DungeonCategory;
import maks.com.groupDungeonPlugin.models.DungeonDrop;
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

    /**
     * Opens the drop preview GUI for a player.
     *
     * @param player The player to show the GUI to
     * @param dungeonId The ID of the dungeon to show drops for
     */
    public void openDropPreviewGUI(Player player, String dungeonId) {
        Dungeon dungeon = dungeonManager.getDungeon(dungeonId);
        if (dungeon == null) return;

        // Creating inventory
        Inventory inv = Bukkit.createInventory(null, 54, "§8Possible Drops - " + dungeon.getName());

        // Filling background with white glass panes
        ItemStack background = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = background.getItemMeta();
        meta.setDisplayName(" ");
        background.setItemMeta(meta);

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, background);
        }

        // Adding dungeon information
        ItemStack dungeonItem = new ItemStack(dungeon.getIcon());
        ItemMeta dungeonMeta = dungeonItem.getItemMeta();
        dungeonMeta.setDisplayName("§e" + dungeon.getName());

        List<String> dungeonLore = new ArrayList<>();
        dungeonLore.add("§7" + dungeon.getDescription());
        dungeonLore.add("");
        dungeonLore.add("§7Tier: §f" + dungeon.getTier());
        dungeonLore.add("§7Required Level: §f" + dungeon.getRequiredLevel() + "+");
        dungeonLore.add("");
        dungeonLore.add("§7These are the possible drops from this dungeon");

        dungeonMeta.setLore(dungeonLore);
        dungeonItem.setItemMeta(dungeonMeta);

        inv.setItem(4, dungeonItem);

        // Adding drops to GUI
        List<DungeonDrop> drops = dungeon.getPossibleDrops();
        addDropsToPreviewGUI(inv, drops);

        // Adding back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cBack");
        back.setItemMeta(backMeta);

        inv.setItem(49, back);

        // Opening inventory
        player.openInventory(inv);
    }

    /**
     * Adds drops to the preview GUI.
     *
     * @param inv The inventory to add drops to
     * @param drops The drops to add
     */
    private void addDropsToPreviewGUI(Inventory inv, List<DungeonDrop> drops) {
        // Start at slot 19 (third row, first column)
        int[] slots = {
            19, 20, 21, 22, 23, 24, 25, 26,
            28, 29, 30, 31, 32, 33, 34, 35,
            37, 38, 39, 40, 41, 42, 43, 44
        };

        int index = 0;
        for (DungeonDrop drop : drops) {
            if (index >= slots.length) break; // Maximum items that can fit

            // Convert drop to ItemStack
            ItemStack item = drop.toItemStack();

            // Add to inventory
            inv.setItem(slots[index], item);
            index++;
        }
    }


    /**
     * Opens the drop edit GUI for a player.
     *
     * @param player The player to show the GUI to
     * @param dungeonId The ID of the dungeon to edit drops for
     */
    public void openDropEditGUI(Player player, String dungeonId) {
        // Check permission
        if (!player.hasPermission("partydungeon.admin")) {
            player.sendMessage("§cYou don't have permission to use this.");
            return;
        }

        Dungeon dungeon = dungeonManager.getDungeon(dungeonId);
        if (dungeon == null) {
            player.sendMessage("§cDungeon not found: " + dungeonId);
            return;
        }

        // Use the new improved DropEditGUI
        maks.com.groupDungeonPlugin.gui.DropEditGUI gui = new maks.com.groupDungeonPlugin.gui.DropEditGUI(player, dungeon, dungeonManager, "main");
        gui.open();
    }

    /**
     * Adds drops to the edit GUI with remove options.
     *
     * @param inv The inventory to add drops to
     * @param drops The drops to add
     */
    private void addDropsToEditGUI(Inventory inv, List<DungeonDrop> drops) {
        // Start at slot 19 (third row, first column)
        int[] slots = {
            19, 20, 21, 22, 23, 24, 25, 26,
            28, 29, 30, 31, 32, 33, 34, 35,
            37, 38, 39, 40, 41, 42, 43, 44
        };

        int index = 0;
        for (DungeonDrop drop : drops) {
            if (index >= slots.length) break; // Maximum items that can fit

            // Convert drop to ItemStack
            ItemStack item = drop.toItemStack();
            ItemMeta itemMeta = item.getItemMeta();

            // Add lore for edit options
            List<String> lore = new ArrayList<>();
            if (itemMeta.hasLore()) {
                lore.addAll(itemMeta.getLore());
                lore.add("");
            }

            lore.add("§cShift + Right Click to remove");

            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);

            // Add to inventory
            inv.setItem(slots[index], item);
            index++;
        }
    }
}
