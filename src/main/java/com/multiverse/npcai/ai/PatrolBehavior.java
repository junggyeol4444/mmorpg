package com.multiverse.npcai.ai;

import java.util.List;

/**
 * 패트롤(순찰) 행동을 담당하는 클래스
 */
public class PatrolBehavior {
    private List<String> patrolPoints;
    private int currentPoint;

    public PatrolBehavior(List<String> patrolPoints) {
        this.patrolPoints = patrolPoints;
        this.currentPoint = 0;
    }

    public String getNextPatrolPoint() {
        if (patrolPoints.isEmpty()) return null;
        currentPoint = (currentPoint + 1) % patrolPoints.size();
        return patrolPoints.get(currentPoint);
    }

    public String getCurrentPatrolPoint() {
        if (patrolPoints.isEmpty()) return null;
        return patrolPoints.get(currentPoint);
    }
}