package com.multiverse.party.skills;

import com.multiverse.party.models.PartySkill;
import java.util.*;

/**
 * 파티 스킬 등록/관리 (ID로 Skill 객체 관리, 조회)
 */
public class SkillRegistry {

    private final Map<String, PartySkill> skills = new HashMap<>();

    public void registerSkill(PartySkill skill) {
        if (skill == null || skill.getSkillId() == null) return;
        skills.put(skill.getSkillId(), skill);
    }

    public PartySkill getSkill(String skillId) {
        return skills.get(skillId);
    }

    public List<PartySkill> getAllSkills() {
        return new ArrayList<>(skills.values());
    }
}