package maks.com.groupDungeonPlugin.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents a possible dungeon drop. Wraps a full ItemStack so custom
 * metadata such as enchantments are preserved when stored in the database.
 */
public class DungeonDrop {
    private final ItemStack item; // Stored item

    /**
     * Creates a new dungeon drop from material and display name.
     *
     * @param material    The material of the item
     * @param displayName The display name of the item
     */
    public DungeonDrop(Material material, String displayName) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            stack.setItemMeta(meta);
        }
        this.item = stack;
    }

    /**
     * Creates a new dungeon drop from an ItemStack.
     *
     * @param item The ItemStack to create the drop from
     */
    public DungeonDrop(ItemStack item) {
        this.item = item.clone();
    }

    /**
     * Gets the material of the item.
     *
     * @return The material
     */
    public Material getMaterial() {
        return item.getType();
    }

    /**
     * Gets the display name of the item.
     *
     * @return The display name
     */
    public String getDisplayName() {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return formatMaterialName(item.getType().name());
    }

    /**
     * Converts this drop to an ItemStack.
     *
     * @return The ItemStack representation of this drop
     */
    public ItemStack toItemStack() {
        return item.clone();
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

