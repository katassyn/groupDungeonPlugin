package maks.com.groupDungeonPlugin.api;

import maks.com.groupDungeonPlugin.models.Dungeon;
import maks.com.groupDungeonPlugin.models.DungeonCategory;
import maks.com.groupDungeonPlugin.models.DungeonKey;
import maks.com.groupDungeonPlugin.database.MySQLManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages dungeons and categories.
 */
public class DungeonManager {
    private final Map<String, DungeonCategory> categories;
    private final Map<String, Dungeon> dungeons;
    private final JavaPlugin plugin;
    private final MySQLManager mySQLManager;

    // Debug flag
    private final static int debuggingFlag = 1;

    /**
     * Creates a new dungeon manager.
     *
     * @param plugin The plugin instance
     * @param mySQLManager The database manager for preview items
     */
    public DungeonManager(JavaPlugin plugin, MySQLManager mySQLManager) {
        this.plugin = plugin;
        this.mySQLManager = mySQLManager;
        this.categories = new HashMap<>();
        this.dungeons = new HashMap<>();
        initializeCategories();
        initializeDungeons();
        loadPreviewItemsFromDatabase();
    }

    /**
     * Initializes all dungeon categories.
     */
    private void initializeCategories() {
        // Mythology
        DungeonCategory mythology = new DungeonCategory(
            "mythology",
            "Mythology",
            "Ancient legends and mythological adventures",
            Material.GOLDEN_HELMET
        );
        categories.put(mythology.getId(), mythology);

        // Ancient Civilizations
        DungeonCategory ancient = new DungeonCategory(
            "ancient",
            "Ancient Civilizations",
            "Explore the wonders of the ancient world",
            Material.CHISELED_SANDSTONE
        );
        categories.put(ancient.getId(), ancient);

        // Fantasy Realms
        DungeonCategory fantasy = new DungeonCategory(
            "fantasy",
            "Fantasy Realms",
            "Magical worlds of fantasy and wonder",
            Material.DRAGON_EGG
        );
        categories.put(fantasy.getId(), fantasy);


        if (debuggingFlag == 1) {
            plugin.getLogger().info("Initialized " + categories.size() + " dungeon categories");
        }
    }

    /**
     * Initializes all dungeons.
     */
    private void initializeDungeons() {
        // Mythology dungeons
        Dungeon odyssey = new Dungeon(
            "mythology_odyssey",
            "The Odyssey of Shadows",
            "Journey through the trials of Odysseus with dark twists",
            1,  // tier
            20, // requiredLevel
            1,  // minPartySize
            3,  // maxPartySize
            Material.TRIDENT,
            "mythology",
            "iron_ingot",
            "&2Broken Armor Piece"
        );
        dungeons.put(odyssey.getId(), odyssey);
        categories.get(odyssey.getCategoryId()).addDungeon(odyssey);

        Dungeon poseidon = new Dungeon(
            "mythology_poseidon",
            "Poseidon's Mist Isle",
            "Navigate the treacherous waters of Poseidon's domain",
            2,  // tier
            40, // requiredLevel
            2,  // minPartySize
            4,  // maxPartySize
            Material.HEART_OF_THE_SEA,
            "mythology"
        );
        dungeons.put(poseidon.getId(), poseidon);
        categories.get(poseidon.getCategoryId()).addDungeon(poseidon);

        Dungeon zeus = new Dungeon(
            "mythology_zeus",
            "Mount Olympus",
            "Challenge the gods atop Mount Olympus",
            3,  // tier
            60, // requiredLevel
            3,  // minPartySize
            5,  // maxPartySize
            Material.LIGHTNING_ROD,
            "mythology"
        );
        dungeons.put(zeus.getId(), zeus);
        categories.get(zeus.getCategoryId()).addDungeon(zeus);

        Dungeon daedalus = new Dungeon(
            "mythology_daedalus",
            "Daedalus' Eternal Labyrinth",
            "Find your way through the ever-changing maze",
            4,  // tier
            80, // requiredLevel
            3,  // minPartySize
            5,  // maxPartySize
            Material.COBWEB,
            "mythology"
        );
        dungeons.put(daedalus.getId(), daedalus);
        categories.get(daedalus.getCategoryId()).addDungeon(daedalus);

        Dungeon souls = new Dungeon(
            "mythology_souls",
            "Fields of Immortal Souls",
            "Journey through the afterlife to challenge Hades himself",
            5,  // tier
            100, // requiredLevel
            5,  // minPartySize
            5,  // maxPartySize
            Material.SOUL_LANTERN,
            "mythology"
        );
        dungeons.put(souls.getId(), souls);
        categories.get(souls.getCategoryId()).addDungeon(souls);

        // Ancient Civilizations dungeons
        Dungeon atlantis = new Dungeon(
            "ancient_atlantis",
            "Sunken Spires of Atlantis",
            "Explore the mysterious underwater city",
            1,  // tier
            25, // requiredLevel
            2,  // minPartySize
            3,  // maxPartySize
            Material.PRISMARINE_BRICKS,
            "ancient"
        );
        dungeons.put(atlantis.getId(), atlantis);
        categories.get(atlantis.getCategoryId()).addDungeon(atlantis);

        Dungeon pyramid = new Dungeon(
            "ancient_pyramid",
            "Pyramid of Pharaoh Khufu's Curse",
            "Navigate the trapped corridors of the ancient pyramid",
            2,  // tier
            45, // requiredLevel
            2,  // minPartySize
            4,  // maxPartySize
            Material.SANDSTONE_STAIRS,
            "ancient"
        );
        dungeons.put(pyramid.getId(), pyramid);
        categories.get(pyramid.getCategoryId()).addDungeon(pyramid);

        Dungeon gardens = new Dungeon(
            "ancient_gardens",
            "Hanging Gardens of Queen Amytis",
            "Discover the secrets hidden within the legendary gardens",
            3,  // tier
            65, // requiredLevel
            3,  // minPartySize
            4,  // maxPartySize
            Material.FLOWERING_AZALEA,
            "ancient"
        );
        dungeons.put(gardens.getId(), gardens);
        categories.get(gardens.getCategoryId()).addDungeon(gardens);

        Dungeon colosseum = new Dungeon(
            "ancient_colosseum",
            "Bloodstained Colosseum of Emperor Titus",
            "Fight for glory in the ancient Roman arena",
            4,  // tier
            85, // requiredLevel
            3,  // minPartySize
            5,  // maxPartySize
            Material.IRON_BARS,
            "ancient"
        );
        dungeons.put(colosseum.getId(), colosseum);
        categories.get(colosseum.getCategoryId()).addDungeon(colosseum);

        Dungeon wall = new Dungeon(
            "ancient_wall",
            "Dragon Emperor's Forbidden Wall",
            "Scale the massive wall and face the guardians within",
            5,  // tier
            105, // requiredLevel
            4,  // minPartySize
            5,  // maxPartySize
            Material.MOSSY_STONE_BRICK_WALL,
            "ancient"
        );
        dungeons.put(wall.getId(), wall);
        categories.get(wall.getCategoryId()).addDungeon(wall);

        // Fantasy Realms dungeons
        Dungeon drakonis = new Dungeon(
            "fantasy_drakonis",
            "Lair of Drakonis the Undying",
            "Confront the immortal dragon in its volcanic lair",
            1,  // tier
            30, // requiredLevel
            2,  // minPartySize
            3,  // maxPartySize
            Material.DRAGON_HEAD,
            "fantasy"
        );
        dungeons.put(drakonis.getId(), drakonis);
        categories.get(drakonis.getCategoryId()).addDungeon(drakonis);

        Dungeon woods = new Dungeon(
            "fantasy_woods",
            "Whispering Woods of Eternal Twilight",
            "Navigate the enchanted forest where day never comes",
            2,  // tier
            50, // requiredLevel
            2,  // minPartySize
            4,  // maxPartySize
            Material.DARK_OAK_LOG,
            "fantasy"
        );
        dungeons.put(woods.getId(), woods);
        categories.get(woods.getCategoryId()).addDungeon(woods);

        Dungeon caverns = new Dungeon(
            "fantasy_caverns",
            "Crystal Caverns of the Fae Kingdom",
            "Discover the hidden realm of the fae beneath the earth",
            3,  // tier
            70, // requiredLevel
            3,  // minPartySize
            4,  // maxPartySize
            Material.AMETHYST_CLUSTER,
            "fantasy"
        );
        dungeons.put(caverns.getId(), caverns);
        categories.get(caverns.getCategoryId()).addDungeon(caverns);

        Dungeon spire = new Dungeon(
            "fantasy_spire",
            "Celestial Spire of the Ascended Mage",
            "Climb the tower of the most powerful wizard in the realm",
            4,  // tier
            90, // requiredLevel
            3,  // minPartySize
            5,  // maxPartySize
            Material.END_ROD,
            "fantasy"
        );
        dungeons.put(spire.getId(), spire);
        categories.get(spire.getCategoryId()).addDungeon(spire);

        Dungeon citadel = new Dungeon(
            "fantasy_citadel",
            "Shadowfell Citadel of the Banished King",
            "Enter the dark fortress of the exiled monarch",
            5,  // tier
            110, // requiredLevel
            4,  // minPartySize
            5,  // maxPartySize
            Material.NETHERITE_BLOCK,
            "fantasy"
        );
        dungeons.put(citadel.getId(), citadel);
        categories.get(citadel.getCategoryId()).addDungeon(citadel);



        if (debuggingFlag == 1) {
            plugin.getLogger().info("Initialized " + dungeons.size() + " dungeons");
        }
    }

    /**
     * Loads drops from the database.
     */
    private void loadPreviewItemsFromDatabase() {
        for (Dungeon dungeon : dungeons.values()) {
            Map<Integer, ItemStack> items = mySQLManager.loadPreviewItems(dungeon.getId());
            dungeon.setPreviewItems(items);

            if (debuggingFlag == 1) {
                plugin.getLogger().info("Loaded " + items.size() + " preview items for dungeon " + dungeon.getName());
            }
        }
    }

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
            // TODO: Replace with your level system
            int playerLevel = 100; // Placeholder for now

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

            // Create a key object
            Material keyMaterial;
            try {
                keyMaterial = Material.getMaterial(keyId.toUpperCase());
                if (keyMaterial == null) {
                    keyMaterial = Material.TRIPWIRE_HOOK; // Default if not found
                }
            } catch (Exception e) {
                keyMaterial = Material.TRIPWIRE_HOOK;
            }

            DungeonKey key = new DungeonKey(keyId, keyName, keyMaterial);

            // Check if the party leader has the key
            if (!key.consumeKey(leader.getInventory())) {
                if (debuggingFlag == 1) {
                    plugin.getLogger().info("Player " + leader.getName() + " doesn't have the required key: " + keyName);
                }

                leader.sendMessage("§cYou need a key to enter this dungeon: §f" + keyName.replace("&", "§"));
                return false;
            }

            if (debuggingFlag == 1) {
                plugin.getLogger().info("Key " + keyId + " consumed from player " + leader.getName());
            }
        }

        // Special handling for each dungeon
        String warpCommand = "warp " + dungeonId.toLowerCase();
        String entryMessage = "§aEntering dungeon: §f" + dungeon.getName();

        // Override for specific dungeons
        if (dungeonId.equals("mythology_odyssey")) {
            warpCommand = "warp m1";
            entryMessage = "§aEntering The Odyssey of Shadows";
        } else if (dungeonId.equals("mythology_poseidon")) {
            warpCommand = "warp m2";
            entryMessage = "§aEntering Poseidon's Mist Isle";
        } else if (dungeonId.equals("mythology_zeus")) {
            warpCommand = "warp m3";
            entryMessage = "§aEntering Throne of Zeus (Mount Olympus)";
        } else if (dungeonId.startsWith("ancient_")) {
            warpCommand = "warp a" + dungeonId.charAt(dungeonId.length() - 1);
        } else if (dungeonId.startsWith("fantasy_")) {
            warpCommand = "warp f" + dungeonId.charAt(dungeonId.length() - 1);
        }

        // Send pre-entry message
        for (Player member : partyMembers) {
            member.sendMessage("§6Preparing to enter dungeon...");
        }

        // Execute warp command
        if (debuggingFlag == 1) {
            plugin.getLogger().info("Executing warp command: " + warpCommand + " for player " + leader.getName());
        }

        leader.performCommand(warpCommand);

        // Notify all party members
        for (Player member : partyMembers) {
            member.sendMessage(entryMessage);
            member.sendMessage("§eGood luck on your adventure!");
        }

        if (debuggingFlag == 1) {
            plugin.getLogger().info("Party led by " + leader.getName() + " entered dungeon " + dungeon.getName());
        }

        return true;
    }

    /**
     * Gets all dungeon categories.
     *
     * @return A map of categories, keyed by their IDs
     */
    public Map<String, DungeonCategory> getCategories() {
        return categories;
    }

    /**
     * Gets a dungeon category by its ID.
     *
     * @param categoryId The ID of the category
     * @return The category, or null if not found
     */
    public DungeonCategory getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    /**
     * Gets all dungeons.
     *
     * @return A map of dungeons, keyed by their IDs
     */
    public Map<String, Dungeon> getDungeons() {
        return dungeons;
    }

    /**
     * Gets a dungeon by its ID.
     *
     * @param dungeonId The ID of the dungeon
     * @return The dungeon, or null if not found
     */
    public Dungeon getDungeon(String dungeonId) {
        return dungeons.get(dungeonId);
    }

    /**
     * Gets all dungeons in a category.
     *
     * @param categoryId The ID of the category
     * @return A list of dungeons in the category
     */
    public List<Dungeon> getDungeonsByCategory(String categoryId) {
        DungeonCategory category = categories.get(categoryId);
        return category != null ? category.getDungeons() : null;
    }

    /**
     * Saves drops for a dungeon to the database.
     *
     * @param dungeonId The ID of the dungeon
     */
    public void saveDrops(String dungeonId) {
        Dungeon dungeon = dungeons.get(dungeonId);
        if (dungeon != null) {
            mySQLManager.savePreviewItems(dungeonId, dungeon.getPreviewItems());
            if (debuggingFlag == 1) {
                plugin.getLogger().info("Saved " + dungeon.getPreviewItems().size() +
                        " preview items for dungeon " + dungeon.getName());
            }
        }
    }
}
