package com.multiverse. pet.listener;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.api.event.PetSkillUseEvent;
import com.multiverse. pet.entity.PetEntity;
import com.multiverse.pet.model.Pet;
import com.multiverse. pet.model.skill.PetSkill;
import com. multiverse.pet. model.skill. SkillType;
import com.multiverse.pet.util. MessageUtil;
import org.bukkit. Location;
import org. bukkit. Particle;
import org. bukkit.Sound;
import org. bukkit.entity. Entity;
import org. bukkit.entity. LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit. event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. entity.EntityDamageByEntityEvent;
import org.bukkit. event.entity.EntityDamageEvent;
import org.bukkit.event.player. PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org. bukkit.potion.PotionEffectType;

import java.util.Collection;
import java. util.List;
import java.util. UUID;

/**
 * 펫 스킬 관련 리스너
 * 스킬 발동 조건, 패시브 스킬 효과 등
 */
public class PetSkillListener implements Listener {

    private final PetCore plugin;

    public PetSkillListener(PetCore plugin) {
        this.plugin = plugin;
    }

    // ===== 스킬 사용 이벤트 =====

    /**
     * 펫 스킬 사용 이벤트
     */
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onPetSkillUse(PetSkillUseEvent event) {
        Player player = event.getPlayer();
        Pet pet = event.getPet();
        PetSkill skill = event.getSkill();

        // 스킬 이펙트
        playSkillEffects(pet, skill);

        // 스킬 타입별 처리
        handleSkillType(player, pet, skill, event.getTarget());

        // 디버그 로그
        if (plugin.isDebugMode()) {
            plugin.getLogger().info("[DEBUG] " + player. getName() + "의 " + 
                    pet.getPetName() + "이(가) " + skill.getName() + " 스킬 사용");
        }
    }

    /**
     * 스킬 타입별 처리
     */
    private void handleSkillType(Player player, Pet pet, PetSkill skill, LivingEntity target) {
        SkillType type = skill.getType();
        if (type == null) return;

        switch (type) {
            case ATTACK:
                handleAttackSkill(pet, skill, target);
                break;
            case DEFENSE:
                handleDefenseSkill(pet, skill);
                break;
            case BUFF:
                handleBuffSkill(player, pet, skill);
                break;
            case DEBUFF:
                handleDebuffSkill(pet, skill, target);
                break;
            case HEAL:
                handleHealSkill(player, pet, skill);
                break;
            case GATHERING:
                handleGatheringSkill(player, pet, skill);
                break;
            case SUPPORT:
                handleSupportSkill(player, pet, skill);
                break;
            case SPECIAL:
            case ULTIMATE:
                handleSpecialSkill(player, pet, skill, target);
                break;
        }
    }

    /**
     * 공격 스킬 처리
     */
    private void handleAttackSkill(Pet pet, PetSkill skill, LivingEntity target) {
        if (target == null) return;

        double damage = skill.getEffectValue("damage");
        if (damage > 0) {
            // 펫 공격력 보정
            damage *= (1 + pet.getTotalStat("attack") / 100);

            // 스킬 레벨 보정
            damage *= (1 + (skill.getCurrentLevel() - 1) * 0.1);

            target.damage(damage);
        }

        // 추가 효과
        if (skill.hasEffect("burn")) {
            target.setFireTicks((int) (skill.getEffectValue("burn") * 20));
        }

        if (skill.hasEffect("slow")) {
            target.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOW,
                    (int) (skill.getEffectValue("slow") * 20),
                    1
            ));
        }

        if (skill.hasEffect("knockback")) {
            target.setVelocity(target.getLocation().getDirection().multiply(-skill.getEffectValue("knockback")));
        }
    }

    /**
     * 방어 스킬 처리
     */
    private void handleDefenseSkill(Pet pet, PetSkill skill) {
        PetEntity petEntity = plugin.getPetEntityManager().getPetEntity(pet. getPetId());
        if (petEntity == null || petEntity.getEntity() == null) return;

        int duration = (int) skill.getEffectValue("duration");
        if (duration <= 0) duration = 5;

        // 방어력 증가 버프
        if (skill.hasEffect("defense_boost")) {
            petEntity.getEntity().addPotionEffect(new PotionEffect(
                    PotionEffectType.DAMAGE_RESISTANCE,
                    duration * 20,
                    (int) skill.getEffectValue("defense_boost") / 10
            ));
        }

        // 무적
        if (skill. hasEffect("invincible")) {
            // 일시적 무적 처리 (별도 시스템 필요)
        }
    }

    /**
     * 버프 스킬 처리
     */
    private void handleBuffSkill(Player player, Pet pet, PetSkill skill) {
        int duration = (int) skill.getEffectValue("duration");
        if (duration <= 0) duration = 10;

        // 주인에게 버프
        if (skill. hasEffect("speed")) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.SPEED,
                    duration * 20,
                    (int) skill.getEffectValue("speed")
            ));
        }

        if (skill.hasEffect("strength")) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.INCREASE_DAMAGE,
                    duration * 20,
                    (int) skill.getEffectValue("strength")
            ));
        }

        if (skill.hasEffect("regeneration")) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType. REGENERATION,
                    duration * 20,
                    (int) skill.getEffectValue("regeneration")
            ));
        }

        if (skill. hasEffect("jump")) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.JUMP,
                    duration * 20,
                    (int) skill.getEffectValue("jump")
            ));
        }

        if (skill. hasEffect("night_vision")) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType. NIGHT_VISION,
                    duration * 20,
                    0
            ));
        }
    }

    /**
     * 디버프 스킬 처리
     */
    private void handleDebuffSkill(Pet pet, PetSkill skill, LivingEntity target) {
        if (target == null) return;

        int duration = (int) skill.getEffectValue("duration");
        if (duration <= 0) duration = 5;

        if (skill.hasEffect("weakness")) {
            target.addPotionEffect(new PotionEffect(
                    PotionEffectType.WEAKNESS,
                    duration * 20,
                    (int) skill.getEffectValue("weakness")
            ));
        }

        if (skill.hasEffect("poison")) {
            target.addPotionEffect(new PotionEffect(
                    PotionEffectType. POISON,
                    duration * 20,
                    (int) skill.getEffectValue("poison")
            ));
        }

        if (skill.hasEffect("blindness")) {
            target.addPotionEffect(new PotionEffect(
                    PotionEffectType. BLINDNESS,
                    duration * 20,
                    0
            ));
        }

        if (skill.hasEffect("wither")) {
            target.addPotionEffect(new PotionEffect(
                    PotionEffectType. WITHER,
                    duration * 20,
                    (int) skill.getEffectValue("wither")
            ));
        }
    }

    /**
     * 힐 스킬 처리
     */
    private void handleHealSkill(Player player, Pet pet, PetSkill skill) {
        double healAmount = skill.getEffectValue("healing");

        // 펫 자신 회복
        if (skill.hasEffect("self_heal")) {
            pet.heal(healAmount);
            plugin.getPetEntityManager().healPet(pet. getPetId(), healAmount);
        }

        // 주인 회복
        if (skill.hasEffect("owner_heal")) {
            double ownerHeal = healAmount * 0.5;
            double newHealth = Math.min(player.getHealth() + ownerHeal, player.getMaxHealth());
            player.setHealth(newHealth);
        }

        // 범위 회복
        if (skill.hasEffect("aoe_heal")) {
            double range = skill.getEffectValue("range");
            if (range <= 0) range = 5;

            PetEntity petEntity = plugin.getPetEntityManager().getPetEntity(pet.getPetId());
            if (petEntity != null && petEntity.getEntity() != null) {
                Location loc = petEntity.getEntity().getLocation();
                Collection<Entity> nearby = loc.getWorld().getNearbyEntities(loc, range, range, range);

                for (Entity entity : nearby) {
                    if (entity instanceof Player) {
                        Player nearbyPlayer = (Player) entity;
                        double heal = healAmount * 0.3;
                        double newHp = Math.min(nearbyPlayer.getHealth() + heal, nearbyPlayer.getMaxHealth());
                        nearbyPlayer.setHealth(newHp);
                    }
                }
            }
        }
    }

    /**
     * 채집 스킬 처리
     */
    private void handleGatheringSkill(Player player, Pet pet, PetSkill skill) {
        // 자동 채집, 행운 증가 등
        if (skill. hasEffect("fortune")) {
            // 일시적 행운 버프
            player. addPotionEffect(new PotionEffect(
                    PotionEffectType.LUCK,
                    (int) (skill.getEffectValue("duration") * 20),
                    (int) skill.getEffectValue("fortune")
            ));
        }

        if (skill.hasEffect("haste")) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType. FAST_DIGGING,
                    (int) (skill.getEffectValue("duration") * 20),
                    (int) skill.getEffectValue("haste")
            ));
        }
    }

    /**
     * 서포트 스킬 처리
     */
    private void handleSupportSkill(Player player, Pet pet, PetSkill skill) {
        // 경험치 부스트, 드롭률 증가 등
        if (skill.hasEffect("exp_boost")) {
            // 일시적 경험치 부스트 (별도 시스템 필요)
        }

        if (skill.hasEffect("loot_boost")) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.LUCK,
                    (int) (skill.getEffectValue("duration") * 20),
                    (int) skill.getEffectValue("loot_boost")
            ));
        }
    }

    /**
     * 특수/궁극기 스킬 처리
     */
    private void handleSpecialSkill(Player player, Pet pet, PetSkill skill, LivingEntity target) {
        // 스킬 ID별 특수 처리
        String skillId = skill.getSkillId();

        switch (skillId) {
            case "meteor_strike":
                handleMeteorStrike(player, pet, skill, target);
                break;
            case "divine_protection":
                handleDivineProtection(player, pet, skill);
                break;
            case "dragon_breath":
                handleDragonBreath(pet, skill);
                break;
            default:
                // 기본 특수 스킬 처리
                if (skill.hasEffect("damage")) {
                    handleAttackSkill(pet, skill, target);
                }
                if (skill.hasEffect("healing")) {
                    handleHealSkill(player, pet, skill);
                }
                break;
        }
    }

    /**
     * 메테오 스트라이크 스킬
     */
    private void handleMeteorStrike(Player player, Pet pet, PetSkill skill, LivingEntity target) {
        if (target == null) return;

        Location loc = target.getLocation();
        double damage = skill.getEffectValue("damage") * 2;
        double range = 5;

        // 범위 데미지
        Collection<Entity> nearby = loc.getWorld().getNearbyEntities(loc, range, range, range);
        for (Entity entity : nearby) {
            if (entity instanceof LivingEntity && !entity.equals(player)) {
                if (! plugin.getPetEntityManager().isPetEntity(entity)) {
                    ((LivingEntity) entity).damage(damage);
                }
            }
        }

        // 이펙트
        loc.getWorld().spawnParticle(Particle. EXPLOSION_HUGE, loc, 1);
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 100, 3, 1, 3, 0.1);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
    }

    /**
     * 신성한 보호 스킬
     */
    private void handleDivineProtection(Player player, Pet pet, PetSkill skill) {
        int duration = (int) skill.getEffectValue("duration");
        if (duration <= 0) duration = 10;

        // 주인 무적 + 재생
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration * 20, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType. REGENERATION, duration * 20, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration * 20, 0));

        // 이펙트
        player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation().add(0, 1, 0), 50, 0. 5, 1, 0.5, 0.1);
        player.getWorld().playSound(player. getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);
    }

    /**
     * 드래곤 브레스 스킬
     */
    private void handleDragonBreath(Pet pet, PetSkill skill) {
        PetEntity petEntity = plugin.getPetEntityManager().getPetEntity(pet.getPetId());
        if (petEntity == null || petEntity.getEntity() == null) return;

        LivingEntity entity = petEntity.getEntity();
        Location loc = entity.getEyeLocation();
        double damage = skill.getEffectValue("damage");
        double range = 8;

        // 전방 범위 공격
        for (Entity nearby : entity.getNearbyEntities(range, range, range)) {
            if (nearby instanceof LivingEntity) {
                if (! nearby.equals(petEntity. getOwner()) && !plugin.getPetEntityManager().isPetEntity(nearby)) {
                    // 전방 각도 확인
                    double angle = loc.getDirection().angle(
                            nearby.getLocation().subtract(loc).toVector()
                    );

                    if (angle < Math.PI / 3) { // 60도 이내
                        ((LivingEntity) nearby).damage(damage);
                        ((LivingEntity) nearby).setFireTicks(100);
                    }
                }
            }
        }

        // 이펙트
        loc.getWorld().spawnParticle(Particle. DRAGON_BREATH, loc, 100, 0.5, 0.5, 0.5, 0.2);
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 50, 0.5, 0.5, 0.5, 0.3);
        loc.getWorld().playSound(loc, Sound. ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
    }

    /**
     * 스킬 이펙트 재생
     */
    private void playSkillEffects(Pet pet, PetSkill skill) {
        PetEntity petEntity = plugin.getPetEntityManager().getPetEntity(pet.getPetId());
        if (petEntity == null || petEntity.getEntity() == null) return;

        Location loc = petEntity. getEntity().getLocation().add(0, 1, 0);

        // 스킬 타입별 기본 이펙트
        SkillType type = skill.getType();
        if (type != null) {
            switch (type) {
                case ATTACK:
                    loc.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 20, 0.5, 0.5, 0.5, 0.1);
                    loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
                    break;
                case DEFENSE: 
                    loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 20, 0.5, 0.5, 0.5, 0.1);
                    loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
                    break;
                case BUFF:
                    loc.getWorld().spawnParticle(Particle.SPELL_INSTANT, loc, 30, 0.5, 0.5, 0.5, 0.1);
                    loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
                    break;
                case HEAL:
                    loc.getWorld().spawnParticle(Particle.HEART, loc, 10, 0.5, 0.5, 0.5, 0.1);
                    loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
                    break;
                case SPECIAL:
                case ULTIMATE:
                    loc.getWorld().spawnParticle(Particle. END_ROD, loc, 50, 1, 1, 1, 0.2);
                    loc.getWorld().playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                    break;
                default:
                    loc.getWorld().spawnParticle(Particle.SPELL, loc, 15, 0.3, 0.3, 0.3, 0.1);
                    break;
            }
        }
    }

    // ===== 패시브 스킬 트리거 =====

    /**
     * 펫이 데미지 받을 때 패시브 스킬 체크
     */
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onPetDamagePassive(EntityDamageEvent event) {
        if (! plugin.getPetEntityManager().isPetEntity(event. getEntity())) {
            return;
        }

        PetEntity petEntity = plugin.getPetEntityManager().getPetEntityByEntity(event.getEntity());
        if (petEntity == null) return;

        Pet pet = petEntity. getPet();

        // 패시브 스킬 확인
        for (PetSkill skill :  pet.getSkills()) {
            if (! skill.isPassive()) continue;

            // 피해 감소 패시브
            if (skill.hasEffect("damage_reduction")) {
                double reduction = skill.getEffectValue("damage_reduction") / 100;
                event.setDamage(event. getDamage() * (1 - reduction));
            }

            // 반사 패시브
            if (skill.hasEffect("thorns") && event instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                if (damager instanceof LivingEntity) {
                    double thornsDamage = event.getDamage() * skill.getEffectValue("thorns") / 100;
                    ((LivingEntity) damager).damage(thornsDamage);
                }
            }
        }
    }

    /**
     * 주인이 웅크리기할 때 패시브 발동
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event. getPlayer();
        UUID playerId = player.getUniqueId();

        List<PetEntity> activePets = plugin. getPetManager().getActivePets(playerId);
        for (PetEntity petEntity : activePets) {
            Pet pet = petEntity. getPet();

            // 스텔스 패시브
            for (PetSkill skill : pet.getSkills()) {
                if (skill. isPassive() && skill.hasEffect("stealth_on_sneak")) {
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.INVISIBILITY,
                            60, // 3초
                            0
                    ));
                    break;
                }
            }
        }
    }
}