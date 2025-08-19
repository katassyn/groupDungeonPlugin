package maks.com.groupDungeonPlugin.commands;

import maks.com.groupDungeonPlugin.api.DungeonManager;
import maks.com.groupDungeonPlugin.api.GUIManager;
import maks.com.groupDungeonPlugin.models.Dungeon;
import maks.com.groupDungeonPlugin.models.DungeonCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Main plugin command.
 */
public class DungeonCommand implements CommandExecutor, TabCompleter {
    private final DungeonManager dungeonManager;
    private final GUIManager guiManager;
    
    /**
     * Creates a new dungeon command.
     *
     * @param dungeonManager The dungeon manager
     * @param guiManager The GUI manager
     */
    public DungeonCommand(DungeonManager dungeonManager, GUIManager guiManager) {
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
        
        if (args.length == 0) {
            // Open the category selection GUI
            guiManager.openCategoryGUI(player);
            return true;
        }
        
        if (args.length == 1) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("help")) {
                sendHelp(player);
                return true;
            }
            
            if (subCommand.equals("list")) {
                listCategories(player);
                return true;
            }
            
            // Check if the argument is a category ID
            DungeonCategory category = dungeonManager.getCategory(subCommand);
            if (category != null) {
                guiManager.openDungeonGUI(player, category.getId());
                return true;
            }
            
            // Check if the argument is a dungeon ID
            Dungeon dungeon = dungeonManager.getDungeon(subCommand);
            if (dungeon != null) {
                dungeonManager.enterDungeon(player, dungeon.getId());
                return true;
            }
            
            player.sendMessage("§cUnknown category or dungeon: " + subCommand);
            return true;
        }
        
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String id = args[1];
            
            if (subCommand.equals("enter")) {
                Dungeon dungeon = dungeonManager.getDungeon(id);
                if (dungeon != null) {
                    dungeonManager.enterDungeon(player, dungeon.getId());
                    return true;
                }
                
                player.sendMessage("§cUnknown dungeon: " + id);
                return true;
            }
            
            if (subCommand.equals("preview")) {
                Dungeon dungeon = dungeonManager.getDungeon(id);
                if (dungeon != null) {
                    guiManager.openPreviewGUI(player, dungeon.getId());
                    return true;
                }
                
                player.sendMessage("§cUnknown dungeon: " + id);
                return true;
            }
            
            if (subCommand.equals("category")) {
                DungeonCategory category = dungeonManager.getCategory(id);
                if (category != null) {
                    guiManager.openDungeonGUI(player, category.getId());
                    return true;
                }
                
                player.sendMessage("§cUnknown category: " + id);
                return true;
            }
        }
        
        sendHelp(player);
        return true;
    }
    
    /**
     * Sends help information to a player.
     *
     * @param player The player to send help to
     */
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Party Dungeon Help ===");
        player.sendMessage("§e/party_dungeon §7- Open the dungeon category selection GUI");
        player.sendMessage("§e/party_dungeon help §7- Show this help message");
        player.sendMessage("§e/party_dungeon list §7- List all dungeon categories");
        player.sendMessage("§e/party_dungeon <category> §7- Open the dungeon selection GUI for a category");
        player.sendMessage("§e/party_dungeon category <id> §7- Open the dungeon selection GUI for a category");
        player.sendMessage("§e/party_dungeon enter <dungeon> §7- Enter a dungeon");
        player.sendMessage("§e/party_dungeon preview <dungeon> §7- Preview rewards for a dungeon");
    }
    
    /**
     * Lists all dungeon categories to a player.
     *
     * @param player The player to send the list to
     */
    private void listCategories(Player player) {
        player.sendMessage("§6=== Dungeon Categories ===");
        
        for (DungeonCategory category : dungeonManager.getCategories().values()) {
            player.sendMessage("§e" + category.getName() + " §7(" + category.getId() + ") - " + 
                              category.getDungeons().size() + " dungeons");
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
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("help");
            completions.add("list");
            completions.add("enter");
            completions.add("preview");
            completions.add("category");
            
            // Add category IDs
            for (DungeonCategory category : dungeonManager.getCategories().values()) {
                completions.add(category.getId());
            }
            
            return filterCompletions(completions, args[0]);
        }
        
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("enter") || subCommand.equals("preview")) {
                // Add dungeon IDs
                for (Dungeon dungeon : dungeonManager.getDungeons().values()) {
                    completions.add(dungeon.getId());
                }
            }
            
            if (subCommand.equals("category")) {
                // Add category IDs
                for (DungeonCategory category : dungeonManager.getCategories().values()) {
                    completions.add(category.getId());
                }
            }
            
            return filterCompletions(completions, args[1]);
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