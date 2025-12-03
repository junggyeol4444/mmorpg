package com.multiverse.npcai.tasks;

/**
 * NPC 평판(명성) 점수가 시간에 따라 감소하도록 처리하는 태스크 클래스
 */
public class ReputationDecayTask implements Runnable {
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
        // NPC 평판 감소 로직 (더미)
        System.out.println("[ReputationDecayTask] NPC 평판 점수를 감소시킵니다.");
    }
}