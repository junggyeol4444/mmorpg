package com. multiverse.combat. data;

import org.bukkit.entity.Player;
import com.multiverse.combat.models. Skill;
import com.multiverse. combat.models.ComboData;
import com.multiverse. combat.models.PvPData;
import java.util.*;

/**
 * 데이터 관리 인터페이스
 * YAML, MySQL 등 다양한 저장소를 지원하기 위한 추상 클래스
 */
public abstract class DataManager {
    
    /**
     * 모든 데이터 로드
     */
    public abstract void loadAll();
    
    /**
     * 모든 데이터 저장
     */
    public abstract void saveAll();
    
    // ===== 스킬 데이터 =====
    
    /**
     * 모든 스킬 로드
     * @return 스킬 ID와 Skill 객체의 맵
     */
    public abstract Map<String, Skill> loadAllSkills();
    
    /**
     * 플레이어 스킬 데이터 로드
     * @param player 플레이어
     * @return 스킬 ID와 레벨의 맵
     */
    public abstract Map<String, Integer> loadPlayerSkills(Player player);
    
    /**
     * 플레이어 스킬 데이터 저장
     * @param player 플레이어
     * @param skills 스킬 ID와 레벨의 맵
     * @param hotbar 핫바 스킬 배열
     */
    public abstract void savePlayerSkills(Player player, Map<String, Integer> skills, String[] hotbar);
    
    /**
     * 플레이어 핫바 데이터 로드
     * @param player 플레이어
     * @return 핫바 스킬 ID 배열
     */
    public abstract String[] loadPlayerHotbar(Player player);
    
    // ===== 콤보 데이터 =====
    
    /**
     * 플레이어 콤보 데이터 로드
     * @param player 플레이어
     * @return 콤보 데이터
     */
    public abstract ComboData loadComboData(Player player);
    
    /**
     * 플레이어 콤보 데이터 저장
     * @param player 플레이어
     * @param combo 콤보 데이터
     */
    public abstract void saveComboData(Player player, ComboData combo);
    
    // ===== PvP 데이터 =====
    
    /**
     * 플레이어 PvP 데이터 로드
     * @param player 플레이어
     * @return PvP 데이터
     */
    public abstract PvPData loadPvPData(Player player);
    
    /**
     * 플레이어 PvP 데이터 저장
     * @param player 플레이어
     * @param pvpData PvP 데이터
     */
    public abstract void savePvPData(Player player, PvPData pvpData);
    
    // ===== 전투 통계 =====
    
    /**
     * 플레이어 전투 통계 로드
     * @param player 플레이어
     * @return 통계 맵
     */
    public abstract Map<String, Object> loadCombatStats(Player player);
    
    /**
     * 플레이어 전투 통계 저장
     * @param player 플레이어
     * @param stats 통계 맵
     */
    public abstract void saveCombatStats(Player player, Map<String, Object> stats);
    
    // ===== 유틸리티 =====
    
    /**
     * 플레이어 데이터 삭제
     * @param player 플레이어
     */
    public abstract void deletePlayerData(Player player);
    
    /**
     * 플레이어 데이터 존재 확인
     * @param player 플레이어
     * @return 존재하면 true
     */
    public abstract boolean playerDataExists(Player player);
    
    /**
     * 데이터 백업
     */
    public abstract void backup();
}