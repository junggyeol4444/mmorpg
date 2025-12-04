package com.multiverse.quest.models.enums;

/**
 * 토큰 타입 열거형
 * 일일/주간 퀘스트에서 획득하는 토큰의 종류를 정의합니다.
 */
public enum TokenType {
    DAILY("일일 토큰", "매일 리셋되는 일일 퀘스트 보상 토큰", "🟡", 100),
    WEEKLY("주간 토큰", "매주 리셋되는 주간 퀘스트 보상 토큰", "🔵", 50);

    private final String displayName;
    private final String description;
    private final String emoji;
    private final int maxCapacity;  // 최대 보유 가능량

    /**
     * TokenType 생성자
     * @param displayName 표시명
     * @param description 설명
     * @param emoji 이모지
     * @param maxCapacity 최대 보유 가능량
     */
    TokenType(String displayName, String description, String emoji, int maxCapacity) {
        this. displayName = displayName;
        this.description = description;
        this.emoji = emoji;
        this.maxCapacity = maxCapacity;
    }

    /**
     * 표시명 반환
     * @return 표시명
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 설명 반환
     * @return 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 이모지 반환
     * @return 이모지
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * 최대 보유 가능량 반환
     * @return 최대 보유 가능량
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * 포맷된 토큰명 반환 (이모지 + 이름)
     * @return 포맷된 토큰명
     */
    public String getFormattedName() {
        return emoji + " " + displayName;
    }

    /**
     * 문자열로부터 TokenType 찾기
     * @param name 이름
     * @return TokenType (없으면 null)
     */
    public static TokenType fromString(String name) {
        try {
            return TokenType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 모든 토큰 타입 반환
     * @return TokenType 배열
     */
    public static TokenType[] getAllTokenTypes() {
        return TokenType.values();
    }

    /**
     * 일일 토큰인지 확인
     * @return 일일 토큰 여부
     */
    public boolean isDaily() {
        return this == DAILY;
    }

    /**
     * 주간 토큰인지 확인
     * @return 주간 토큰 여부
     */
    public boolean isWeekly() {
        return this == WEEKLY;
    }

    /**
     * 리셋 주기 반환 (시간 단위)
     * @return 리셋 주기 (시간)
     */
    public int getResetPeriodHours() {
        switch (this) {
            case DAILY:
                return 24;      // 24시간마다 리셋
            case WEEKLY:
                return 168;     // 7일(168시간)마다 리셋
            default:
                return 24;
        }
    }

    /**
     * 리셋 주기 반환 (분 단위)
     * @return 리셋 주기 (분)
     */
    public int getResetPeriodMinutes() {
        return getResetPeriodHours() * 60;
    }

    /**
     * 리셋 주기 반환 (밀리초 단위)
     * @return 리셋 주기 (밀리초)
     */
    public long getResetPeriodMillis() {
        return getResetPeriodMinutes() * 60L * 1000L;
    }

    /**
     * UI에 표시할 색상 코드 반환 (ChatColor 호환)
     * @return 색상 코드
     */
    public String getColorCode() {
        switch (this) {
            case DAILY:
                return "§e"; // 노랑색
            case WEEKLY:
                return "§b"; // 하늘색
            default:
                return "§f"; // 기본 흰색
        }
    }

    /**
     * 토큰 가격(교환값) 반환 (퀘스트 포인트 기준)
     * @return 토큰 가격
     */
    public int getExchangeValue() {
        switch (this) {
            case DAILY:
                return 10;   // 일일 토큰 1개 = 퀘스트 포인트 10개
            case WEEKLY:
                return 50;   // 주간 토큰 1개 = 퀘스트 포인트 50개
            default:
                return 10;
        }
    }

    /**
     * 기본 획득 보상 (퀘스트 1개 완료 시)
     * @return 기본 획득 토큰 수
     */
    public int getDefaultReward() {
        switch (this) {
            case DAILY:
                return 5;    // 일일 퀘스트 1개 = 토큰 5개
            case WEEKLY:
                return 15;   // 주간 퀘스트 1개 = 토큰 15개
            default:
                return 5;
        }
    }

    /**
     * 리셋 시간 반환 (하루 기준)
     * @return 리셋 시간 문자열 (HH:mm 형식)
     */
    public String getResetTime() {
        switch (this) {
            case DAILY:
                return "00:00";  // 매일 자정
            case WEEKLY:
                return "월 00:00"; // 매주 월요일 자정
            default:
                return "00:00";
        }
    }

    /**
     * 토큰 값 유효성 검증
     * @param amount 토큰 개수
     * @return 유효 여부
     */
    public boolean isValidAmount(int amount) {
        return amount >= 0 && amount <= maxCapacity;
    }

    /**
     * 토큰 개수 제한 적용 (최대값 초과 시 잘라냄)
     * @param amount 원래 토큰 개수
     * @return 제한이 적용된 토큰 개수
     */
    public int capAmount(int amount) {
        return Math.min(Math.max(amount, 0), maxCapacity);
    }

    /**
     * 토큰 부족 여부 확인
     * @param current 현재 토큰 개수
     * @param required 필요한 토큰 개수
     * @return 부족 여부
     */
    public boolean isInsufficient(int current, int required) {
        return current < required;
    }

    /**
     * 토큰 진행도 백분율 계산
     * @param current 현재 토큰 개수
     * @return 백분율 (0~100)
     */
    public int getProgressPercentage(int current) {
        return Math.min((int) ((double) current / maxCapacity * 100), 100);
    }

    /**
     * 토큰 상태 문자열 반환
     * @param current 현재 토큰 개수
     * @return 상태 문자열 (예: "5/100")
     */
    public String getStatusString(int current) {
        return String.format("%s: %d/%d", getFormattedName(), current, maxCapacity);
    }

    /**
     * 토큰 진행 표시줄 생성
     * @param current 현재 토큰 개수
     * @param barLength 표시줄 길이
     * @return 진행 표시줄 문자열
     */
    public String getProgressBar(int current, int barLength) {
        int percentage = getProgressPercentage(current);
        int filledLength = (int) ((double) percentage / 100 * barLength);
        
        StringBuilder bar = new StringBuilder();
        bar.append(getColorCode());
        
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        
        bar.append("§f ").append(percentage).append("%");
        return bar.toString();
    }

    /**
     * 다음 리셋까지의 시간 계산 (밀리초)
     * @param lastResetTime 마지막 리셋 시간 (타임스탬프)
     * @return 다음 리셋까지의 시간 (밀리초)
     */
    public long getTimeUntilReset(long lastResetTime) {
        long nextResetTime = lastResetTime + getResetPeriodMillis();
        long currentTime = System.currentTimeMillis();
        
        if (currentTime >= nextResetTime) {
            return 0; // 이미 리셋 가능
        }
        
        return nextResetTime - currentTime;
    }

    /**
     * 리셋 필요 여부 확인
     * @param lastResetTime 마지막 리셋 시간 (타임스탬프)
     * @return 리셋 필요 여부
     */
    public boolean needsReset(long lastResetTime) {
        return getTimeUntilReset(lastResetTime) == 0;
    }

    /**
     * 시간을 읽기 쉬운 형식으로 변환 (예: "2시간 30분")
     * @param millis 밀리초
     * @return 포맷된 시간 문자열
     */
    public static String formatTimeRemaining(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%d일 %d시간", days, hours % 24);
        } else if (hours > 0) {
            return String. format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String.format("%d초", seconds);
        }
    }
}