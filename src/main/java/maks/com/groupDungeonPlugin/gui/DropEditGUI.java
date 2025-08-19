package maks.com.groupDungeonPlugin.gui;

import maks.com.groupDungeonPlugin.models.Dungeon;
import maks.com.groupDungeonPlugin.models.DungeonDrop;
import maks.com.groupDungeonPlugin.api.DungeonManager;
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
 * GUI for editing dungeon drops.
 */
public class DropEditGUI extends GUI {
    private final Dungeon dungeon;
    private final DungeonManager dungeonManager;
    private final Map<Integer, String> slotToActionMap = new HashMap<>();
    private final Map<Integer, DungeonDrop> slotToDropMap = new HashMap<>();
    private final String mode; // "main", "items", "materials", "other"
    
    private static final int debuggingFlag = 1;

    /**
     * Creates a new drop edit GUI.
     * 
     * @param player The player viewing the GUI
     * @param dungeon The dungeon to edit drops for
     * @param dungeonManager The dungeon manager
     * @param mode The mode to display
     */
    public DropEditGUI(Player player, Dungeon dungeon, DungeonManager dungeonManager, String mode) {
        super(player, "§8Edit Drops - " + dungeon.getName(), 54);
        this.dungeon = dungeon;
        this.dungeonManager = dungeonManager;
        this.mode = mode;
    }

    @Override
    protected void initializeItems() {
        // Clear existing items
        inventory.clear();
        slotToActionMap.clear();
        slotToDropMap.clear();
        
        // Add background
        ItemStack backgroundItem = createBackgroundItem();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, backgroundItem);
        }
        
        // Add dungeon information
        ItemStack dungeonItem = new ItemStack(dungeon.getIcon());
        ItemMeta dungeonMeta = dungeonItem.getItemMeta();
        dungeonMeta.setDisplayName("§e" + dungeon.getName());
        
        List<String> dungeonLore = new ArrayList<>();
        dungeonLore.add("§7" + dungeon.getDescription());
        dungeonLore.add("");
        dungeonLore.add("§7Tier: §f" + dungeon.getTier());
        dungeonLore.add("§7Required Level: §f" + dungeon.getRequiredLevel() + "+");
        dungeonLore.add("");
        dungeonLore.add("§7Editing drops for this dungeon");
        
        dungeonMeta.setLore(dungeonLore);
        dungeonItem.setItemMeta(dungeonMeta);
        
        setItem(4, dungeonItem);
        
        // Handle different modes
        if (mode.equals("main")) {
            setupMainMenu();
        } else if (mode.equals("items")) {
            setupItemsMenu();
        } else if (mode.equals("materials")) {
            setupMaterialsMenu();
        } else if (mode.equals("other")) {
            setupOtherMenu();
        }
        
        // Add back button in all modes except main
        if (!mode.equals("main")) {
            ItemStack backItem = new ItemStack(Material.ARROW);
            ItemMeta backMeta = backItem.getItemMeta();
            backMeta.setDisplayName("§cBack to Main Menu");
            backItem.setItemMeta(backMeta);
            setItem(45, backItem);
            slotToActionMap.put(45, "back_to_main");
        }
        
        // Add back to dungeon menu button
        ItemStack exitItem = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitItem.getItemMeta();
        exitMeta.setDisplayName("§cBack to Dungeon Menu");
        
        List<String> exitLore = new ArrayList<>();
        exitLore.add("§7Click to return to dungeon selection");
        exitLore.add("§cWarning: Unsaved changes will be lost");
        
        exitMeta.setLore(exitLore);
        exitItem.setItemMeta(exitMeta);
        
        setItem(49, exitItem);
        slotToActionMap.put(49, "exit");
        
        // Add save button
        ItemStack saveItem = new ItemStack(Material.EMERALD);
        ItemMeta saveMeta = saveItem.getItemMeta();
        saveMeta.setDisplayName("§aSave Changes");
        
        List<String> saveLore = new ArrayList<>();
        saveLore.add("§7Click to save all changes to the database");
        
        saveMeta.setLore(saveLore);
        saveItem.setItemMeta(saveMeta);
        
        setItem(53, saveItem);
        slotToActionMap.put(53, "save");
    }
    
    /**
     * Sets up the main menu.
     */
    private void setupMainMenu() {
        // Category: Items
        ItemStack itemsItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemsMeta = itemsItem.getItemMeta();
        itemsMeta.setDisplayName("§bItem Drops");
        
        List<String> itemsLore = new ArrayList<>();
        itemsLore.add("§7Add or remove item drops");
        itemsLore.add("§7like weapons, armor, and tools");
        itemsLore.add("");
        itemsLore.add("§eClick to manage item drops");
        
        itemsMeta.setLore(itemsLore);
        itemsItem.setItemMeta(itemsMeta);
        
        setItem(20, itemsItem);
        slotToActionMap.put(20, "items");
        
        // Category: Materials
        ItemStack materialsItem = new ItemStack(Material.IRON_INGOT);
        ItemMeta materialsMeta = materialsItem.getItemMeta();
        materialsMeta.setDisplayName("§aMaterial Drops");
        
        List<String> materialsLore = new ArrayList<>();
        materialsLore.add("§7Add or remove material drops");
        materialsLore.add("§7like ingots, blocks, and resources");
        materialsLore.add("");
        materialsLore.add("§eClick to manage material drops");
        
        materialsMeta.setLore(materialsLore);
        materialsItem.setItemMeta(materialsMeta);
        
        setItem(22, materialsItem);
        slotToActionMap.put(22, "materials");
        
        // Category: Other
        ItemStack otherItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta otherMeta = otherItem.getItemMeta();
        otherMeta.setDisplayName("§dOther Drops");
        
        List<String> otherLore = new ArrayList<>();
        otherLore.add("§7Add or remove miscellaneous drops");
        otherLore.add("§7like potions, books, and special items");
        otherLore.add("");
        otherLore.add("§eClick to manage other drops");
        
        otherMeta.setLore(otherLore);
        otherItem.setItemMeta(otherMeta);
        
        setItem(24, otherItem);
        slotToActionMap.put(24, "other");
        
        // Current Drops Display
        List<DungeonDrop> drops = dungeon.getPossibleDrops();
        int dropCount = drops.size();
        
        ItemStack dropsItem = new ItemStack(Material.CHEST);
        ItemMeta dropsMeta = dropsItem.getItemMeta();
        dropsMeta.setDisplayName("§6Current Drops");
        
        List<String> dropsLore = new ArrayList<>();
        dropsLore.add("§7Total drops: §f" + dropCount);
        dropsLore.add("");
        
        if (dropCount == 0) {
            dropsLore.add("§7No drops configured yet");
        } else {
            dropsLore.add("§7First few drops:");
            int count = 0;
            for (DungeonDrop drop : drops) {
                if (count >= 5) break;
                dropsLore.add("§8- §f" + drop.getDisplayName().replace("&", "§"));
                count++;
            }
            
            if (dropCount > 5) {
                dropsLore.add("§7... and " + (dropCount - 5) + " more");
            }
        }
        
        dropsMeta.setLore(dropsLore);
        dropsItem.setItemMeta(dropsMeta);
        
        setItem(31, dropsItem);
    }
    
    /**
     * Sets up the items menu.
     */
    private void setupItemsMenu() {
        setupDropsGrid("items");
        setupCommonItems(Material.DIAMOND_SWORD, Material.DIAMOND_CHESTPLATE, Material.BOW);
    }
    
    /**
     * Sets up the materials menu.
     */
    private void setupMaterialsMenu() {
        setupDropsGrid("materials");
        setupCommonItems(Material.IRON_INGOT, Material.GOLD_INGOT, Material.EMERALD);
    }
    
    /**
     * Sets up the other menu.
     */
    private void setupOtherMenu() {
        setupDropsGrid("other");
        setupCommonItems(Material.EXPERIENCE_BOTTLE, Material.GOLDEN_APPLE, Material.ENCHANTED_BOOK);
    }
    
    /**
     * Sets up common items for quick adding.
     */
    private void setupCommonItems(Material item1, Material item2, Material item3) {
        // Add from hand button
        ItemStack handItem = new ItemStack(Material.ORANGE_STAINED_GLASS);
        ItemMeta handMeta = handItem.getItemMeta();
        handMeta.setDisplayName("§6Add Item From Hand");
        
        List<String> handLore = new ArrayList<>();
        handLore.add("§7Click to add the item in your main hand");
        handLore.add("§7as a drop for this dungeon");
        
        handMeta.setLore(handLore);
        handItem.setItemMeta(handMeta);
        
        setItem(47, handItem);
        slotToActionMap.put(47, "add_from_hand");
        
        // Quick-add common items
        addQuickAddItem(38, item1);
        addQuickAddItem(40, item2);
        addQuickAddItem(42, item3);
    }
    
    /**
     * Adds a quick-add item button.
     */
    private void addQuickAddItem(int slot, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aAdd " + formatMaterialName(material.name()));
        
        List<String> lore = new ArrayList<>();
        lore.add("§7Click to add this item as a drop");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        setItem(slot, item);
        slotToActionMap.put(slot, "add_" + material.name().toLowerCase());
    }
    
    /**
     * Sets up the drops grid based on category.
     */
    private void setupDropsGrid(String category) {
        List<DungeonDrop> drops = dungeon.getPossibleDrops();
        List<DungeonDrop> filteredDrops = new ArrayList<>();
        
        // Filter drops by category
        for (DungeonDrop drop : drops) {
            if (getDropCategory(drop.getMaterial()).equals(category)) {
                filteredDrops.add(drop);
            }
        }
        
        // Display drops in a grid
        int[] slots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
        };
        
        int index = 0;
        for (DungeonDrop drop : filteredDrops) {
            if (index >= slots.length) break;
            
            ItemStack dropItem = drop.toItemStack();
            ItemMeta meta = dropItem.getItemMeta();
            
            List<String> lore = new ArrayList<>();
            if (meta.hasLore()) {
                lore.addAll(meta.getLore());
                lore.add("");
            }
            
            lore.add("§cRight-click to remove this drop");
            
            meta.setLore(lore);
            dropItem.setItemMeta(meta);
            
            setItem(slots[index], dropItem);
            slotToDropMap.put(slots[index], drop);
            
            index++;
        }
    }
    
    /**
     * Determines the category of a material.
     */
    private String getDropCategory(Material material) {
        String name = material.name();
        
        // Items category
        if (name.endsWith("_SWORD") || name.endsWith("_AXE") || name.endsWith("_PICKAXE") ||
            name.endsWith("_SHOVEL") || name.endsWith("_HOE") || name.endsWith("_HELMET") ||
            name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS") ||
            name.equals("BOW") || name.equals("CROSSBOW") || name.equals("SHIELD") ||
            name.equals("FISHING_ROD") || name.equals("ELYTRA")) {
            return "items";
        }
        
        // Materials category
        if (name.endsWith("_INGOT") || name.endsWith("_ORE") || name.endsWith("_BLOCK") ||
            name.equals("DIAMOND") || name.equals("EMERALD") || name.equals("GOLD_NUGGET") ||
            name.equals("IRON_NUGGET") || name.equals("REDSTONE") || name.equals("COAL") ||
            name.equals("LAPIS_LAZULI") || name.equals("QUARTZ") || name.equals("GLOWSTONE_DUST") ||
            name.equals("LEATHER") || name.equals("PAPER") || name.equals("STRING") ||
            name.endsWith("_PLANKS") || name.endsWith("_LOG") || name.endsWith("_WOOD")) {
            return "materials";
        }
        
        // Default to "other"
        return "other";
    }
    
    /**
     * Formats a material name to be more readable.
     */
    private String formatMaterialName(String materialName) {
        String[] words = materialName.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    /**
     * Creates a background item.
     */
    private ItemStack createBackgroundItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        
        int slot = event.getSlot();
        
        // Check if the slot has an action
        String action = slotToActionMap.get(slot);
        if (action != null) {
            if (action.equals("exit")) {
                player.closeInventory();
                // Open dungeon category
                player.performCommand("dungeon " + dungeon.getCategoryId());
                return;
            } else if (action.equals("save")) {
                player.closeInventory();
                // Save drops to database
                dungeonManager.saveDrops(dungeon.getId());
                player.sendMessage("§aSaved all drop changes to the database.");
                return;
            } else if (action.equals("back_to_main")) {
                player.closeInventory();
                // Return to main drop edit menu
                DropEditGUI mainGUI = new DropEditGUI(player, dungeon, dungeonManager, "main");
                mainGUI.open();
                return;
            } else if (action.equals("items")) {
                player.closeInventory();
                // Open items menu
                DropEditGUI itemsGUI = new DropEditGUI(player, dungeon, dungeonManager, "items");
                itemsGUI.open();
                return;
            } else if (action.equals("materials")) {
                player.closeInventory();
                // Open materials menu
                DropEditGUI materialsGUI = new DropEditGUI(player, dungeon, dungeonManager, "materials");
                materialsGUI.open();
                return;
            } else if (action.equals("other")) {
                player.closeInventory();
                // Open other menu
                DropEditGUI otherGUI = new DropEditGUI(player, dungeon, dungeonManager, "other");
                otherGUI.open();
                return;
            } else if (action.equals("add_from_hand")) {
                // Add item from hand
                ItemStack handItem = player.getInventory().getItemInMainHand();
                
                if (handItem == null || handItem.getType() == Material.AIR) {
                    player.sendMessage("§cYou must hold an item in your main hand to add it.");
                    return;
                }
                
                // Create a drop from the hand item
                DungeonDrop drop = new DungeonDrop(handItem);
                dungeon.addDrop(drop);
                
                player.sendMessage("§aAdded " + handItem.getType().name() + " as a drop.");
                
                // Refresh the GUI
                player.closeInventory();
                DropEditGUI refreshedGUI = new DropEditGUI(player, dungeon, dungeonManager, mode);
                refreshedGUI.open();
                return;
            } else if (action.startsWith("add_")) {
                // Add a common item
                String materialName = action.substring(4).toUpperCase();
                try {
                    Material material = Material.valueOf(materialName);
                    ItemStack newItem = new ItemStack(material);
                    DungeonDrop drop = new DungeonDrop(newItem);
                    dungeon.addDrop(drop);
                    
                    player.sendMessage("§aAdded " + formatMaterialName(materialName) + " as a drop.");
                    
                    // Refresh the GUI
                    player.closeInventory();
                    DropEditGUI refreshedGUI = new DropEditGUI(player, dungeon, dungeonManager, mode);
                    refreshedGUI.open();
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cError: Invalid material.");
                    if (debuggingFlag == 1) {
                        Bukkit.getLogger().warning("Invalid material name: " + materialName);
                    }
                }
                return;
            }
        }
        
        // Check if the slot has a drop
        DungeonDrop drop = slotToDropMap.get(slot);
        if (drop != null && (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT)) {
            // Remove the drop
            dungeon.getPossibleDrops().remove(drop);
            player.sendMessage("§aRemoved " + drop.getDisplayName().replace("&", "§") + " from drops.");
            
            // Refresh the GUI
            player.closeInventory();
            DropEditGUI refreshedGUI = new DropEditGUI(player, dungeon, dungeonManager, mode);
            refreshedGUI.open();
        }
    }
}