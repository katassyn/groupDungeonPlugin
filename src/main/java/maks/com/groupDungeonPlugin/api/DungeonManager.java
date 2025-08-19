package maks.com.groupDungeonPlugin.api;

import maks.com.groupDungeonPlugin.models.Dungeon;
import maks.com.groupDungeonPlugin.models.DungeonCategory;
import maks.com.groupDungeonPlugin.models.QuestStage;
import maks.com.groupDungeonPlugin.database.DatabaseManager;
import maks.com.groupDungeonPlugin.api.PartyManager;
import maks.com.groupDungeonPlugin.api.MyExperienceAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

/**
 * Manages dungeons and categories.
 */
public class DungeonManager {
    private final Map<String, DungeonCategory> categories;
    private final Map<String, Dungeon> dungeons;
    private final JavaPlugin plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, Integer> playerStages;

    private static final int debuggingFlag = 1;

    public DungeonManager(JavaPlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.categories = new LinkedHashMap<>();
        this.dungeons = new HashMap<>();
        this.playerStages = new HashMap<>();
        loadDungeonConfig();
        loadPreviewItems();
    }

    /**
     * Loads categories and dungeons from the dungeons.yml file.
     */
    private void loadDungeonConfig() {
        File file = new File(plugin.getDataFolder(), "dungeons.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection categoriesSection = config.getConfigurationSection("categories");
        if (categoriesSection == null) return;

        for (String categoryId : categoriesSection.getKeys(false)) {
            ConfigurationSection catSec = categoriesSection.getConfigurationSection(categoryId);
            String name = catSec.getString("name");
            String description = catSec.getString("description");
            Material icon = Material.matchMaterial(catSec.getString("icon", "STONE"));
            DungeonCategory category = new DungeonCategory(categoryId, name, description, icon);
            categories.put(categoryId, category);

            ConfigurationSection dungeonsSection = catSec.getConfigurationSection("dungeons");
            if (dungeonsSection != null) {
                for (String dungeonId : dungeonsSection.getKeys(false)) {
                    ConfigurationSection dSec = dungeonsSection.getConfigurationSection(dungeonId);
                    String dName = dSec.getString("name");
                    String dDesc = dSec.getString("description");
                    int tier = dSec.getInt("tier");
                    int requiredLevel = dSec.getInt("requiredLevel");
                    int minParty = dSec.getInt("minPartySize");
                    int maxParty = dSec.getInt("maxPartySize");
                    Material dIcon = Material.matchMaterial(dSec.getString("icon", "STONE"));
                    String fullId = categoryId + "_" + dungeonId;
                    Dungeon dungeon = new Dungeon(fullId, dName, dDesc, tier, requiredLevel,
                            minParty, maxParty, dIcon, categoryId);
                    if (dSec.contains("keyId") && dSec.contains("keyDisplayName")) {
                        dungeon.setKeyId(dSec.getString("keyId"));
                        dungeon.setKeyDisplayName(dSec.getString("keyDisplayName"));
                    }
                    dungeons.put(fullId, dungeon);
                    category.addDungeon(dungeon);
                }
            }
        }

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Loaded " + categories.size() + " categories and " + dungeons.size() + " dungeons from config");
        }
    }

    /**
     * Loads preview items for all dungeons from the database.
     */
    private void loadPreviewItems() {
        for (Dungeon dungeon : dungeons.values()) {
            Map<Integer, ItemStack> items = databaseManager.loadPreviewItems(dungeon.getId());
            dungeon.setPreviewItems(items);
            if (debuggingFlag == 1) {
                plugin.getLogger().info("Loaded " + items.size() + " preview items for dungeon " + dungeon.getName());
            }
        }
    }

    // ---------------------------------------------------------------------
    // Existing functionality
    // ---------------------------------------------------------------------

    /**
     * Checks if a party can enter a dungeon.
     *
     * @param leader The party leader
     * @param dungeonId The ID of the dungeon
     * @return True if the party can enter the dungeon, false otherwise
     */
    public boolean canEnterDungeon(Player leader, String dungeonId) {
        Dungeon dungeon = dungeons.get(dungeonId);
        if (dungeon == null) {
            leader.sendMessage("§cDungeon not found: " + dungeonId);
            return false;
        }

        // Check if player is party leader
        PartyManager partyManager = new PartyManager(plugin);
        if (!partyManager.isPartyLeader(leader)) {
            leader.sendMessage("§cYou must be the party leader to enter a dungeon.");
            return false;
        }

        // Check if player is in a party
        if (!partyManager.isInParty(leader)) {
            leader.sendMessage("§cYou must be in a party to enter a dungeon.");
            leader.sendMessage("§cUse /party create to create a party.");
            return false;
        }

        // Check if party has appropriate size
        int partySize = partyManager.getPartySize(leader);
        if (partySize < dungeon.getMinPartySize() || partySize > dungeon.getMaxPartySize()) {
            leader.sendMessage("§cYour party size (" + partySize + ") does not meet the requirements (" +
                              dungeon.getMinPartySize() + "-" + dungeon.getMaxPartySize() + ").");
            return false;
        }

        // Check if all players have required level
        List<Player> partyMembers = partyManager.getPartyMembers(leader);
        for (Player member : partyMembers) {
            int playerLevel = getPlayerLevel(member);
            if (playerLevel < dungeon.getRequiredLevel()) {
                leader.sendMessage("§c" + member.getName() + " does not meet the level requirement (" +
                                  dungeon.getRequiredLevel() + "+).");
                return false;
            }
        }

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Party led by " + leader.getName() + " meets all requirements for dungeon: " + dungeon.getName());
        }

        return true;
    }

    /**
     * Handles dungeon entry.
     *
     * @param leader The party leader
     * @param dungeonId The ID of the dungeon
     * @return True if the party successfully entered the dungeon, false otherwise
     */
    public boolean enterDungeon(Player leader, String dungeonId) {
        if (!canEnterDungeon(leader, dungeonId)) {
            if (debuggingFlag == 1) {
                plugin.getLogger().info("Player " + leader.getName() + " cannot enter dungeon " + dungeonId);
            }
            return false;
        }

        Dungeon dungeon = dungeons.get(dungeonId);
        PartyManager partyManager = new PartyManager(plugin);
        List<Player> partyMembers = partyManager.getPartyMembers(leader);

        // Check if there's a required key
        if (dungeon.requiresKey()) {
            String keyId = dungeon.getKeyId();
            String keyName = dungeon.getKeyDisplayName();

            if (debuggingFlag == 1) {
                plugin.getLogger().info("Dungeon " + dungeon.getName() + " requires key: " + keyId);
            }

            Material keyMaterial = Material.TRIPWIRE_HOOK;
            try {
                Material matched = Material.getMaterial(keyId.toUpperCase());
                if (matched != null) {
                    keyMaterial = matched;
                }
            } catch (Exception ignored) {
            }

            Inventory inv = leader.getInventory();
            boolean removed = false;
            String strippedRequired = ChatColor.stripColor(keyName.replace("&", "§"));
            ItemStack[] contents = inv.getContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item == null || item.getType() != keyMaterial) continue;
                ItemMeta meta = item.getItemMeta();
                if (meta == null || !meta.hasDisplayName()) continue;
                String stripped = ChatColor.stripColor(meta.getDisplayName());
                if (stripped.equals(strippedRequired)) {
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        inv.setItem(i, null);
                    }
                    removed = true;
                    break;
                }
            }

            if (!removed) {
                leader.sendMessage("§cYou need the key: " + keyName.replace("&", "§"));
                return false;
            }
        }

        String warpCommand = "warp " + dungeonId.toLowerCase();
        String entryMessage = "§aEntering dungeon: §f" + dungeon.getName();

        if (dungeonId.equals("mythology_odyssey")) {
            warpCommand = "warp m1";
            entryMessage = "§aEntering The Odyssey of Shadows";
        } else if (dungeonId.equals("mythology_poseidon")) {
            warpCommand = "warp m2";
            entryMessage = "§aEntering Poseidon's Mist Isle";
        } else if (dungeonId.equals("mythology_zeus")) {
            warpCommand = "warp m3";
            entryMessage = "§aEntering Throne of Zeus (Mount Olympus)";
        }

        for (Player member : partyMembers) {
            member.sendMessage("§6Preparing to enter dungeon...");
        }

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Executing warp command: " + warpCommand + " for player " + leader.getName());
        }

        leader.performCommand(warpCommand);

        for (Player member : partyMembers) {
            member.sendMessage(entryMessage);
            member.sendMessage("§eGood luck on your adventure!");
        }

        playerStages.put(leader.getUniqueId(), 1);

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Party led by " + leader.getName() + " entered dungeon " + dungeon.getName());
        }

        return true;
    }

    /**
     * Advances the party to the next quest stage for a dungeon.
     *
     * @param leader    party leader
     * @param dungeonId dungeon identifier
     */
    public void advanceStage(Player leader, String dungeonId) {
        Dungeon dungeon = dungeons.get(dungeonId);
        if (dungeon == null) return;

        int stageIndex = playerStages.getOrDefault(leader.getUniqueId(), 0);
        List<QuestStage> stages = dungeon.getQuestStages();
        if (stageIndex >= stages.size()) {
            leader.sendMessage("§cNo more stages in this dungeon.");
            return;
        }

        QuestStage stage = stages.get(stageIndex);
        PartyManager partyManager = new PartyManager(plugin);
        List<Player> members = partyManager.getPartyMembers(leader);
        for (Player member : members) {
            member.performCommand("warp " + stage.getWarp());
            member.sendMessage("§a" + stage.getDescription());
        }

        playerStages.put(leader.getUniqueId(), stageIndex + 1);
    }

    /**
     * Gets player level from MyExperiencePlugin if available.
     */
    private int getPlayerLevel(Player player) {
        Plugin exp = Bukkit.getPluginManager().getPlugin("MyExperiencePlugin");
        if (exp instanceof MyExperienceAPI) {
            return ((MyExperienceAPI) exp).getPlayerLevel(player);
        }
        return 0;
    }

    // ---------------------------------------------------------------------
    // Getters and persistence
    // ---------------------------------------------------------------------

    public Map<String, DungeonCategory> getCategories() {
        return categories;
    }

    public DungeonCategory getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    public Map<String, Dungeon> getDungeons() {
        return dungeons;
    }

    public Dungeon getDungeon(String dungeonId) {
        return dungeons.get(dungeonId);
    }

    public List<Dungeon> getDungeonsByCategory(String categoryId) {
        DungeonCategory category = categories.get(categoryId);
        return category != null ? category.getDungeons() : Collections.emptyList();
    }

    /**
     * Saves preview items for a dungeon to the database.
     *
     * @param dungeonId The ID of the dungeon
     */
    public void savePreviewItems(String dungeonId) {
        Dungeon dungeon = dungeons.get(dungeonId);
        if (dungeon != null) {
            databaseManager.savePreviewItems(dungeonId, dungeon.getPreviewItems());
            if (debuggingFlag == 1) {
                plugin.getLogger().info("Saved preview items for dungeon " + dungeon.getName());
            }
        }
    }
}
