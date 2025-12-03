package com.multiverse.npcai.tasks;

/**
 * NPC AI 상태를 주기적으로 업데이트하는 태스크 클래스
 */
public class AIUpdateTask implements Runnable {
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
        // NPC AI 상태 갱신 로직 (더미)
        System.out.println("[AIUpdateTask] NPC AI를 주기적으로 업데이트합니다.");
    }
}