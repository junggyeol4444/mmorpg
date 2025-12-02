package com.multiverse.core.models;

public class FusionStatus {
    private boolean fused;
    private int currentStage;
    private Long stageStartTime;
    private Long stageEndTime;

    public FusionStatus(boolean fused, int currentStage, Long stageStartTime, Long stageEndTime) {
        this.fused = fused;
        this.currentStage = currentStage;
        this.stageStartTime = stageStartTime;
        this.stageEndTime = stageEndTime;
    }

    public boolean isFused() {
        return fused;
    }
    public void setFused(boolean fused) {
        this.fused = fused;
    }

    public int getCurrentStage() {
        return currentStage;
    }
    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public Long getStageStartTime() {
        return stageStartTime;
    }
    public void setStageStartTime(Long stageStartTime) {
        this.stageStartTime = stageStartTime;
    }

    public Long getStageEndTime() {
        return stageEndTime;
    }
    public void setStageEndTime(Long stageEndTime) {
        this.stageEndTime = stageEndTime;
    }
}