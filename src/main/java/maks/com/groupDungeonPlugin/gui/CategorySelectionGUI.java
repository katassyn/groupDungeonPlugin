package maks.com.groupDungeonPlugin.gui;

import maks.com.groupDungeonPlugin.models.DungeonCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI for selecting a dungeon category.
 * This is the main menu that players see when they use the /party_dungeon command.
 */
public class CategorySelectionGUI extends GUI {
    private final Map<String, DungeonCategory> categories;
    private final Map<Integer, DungeonCategory> slotToCategoryMap = new HashMap<>();
    private static final int debuggingFlag = 1;

    /**
     * Creates a new category selection GUI.
     *
     * @param player The player viewing the GUI
     * @param categories The available dungeon categories
     */
    public CategorySelectionGUI(Player player, Map<String, DungeonCategory> categories) {
        super(player, "§8Select Dungeon Category", 54);
        this.categories = categories;
    }

    @Override
    protected void initializeItems() {
        // Clear any existing items
        inventory.clear();
        slotToCategoryMap.clear();

        // Add decorative border using glass panes
        ItemStack borderItem = createBorderItem();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, borderItem); // Top row
            inventory.setItem(45 + i, borderItem); // Bottom row
        }
        for (int i = 0; i < 6; i++) {
            inventory.setItem(i * 9, borderItem); // Left column
            inventory.setItem(i * 9 + 8, borderItem); // Right column
        }

        // Add header information
        ItemStack headerItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta headerMeta = headerItem.getItemMeta();
        headerMeta.setDisplayName("§6§lDungeon Categories");

        List<String> headerLore = new ArrayList<>();
        headerLore.add("§7Select a category to view available dungeons");
        headerLore.add("§7Each category offers unique challenges");

        headerMeta.setLore(headerLore);
        headerItem.setItemMeta(headerMeta);
        setItem(4, headerItem);

        // Add category items
        if (categories.isEmpty()) {
            ItemStack noItem = new ItemStack(Material.BARRIER);
            ItemMeta noMeta = noItem.getItemMeta();
            noMeta.setDisplayName("§cNo categories available");
            noItem.setItemMeta(noMeta);
            setItem(22, noItem);
            return;
        }

        // Ordered category IDs according to the file
        String[] orderedCategoryIds = {
            "mythology",
            "ancient",
            "fantasy"
        };
        // Place categories in the center area in a visually appealing way
        // Using vertical arrangement with spaces between categories
        int[] slots = {19, 22, 25};

        for (int i = 0; i < orderedCategoryIds.length; i++) {
            String categoryId = orderedCategoryIds[i];
            DungeonCategory category = categories.get(categoryId);

            if (category != null) {
                int slot = slots[i];

                ItemStack categoryItem = new ItemStack(category.getIcon());
                ItemMeta categoryMeta = categoryItem.getItemMeta();
                categoryMeta.setDisplayName("§6" + category.getName());

                List<String> lore = new ArrayList<>();
                lore.add("§7" + category.getDescription());
                lore.add("");
                lore.add("§e» Click to view dungeons «");

                categoryMeta.setLore(lore);
                categoryItem.setItemMeta(categoryMeta);

                setItem(slot, categoryItem);
                slotToCategoryMap.put(slot, category);

                if (debuggingFlag == 1) {
                    Bukkit.getLogger().info("[CategorySelectionGUI] Added category " + 
                                           category.getName() + " to slot " + slot);
                }
            }
        }

        // Add informational footer
        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("§6Party Dungeon System");

        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7Available Categories: §f" + categories.size());
        infoLore.add("§7You must be in a party to enter dungeons");
        infoLore.add("§7Some dungeons require keys to enter");

        infoMeta.setLore(infoLore);
        infoItem.setItemMeta(infoMeta);

        setItem(49, infoItem);
    }

    /**
     * Creates a border item (light blue stained glass pane).
     *
     * @return The border item
     */
    private ItemStack createBorderItem() {
        ItemStack item = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a background item (white stained glass pane).
     *
     * @return The background item
     */
    private ItemStack createBackgroundItem() {
        ItemStack item = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        int slot = event.getSlot();

        DungeonCategory category = slotToCategoryMap.get(slot);
        if (category != null) {
            // Open the dungeon selection GUI for this category
            player.closeInventory();
            DungeonSelectionGUI dungeonGUI = new DungeonSelectionGUI(player, category);
            dungeonGUI.open();

            if (debuggingFlag == 1) {
                Bukkit.getLogger().info("[CategorySelectionGUI] Player " + player.getName() + 
                                       " selected category: " + category.getName());
            }
        }
    }
}
