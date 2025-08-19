package maks.com.groupDungeonPlugin.commands;

import maks.com.groupDungeonPlugin.api.DungeonManager;
import maks.com.groupDungeonPlugin.gui.CategorySelectionGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command for opening the dungeon selection GUI
 */
public class DungeonGUICommand implements CommandExecutor {
    private final DungeonManager dungeonManager;
    private static final int debuggingFlag = 1;

    public DungeonGUICommand(DungeonManager dungeonManager) {
        this.dungeonManager = dungeonManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // Open the category selection GUI
        CategorySelectionGUI gui = new CategorySelectionGUI(player, dungeonManager.getCategories());
        gui.open();

        if (debuggingFlag == 1) {
            player.getServer().getLogger().info("[Dungeons] Player " + player.getName() + " opened dungeon selection GUI");
        }

        return true;
    }
}
