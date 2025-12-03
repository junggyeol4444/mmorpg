package com.multiverse.npcai.tasks;

/**
 * NPC 스킬 학습 처리 및 갱신을 담당하는 태스크 클래스
 */
public class SkillLearningTask implements Runnable {
    private boolean running = false;

    public void start() {
        running = true;
        // 실제 스케줄러와 연동해 주기적 실행
    }

    public void stop() {
        running = false;
        // 실제 스케줄러 해제
    }

    @Override
    public void run() {
        if (!running) return;
        // NPC 스킬 학습 처리 로직 (더미)
        System.out.println("[SkillLearningTask] NPC 스킬 학습을 처리합니다.");
    }
}