# Group Dungeon Plugin

A Minecraft plugin for creating and managing group dungeons with different difficulty levels and quests.

## Features

- Multiple difficulty levels (Blood, Hell, Infernal)
- Custom quests for each difficulty level
- GUI for selecting difficulty and quests
- Party size requirements
- Level requirements
- Entry fees
- Portal-based entry
- Command-based management

## Commands

- `/dungeon list` - List all available dungeons
- `/dungeon info <dungeon_id>` - Show information about a dungeon
- `/dungeon enter <dungeon_id>` - Enter a dungeon
- `/dungeon help` - Show help message

## Configuration

The plugin uses a `dungeons.yml` file to store dungeon and quest data. An example configuration is provided.

### Database Configuration

Reward previews for each dungeon are stored in a MySQL database. Configure the connection in `config.yml`:

```yaml
database:
  host: "localhost"
  port: 3306
  dbname: "groupdungeon"
  user: "dungeon_user"
  password: "strong_password"
  pool-size: 10
  table-prefix: ""
```

Administrators can edit the reward preview in game using `/edit_preview <dungeon_id>`; closing the GUI will persist changes to MySQL.

### Dungeon Configuration

Each dungeon is defined in `dungeons.yml` inside its category. Besides basic
information like name and level requirements you can now configure keys, warps
and quest stages:

```yaml
categories:
  mythology:
    dungeons:
      odyssey:
        name: "The Odyssey of Shadows"
        description: "Journey through the trials of Odysseus with dark twists"
        tier: 1
        requiredLevel: 20
        minPartySize: 1
        maxPartySize: 3
        icon: TRIDENT
        keyId: TRIPWIRE_HOOK            # optional key material
        keyDisplayName: "&6Shadow Key"  # optional key display name
        entryWarp: m1                   # warp used when entering
        stages:
          1:
            description: "Arrive at the haunted shores"
            warp: m1_s1
            triggerMob: "Shore Guardian"   # killing this mob advances the stage
          2:
            description: "Defeat the shadow cyclops"
            warp: m1_s2
            triggerMob: "Shadow Cyclops"
```

`entryWarp` sets the warp command executed when the party enters the dungeon.
Each stage contains a description sent to the party, a warp to teleport to and
an optional `triggerMob` name. When a mob with that name is killed the stage
advances automatically.

## Integration with Party System

This plugin is designed to work with the existing party system from the MyExperiencePlugin. It checks party size requirements before allowing players to enter dungeons.

## Future Enhancements

- Admin commands for creating and managing dungeons in-game
- More detailed quest objectives and completion tracking
- Custom rewards for completing quests
- Time limits for dungeons
- Leaderboards for fastest completion times