package com.multiverse.npcai.ai;

/**
 * NPC가 위험으로부터 도망치는 행동을 담당하는 클래스
 */
public class FleeBehavior {
    private String threatSource;
    private boolean isFleeing;

    public FleeBehavior(String threatSource) {
        this.threatSource = threatSource;
        this.isFleeing = false;
    }

    public void startFleeing() {
        isFleeing = true;
    }

    public void stopFleeing() {
        isFleeing = false;
    }

    public boolean isFleeing() {
        return isFleeing;
    }

    public String getThreatSource() {
        return threatSource;
    }
}