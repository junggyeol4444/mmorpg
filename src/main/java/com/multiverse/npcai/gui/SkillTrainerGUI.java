package com.multiverse.npcai.gui;

import com.multiverse.npcai.models.TrainableSkill;
import com.multiverse.npcai.models.enums.TrainerType;
import java.util.List;

/**
 * NPC 트레이너 인터페이스를 관리하는 GUI 클래스
 */
public class SkillTrainerGUI {
    private String trainerName;
    private TrainerType trainerType;
    private List<TrainableSkill> skills;

    public SkillTrainerGUI(String trainerName, TrainerType trainerType, List<TrainableSkill> skills) {
        this.trainerName = trainerName;
        this.trainerType = trainerType;
        this.skills = skills;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public TrainerType getTrainerType() {
        return trainerType;
    }

    public List<TrainableSkill> getSkills() {
        return skills;
    }

    // 트레이너 스킬 목록 표시 기능 (실제 구현은 게임/엔진 시스템과 연동 필요)
    public void displaySkills() {
        System.out.println(trainerName + " (" + trainerType + ")의 수련 가능 스킬:");
        for (TrainableSkill skill : skills) {
            System.out.println("- " + skill.getDisplayName() + " (필요 레벨: " + skill.getRequiredLevel() + ", 비용: " + skill.getTrainingCost() + ")");
        }
    }
}