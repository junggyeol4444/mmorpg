package com.multiverse.npcai.ai;

/**
 * 특정 지역 또는 오브젝트를 지키는 행동을 담당하는 클래스
 */
public class GuardBehavior {
    private String guardTarget;
    private boolean isGuarding;

    public GuardBehavior(String guardTarget) {
        this.guardTarget = guardTarget;
        this.isGuarding = false;
    }

    public void startGuarding() {
        isGuarding = true;
    }

    public void stopGuarding() {
        isGuarding = false;
    }

    public boolean isGuarding() {
        return isGuarding;
    }

    public String getGuardTarget() {
        return guardTarget;
    }
}