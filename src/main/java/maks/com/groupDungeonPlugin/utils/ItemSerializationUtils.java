package maks.com.groupDungeonPlugin.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Utility class for serializing and deserializing ItemStacks to Base64 strings.
 */
public final class ItemSerializationUtils {

    private ItemSerializationUtils() {
    }

    /**
     * Serializes an ItemStack to a Base64 string.
     *
     * @param item the ItemStack to serialize
     * @return the Base64 string or null on failure
     */
    public static String serializeItem(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Deserializes an ItemStack from a Base64 string.
     *
     * @param data the Base64 data
     * @return the ItemStack or null on failure
     */
    public static ItemStack deserializeItem(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}

