package maks.com.groupDungeonPlugin.models;

/**
 * Represents a single stage within a dungeon quest.
 */
public class QuestStage {
    private final int stageNumber;
    private final String description;
    private final String warp;

    public QuestStage(int stageNumber, String description, String warp) {
        this.stageNumber = stageNumber;
        this.description = description;
        this.warp = warp;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getWarp() {
        return warp;
    }
}

