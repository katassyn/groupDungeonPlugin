package maks.com.groupDungeonPlugin.commands;

import maks.com.groupDungeonPlugin.api.DungeonManager;
import maks.com.groupDungeonPlugin.api.GUIManager;
import maks.com.groupDungeonPlugin.models.Dungeon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Admin command for drop editing.
 */
public class DropEditCommand implements CommandExecutor, TabCompleter {
    private final DungeonManager dungeonManager;
    private final GUIManager guiManager;
    private static final int debuggingFlag = 1;

    /**
     * Creates a new drop edit command.
     *
     * @param dungeonManager The dungeon manager
     * @param guiManager The GUI manager
     */
    public DropEditCommand(DungeonManager dungeonManager, GUIManager guiManager) {
        this.dungeonManager = dungeonManager;
        this.guiManager = guiManager;
    }

    /**
     * Executes the command.
     *
     * @param sender The command sender
     * @param command The command
     * @param label The command label
     * @param args The command arguments
     * @return True if the command was executed successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("partydungeon.admin")) {
            player.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /edit_drops <dungeon_id>");
            listAvailableDungeons(player);
            return true;
        }

        String dungeonId = args[0];
        Dungeon dungeon = dungeonManager.getDungeon(dungeonId);

        if (dungeon == null) {
            player.sendMessage("§cDungeon not found: " + dungeonId);
            listAvailableDungeons(player);
            return true;
        }

        if (debuggingFlag == 1) {
            player.getServer().getLogger().info("[DropEditCommand] Player " + player.getName() +
                                               " editing drops for dungeon: " + dungeon.getName());
        }

        // Open the new drop edit GUI
        guiManager.openDropPreviewGUI(player, dungeonId, true);

        return true;
    }

    /**
     * Lists available dungeons to the player.
     * 
     * @param player The player to send the list to
     */
    private void listAvailableDungeons(Player player) {
        player.sendMessage("§6Available dungeons:");

        Map<String, Dungeon> dungeons = dungeonManager.getDungeons();
        if (dungeons.isEmpty()) {
            player.sendMessage("§cNo dungeons found.");
            return;
        }

        for (Map.Entry<String, Dungeon> entry : dungeons.entrySet()) {
            player.sendMessage("§7- §f" + entry.getKey() + " §7(" + entry.getValue().getName() + ")");
        }
    }

    /**
     * Provides tab completion for the command.
     *
     * @param sender The command sender
     * @param command The command
     * @param alias The command alias
     * @param args The command arguments
     * @return A list of tab completions
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("partydungeon.admin")) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Add dungeon IDs
            for (Dungeon dungeon : dungeonManager.getDungeons().values()) {
                completions.add(dungeon.getId());
            }

            return filterCompletions(completions, args[0]);
        }

        return completions;
    }

    /**
     * Filters tab completions based on the current argument.
     *
     * @param completions The list of possible completions
     * @param current The current argument
     * @return A filtered list of completions
     */
    private List<String> filterCompletions(List<String> completions, String current) {
        if (current.isEmpty()) {
            return completions;
        }

        List<String> filtered = new ArrayList<>();

        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(current.toLowerCase())) {
                filtered.add(completion);
            }
        }

        return filtered;
    }
}
