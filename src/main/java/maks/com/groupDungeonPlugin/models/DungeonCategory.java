package maks.com.groupDungeonPlugin.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a category of dungeons (e.g., Mythology, Ancient Civilizations, etc.).
 * Each category contains multiple dungeons and has a display item for the GUI.
 */
public class DungeonCategory {
    private final String id;            // Category identifier
    private final String name;          // Category name in English
    private final String description;   // Category description in English
    private final Material icon;        // GUI icon
    private final List<Dungeon> dungeons; // List of dungeons in this category

    /**
     * Creates a new dungeon category.
     *
     * @param id The unique ID of the category
     * @param name The name of the category
     * @param description The description of the category
     * @param icon The material to use for the icon in the GUI
     */
    public DungeonCategory(String id, String name, String description, Material icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.dungeons = new ArrayList<>();
    }

    /**
     * Gets the unique ID of the category.
     *
     * @return The category ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the category.
     *
     * @return The category name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the category.
     *
     * @return The category description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the material used for the icon in the GUI.
     *
     * @return The icon material
     */
    public Material getIcon() {
        return icon;
    }

    /**
     * Gets all dungeons in this category.
     *
     * @return A list of dungeons
     */
    public List<Dungeon> getDungeons() {
        return dungeons;
    }

    /**
     * Adds a dungeon to this category.
     *
     * @param dungeon The dungeon to add
     */
    public void addDungeon(Dungeon dungeon) {
        dungeons.add(dungeon);
    }

    /**
     * Removes a dungeon from this category.
     *
     * @param dungeonId The ID of the dungeon to remove
     */
    public void removeDungeon(String dungeonId) {
        dungeons.removeIf(dungeon -> dungeon.getId().equals(dungeonId));
    }

    /**
     * Gets a dungeon by its ID.
     *
     * @param dungeonId The ID of the dungeon to get
     * @return The dungeon, or null if not found
     */
    public Dungeon getDungeon(String dungeonId) {
        return dungeons.stream()
                .filter(dungeon -> dungeon.getId().equals(dungeonId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates an item stack for displaying this category in a GUI.
     *
     * @return The item stack
     */
    public ItemStack createDisplayItem() {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6" + name);

        List<String> lore = new ArrayList<>();
        lore.add("§7" + description);
        lore.add("§7Available Dungeons: §f" + dungeons.size());
        lore.add("");
        lore.add("§eClick to view dungeons in this category");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
}
