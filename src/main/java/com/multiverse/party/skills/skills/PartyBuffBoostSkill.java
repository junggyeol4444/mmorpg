package com.multiverse.party.skills.skills;

import com.multiverse.party.models.Party;
import com.multiverse.party.models.PartySkill;

/**
 * 파티 버프 강화 스킬
 */
public class PartyBuffBoostSkill {

    public boolean execute(Party party, PartySkill skill) {
        if (party == null || skill == null) return false;

        // 버프 강화 로직 (예시: 모든 버프 효과 배수 적용)
        party.getActiveBuffs().forEach(buff -> {
            buff.setDuration(buff.getDuration() + 60); // 지속시간 60초 증가
            // 효과값 배수 예시
            buff.getEffects().replaceAll((k, v) -> v * 1.2);
        });
        return true;
    }
}