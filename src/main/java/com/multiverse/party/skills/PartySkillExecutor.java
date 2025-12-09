package com.multiverse.party.skills;

import com.multiverse.party.models.Party;
import com.multiverse.party.models.PartySkill;
import org.bukkit.entity.Player;

/**
 * 파티 스킬 실행기 - 특정 스킬을 파티 내에서 실제로 동작/적용
 */
public class PartySkillExecutor {

    /**
     * 스킬 실행(예시) - 각 스킬별 로직 분기
     */
    public boolean execute(Party party, Player caster, PartySkill skill) {
        if (party == null || caster == null || skill == null) return false;

        switch (skill.getSkillId()) {
            case "party_heal":
                return healParty(party, caster, skill);
            case "party_shield":
                return shieldParty(party, caster, skill);
            // TODO: 나머지 스킬 처리 추가
            default:
                return false;
        }
    }

    // 파티원 힐 (예시)
    private boolean healParty(Party party, Player caster, PartySkill skill) {
        // 힐 처리 로직
        return true;
    }

    // 파티원 실드 (예시)
    private boolean shieldParty(Party party, Player caster, PartySkill skill) {
        // 실드 처리 로직
        return true;
    }
}