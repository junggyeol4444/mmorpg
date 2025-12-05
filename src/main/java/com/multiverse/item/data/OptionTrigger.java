package com.multiverse.item.data;

public enum OptionTrigger {
    // 항상 활성화
    ALWAYS("항상", "항상 활성화되는 옵션입니다. "),
    
    // 공격 관련
    ON_ATTACK("공격시", "공격할 때 발동됩니다."),
    ON_HIT("명중시", "적에게 명중할 때 발동됩니다."),
    ON_CRITICAL("치명타시", "치명타가 발동될 때 작동합니다."),
    ON_KILL("처치시", "적을 처치할 때 발동됩니다."),
    
    // 방어 관련
    ON_DAMAGED("피격시", "피해를 입을 때 발동됩니다."),
    ON_BLOCK("방어시", "방어할 때 발동됩니다."),
    
    // 이동 관련
    ON_MOVE("이동시", "이동할 때 발동됩니다."),
    ON_SPRINT("질주시", "질주할 때 발동됩니다."),
    ON_JUMP("점프시", "점프할 때 발동됩니다."),
    
    // 시간 관련
    ON_PASSIVE("수동", "시간이 지남에 따라 계속 작동합니다."),
    ON_COOLDOWN("쿨타임", "쿨타임 후에 발동됩니다."),
    
    // 특수 조건
    ON_HEALTH_LOW("저체력시", "체력이 낮을 때 발동됩니다."),
    ON_HEALTH_FULL("만체력시", "체력이 가득 찼을 때 발동됩니다."),
    ON_KILL_STREAK("연속처치", "연속으로 적을 처치할 때 발동됩니다.");
    
    private String koreanName;
    private String description;
    
    OptionTrigger(String koreanName, String description) {
        this.koreanName = koreanName;
        this.description = description;
    }
    
    /**
     * 한글 이름 반환
     */
    public String getKoreanName() {
        return koreanName;
    }
    
    /**
     * 설명 반환
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 공격 관련 트리거인지 확인
     */
    public boolean isAttackTrigger() {
        return this == ON_ATTACK || this == ON_HIT || 
               this == ON_CRITICAL || this == ON_KILL;
    }
    
    /**
     * 방어 관련 트리거인지 확인
     */
    public boolean isDefenseTrigger() {
        return this == ON_DAMAGED || this == ON_BLOCK ||
               this == ON_HEALTH_LOW;
    }
    
    /**
     * 이동 관련 트리거인지 확인
     */
    public boolean isMovementTrigger() {
        return this == ON_MOVE || this == ON_SPRINT || this == ON_JUMP;
    }
    
    /**
     * 항상 활성화 트리거인지 확인
     */
    public boolean isAlwaysActive() {
        return this == ALWAYS || this == ON_PASSIVE;
    }
}