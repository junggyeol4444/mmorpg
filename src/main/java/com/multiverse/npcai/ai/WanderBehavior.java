package com.multiverse.npcai.ai;

import java.util.List;
import java.util.Random;

/**
 * 랜덤 워킹(배회) 행동을 담당하는 클래스
 */
public class WanderBehavior {
    private List<String> possiblePoints;
    private Random random;

    public WanderBehavior(List<String> possiblePoints) {
        this.possiblePoints = possiblePoints;
        this.random = new Random();
    }

    public String getRandomDestination() {
        if (possiblePoints == null || possiblePoints.isEmpty()) return null;
        int index = random.nextInt(possiblePoints.size());
        return possiblePoints.get(index);
    }
}