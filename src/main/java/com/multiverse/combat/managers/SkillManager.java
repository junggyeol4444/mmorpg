package com.multiverse.combat. managers;

import org.bukkit.entity.Player;
import org.bukkit. Bukkit;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models. Skill;
import com.multiverse.combat.models.enums.CostType;
import java.util.*;

/**
 * 스킬 관리 클래스
 * 플레이어의 스킬 학습, 업그레이드, 사용을 관리합니다. 
 */
public class SkillManager {
    
    private final CombatCore plugin;
    private final Map<String, Skill> skillRegistry = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> playerSkillLevels = new HashMap<>();
    private final Map<UUID, Map<String, Long>> playerSkillCooldowns = new HashMap<>();
    private final Map<UUID, String[]> playerHotbars = new HashMap<>();
    private final Map<String, Integer> skillExp = new HashMap<>();
    
    // 스킬 경험치 필요량
    private static final int BASE_EXP_REQUIREMENT = 100;
    
    /**
     * SkillManager 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public SkillManager(CombatCore plugin) {
        this.plugin = plugin;
        loadSkills();
        initializePlayerData();
    }
    
    /**
     * 스킬 레지스트리에서 로드
     */
    private void loadSkills() {
        // 스킬 데이터는 YAMLDataManager에서 로드됨
        Map<String, Skill> loadedSkills = plugin.getDataManager().loadAllSkills();
        if (loadedSkills != null) {
            skillRegistry.putAll(loadedSkills);
        }
        plugin.getLogger().info("✓ " + skillRegistry.size() + "개의 스킬이 로드되었습니다.");
    }
    
    /**
     * 플레이어 데이터 초기화
     */
    private void initializePlayerData() {
        for (Player player : Bukkit. getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            playerSkillLevels.putIfAbsent(uuid, new HashMap<>());
            playerSkillCooldowns.putIfAbsent(uuid, new HashMap<>());
            playerHotbars.putIfAbsent(uuid, new String[5]);
        }
    }
    
    /**
     * 스킬 ID로 스킬 조회
     * @param skillId 스킬 ID
     * @return 스킬 객체, 없으면 null
     */
    public Skill getSkill(String skillId) {
        return skillRegistry.get(skillId);
    }
    
    /**
     * 모든 스킬 반환
     * @return 스킬 리스트
     */
    public List<Skill> getAllSkills() {
        return new ArrayList<>(skillRegistry.values());
    }
    
    /**
     * 플레이어가 배운 스킬 목록
     * @param player 플레이어
     * @return 스킬 리스트
     */
    public List<Skill> getPlayerSkills(Player player) {
        List<Skill> playerSkills = new ArrayList<>();
        Map<String, Integer> levels = playerSkillLevels. getOrDefault(player.getUniqueId(), new HashMap<>());
        
        for (String skillId : levels.keySet()) {
            Skill skill = getSkill(skillId);
            if (skill != null) {
                playerSkills.add(skill);
            }
        }
        
        return playerSkills;
    }
    
    /**
     * 플레이어가 스킬을 배웠는지 확인
     * @param player 플레이어
     * @param skillId 스킬 ID
     * @return 배웠으면 true
     */
    public boolean hasSkill(Player player, String skillId) {
        Map<String, Integer> levels = playerSkillLevels.getOrDefault(player.getUniqueId(), new HashMap<>());
        return levels.containsKey(skillId);
    }
    
    /**
     * 플레이어의 스킬 레벨 조회
     * @param player 플레이어
     * @param skillId 스킬 ID
     * @return 스킬 레벨
     */
    public int getSkillLevel(Player player, String skillId) {
        Map<String, Integer> levels = playerSkillLevels.getOrDefault(player.getUniqueId(), new HashMap<>());
        return levels.getOrDefault(skillId, 0);
    }
    
    /**
     * 스킬 학습
     * @param player 플레이어
     * @param skillId 스킬 ID
     * @return 성공 여부
     */
    public boolean learnSkill(Player player, String skillId) {
        Skill skill = getSkill(skillId);
        if (skill == null) return false;
        
        // 학습 가능 여부 확인
        if (! canLearnSkill(player, skill)) {
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c이 스킬을 배울 조건을 충족하지 못했습니다.");
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        playerSkillLevels.putIfAbsent(uuid, new HashMap<>());
        
        if (!hasSkill(player, skillId)) {
            playerSkillLevels.get(uuid).put(skillId, 1);
            skillExp.put(skillId + ":" + uuid, 0);
            
            String message = plugin.getCombatConfig(). getString("messages.skill. learned",
                "§a스킬 §e{skill}§a을(를) 습득했습니다!")
                .replace("{skill}", skill.getName());
            
            player.sendMessage(plugin. getCombatConfig().getString("messages.prefix", "[전투] ") + message);
            return true;
        }
        
        return false;
    }
    
    /**
     * 스킬 업그레이드
     * @param player 플레이어
     * @param skillId 스킬 ID
     * @return 성공 여부
     */
    public boolean upgradeSkill(Player player, String skillId) {
        Skill skill = getSkill(skillId);
        if (skill == null) return false;
        
        int currentLevel = getSkillLevel(player, skillId);
        if (currentLevel >= skill.getMaxLevel()) {
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c이미 최대 레벨입니다.");
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        playerSkillLevels.get(uuid).put(skillId, currentLevel + 1);
        skillExp.put(skillId + ":" + uuid, 0);
        
        String message = plugin.getCombatConfig().getString("messages.skill.upgraded",
            "§a스킬 §e{skill}§a이(가) §6Lv. {level}§a(으)로 성장했습니다!")
            . replace("{skill}", skill.getName())
            .replace("{level}", String.valueOf(currentLevel + 1));
        
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") + message);
        return true;
    }
    
    /**
     * 스킬 레벨 설정 (관리자용)
     * @param player 플레이어
     * @param skillId 스킬 ID
     * @param level 레벨
     */
    public void setSkillLevel(Player player, String skillId, int level) {
        Skill skill = getSkill(skillId);
        if (skill == null) return;
        
        UUID uuid = player.getUniqueId();
        playerSkillLevels.putIfAbsent(uuid, new HashMap<>());
        
        int maxLevel = skill.getMaxLevel();
        level = Math.min(level, maxLevel);
        level = Math.max(level, 1);
        
        playerSkillLevels.get(uuid).put(skillId, level);
    }
    
    /**
     * 스킬을 배울 수 있는지 확인
     * @param player 플레이어
     * @param skill 스킬 객체
     * @return 배울 수 있으면 true
     */
    public boolean canLearnSkill(Player player, Skill skill) {
        // 플레이어 레벨 확인
        if (player.getLevel() < skill.getRequiredLevel()) {
            player. sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c필요 레벨: " + skill.getRequiredLevel());
            return false;
        }
        
        // 선행 스킬 확인
        if (skill.getRequiredSkill() != null && ! skill.getRequiredSkill().isEmpty()) {
            if (!hasSkill(player, skill.getRequiredSkill())) {
                player.sendMessage(plugin. getCombatConfig().getString("messages.prefix", "[전투] ") +
                    "§c선행 스킬을 먼저 배워야 합니다.");
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 스킬 사용 가능 확인
     * @param player 플레이어
     * @param skill 스킬 객체
     * @return 사용 가능하면 true
     */
    public boolean canUseSkill(Player player, Skill skill) {
        // 스킬 보유 확인
        if (!hasSkill(player, skill. getSkillId())) {
            player.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
                "§c배우지 않은 스킬입니다.");
            return false;
        }
        
        // 비용 확인
        if (! hasEnoughCost(player, skill)) {
            String costType = skill.getCostType(). name();
            player.sendMessage(plugin.getCombatConfig().getString("messages.skill.insufficient-cost",
                "§c{type}이(가) 부족합니다.")
                .replace("{type}", costType));
            return false;
        }
        
        // 쿨다운 확인
        if (isOnCooldown(player, skill. getSkillId())) {
            long remaining = getRemainingCooldown(player, skill.getSkillId());
            player.sendMessage(plugin.getCombatConfig().getString("messages.skill.cooldown",
                "§c스킬 재사용 대기 중...  ({time}초)")
                .replace("{time}", String.format("%.1f", remaining / 1000.0)));
            return false;
        }
        
        return true;
    }
    
    /**
     * 비용이 충분한지 확인
     * @param player 플레이어
     * @param skill 스킬 객체
     * @return 충분하면 true
     */
    public boolean hasEnoughCost(Player player, Skill skill) {
        CostType costType = skill.getCostType();
        int skillLevel = getSkillLevel(player, skill.getSkillId());
        double cost = skill.getBaseCost() * skillLevel;
        
        switch (costType) {
            case MANA:
                // PlayerDataCore에서 마나 조회 (연동 필요)
                return true;
            case STAMINA:
                return player.getFoodLevel() >= cost;
            case HP:
                return player.getHealth() > cost;
            case QI:
                // 기력 (커스텀 스탯) - 구현 필요
                return true;
            case NONE:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * 스킬 비용 소모
     * @param player 플레이어
     * @param skill 스킬 객체
     */
    public void consumeCost(Player player, Skill skill) {
        CostType costType = skill.getCostType();
        int skillLevel = getSkillLevel(player, skill.getSkillId());
        double cost = skill.getBaseCost() * skillLevel;
        
        switch (costType) {
            case STAMINA:
                int currentFood = player.getFoodLevel();
                player.setFoodLevel((int) Math.max(0, currentFood - cost));
                break;
            case HP:
                player.damage(cost);
                break;
            case MANA:
            case QI:
            case NONE:
                break;
        }
    }
    
    /**
     * 쿨다운 중인지 확인
     * @param player 플레이어
     * @param skillId 스킬 ID
     * @return 쿨다운 중이면 true
     */
    public boolean isOnCooldown(Player player, String skillId) {
        Map<String, Long> cooldowns = playerSkillCooldowns.getOrDefault(player.getUniqueId(), new HashMap<>());
        Long cooldownTime = cooldowns.get(skillId);
        
        if (cooldownTime == null) return false;
        
        long now = System.currentTimeMillis();
        return cooldownTime > now;
    }
    
    /**
     * 남은 쿨다운 시간 조회
     * @param player 플레이어
     * @param skillId 스킬 ID
     * @return 남은 시간 (밀리초)
     */
    public long getRemainingCooldown(Player player, String skillId) {
        Map<String, Long> cooldowns = playerSkillCooldowns.getOrDefault(player.getUniqueId(), new HashMap<>());
        Long cooldownTime = cooldowns.get(skillId);
        
        if (cooldownTime == null) return 0;
        
        long now = System.currentTimeMillis();
        long remaining = cooldownTime - now;
        
        return remaining > 0 ? remaining : 0;
    }
    
    /**
     * 스킬 쿨다운 설정
     * @param player 플레이어
     * @param skillId 스킬 ID
     * @param cooldownMs 쿨다운 (밀리초)
     */
    public void setCooldown(Player player, String skillId, long cooldownMs) {
        UUID uuid = player.getUniqueId();
        playerSkillCooldowns.putIfAbsent(uuid, new HashMap<>());
        
        long cooldownTime = System.currentTimeMillis() + cooldownMs;
        playerSkillCooldowns.get(uuid). put(skillId, cooldownTime);
    }
    
    /**
     * 모든 쿨다운 초기화
     * @param player 플레이어
     */
    public void resetAllCooldowns(Player player) {
        UUID uuid = player.getUniqueId();
        playerSkillCooldowns.put(uuid, new HashMap<>());
    }
    
    /**
     * 스킬을 핫바에 바인드
     * @param player 플레이어
     * @param slot 슬롯 (0~4)
     * @param skillId 스킬 ID
     * @return 성공 여부
     */
    public boolean bindSkillToHotbar(Player player, int slot, String skillId) {
        if (slot < 0 || slot > 4) return false;
        if (skillId != null && !hasSkill(player, skillId)) return false;
        
        UUID uuid = player.getUniqueId();
        String[] hotbar = playerHotbars.getOrDefault(uuid, new String[5]);
        hotbar[slot] = skillId;
        playerHotbars. put(uuid, hotbar);
        
        return true;
    }
    
    /**
     * 핫바에서 스킬 조회
     * @param player 플레이어
     * @param slot 슬롯 (0~4)
     * @return 스킬 ID
     */
    public String getHotbarSkill(Player player, int slot) {
        if (slot < 0 || slot > 4) return null;
        
        String[] hotbar = playerHotbars.getOrDefault(player. getUniqueId(), new String[5]);
        return hotbar[slot];
    }
    
    /**
     * 핫바 반환
     * @param player 플레이어
     * @return 스킬 ID 배열
     */
    public String[] getHotbar(Player player) {
        return playerHotbars.getOrDefault(player.getUniqueId(), new String[5]);
    }
    
    /**
     * 플레이어 데이터 저장
     * @param player 플레이어
     */
    public void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        Map<String, Integer> levels = playerSkillLevels.getOrDefault(uuid, new HashMap<>());
        String[] hotbar = playerHotbars.getOrDefault(uuid, new String[5]);
        
        plugin.getDataManager().savePlayerSkills(player, levels, hotbar);
    }
    
    /**
     * 플레이어 데이터 로드
     * @param player 플레이어
     */
    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        
        Map<String, Integer> levels = plugin.getDataManager().loadPlayerSkills(player);
        playerSkillLevels.put(uuid, levels != null ? levels : new HashMap<>());
        
        String[] hotbar = plugin.getDataManager(). loadPlayerHotbar(player);
        playerHotbars. put(uuid, hotbar != null ? hotbar : new String[5]);
    }
}