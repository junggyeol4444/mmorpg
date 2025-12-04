package com.multiverse.combat.skills;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org. bukkit. Bukkit;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models. Skill;
import com.multiverse. combat.events.SkillUseEvent;

/**
 * 스킬 실행자 클래스
 * 스킬 사용 검증 및 실행을 관리합니다.
 */
public class SkillExecutor {
    
    private final CombatCore plugin;
    
    /**
     * SkillExecutor 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public SkillExecutor(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 스킬 실행 가능 여부 확인
     * @param player 플레이어
     * @param skill 스킬
     * @return 실행 가능하면 true
     */
    public boolean canExecute(Player player, Skill skill) {
        // 스킬 보유 확인
        if (! plugin.getSkillManager().hasSkill(player, skill.getSkillId())) {
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c배우지 않은 스킬입니다.");
            return false;
        }
        
        // 비용 확인
        if (! plugin.getSkillManager().hasEnoughCost(player, skill)) {
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c" + skill.getCostType().getDisplayName() + "이(가) 부족합니다.");
            return false;
        }
        
        // 쿨다운 확인
        if (plugin.getSkillManager().isOnCooldown(player, skill. getSkillId())) {
            long remaining = plugin.getSkillManager(). getRemainingCooldown(player, skill.getSkillId());
            player.sendMessage(plugin. getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§c쿨다운 중...  (" + String.format("%.1f", remaining / 1000.0) + "초)");
            return false;
        }
        
        // 캐스팅 시간 체크
        if (skill.getCastTime() > 0 && ! skill.isCanMove()) {
            // 캐스팅 상태 확인
            if (plugin.getCombatDataManager().getAllStats(player).containsKey("is_casting")) {
                player. sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                    "§c이미 캐스팅 중입니다.");
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 스킬 실행
     * @param player 플레이어
     * @param skill 스킬
     * @param target 대상
     */
    public void execute(Player player, Skill skill, LivingEntity target) {
        // 실행 가능 확인
        if (!canExecute(player, skill)) {
            return;
        }
        
        // 스킬 사용 이벤트 발생
        SkillUseEvent event = new SkillUseEvent(player, skill, target);
        Bukkit.getPluginManager(). callEvent(event);
        
        if (event.isCancelled()) {
            return;
        }
        
        // 캐스팅 시간 처리
        if (skill.getCastTime() > 0) {
            performCasting(player, skill, event. getTarget());
        } else {
            // 즉시 발동
            executeSkillEffect(player, skill, event.getTarget());
        }
    }
    
    /**
     * 캐스팅 수행
     * @param player 플레이어
     * @param skill 스킬
     * @param target 대상
     */
    private void performCasting(Player player, Skill skill, LivingEntity target) {
        long castTime = skill.getCastTime();
        
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a" + skill.getName() + "§a을(를) 시전 중...  (" + (castTime / 1000.0) + "초)");
        
        // 비동기 작업으로 캐스팅 시간 처리
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            // 캐스팅 중단 확인
            if (player.isDead() || ! player.isOnline()) {
                return;
            }
            
            // 이동했는지 확인 (이동 불가 스킬)
            if (!skill.isCanMove()) {
                // 이동 확인 로직 필요
            }
            
            // 스킬 효과 실행
            executeSkillEffect(player, skill, target);
        }, castTime / 50);  // 밀리초를 틱으로 변환
    }
    
    /**
     * 스킬 효과 실행
     * @param player 플레이어
     * @param skill 스킬
     * @param target 대상
     */
    private void executeSkillEffect(Player player, Skill skill, LivingEntity target) {
        // 비용 소모
        plugin.getSkillManager().consumeCost(player, skill);
        
        // 쿨다운 설정
        plugin.getSkillManager().setCooldown(player, skill.getSkillId(), skill.getBaseCooldown());
        
        // 효과 처리
        plugin.getSkillManager().getSkill(skill.getSkillId());
        
        if (skill.getSkillEffect() != null) {
            SkillEffectHandler effectHandler = new SkillEffectHandler(plugin);
            effectHandler.handle(player, skill, target);
        }
        
        // 내구도 감소
        plugin.getDurabilityManager().damageWeaponFromSkill(player);
        
        // 콤보 증가
        plugin.getComboManager().addCombo(player, 1);
    }
    
    /**
     * 스킬 준비 상태 설정
     * @param player 플레이어
     * @param skillId 스킬 ID
     */
    public void prepareSkill(Player player, String skillId) {
        Skill skill = plugin.getSkillManager().getSkill(skillId);
        if (skill == null) return;
        
        if (! canExecute(player, skill)) {
            return;
        }
        
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§e" + skill.getName() + "§e 스킬이 준비되었습니다.");
    }
    
    /**
     * 스킬 취소
     * @param player 플레이어
     */
    public void cancelSkill(Player player) {
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§c스킬 시전이 취소되었습니다.");
    }
}