package maks.com.groupDungeonPlugin.models;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents a key required to enter a dungeon.
 */
public class DungeonKey {
    private final String id;            // Key identifier
    private final String displayName;   // Key display name
    private final Material material;    // Key material
    private static final int debuggingFlag = 1;
    private static JavaPlugin plugin;

    /**
     * Sets the plugin instance for debugging.
     *
     * @param pluginInstance The plugin instance
     */
    public static void setPlugin(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
    }

    /**
     * Creates a new dungeon key.
     *
     * @param id The unique ID of the key
     * @param displayName The display name of the key
     * @param material The material of the key
     */
    public DungeonKey(String id, String displayName, Material material) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
    }

    /**
     * Gets the unique ID of the key.
     *
     * @return The key ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the display name of the key.
     *
     * @return The key display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the material of the key.
     *
     * @return The key material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Checks if an inventory contains the key.
     *
     * @param inventory The inventory to check
     * @return True if the inventory contains the key, false otherwise
     */
    public boolean hasKey(Inventory inventory) {
        if (inventory == null) return false;

        for (ItemStack item : inventory.getContents()) {
            if (isKeyItem(item)) {
                if (debuggingFlag == 1 && plugin != null) {
                    plugin.getLogger().info("Found key " + displayName + " in inventory");
                }
                return true;
            }
        }

        if (debuggingFlag == 1 && plugin != null) {
            plugin.getLogger().info("Key " + displayName + " not found in inventory");
        }
        return false;
    }

    /**
     * Consumes a key from an inventory.
     *
     * @param inventory The inventory to consume the key from
     * @return True if the key was consumed, false if the inventory doesn't contain the key
     */
    public boolean consumeKey(Inventory inventory) {
        if (inventory == null) return false;

        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (isKeyItem(item)) {
                // Found the key, consume it
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    inventory.setItem(i, null);
                }

                if (debuggingFlag == 1 && plugin != null) {
                    plugin.getLogger().info("Consumed key " + displayName + " from inventory");
                }

                return true;
            }
        }

        if (debuggingFlag == 1 && plugin != null) {
            plugin.getLogger().info("Could not consume key " + displayName + " (not found in inventory)");
        }

        return false;
    }

    /**
     * Checks if an item is the key.
     *
     * @param item The item to check
     * @return True if the item is the key, false otherwise
     */
    private boolean isKeyItem(ItemStack item) {
        if (item == null || item.getType() != material) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }

        String name = meta.getDisplayName();
        String formattedKeyName = displayName.replace("&", "§");

        return name.equals(formattedKeyName);
    }
}
