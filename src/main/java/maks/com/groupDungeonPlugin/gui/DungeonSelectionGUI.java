package maks.com.groupDungeonPlugin.gui;

import maks.com.groupDungeonPlugin.GroupDungeonPlugin;
import maks.com.groupDungeonPlugin.api.DungeonManager;
import maks.com.groupDungeonPlugin.api.GUIManager;
import maks.com.groupDungeonPlugin.models.Dungeon;
import maks.com.groupDungeonPlugin.models.DungeonCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI for selecting a dungeon from a category.
 */
public class DungeonSelectionGUI extends GUI {
    private final DungeonCategory category;
    private final Map<Integer, Dungeon> slotToDungeonMap = new HashMap<>();
    private static final int debuggingFlag = 1;

    /**
     * Creates a new dungeon selection GUI showing dungeons from a specific category.
     * 
     * @param player The player viewing the GUI
     * @param category The dungeon category
     */
    public DungeonSelectionGUI(Player player, DungeonCategory category) {
        super(player, "§8Dungeons - " + category.getName(), 54);
        this.category = category;
    }

    @Override
    protected void initializeItems() {
        // Clear any existing items
        inventory.clear();
        slotToDungeonMap.clear();

        // Add background glass panes
        ItemStack backgroundItem = createBackgroundItem();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, backgroundItem);
        }

        // Add category information
        ItemStack categoryItem = new ItemStack(category.getIcon());
        ItemMeta categoryMeta = categoryItem.getItemMeta();
        categoryMeta.setDisplayName("§6" + category.getName());

        List<String> categoryLore = new ArrayList<>();
        categoryLore.add("§7" + category.getDescription());
        categoryLore.add("");
        categoryLore.add("§7Available Dungeons: §f" + category.getDungeons().size());
        categoryLore.add("");
        categoryLore.add("§7Select a dungeon to view details");

        categoryMeta.setLore(categoryLore);
        categoryItem.setItemMeta(categoryMeta);

        setItem(4, categoryItem);

        // Add dungeons
        List<Dungeon> dungeons = category.getDungeons();

        // Arrange dungeons by tier
        int[] tier1Slots = {19, 20, 21};
        int[] tier2Slots = {22, 23, 24, 25};
        int[] tier3Slots = {28, 29, 30, 31};
        int[] tier4Slots = {32, 33, 34};
        int[] tier5Slots = {37, 38, 39, 40, 41, 42, 43};

        // Add tier labels
        addTierLabel(10, "§fTier 1", "§7Beginner");
        addTierLabel(16, "§aTier 2", "§7Intermediate");
        addTierLabel(27, "§9Tier 3", "§7Advanced");
        addTierLabel(35, "§5Tier 4", "§7Expert");
        addTierLabel(46, "§6Tier 5", "§7Master");

        Map<Integer, List<Dungeon>> dungeonsByTier = new HashMap<>();
        for (Dungeon dungeon : dungeons) {
            int tier = dungeon.getTier();
            if (!dungeonsByTier.containsKey(tier)) {
                dungeonsByTier.put(tier, new ArrayList<>());
            }
            dungeonsByTier.get(tier).add(dungeon);
        }

        // Add tier 1 dungeons
        if (dungeonsByTier.containsKey(1)) {
            addDungeonsToSlots(dungeonsByTier.get(1), tier1Slots);
        }

        // Add tier 2 dungeons
        if (dungeonsByTier.containsKey(2)) {
            addDungeonsToSlots(dungeonsByTier.get(2), tier2Slots);
        }

        // Add tier 3 dungeons
        if (dungeonsByTier.containsKey(3)) {
            addDungeonsToSlots(dungeonsByTier.get(3), tier3Slots);
        }

        // Add tier 4 dungeons
        if (dungeonsByTier.containsKey(4)) {
            addDungeonsToSlots(dungeonsByTier.get(4), tier4Slots);
        }

        // Add tier 5 dungeons
        if (dungeonsByTier.containsKey(5)) {
            addDungeonsToSlots(dungeonsByTier.get(5), tier5Slots);
        }

        // Add back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cBack to Categories");
        back.setItemMeta(backMeta);

        setItem(49, back);
    }

    /**
     * Adds a tier label to the GUI.
     *
     * @param slot The slot to add the label to
     * @param name The name of the tier
     * @param description The description of the tier
     */
    private void addTierLabel(int slot, String name, String description) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();
        lore.add(description);

        meta.setLore(lore);
        item.setItemMeta(meta);

        setItem(slot, item);
    }

    /**
     * Adds dungeons to the specified slots.
     *
     * @param dungeons The dungeons to add
     * @param slots The slots to add the dungeons to
     */
    private void addDungeonsToSlots(List<Dungeon> dungeons, int[] slots) {
        int index = 0;
        for (Dungeon dungeon : dungeons) {
            if (index >= slots.length) break;

            // Add space between dungeons for better visual separation
            int slot = slots[index];

            ItemStack icon = new ItemStack(dungeon.getIcon());
            ItemMeta iconMeta = icon.getItemMeta();
            iconMeta.setDisplayName("§e" + dungeon.getName());

            List<String> lore = new ArrayList<>();
            lore.add("§7" + dungeon.getDescription());
            lore.add("");
            lore.add("§7Tier: §f" + dungeon.getTier());
            lore.add("§7Required Level: §f" + dungeon.getRequiredLevel() + "+");
            lore.add("§7Party Size: §f" + dungeon.getMinPartySize() + "-" + dungeon.getMaxPartySize());

            // Add key requirement if present
            if (dungeon.requiresKey()) {
                lore.add("§7Required Key: §c" + dungeon.getKeyDisplayName().replace("&", "§"));
            }

            lore.add("");
            lore.add("§eClick to enter dungeon");
            lore.add("§eShift + Right Click to view possible drops");

            iconMeta.setLore(lore);
            icon.setItemMeta(iconMeta);

            setItem(slot, icon);
            slotToDungeonMap.put(slot, dungeon);

            index++;
        }
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

        // Back button
        if (slot == 49) {
            player.closeInventory();

            // Get the plugin instance and open category GUI
            GroupDungeonPlugin plugin = (GroupDungeonPlugin) Bukkit.getPluginManager().getPlugin("GroupDungeonPlugin");
            if (plugin != null) {
                plugin.getGuiManager().openCategoryGUI(player);

                if (debuggingFlag == 1) {
                    Bukkit.getLogger().info("[DungeonSelectionGUI] Player " + player.getName() + " returned to category selection");
                }
            }
            return;
        }

        // Dungeon selection
        Dungeon dungeon = slotToDungeonMap.get(slot);
        if (dungeon != null) {
            // Shift + Right Click to view drops
            if (event.getClick() == ClickType.SHIFT_RIGHT) {
                player.closeInventory();

                // Get the plugin instance and open drop preview GUI
                GroupDungeonPlugin plugin = (GroupDungeonPlugin) Bukkit.getPluginManager().getPlugin("GroupDungeonPlugin");
                if (plugin != null) {
                    plugin.getGuiManager().openDropPreviewGUI(player, dungeon.getId());

                    if (debuggingFlag == 1) {
                        Bukkit.getLogger().info("[DungeonSelectionGUI] Player " + player.getName() + 
                                               " viewing drops for dungeon: " + dungeon.getName());
                    }
                }
                return;
            }

            // Left Click to enter dungeon
            if (event.getClick() == ClickType.LEFT) {
                player.closeInventory();

                // Get the plugin instance and enter dungeon
                GroupDungeonPlugin plugin = (GroupDungeonPlugin) Bukkit.getPluginManager().getPlugin("GroupDungeonPlugin");
                if (plugin != null) {
                    plugin.getDungeonManager().enterDungeon(player, dungeon.getId());

                    if (debuggingFlag == 1) {
                        Bukkit.getLogger().info("[DungeonSelectionGUI] Player " + player.getName() + 
                                               " attempting to enter dungeon: " + dungeon.getName());
                    }
                }
                return;
            }
        }
    }
}
