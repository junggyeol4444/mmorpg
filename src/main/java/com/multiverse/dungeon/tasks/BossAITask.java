package com.multiverse.dungeon. tasks;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse.dungeon.data.model.BossSkill;
import com.multiverse. dungeon.data.model.DungeonBoss;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 보스 AI 태스크
 * 0.5초마다 실행되어 보스 행동 업데이트
 */
public class BossAITask extends BukkitRunnable {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public BossAITask(DungeonCore plugin) {
        this. plugin = plugin;
    }

    /**
     * 시작
     */
    public void start() {
        this.runTaskTimer(plugin, 10L, 10L); // 0.5초마다 실행
    }

    @Override
    public void run() {
        try {
            var instances = plugin.getInstanceManager().getAllInstances();

            for (var instance : instances) {
                if (! instance.isActive()) {
                    continue;
                }

                // 인스턴스의 모든 보스 처리
                var dungeon = plugin.getDungeonManager().getDungeon(instance.getDungeonId());
                if (dungeon == null || ! dungeon.hasBosses()) {
                    continue;
                }

                for (var boss : dungeon.getBosses()) {
                    updateBossAI(instance, boss);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 보스 AI 업데이트 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 보스 AI 업데이트
     */
    private void updateBossAI(com.multiverse.dungeon.data.model.DungeonInstance instance, DungeonBoss boss) {
        // 보스 엔티티 찾기 (MythicMobs 연동)
        var mythicMobsHook = plugin.getMythicMobsHook();
        if (mythicMobsHook == null) {
            return;
        }

        var bossEntity = mythicMobsHook.getMobByName(boss.getMythicMobId());
        if (bossEntity == null || ! bossEntity.isAlive()) {
            return;
        }

        // 현재 체력 비율 계산
        double maxHealth = boss.getScaledHealth(
            plugin.getScalingManager().getMobHealthMultiplier(
                instance.getDifficulty(), instance.getPlayers().size()
            )
        );
        double currentHealthPercent = (bossEntity.getHealth() / maxHealth) * 100. 0;

        // 페이즈 변경 확인
        var currentPhase = plugin.getBossManager().getCurrentPhase(boss, currentHealthPercent);
        if (currentPhase == null) {
            return;
        }

        // 사용 가능한 스킬 확인
        var availableSkills = plugin.getBossManager().getAvailableSkills(boss, currentHealthPercent);
        if (availableSkills.isEmpty()) {
            return;
        }

        // 랜덤 스킬 선택 및 사용
        BossSkill skill = availableSkills.get((int) (Math.random() * availableSkills.size()));
        if (skill != null && skill.canUse(currentHealthPercent)) {
            // 스킬 사용
            skill.use();

            // 스킬 타입에 따른 처리
            switch (skill.getType()) {
                case AOE_DAMAGE:
                    executeAOEDamage(instance, boss, bossEntity, skill);
                    break;
                case SUMMON:
                    executeSummon(instance, boss, bossEntity, currentPhase);
                    break;
                case BUFF:
                    executeBuff(bossEntity, skill);
                    break;
                case DEBUFF:
                    executeDebuff(instance, boss, skill);
                    break;
                case TELEPORT:
                    executeTeleport(bossEntity);
                    break;
                case CHARGE:
                    executeCharge(instance, bossEntity);
                    break;
                case GROUND_SMASH:
                    executeGroundSmash(instance, bossEntity, skill);
                    break;
                case PROJECTILE:
                    executeProjectile(instance, bossEntity, skill);
                    break;
                case HEALING:
                    executeHealing(bossEntity, skill);
                    break;
            }

            // 스킬 메시지 전송
            if (!  skill.getCastMessage().isEmpty()) {
                for (var playerId : instance.getPlayers()) {
                    var player = org.bukkit. Bukkit.getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        player.sendMessage("§c[보스] " + skill.getCastMessage());
                    }
                }
            }
        }

        // 엔레이지 확인
        if (plugin.getBossManager().shouldEnrage(boss, instance)) {
            double enrageDamage = boss.getBaseDamage() * plugin.getBossManager().getEnrageDamageMultiplier(boss);
            
            for (var playerId : instance.getPlayers()) {
                var player = org.bukkit.Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.sendMessage("§4⚠️ 보스가 광기로 변했습니다!");
                }
            }
        }
    }

    private void executeAOEDamage(com.multiverse.dungeon.data.model.DungeonInstance instance, 
                                   DungeonBoss boss, org.bukkit.entity.LivingEntity bossEntity, BossSkill skill) {
        for (var playerId : instance.getPlayers()) {
            var player = org.bukkit.Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline() && player.getLocation().distance(bossEntity.getLocation()) <= skill.getRadius()) {
                player.damage(skill.getDamage());
            }
        }
    }

    private void executeSummon(com.multiverse.dungeon. data.model.DungeonInstance instance, 
                                DungeonBoss boss, org. bukkit.entity.LivingEntity bossEntity, 
                                com.multiverse.dungeon.data.model.BossPhase phase) {
        if (!  phase.hasSummonMobs()) {
            return;
        }

        var mythicMobsHook = plugin.getMythicMobsHook();
        if (mythicMobsHook == null) {
            return;
        }

        for (var mobId : phase.getSummonMobs()) {
            mythicMobsHook.spawnMob(mobId, bossEntity.getLocation());
        }

        for (var playerId : instance.getPlayers()) {
            var player = org.bukkit.Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendMessage("§e⚠️ 보스가 추가 몬스터를 소환했습니다!");
            }
        }
    }

    private void executeBuff(org.bukkit.entity.LivingEntity bossEntity, BossSkill skill) {
        bossEntity.addPotionEffect(
            new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE,
                skill.getDuration() * 20,
                1
            )
        );
    }

    private void executeDebuff(com.multiverse.dungeon. data.model.DungeonInstance instance, 
                               DungeonBoss boss, BossSkill skill) {
        for (var playerId : instance.getPlayers()) {
            var player = org.bukkit.Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.addPotionEffect(
                    new org.bukkit.potion. PotionEffect(
                        org.bukkit.potion. PotionEffectType. WEAKNESS,
                        skill.getDuration() * 20,
                        0
                    )
                );
            }
        }
    }

    private void executeTeleport(org.bukkit.entity. LivingEntity bossEntity) {
        var nearbyPlayers = bossEntity.getNearbyEntities(20, 20, 20). stream()
            .filter(e -> e instanceof org.bukkit. entity.Player)
            .toList();

        if (!  nearbyPlayers.isEmpty()) {
            var randomPlayer = (org.bukkit.entity.Player) nearbyPlayers. get(
                (int) (Math.random() * nearbyPlayers.size())
            );
            bossEntity.teleport(randomPlayer.getLocation(). add(
                (Math.random() - 0.5) * 5,
                0,
                (Math.random() - 0.5) * 5
            ));
        }
    }

    private void executeCharge(com.multiverse. dungeon.data.model.DungeonInstance instance, 
                               org.bukkit.entity.LivingEntity bossEntity) {
        var nearbyPlayers = bossEntity.getNearbyEntities(30, 30, 30).stream()
            .filter(e -> e instanceof org.bukkit.entity.Player)
            .map(e -> (org.bukkit.entity.Player) e)
            .toList();

        if (! nearbyPlayers.  isEmpty()) {
            var target = nearbyPlayers.get((int) (Math.random() * nearbyPlayers.size()));
            var direction = target.getLocation().subtract(bossEntity.getLocation()). toVector(). normalize();
            bossEntity.setVelocity(direction.multiply(2));
        }
    }

    private void executeGroundSmash(com.multiverse.dungeon. data.model.DungeonInstance instance, 
                                    org.bukkit.entity.LivingEntity bossEntity, BossSkill skill) {
        var location = bossEntity.  getLocation();
        
        // 범위 내의 모든 플레이어에게 데미지
        for (var entity : location.getWorld().getNearbyEntities(location, skill.getRadius(), skill.getRadius(), skill.getRadius())) {
            if (entity instanceof org.bukkit.entity.Player player) {
                if (instance.getPlayers().contains(player.getUniqueId())) {
                    player.damage(skill.getDamage());
                }
            }
        }

        // 파티클 효과 (선택)
        location.getWorld().spawnParticle(
            org.bukkit. Particle.EXPLOSION_LARGE,
            location,
            5
        );
    }

    private void executeProjectile(com.multiverse.dungeon.data.model.DungeonInstance instance, 
                                   org.bukkit.entity.LivingEntity bossEntity, BossSkill skill) {
        var nearbyPlayers = bossEntity. getNearbyEntities(30, 30, 30).stream()
            .filter(e -> e instanceof org.bukkit.entity. Player)
            .map(e -> (org.bukkit.entity.Player) e)
            . toList();

        for (var player : nearbyPlayers) {
            var projectile = bossEntity.launchProjectile(org.bukkit.entity.Snowball.class);
            var direction = player.getLocation().subtract(bossEntity.getLocation()). toVector().normalize();
            projectile.setVelocity(direction.multiply(2));
        }
    }

    private void executeHealing(org.bukkit.entity.LivingEntity bossEntity, BossSkill skill) {
        bossEntity. setHealth(Math.min(bossEntity.getMaxHealth(), bossEntity.getHealth() + skill.getDamage()));
    }
}