package maks.com.groupDungeonPlugin.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents a possible dungeon drop.
 * Simplified to only show visual representation of what can be obtained.
 */
public class DungeonDrop {
    private final Material material;    // Item material
    private final String displayName;   // Item name in English

    /**
     * Creates a new dungeon drop.
     *
     * @param material The material of the item
     * @param displayName The display name of the item
     */
    public DungeonDrop(Material material, String displayName) {
        this.material = material;
        this.displayName = displayName;
    }

    /**
     * Creates a new dungeon drop from an ItemStack.
     *
     * @param item The ItemStack to create the drop from
     */
    public DungeonDrop(ItemStack item) {
        this.material = item.getType();

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            this.displayName = item.getItemMeta().getDisplayName();
        } else {
            this.displayName = formatMaterialName(material.name());
        }
    }

    /**
     * Gets the material of the item.
     *
     * @return The material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Gets the display name of the item.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts this drop to an ItemStack.
     *
     * @return The ItemStack representation of this drop
     */
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName.replace("&", "§"));
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Formats a material name to be more readable.
     * 
     * @param materialName The material name to format
     * @return The formatted material name
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
}
