package com.multiverse.npcai.data;

import com.multiverse.npcai.models.*;
import com.multiverse.npcai.models.enums.*;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * 핵심 데이터 API 추상(인터페이스 역할)
 * YAMLDataManager 등 실제 구현체가 상속
 */
public abstract class DataManager {
    // NPC 데이터
    public abstract List<NPCData> loadAllNPCs();
    public abstract void saveNPC(NPCData npc);
    public abstract void deleteNPC(int npcId);

    // 호감도 데이터
    public abstract Reputation getReputation(UUID playerUUID, int npcId);
    public abstract List<Reputation> getAllReputations(UUID playerUUID);
    public abstract void saveReputation(Reputation rep);

    // 상점
    public abstract Shop getShop(String shopId);
    public abstract Shop getShopByNPC(int npcId);
    public abstract void saveShop(Shop shop);
    public abstract List<Shop> getAllShops();
    public abstract void deleteShop(String shopId);

    // 트레이너, 스킬
    public abstract SkillTrainer getTrainer(int npcId);
    public abstract void saveTrainer(SkillTrainer trainer);

    // 스킬 학습
    public abstract void startSkillLearning(UUID playerUUID, TrainableSkill skill, int npcId, int seconds);
    public abstract void completeSkillLearning(UUID playerUUID, String skillId);
    public abstract List<String> getLearningSkills(UUID playerUUID);
    public abstract List<String> getCompletedSkills(UUID playerUUID);
    public abstract int getDailyLearnedSkillsCount(UUID playerUUID, int npcId);

    // 대화
    public abstract Map<String, DialogueNode> loadDialogueTree(int npcId, String filePath);
    public abstract String getDialogueStartNode(int npcId, String filePath);

    // 캐싱/로딩/저장
    public abstract void loadAll();
    public abstract void saveAll();
    public abstract void reload();
}