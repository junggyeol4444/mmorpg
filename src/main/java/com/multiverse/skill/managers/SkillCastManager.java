package com.multiverse.skill.managers;

import com.multiverse. skill. SkillCore;
import com.multiverse. skill.data.enums.CastStatus;
import com.multiverse. skill.data.models. Skill;
import com.multiverse.skill.data.models. SkillCast;
import com.multiverse.skill.events.SkillCastCompleteEvent;
import com.multiverse.skill.events.SkillUseEvent;
import com.multiverse.skill.utils.MessageUtils;
import org.bukkit. Bukkit;
import org.bukkit.Location;
import org.bukkit. entity.LivingEntity;
import org. bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent. ConcurrentHashMap;

public class SkillCastManager {

    private final SkillCore plugin;
    private final Map<UUID, SkillCast> activeCasts = new ConcurrentHashMap<>();
    private final Map<UUID, Long> skillCooldowns = new ConcurrentHashMap<>();

    public SkillCastManager(SkillCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 단일 대상 스킬 캐스팅 시작
     */
    public void startCast(Player player, Skill skill, LivingEntity target) {
        // 쿨다운 체크
        if (! canUseCooldown(player, skill)) {
            long remainingTime = getRemainingCooldown(player, skill) / 1000;
            MessageUtils.sendMessage(player, String.format("§c쿨다운 중...  (§e%d초§c)", remainingTime));
            return;
        }

        // 스킬 사용 이벤트 발생
        SkillUseEvent event = new SkillUseEvent(player, skill. getSkillId(), target);
        Bukkit.getPluginManager(). callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        // 기존 캐스팅 취소
        if (activeCasts.containsKey(player.getUniqueId())) {
            cancelCast(player);
        }

        // 새로운 캐스트 생성
        SkillCast cast = new SkillCast();
        cast.setCastId(UUID.randomUUID());
        cast.setCaster(player);
        cast.setSkill(skill);
        cast.setTarget(target);
        cast.setStartTime(System.currentTimeMillis());
        cast.setCastDuration(skill.getCastTime());
        cast.setChanneling(skill.isChanneling());
        cast.setStatus(CastStatus.CASTING);

        activeCasts.put(player.getUniqueId(), cast);

        // 캐스팅 메시지 표시
        if (skill.getCastTime() > 0) {
            MessageUtils.sendMessage(player, String.format("§e시전 중... (§b%. 1f초§e)", skill.getCastTime() / 1000.0));
        }

        // 캐스팅 완료 시간 계산
        long delayTicks = skill.getCastTime() / 50; // 1 틱 = 50ms
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (activeCasts.containsKey(player.getUniqueId()) &&
                activeCasts.get(player.getUniqueId()). getCastId().equals(cast.getCastId())) {
                completeCast(cast);
            }
        }, delayTicks);
    }

    /**
     * 위치 대상 스킬 캐스팅 시작
     */
    public void startCast(Player player, Skill skill, Location targetLocation) {
        // 쿨다운 체크
        if (!canUseCooldown(player, skill)) {
            long remainingTime = getRemainingCooldown(player, skill) / 1000;
            MessageUtils.sendMessage(player, String.format("§c쿨다운 중... (§e%d초§c)", remainingTime));
            return;
        }

        // 스킬 사용 이벤트 발생
        SkillUseEvent event = new SkillUseEvent(player, skill.getSkillId(), targetLocation);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        // 기존 캐스팅 취소
        if (activeCasts.containsKey(player.getUniqueId())) {
            cancelCast(player);
        }

        // 새로운 캐스트 생성
        SkillCast cast = new SkillCast();
        cast.setCastId(UUID.randomUUID());
        cast.setCaster(player);
        cast.setSkill(skill);
        cast. setTargetLocation(targetLocation);
        cast.setStartTime(System. currentTimeMillis());
        cast.setCastDuration(skill. getCastTime());
        cast. setChanneling(skill.isChanneling());
        cast.setStatus(CastStatus.CASTING);

        activeCasts.put(player.getUniqueId(), cast);

        if (skill.getCastTime() > 0) {
            MessageUtils.sendMessage(player, String. format("§e시전 중... (§b%.1f초§e)", skill.getCastTime() / 1000.0));
        }

        long delayTicks = skill.getCastTime() / 50;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (activeCasts.containsKey(player.getUniqueId()) &&
                activeCasts.get(player.getUniqueId()).getCastId().equals(cast.getCastId())) {
                completeCast(cast);
            }
        }, delayTicks);
    }

    /**
     * 캐스팅 취소
     */
    public void cancelCast(Player player) {
        SkillCast cast = activeCasts.remove(player.getUniqueId());
        if (cast != null) {
            cast.setStatus(CastStatus. CANCELLED);
            MessageUtils. sendMessage(player, "§c스킬이 취소되었습니다.");
        }
    }

    /**
     * 캐스팅 방해 (피해 등으로 인한)
     */
    public void interruptCast(Player player, String reason) {
        SkillCast cast = activeCasts. remove(player.getUniqueId());
        if (cast != null) {
            cast.setStatus(CastStatus.INTERRUPTED);
            MessageUtils.sendMessage(player, "§c스킬이 방해받았습니다: " + reason);
        }
    }

    /**
     * 캐스팅 진행 중인지 확인
     */
    public boolean isCasting(Player player) {
        SkillCast cast = activeCasts.get(player.getUniqueId());
        return cast != null && (cast.getStatus() == CastStatus.CASTING || cast.getStatus() == CastStatus. CHANNELING);
    }

    /**
     * 현재 캐스트 조회
     */
    public SkillCast getCurrentCast(Player player) {
        return activeCasts.get(player.getUniqueId());
    }

    /**
     * 캐스팅 완료 처리
     */
    public void completeCast(SkillCast cast) {
        cast.setStatus(CastStatus. COMPLETED);
        Player caster = cast.getCaster();

        if (caster == null || ! caster.isOnline()) {
            return;
        }

        // 쿨다운 설정
        setCooldown(caster, cast. getSkill());

        // 스킬 효과 실행
        if (cast.getTarget() != null) {
            plugin.getEffectExecutor().execute(caster, cast.getSkill(), cast.getTarget());
        } else if (cast.getTargetLocation() != null) {
            plugin.getEffectExecutor(). execute(caster, cast.getSkill(), cast.getTargetLocation());
        }

        // 스킬 캐스트 완료 이벤트
        SkillCastCompleteEvent event = new SkillCastCompleteEvent(caster, cast. getSkill(). getSkillId());
        Bukkit.getPluginManager().callEvent(event);

        activeCasts.remove(caster. getUniqueId());

        MessageUtils.sendMessage(caster, String.format("§a스킬 '§e%s§a'을(를) 시전했습니다!", cast.getSkill().getName()));
    }

    /**
     * 쿨다운 체크
     */
    public boolean canUseCooldown(Player player, Skill skill) {
        long key = (player.getUniqueId(). hashCode() * 31L + skill.getSkillId().hashCode());
        Long lastUsed = skillCooldowns.get(key);
        
        if (lastUsed == null) {
            return true;
        }

        long cooldownTime = plugin.getSkillManager().calculateCooldown(skill, 1);
        return System.currentTimeMillis() - lastUsed >= cooldownTime;
    }

    /**
     * 쿨다운 설정
     */
    public void setCooldown(Player player, Skill skill) {
        long key = (player.getUniqueId().hashCode() * 31L + skill.getSkillId().hashCode());
        skillCooldowns.put(key, System.currentTimeMillis());
    }

    /**
     * 남은 쿨다운 시간 조회 (밀리초)
     */
    public long getRemainingCooldown(Player player, Skill skill) {
        long key = (player.getUniqueId().hashCode() * 31L + skill.getSkillId().hashCode());
        Long lastUsed = skillCooldowns.get(key);
        
        if (lastUsed == null) {
            return 0;
        }

        long cooldownTime = plugin.getSkillManager().calculateCooldown(skill, 1);
        long elapsed = System.currentTimeMillis() - lastUsed;
        return Math.max(0, cooldownTime - elapsed);
    }

    /**
     * 모든 활성 캐스트 조회
     */
    public Collection<SkillCast> getActiveCasts() {
        return new ArrayList<>(activeCasts.values());
    }

    /**
     * 플레이어 이동 시 캐스트 취소 처리
     */
    public void onPlayerMove(Player player) {
        SkillCast cast = activeCasts.get(player.getUniqueId());
        if (cast != null && cast.getSkill().isCancelOnMove()) {
            interruptCast(player, "이동");
        }
    }

    /**
     * 플레이어 피해 시 캐스트 취소 처리
     */
    public void onPlayerDamage(Player player) {
        SkillCast cast = activeCasts.get(player. getUniqueId());
        if (cast != null && cast. getSkill().isCancelOnDamage()) {
            interruptCast(player, "피해");
        }
    }

    /**
     * 채널링 틱 처리
     */
    public void tickChanneling(Player player) {
        SkillCast cast = activeCasts.get(player. getUniqueId());
        if (cast == null || cast.getStatus() != CastStatus.CHANNELING) {
            return;
        }

        long elapsedTime = System.currentTimeMillis() - cast.getStartTime();
        long duration = cast.getCastDuration();

        // 채널링 중 액션바 표시
        float progress = (float) elapsedTime / duration;
        player.sendActionBar(String.format("§b▰%. 0f%%▰", progress * 100));

        // 채널링 완료
        if (elapsedTime >= duration) {
            completeCast(cast);
        }
    }

    /**
     * 모든 캐스트 정리 (플레이어 로그아웃 시)
     */
    public void cleanupPlayer(Player player) {
        activeCasts.remove(player.getUniqueId());
    }
}