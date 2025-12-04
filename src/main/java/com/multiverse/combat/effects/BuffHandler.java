package com.multiverse.combat.effects;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.multiverse.combat.CombatCore;

/**
 * 버프 처리 클래스
 * 플레이어에게 적용되는 버프 효과를 관리합니다.
 */
public class BuffHandler {
    
    private final CombatCore plugin;
    
    /**
     * BuffHandler 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public BuffHandler(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 공격력 버프 적용
     * @param player 플레이어
     * @param level 버프 레벨
     * @param duration 지속 시간 (틱)
     */
    public void applyStrengthBuff(Player player, int level, int duration) {
        player.addPotionEffect(new PotionEffect(
            PotionEffectType. INCREASE_DAMAGE,
            duration,
            Math.max(0, level - 1),
            false,
            false
        ));
        
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§c공격력 버프§f가 적용되었습니다!   (Lv." + level + ")");
    }
    
    /**
     * 방어력 버프 적용
     * @param player 플레이어
     * @param level 버프 레벨
     * @param duration 지속 시간 (틱)
     */
    public void applyResistanceBuff(Player player, int level, int duration) {
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.DAMAGE_RESISTANCE,
            duration,
            Math.max(0, level - 1),
            false,
            false
        ));
        
        player.sendMessage(plugin.getCombatConfig(). getString("messages.prefix", "[전투] ") +
            "§9방어력 버프§f가 적용되었습니다!  (Lv." + level + ")");
    }
    
    /**
     * 속도 버프 적용
     * @param player 플레이어
     * @param level 버프 레벨
     * @param duration 지속 시간 (틱)
     */
    public void applySpeedBuff(Player player, int level, int duration) {
        player.addPotionEffect(new PotionEffect(
            PotionEffectType. SPEED,
            duration,
            Math. max(0, level - 1),
            false,
            false
        ));
        
        player.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
            "§e속도 버프§f가 적용되었습니다!  (Lv." + level + ")");
    }
    
    /**
     * 재생 버프 적용
     * @param player 플레이어
     * @param level 버프 레벨
     * @param duration 지속 시간 (틱)
     */
    public void applyRegenBuff(Player player, int level, int duration) {
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.REGENERATION,
            duration,
            Math.max(0, level - 1),
            false,
            false
        ));
        
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a재생 버프§f가 적용되었습니다!  (Lv." + level + ")");
    }
    
    /**
     * 야맹증 버프 적용
     * @param player 플레이어
     * @param duration 지속 시간 (틱)
     */
    public void applyNightVisionBuff(Player player, int duration) {
        player. addPotionEffect(new PotionEffect(
            PotionEffectType.NIGHT_VISION,
            duration,
            0,
            false,
            false
        ));
        
        player.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
            "§6야맹증 버프§f가 적용되었습니다!");
    }
    
    /**
     * 물 호흡 버프 적용
     * @param player 플레이어
     * @param duration 지속 시간 (틱)
     */
    public void applyWaterBreathingBuff(Player player, int duration) {
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.WATER_BREATHING,
            duration,
            0,
            false,
            false
        ));
        
        player.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
            "§b물 호흡 버프§f가 적용되었습니다!");
    }
    
    /**
     * 낙상 저항 버프 적용
     * @param player 플레이어
     * @param duration 지속 시간 (틱)
     */
    public void applyFeatherFallBuff(Player player, int duration) {
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.JUMP,
            duration,
            1,
            false,
            false
        ));
        
        player.sendMessage(plugin.getCombatConfig().getString("messages. prefix", "[전투] ") +
            "§f낙상 저항 버프§f가 적용되었습니다!");
    }
    
    /**
     * 모든 버프 제거
     * @param player 플레이어
     */
    public void removeAllBuffs(Player player) {
        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.WATER_BREATHING);
        player.removePotionEffect(PotionEffectType.JUMP);
        
        player.sendMessage(plugin. getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§c모든 버프가 제거되었습니다.");
    }
    
    /**
     * 버프 스택 제한
     * @param player 플레이어
     * @param maxBuffs 최대 버프 수
     */
    public void limitBuffs(Player player, int maxBuffs) {
        int currentBuffCount = 0;
        
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (isBuffEffect(effect.  getType())) {
                currentBuffCount++;
            }
        }
        
        if (currentBuffCount > maxBuffs) {
            // 가장 약한 버프 제거
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§e버프 개수 초과로 가장 약한 버프가 제거되었습니다.");
        }
    }
    
    /**
     * 버프 효과인지 확인
     * @param type 포션 효과 타입
     * @return 버프 효과면 true
     */
    private boolean isBuffEffect(PotionEffectType type) {
        return type == PotionEffectType.  INCREASE_DAMAGE ||
               type == PotionEffectType.DAMAGE_RESISTANCE ||
               type == PotionEffectType.SPEED ||
               type == PotionEffectType.REGENERATION ||
               type == PotionEffectType.NIGHT_VISION ||
               type == PotionEffectType.  WATER_BREATHING ||
               type == PotionEffectType.JUMP;
    }
}