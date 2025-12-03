package com.multiverse.npcai.tasks;

/**
 * 상점 재고 초기화 및 갱신을 관리하는 태스크 클래스
 */
public class ShopResetTask implements Runnable {
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
        // 상점 재고 초기화/갱신 로직 (더미)
        System.out.println("[ShopResetTask] NPC 상점의 재고를 초기화/갱신합니다.");
    }
}