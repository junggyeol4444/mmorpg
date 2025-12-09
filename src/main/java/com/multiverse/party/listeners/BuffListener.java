package com.multiverse.party.listeners;

import com. multiverse.party. PartyCore;
import com.multiverse.party.models.Party;
import com.multiverse. party.models.PartyBuff;
import org.bukkit. Bukkit;
import org.bukkit. attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org. bukkit.entity.Player;
import org.bukkit. event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org. bukkit.potion.PotionEffectType;

import java.util. HashMap;
import java. util.Map;
import java.util. UUID;

public class BuffListener implements Listener {

    private final PartyCore plugin;
    private final Map<UUID, Map<String, UUID>> appliedModifiers;

    private static final String MODIFIER_PREFIX = "partycore_";

    public BuffListener(PartyCore plugin) {
        this.plugin = plugin;
        this.appliedModifiers = new HashMap<>();
    }

    // ==================== 공격력 보너스 적용 ====================
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) return;

        // 공격력 보너스 계산
        double damageBonus = plugin.getPartyBuffManager().getDamageBonus(party);
        
        if (damageBonus > 0) {
            double originalDamage = event.getDamage();
            double bonusDamage = originalDamage * damageBonus;
            event.setDamage(originalDamage + bonusDamage);
        }

        // 크리티컬 보너스 계산
        double critBonus = plugin.getPartyBuffManager().getCriticalBonus(party);
        if (critBonus > 0) {
            double random = Math.random();
            if (random < critBonus) {
                double critMultiplier = plugin. getConfig().getDouble("buffs.critical-multiplier", 1.5);
                event. setDamage(event.getDamage() * critMultiplier);
                
                // 크리티컬 효과
                if (plugin.getConfig().getBoolean("buffs.critical-effect", true)) {
                    player.getWorld().spawnParticle(
                            org.bukkit. Particle.CRIT,
                            event.getEntity().getLocation().add(0, 1, 0),
                            10, 0.5, 0.5, 0.5, 0.1
                    );
                }
            }
        }
    }

    // ==================== 방어력 보너스 적용 ====================
    @EventHandler(priority = EventPriority. HIGH, ignoreCancelled = true)
    public void onPlayerDamaged(EntityDamageEvent event) {
        if (!(event. getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Party party = plugin.getPartyManager().getPlayerParty(player);
        
        if (party == null) return;

        // 방어력 보너스 계산
        double defenseBonus = plugin.getPartyBuffManager().getDefenseBonus(party);
        
        if (defenseBonus > 0) {
            double originalDamage = event. getDamage();
            double reducedDamage = originalDamage * (1 - Math.min(defenseBonus, 0.75)); // 최대 75% 감소
            event. setDamage(reducedDamage);
        }
    }

    // ==================== 속성 모디파이어 적용 ====================
    public void applyAttributeModifiers(Player player, Party party) {
        removeAttributeModifiers(player);

        Map<String, UUID> playerModifiers = new HashMap<>();
        appliedModifiers.put(player.getUniqueId(), playerModifiers);

        // 이동속도 보너스
        double speedBonus = plugin. getPartyBuffManager().getSpeedBonus(party);
        if (speedBonus > 0) {
            applyModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, 
                    "speed", speedBonus, AttributeModifier.Operation. MULTIPLY_SCALAR_1, playerModifiers);
        }

        // 체력 보너스
        double healthBonus = plugin.getPartyBuffManager().getHealthBonus(party);
        if (healthBonus > 0) {
            applyModifier(player, Attribute.GENERIC_MAX_HEALTH,
                    "health", healthBonus * 20, AttributeModifier.Operation.ADD_NUMBER, playerModifiers);
        }

        // 공격 속도 보너스
        double attackSpeedBonus = plugin. getPartyBuffManager().getAttackSpeedBonus(party);
        if (attackSpeedBonus > 0) {
            applyModifier(player, Attribute.GENERIC_ATTACK_SPEED,
                    "attack_speed", attackSpeedBonus, AttributeModifier. Operation.MULTIPLY_SCALAR_1, playerModifiers);
        }
    }

    private void applyModifier(Player player, Attribute attribute, String name, 
                               double value, AttributeModifier.Operation operation,
                               Map<String, UUID> playerModifiers) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        UUID modifierId = UUID.randomUUID();
        AttributeModifier modifier = new AttributeModifier(
                modifierId,
                MODIFIER_PREFIX + name,
                value,
                operation
        );

        instance. addModifier(modifier);
        playerModifiers.put(name, modifierId);
    }

    public void removeAttributeModifiers(Player player) {
        Map<String, UUID> playerModifiers = appliedModifiers.remove(player.getUniqueId());
        if (playerModifiers == null) return;

        for (Attribute attribute :  Attribute.values()) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;

            for (AttributeModifier modifier :  instance.getModifiers()) {
                if (modifier.getName().startsWith(MODIFIER_PREFIX)) {
                    instance. removeModifier(modifier);
                }
            }
        }
    }

    // ==================== 포션 효과 적용 ====================
    public void applyPotionEffects(Player player, Party party) {
        removePotionEffects(player);

        // 야간 투시 (파티 레벨 20 이상)
        int partyLevel = plugin.getPartyLevelManager().getPartyLevel(party);
        if (partyLevel >= 20 && plugin.getConfig().getBoolean("buffs.night-vision-enabled", true)) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    Integer.MAX_VALUE,
                    0,
                    true,
                    false,
                    true
            ));
        }

        // 재생 (파티 레벨 30 이상, 범위 내에서만)
        if (partyLevel >= 30 && plugin.getConfig().getBoolean("buffs.regeneration-enabled", true)) {
            // 주기적 재생 효과는 BuffManager에서 처리
        }

        // 발광 효과 (파티원 위치 표시)
        if (plugin.getConfig().getBoolean("buffs.glow-party-members", false)) {
            for (UUID memberUUID : party.getMembers()) {
                if (memberUUID. equals(player.getUniqueId())) continue;
                
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null) {
                    player.sendMessage(""); // 발광 효과 구현 (패킷 사용 필요)
                }
            }
        }
    }

    public void removePotionEffects(Player player) {
        // 플러그인이 적용한 효과만 제거
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        // 다른 플러그인의 효과는 유지
    }

    // ==================== 범위 체크 ====================
    public void checkBuffRange(Party party) {
        if (! plugin.getConfig().getBoolean("buffs.range-check-enabled", true)) return;

        double buffRange = plugin.getConfig().getDouble("buffs.range", 50.0);
        double rangeSquared = buffRange * buffRange;

        // 파티 중심점 계산 (온라인 멤버 평균 위치)
        org.bukkit.Location centerLocation = calculatePartyCenter(party);
        if (centerLocation == null) return;

        for (UUID memberUUID : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member == null || ! member.isOnline()) continue;

            boolean inRange = member.getWorld().equals(centerLocation.getWorld()) &&
                    member.getLocation().distanceSquared(centerLocation) <= rangeSquared;

            if (inRange) {
                // 버프 적용 확인
                if (! hasFullBuffs(member, party)) {
                    plugin.getPartyBuffManager().applyBuffsToPlayer(member, party);
                }
            } else {
                // 범위 이탈 시 버프 약화
                applyRangeDebuff(member);
            }
        }
    }

    private org.bukkit.Location calculatePartyCenter(Party party) {
        double x = 0, y = 0, z = 0;
        int count = 0;
        org.bukkit.World world = null;

        for (UUID memberUUID :  party.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member == null || !member.isOnline()) continue;

            if (world == null) {
                world = member. getWorld();
            } else if (!member.getWorld().equals(world)) {
                continue; // 다른 월드의 멤버 제외
            }

            org.bukkit.Location loc = member.getLocation();
            x += loc.getX();
            y += loc.getY();
            z += loc.getZ();
            count++;
        }

        if (count == 0 || world == null) return null;

        return new org.bukkit.Location(world, x / count, y / count, z / count);
    }

    private boolean hasFullBuffs(Player player, Party party) {
        // 간단한 체크 - 모디파이어가 있는지 확인
        return appliedModifiers.containsKey(player. getUniqueId());
    }

    private void applyRangeDebuff(Player player) {
        // 범위 이탈 시 버프 효과 50% 감소
        // 실제 구현은 모디파이어 값 조정
        if (plugin.getConfig().getBoolean("buffs.show-range-warning", true)) {
            plugin.getActionBarUtil().sendActionBar(player,
                    plugin. getMessageUtil().getMessage("buff. out-of-range"));
        }
    }

    // ==================== 버프 갱신 태스크 ====================
    public void startBuffUpdateTask() {
        int interval = plugin.getConfig().getInt("buffs.update-interval", 20);
        
        Bukkit. getScheduler().runTaskTimer(plugin, () -> {
            for (Party party : plugin.getPartyManager().getAllParties()) {
                updatePartyBuffs(party);
            }
        }, interval, interval);
    }

    private void updatePartyBuffs(Party party) {
        // 만료된 버프 제거
        plugin.getPartyBuffManager().removeExpiredBuffs(party);

        // 범위 체크
        checkBuffRange(party);

        // 주기적 효과 적용 (재생 등)
        applyPeriodicEffects(party);
    }

    private void applyPeriodicEffects(Party party) {
        int partyLevel = plugin.getPartyLevelManager().getPartyLevel(party);

        // 파티 레벨 30 이상:  주기적 재생
        if (partyLevel >= 30 && plugin.getConfig().getBoolean("buffs.regeneration-enabled", true)) {
            double healAmount = plugin.getConfig().getDouble("buffs.regeneration-amount", 0.5);
            double range = plugin.getConfig().getDouble("buffs.regeneration-range", 20.0);
            double rangeSquared = range * range;

            org.bukkit.Location center = calculatePartyCenter(party);
            if (center == null) return;

            for (UUID memberUUID : party. getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member == null || ! member.isOnline()) continue;
                if (! member.getWorld().equals(center.getWorld())) continue;

                if (member.getLocation().distanceSquared(center) <= rangeSquared) {
                    double newHealth = Math.min(
                            member.getHealth() + healAmount,
                            member. getAttribute(Attribute. GENERIC_MAX_HEALTH).getValue()
                    );
                    member. setHealth(newHealth);
                }
            }
        }
    }

    public void cleanup() {
        // 모든 플레이어의 모디파이어 제거
        for (Player player :  Bukkit.getOnlinePlayers()) {
            removeAttributeModifiers(player);
            removePotionEffects(player);
        }
        appliedModifiers.clear();
    }
}