package com.multiverse.guild.model;

public class QuestObjective {
    private String objectiveId;
    private String description;
    private int required;
    private int current;

    public QuestObjective(String objectiveId, String description, int required, int current) {
        this.objectiveId = objectiveId;
        this.description = description;
        this.required = required;
        this.current = current;
    }

    public String getObjectiveId() { return objectiveId; }
    public String getDescription() { return description; }
    public int getRequired() { return required; }
    public void setRequired(int required) { this.required = required; }
    public int getCurrent() { return current; }
    public void setCurrent(int current) { this.current = current; }
}