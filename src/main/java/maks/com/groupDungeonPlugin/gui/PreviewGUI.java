package maks.com.groupDungeonPlugin.gui;

import maks.com.groupDungeonPlugin.api.DungeonManager;
import maks.com.groupDungeonPlugin.models.Dungeon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/**
 * GUI used for displaying and editing preview items for a dungeon.
 */
public class PreviewGUI extends GUI {
    private final Dungeon dungeon;
    private final DungeonManager dungeonManager;
    private final boolean editable;

    public PreviewGUI(Player player, Dungeon dungeon, DungeonManager dungeonManager, boolean editable) {
        super(player, (editable ? "§8Edit Preview - " : "§8Possible Drops - ") + dungeon.getName(), 54);
        this.dungeon = dungeon;
        this.dungeonManager = dungeonManager;
        this.editable = editable;
    }

    @Override
    protected void initializeItems() {
        for (int i = 0; i < 45; i++) {
            ItemStack item = dungeon.getPreviewItems().get(i);
            if (item != null) {
                inventory.setItem(i, item.clone());
            }
        }

        if (editable) {
            ItemStack save = new ItemStack(Material.EMERALD);
            ItemMeta meta = save.getItemMeta();
            meta.setDisplayName("§aSave");
            save.setItemMeta(meta);
            setItem(53, save);
        }

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(editable ? "§cCancel" : "§cClose");
        close.setItemMeta(closeMeta);
        setItem(49, close);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if (!editable || slot >= 45) {
            event.setCancelled(true);
        }

        if (slot == 49) {
            player.closeInventory();
            return;
        }

        if (editable && slot == 53) {
            Map<Integer, ItemStack> map = dungeon.getPreviewItems();
            map.clear();
            for (int i = 0; i < 45; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    map.put(i, item.clone());
                }
            }
            dungeonManager.savePreviewItems(dungeon.getId());
            player.closeInventory();
            player.sendMessage("§aPreview items saved.");
        }
    }
}
