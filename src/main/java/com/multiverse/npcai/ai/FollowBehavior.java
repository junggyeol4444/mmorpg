package com.multiverse.npcai.ai;

/**
 * NPC가 대상을 따라가는 행동을 담당하는 클래스
 */
public class FollowBehavior {
    private String targetId;
    private boolean isFollowing;

    public FollowBehavior(String targetId) {
        this.targetId = targetId;
        this.isFollowing = false;
    }

    public void startFollowing() {
        isFollowing = true;
    }

    public void stopFollowing() {
        isFollowing = false;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public String getTargetId() {
        return targetId;
    }
}