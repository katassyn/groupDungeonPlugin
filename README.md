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

### Dungeon Configuration

```yaml
dungeons:
  example-dungeon-id:
    name: "Example Dungeon"
    description: "An example dungeon for testing purposes"
    entrance:
      world: "world"
      x: 0
      y: 64
      z: 0
      yaw: 0
      pitch: 0
    entry-fee: 100
    min-party-size: 2
    max-party-size: 5
    quests:
      # Blood difficulty quests
      blood:
        example-blood-quest-id:
          name: "Blood Quest 1"
          description: "A blood difficulty quest"
          required-level: 10
          reward-experience: 100
          reward-money: 50
          spawn:
            world: "world"
            x: 100
            y: 64
            z: 100
            yaw: 0
            pitch: 0
```

## Integration with Party System

This plugin is designed to work with the existing party system from the MyExperiencePlugin. It checks party size requirements before allowing players to enter dungeons.

## Future Enhancements

- Admin commands for creating and managing dungeons in-game
- More detailed quest objectives and completion tracking
- Custom rewards for completing quests
- Time limits for dungeons
- Leaderboards for fastest completion times