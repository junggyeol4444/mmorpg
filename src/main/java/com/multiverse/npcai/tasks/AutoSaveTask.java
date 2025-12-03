package com.multiverse.npcai.tasks;

/**
 * NPC 데이터 자동 저장을 처리하는 주기적 태스크 클래스
 */
public class AutoSaveTask implements Runnable {
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
        // NPC 데이터 자동 저장 로직 (더미)
        System.out.println("[AutoSaveTask] NPC 데이터를 주기적으로 저장합니다.");
    }
}