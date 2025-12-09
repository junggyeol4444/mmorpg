package com.multiverse.party.models.enums;

/**
 * 아이템 분배 방식
 */
public enum LootDistribution {
    FREE_FOR_ALL,         // 자유 획득
    ROUND_ROBIN,          // 라운드 로빈(순번제)
    MASTER_LOOTER,        // 마스터 룻(리더가 분배)
    VOTE                  // 투표(필요/욕심/패스)
}