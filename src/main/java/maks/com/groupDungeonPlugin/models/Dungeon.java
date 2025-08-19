package maks.com.groupDungeonPlugin.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single dungeon.
 */
public class Dungeon {
    private final String id;            // Dungeon identifier
    private final String name;          // Dungeon name in English
    private final String description;   // Dungeon description in English
    private final int tier;             // Tier difficulty (1-5)
    private final int requiredLevel;    // Required player level
    private final int minPartySize;     // Minimum party size
    private final int maxPartySize;     // Maximum party size
    private final Material icon;        // GUI icon
    private final String categoryId;    // Category ID this dungeon belongs to

    // Key requirement for the dungeon
    private String keyId;               // Key identifier
    private String keyDisplayName;      // Key display name

    // Preview items stored by slot
    private final Map<Integer, ItemStack> previewItems;

    /**
     * Creates a new dungeon.
     *
     * @param id The unique ID of the dungeon
     * @param name The name of the dungeon
     * @param description The description of the dungeon
     * @param tier The tier difficulty (1-5)
     * @param requiredLevel The required player level
     * @param minPartySize The minimum party size
     * @param maxPartySize The maximum party size
     * @param icon The material to use for the icon in the GUI
     * @param categoryId The ID of the category this dungeon belongs to
     */
    public Dungeon(String id, String name, String description, int tier, int requiredLevel, 
                  int minPartySize, int maxPartySize, Material icon, String categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tier = tier;
        this.requiredLevel = requiredLevel;
        this.minPartySize = minPartySize;
        this.maxPartySize = maxPartySize;
        this.icon = icon;
        this.categoryId = categoryId;
        this.keyId = null;
        this.keyDisplayName = null;
        this.previewItems = new HashMap<>();
    }

    /**
     * Creates a new dungeon with a key requirement.
     *
     * @param id The unique ID of the dungeon
     * @param name The name of the dungeon
     * @param description The description of the dungeon
     * @param tier The tier difficulty (1-5)
     * @param requiredLevel The required player level
     * @param minPartySize The minimum party size
     * @param maxPartySize The maximum party size
     * @param icon The material to use for the icon in the GUI
     * @param categoryId The ID of the category this dungeon belongs to
     * @param keyId The ID of the required key
     * @param keyDisplayName The display name of the required key
     */
    public Dungeon(String id, String name, String description, int tier, int requiredLevel, 
                  int minPartySize, int maxPartySize, Material icon, String categoryId,
                  String keyId, String keyDisplayName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tier = tier;
        this.requiredLevel = requiredLevel;
        this.minPartySize = minPartySize;
        this.maxPartySize = maxPartySize;
        this.icon = icon;
        this.categoryId = categoryId;
        this.keyId = keyId;
        this.keyDisplayName = keyDisplayName;
        this.previewItems = new HashMap<>();
    }

    /**
     * Gets the unique ID of the dungeon.
     *
     * @return The dungeon ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the dungeon.
     *
     * @return The dungeon name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the dungeon.
     *
     * @return The dungeon description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the tier difficulty of the dungeon.
     *
     * @return The tier difficulty (1-5)
     */
    public int getTier() {
        return tier;
    }

    /**
     * Gets the required player level for the dungeon.
     *
     * @return The required level
     */
    public int getRequiredLevel() {
        return requiredLevel;
    }

    /**
     * Gets the minimum party size for the dungeon.
     *
     * @return The minimum party size
     */
    public int getMinPartySize() {
        return minPartySize;
    }

    /**
     * Gets the maximum party size for the dungeon.
     *
     * @return The maximum party size
     */
    public int getMaxPartySize() {
        return maxPartySize;
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
     * Gets the ID of the category this dungeon belongs to.
     *
     * @return The category ID
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Gets the map of preview items for this dungeon.
     *
     * @return preview item map
     */
    public Map<Integer, ItemStack> getPreviewItems() {
        return previewItems;
    }

    /**
     * Replaces the current preview items with the provided map.
     *
     * @param items new preview items
     */
    public void setPreviewItems(Map<Integer, ItemStack> items) {
        previewItems.clear();
        previewItems.putAll(items);
    }

    /**
     * Removes all preview items.
     */
    public void clearPreviewItems() {
        previewItems.clear();
    }

    /**
     * Gets the ID of the required key.
     *
     * @return The key ID, or null if no key is required
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * Sets the ID of the required key.
     *
     * @param keyId The key ID
     */
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    /**
     * Gets the display name of the required key.
     *
     * @return The key display name, or null if no key is required
     */
    public String getKeyDisplayName() {
        return keyDisplayName;
    }

    /**
     * Sets the display name of the required key.
     *
     * @param keyDisplayName The key display name
     */
    public void setKeyDisplayName(String keyDisplayName) {
        this.keyDisplayName = keyDisplayName;
    }

    /**
     * Checks if this dungeon requires a key.
     *
     * @return True if a key is required, false otherwise
     */
    public boolean requiresKey() {
        return keyId != null && !keyId.isEmpty();
    }
}
