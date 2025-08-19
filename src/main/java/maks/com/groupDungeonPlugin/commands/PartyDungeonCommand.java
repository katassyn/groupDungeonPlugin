package maks.com.groupDungeonPlugin.commands;

import maks.com.groupDungeonPlugin.api.DungeonManager;
import maks.com.groupDungeonPlugin.api.PartyIntegrationAPI;
import maks.com.groupDungeonPlugin.gui.CategorySelectionGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command for opening the dungeon category selection GUI
 */
public class PartyDungeonCommand implements CommandExecutor {
    private final DungeonManager dungeonManager;
    private static final int debuggingFlag = 1;

    public PartyDungeonCommand(DungeonManager dungeonManager) {
        this.dungeonManager = dungeonManager;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // Check if player is in a party
        if (!PartyIntegrationAPI.isInParty(player)) {
            player.sendMessage("§cYou must be in a party to use this command!");
            player.sendMessage("§cUse /party create to create a party.");
            return true;
        }

        // Check if player is the party leader
        if (!PartyIntegrationAPI.isPartyLeader(player)) {
            player.sendMessage("§cOnly the party leader can use this command!");
            return true;
        }

        // Open the category selection GUI
        CategorySelectionGUI gui = new CategorySelectionGUI(player, dungeonManager.getCategories());
        gui.open();

        if (debuggingFlag == 1) {
            player.getServer().getLogger().info("[PartyDungeon] Player " + player.getName() + 
                                               " opened category selection GUI with " + 
                                               dungeonManager.getCategories().size() + " categories");
        }

        return true;
    }
}
