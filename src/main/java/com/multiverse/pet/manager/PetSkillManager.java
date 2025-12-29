package com.multiverse.pet.  manager;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. api.event.PetSkillUseEvent;
import com.  multiverse.pet.  model.Pet;
import com.multiverse. pet.model.PetSpecies;
import com. multiverse.pet. model.skill.PetSkill;
import com. multiverse.pet.  model.skill. SkillEffect;
import com. multiverse.pet.  model.skill. SkillType;
import com.multiverse.  pet.util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit.  Location;
import org. bukkit.  Particle;
import org.bukkit.Sound;
import org. bukkit.entity. LivingEntity;
import org.bukkit.entity.  Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 펫 스킬 매니저 클래스
 * 펫 스킬 사용, 해금, 강화 관리
 */
public class PetSkillManager {

    private final PetCore plugin;

    // 스킬 템플릿 저장소 (스킬ID -> 스킬)
    private final Map<String, PetSkill> skillTemplates;

    // 활성 스킬 효과 (펫ID -> 효과 목록)
    private final Map<UUID, List<SkillEffect>> activeEffects;

    // 스킬 쿨다운 관리
    private final Map<String, Long> globalCooldowns;

    // 설정 값
    private double skillDamageMultiplier;
    private double skillHealingMultiplier;
    private double skillCooldownReduction;
    private int maxSkillLevel;
    private double skillUpgradeCost;
    private boolean autoUsePassiveSkills;

    /**
     * 생성자
     */
    public PetSkillManager(PetCore plugin) {
        this.plugin = plugin;
        this.skillTemplates = new ConcurrentHashMap<>();
        this.activeEffects = new ConcurrentHashMap<>();
        this.  globalCooldowns = new ConcurrentHashMap<>();
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.skillDamageMultiplier = plugin.getSkillSettings().getDamageMultiplier();
        this.skillHealingMultiplier = plugin.getSkillSettings().getHealingMultiplier();
        this.skillCooldownReduction = plugin.getSkillSettings().getCooldownReduction();
        this.maxSkillLevel = plugin.getSkillSettings().getMaxSkillLevel();
        this.  skillUpgradeCost = plugin.getSkillSettings().getUpgradeCost();
        this.autoUsePassiveSkills = plugin.getSkillSettings().isAutoUsePassiveSkills();
    }

    // ===== 스킬 템플릿 관리 =====

    /**
     * 스킬 템플릿 등록
     */
    public void registerSkill(PetSkill skill) {
        skillTemplates.  put(skill.getSkillId(), skill);
    }

    /**
     * 스킬 템플릿 가져오기
     */
    public PetSkill getSkillTemplate(String skillId) {
        return skillTemplates. get(skillId);
    }

    /**
     * 모든 스킬 템플릿 가져오기
     */
    public Collection<PetSkill> getAllSkillTemplates() {
        return Collections.  unmodifiableCollection(skillTemplates.values());
    }

    /**
     * 타입별 스킬 목록 가져오기
     */
    public List<PetSkill> getSkillsByType(SkillType type) {
        List<PetSkill> result = new ArrayList<>();
        for (PetSkill skill : skillTemplates.values()) {
            if (skill.getType() == type) {
                result.add(skill);
            }
        }
        return result;
    }

    // ===== 스킬 해금 =====

    /**
     * 스킬 해금
     *
     * @param pet 펫
     * @param skillId 스킬 ID
     * @return 해금 성공 여부
     */
    public boolean unlockSkill(Pet pet, String skillId) {
        // 이미 보유한 스킬인지 확인
        if (pet.hasSkill(skillId)) {
            return false;
        }

        // 스킬 템플릿 확인
        PetSkill template = getSkillTemplate(skillId);
        if (template == null) {
            plugin.getLogger().warning("스킬 템플릿을 찾을 수 없습니다:   " + skillId);
            return false;
        }

        // 스킬 슬롯 확인
        int maxSlots = pet.getRarity().getSkillSlots();
        if (pet.getSkills().size() >= maxSlots) {
            Player owner = Bukkit.getPlayer(pet.  getOwnerId());
            if (owner != null) {
                MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("pet.skill-slots-full")
                        . replace("{max}", String.valueOf(maxSlots)));
            }
            return false;
        }

        // 요구 레벨 확인
        if (pet.getLevel() < template.  getRequiredPetLevel()) {
            Player owner = Bukkit.getPlayer(pet. getOwnerId());
            if (owner != null) {
                MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("pet.skill-level-required")
                        . replace("{level}", String.valueOf(template.getRequiredPetLevel())));
            }
            return false;
        }

        // 선행 스킬 확인
        for (String requiredSkillId :   template.getRequiredSkills()) {
            if (!  pet.hasSkill(requiredSkillId)) {
                Player owner = Bukkit.  getPlayer(pet. getOwnerId());
                if (owner != null) {
                    MessageUtil.  sendMessage(owner, plugin.getConfigManager().getMessage("pet.skill-prerequisite")
                            .replace("{skill}", requiredSkillId));
                }
                return false;
            }
        }

        // 스킬 복제 및 추가
        PetSkill newSkill = new PetSkill(template);
        pet.addSkill(newSkill);

        // 저장
        plugin.getPetManager().savePetData(pet. getOwnerId(), pet);

        // 알림
        Player owner = Bukkit. getPlayer(pet. getOwnerId());
        if (owner != null) {
            MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("pet.skill-learned")
                    . replace("{name}", pet.  getPetName())
                    .replace("{skill}", newSkill. getName()));
        }

        return true;
    }

    /**
     * 스킬 제거
     */
    public boolean removeSkill(Pet pet, String skillId) {
        if (! pet.hasSkill(skillId)) {
            return false;
        }

        pet.removeSkill(skillId);
        plugin.getPetManager().savePetData(pet.getOwnerId(), pet);

        return true;
    }

    // ===== 스킬 사용 =====

    /**
     * 스킬 사용
     *
     * @param pet 펫
     * @param skillId 스킬 ID
     * @param target 타겟 (없으면 null)
     * @return 사용 성공 여부
     */
    public boolean useSkill(Pet pet, String skillId, LivingEntity target) {
        // 스킬 가져오기
        PetSkill skill = pet.  getSkill(skillId);
        if (skill == null) {
            return false;
        }

        // 사용 가능 여부 확인
        String cannotUseReason = skill.getCannotUseReason(pet. getLevel(), pet.getHunger());
        if (cannotUseReason != null) {
            Player owner = Bukkit.getPlayer(pet.  getOwnerId());
            if (owner != null) {
                MessageUtil.sendMessage(owner, "&c" + cannotUseReason);
            }
            return false;
        }

        // 패시브 스킬은 수동 사용 불가
        if (skill.isPassive()) {
            return false;
        }

        // 펫 상태 확인
        if (!  pet.canAct()) {
            return false;
        }

        // 스킬 사용 이벤트
        Player owner = Bukkit. getPlayer(pet. getOwnerId());
        if (owner != null) {
            PetSkillUseEvent event = new PetSkillUseEvent(owner, pet, skill, target);
            Bukkit.getPluginManager().callEvent(event);
            if (event.  isCancelled()) {
                return false;
            }
        }

        // 비용 소모
        if (skill.  getHungerCost() > 0) {
            pet.decreaseHunger(skill.getHungerCost());
        }

        // 쿨다운 시작
        skill.use();

        // 효과 적용
        applySkillEffect(pet, skill, target, owner);

        // 저장
        plugin. getPetManager().savePetData(pet.  getOwnerId(), pet);

        return true;
    }

    /**
     * 스킬 효과 적용
     */
    private void applySkillEffect(Pet pet, PetSkill skill, LivingEntity target, Player owner) {
        SkillEffect effect = skill.  createEffect();
        effect. setSourceEntityId(pet.getPetId());
        effect. setOwnerPlayerId(pet.getOwnerId());

        // 타입별 효과 적용
        switch (skill.getType()) {
            case ATTACK:
                applyAttackSkill(pet, skill, effect, target, owner);
                break;
            case DEFENSE:
                applyDefenseSkill(pet, skill, effect, owner);
                break;
            case BUFF:
                applyBuffSkill(pet, skill, effect, owner);
                break;
            case DEBUFF:  
                applyDebuffSkill(pet, skill, effect, target, owner);
                break;
            case HEAL: 
                applyHealSkill(pet, skill, effect, owner);
                break;
            case GATHERING:
                applyGatheringSkill(pet, skill, effect, owner);
                break;
            case SUPPORT:
                applySupportSkill(pet, skill, effect, owner);
                break;
            case SPECIAL:
            case ULTIMATE:
                applySpecialSkill(pet, skill, effect, target, owner);
                break;
            default:
                break;
        }

        // 지속 효과면 등록
        if (skill.getDuration() > 0) {
            registerActiveEffect(pet. getPetId(), effect);
        }

        // 이펙트 재생
        playSkillEffects(pet, skill, owner);

        // 스킬 사용 알림
        if (owner != null) {
            MessageUtil. sendMessage(owner, plugin.getConfigManager().getMessage("pet.skill-used")
                    .replace("{name}", pet.getPetName())
                    .replace("{skill}", skill.getName()));
        }
    }

    /**
     * 공격 스킬 적용
     */
    private void applyAttackSkill(Pet pet, PetSkill skill, SkillEffect effect, 
                                   LivingEntity target, Player owner) {
        if (target == null) return;

        double baseDamage = effect. getDamage();
        double petAttack = pet.getTotalStat("attack");
        double finalDamage = (baseDamage + petAttack * 0.5) * skillDamageMultiplier;

        // 치명타 확률
        double critChance = pet.getTotalStat("critical_chance");
        boolean isCrit = Math.random() * 100 < critChance;
        if (isCrit) {
            double critDamage = pet.getTotalStat("critical_damage");
            finalDamage *= (1 + critDamage / 100);
        }

        target.damage(finalDamage);
        effect.addAffectedEntity(target. getUniqueId());

        // 포션 효과 적용
        effect.  applyPotionEffects(target);
    }

    /**
     * 방어 스킬 적용
     */
    private void applyDefenseSkill(Pet pet, PetSkill skill, SkillEffect effect, Player owner) {
        // 방어력 증가 버프를 펫에 적용
        double defenseBonus = effect.getValue("defense_bonus");
        pet.addBonusStat("defense", defenseBonus);

        effect.activate();
    }

    /**
     * 버프 스킬 적용
     */
    private void applyBuffSkill(Pet pet, PetSkill skill, SkillEffect effect, Player owner) {
        if (owner == null) return;

        // 주인에게 버프 적용
        effect.applyPotionEffects(owner);
        effect.addAffectedEntity(owner.  getUniqueId());
        effect.activate();
    }

    /**
     * 디버프 스킬 적용
     */
    private void applyDebuffSkill(Pet pet, PetSkill skill, SkillEffect effect, 
                                   LivingEntity target, Player owner) {
        if (target == null) return;

        effect.  applyPotionEffects(target);
        effect.addAffectedEntity(target.getUniqueId());
        effect.activate();
    }

    /**
     * 힐 스킬 적용
     */
    private void applyHealSkill(Pet pet, PetSkill skill, SkillEffect effect, Player owner) {
        double baseHealing = effect.getHealing();
        double finalHealing = baseHealing * skillHealingMultiplier;

        // 타겟에 따라 적용
        switch (skill.getTarget()) {
            case SELF:
                pet.heal(finalHealing);
                break;
            case OWNER:
                if (owner != null) {
                    double newHealth = Math.min(owner.getMaxHealth(), owner.  getHealth() + finalHealing);
                    owner.setHealth(newHealth);
                }
                break;
            case ALLY:
                // 주인과 펫 모두 회복
                pet.heal(finalHealing * 0.5);
                if (owner != null) {
                    double newHealth = Math. min(owner.  getMaxHealth(), owner. getHealth() + finalHealing * 0.5);
                    owner.setHealth(newHealth);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 채집 스킬 적용
     */
    private void applyGatheringSkill(Pet pet, PetSkill skill, SkillEffect effect, Player owner) {
        // 채집 버프 적용
        double gatheringBonus = effect. getValue("gathering_bonus");
        pet.addBonusStat("gathering", gatheringBonus);
        effect.activate();
    }

    /**
     * 지원 스킬 적용
     */
    private void applySupportSkill(Pet pet, PetSkill skill, SkillEffect effect, Player owner) {
        if (owner == null) return;

        // 다양한 지원 효과 적용
        effect.applyPotionEffects(owner);
        effect.activate();
    }

    /**
     * 특수/궁극기 스킬 적용
     */
    private void applySpecialSkill(Pet pet, PetSkill skill, SkillEffect effect, 
                                    LivingEntity target, Player owner) {
        // 범위 스킬 처리
        if (skill.getTarget() == PetSkill.  SkillTarget.AREA && owner != null) {
            List<LivingEntity> targets = effect.findTargets(owner. getLocation(), pet, owner);
            
            double baseDamage = effect.getDamage();
            double petAttack = pet.getTotalStat("attack");
            double finalDamage = (baseDamage + petAttack) * skillDamageMultiplier;

            for (LivingEntity entity : targets) {
                entity.damage(finalDamage);
                effect.applyPotionEffects(entity);
                effect.addAffectedEntity(entity. getUniqueId());
            }
        } else if (target != null) {
            // 단일 대상
            double baseDamage = effect.  getDamage();
            double petAttack = pet.getTotalStat("attack");
            double finalDamage = (baseDamage + petAttack) * skillDamageMultiplier * 1.  5; // 궁극기 보너스

            target.  damage(finalDamage);
            effect.  applyPotionEffects(target);
            effect.  addAffectedEntity(target.  getUniqueId());
        }

        effect.activate();
    }

    /**
     * 스킬 이펙트 재생
     */
    private void playSkillEffects(Pet pet, PetSkill skill, Player owner) {
        // 펫 엔티티 위치 가져오기
        com.multiverse.pet.entity.  PetEntity petEntity = 
            plugin.getPetManager().getActivePetEntity(pet. getOwnerId(), pet.getPetId());
        
        if (petEntity == null || petEntity.getEntity() == null) return;

        Location loc = petEntity.getEntity().getLocation();

        // 파티클 효과
        String particleEffect = skill. getParticleEffect();
        if (particleEffect != null && !particleEffect. isEmpty()) {
            try {
                Particle particle = Particle.  valueOf(particleEffect. toUpperCase());
                loc.getWorld().spawnParticle(particle, loc.add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0. 1);
            } catch (IllegalArgumentException ignored) {
            }
        }

        // 사운드 효과
        String soundEffect = skill.getSoundEffect();
        if (soundEffect != null && !soundEffect. isEmpty()) {
            try {
                Sound sound = Sound. valueOf(soundEffect. toUpperCase());
                loc.getWorld().playSound(loc, sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    // ===== 활성 효과 관리 =====

    /**
     * 활성 효과 등록
     */
    public void registerActiveEffect(UUID petId, SkillEffect effect) {
        activeEffects.  computeIfAbsent(petId, k -> new ArrayList<>()).add(effect);
    }

    /**
     * 활성 효과 제거
     */
    public void removeActiveEffect(UUID petId, String skillId) {
        List<SkillEffect> effects = activeEffects.get(petId);
        if (effects != null) {
            effects.removeIf(e -> e. getSkillId().equals(skillId));
        }
    }

    /**
     * 펫의 모든 활성 효과 제거
     */
    public void clearActiveEffects(UUID petId) {
        activeEffects.remove(petId);
    }

    /**
     * 활성 효과 틱 처리
     */
    public void tickActiveEffects() {
        for (Map.Entry<UUID, List<SkillEffect>> entry :  activeEffects.entrySet()) {
            List<SkillEffect> effects = entry.getValue();
            Iterator<SkillEffect> iterator = effects.iterator();

            while (iterator. hasNext()) {
                SkillEffect effect = iterator.next();
                
                if (effect. isExpired()) {
                    // 효과 만료 처리
                    onEffectExpire(entry.getKey(), effect);
                    iterator. remove();
                } else {
                    effect.tick();
                }
            }
        }
    }

    /**
     * 효과 만료 처리
     */
    private void onEffectExpire(UUID petId, SkillEffect effect) {
        Pet pet = plugin.getPetCache().getPet(petId);
        if (pet == null) return;

        // 버프 효과 제거
        Map<String, Double> values = effect.getValues();
        for (String statName : values.keySet()) {
            if (statName.endsWith("_bonus")) {
                String actualStat = statName. replace("_bonus", "");
                double currentBonus = pet.getBonusStats().getOrDefault(actualStat, 0.0);
                pet.addBonusStat(actualStat, -values.get(statName));
            }
        }
    }

    // ===== 스킬 강화 =====

    /**
     * 스킬 강화
     *
     * @param player 플레이어
     * @param pet 펫
     * @param skillId 스킬 ID
     * @return 강화 성공 여부
     */
    public boolean upgradeSkill(Player player, Pet pet, String skillId) {
        PetSkill skill = pet. getSkill(skillId);
        if (skill == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.skill-not-found"));
            return false;
        }

        // 최대 레벨 확인
        if (skill.isMaxLevel()) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.skill-max-level"));
            return false;
        }

        // 스킬 포인트 확인
        if (pet.getSkillPoints() <= 0) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.no-skill-points"));
            return false;
        }

        // 비용 확인
        double cost = skillUpgradeCost * skill.getCurrentLevel();
        if (cost > 0 && ! plugin.getPlayerDataCoreHook().hasGold(player.getUniqueId(), cost)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-enough-gold")
                    .replace("{cost}", String. format("%.0f", cost)));
            return false;
        }

        // 비용 차감
        if (cost > 0) {
            plugin.getPlayerDataCoreHook().withdrawGold(player. getUniqueId(), cost);
        }

        // 스킬 포인트 사용
        pet. useSkillPoint();

        // 스킬 레벨업
        skill.levelUp();

        // 저장
        plugin. getPetManager().savePetData(pet.getOwnerId(), pet);

        // 알림
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.skill-upgraded")
                .replace("{skill}", skill.getName())
                .replace("{level}", String.  valueOf(skill.getCurrentLevel())));

        return true;
    }

    /**
     * 스킬 리셋
     */
    public boolean resetSkills(Player player, Pet pet) {
        if (pet.getSkills().isEmpty()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.no-skills-to-reset"));
            return false;
        }

        // 스킬 포인트 반환 계산
        int refundPoints = 0;
        for (PetSkill skill :  pet.getSkills()) {
            refundPoints += skill. getCurrentLevel() - 1;
        }

        // 스킬 초기화
        for (PetSkill skill : pet.  getSkills()) {
            skill.setCurrentLevel(1);
            skill.resetCooldown();
        }

        // 스킬 포인트 반환
        pet.setSkillPoints(pet.getSkillPoints() + refundPoints);

        // 저장
        plugin.getPetManager().savePetData(pet.getOwnerId(), pet);

        // 알림
        MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("pet.skills-reset")
                .replace("{points}", String.  valueOf(refundPoints)));

        return true;
    }

    // ===== 패시브 스킬 처리 =====

    /**
     * 패시브 스킬 적용
     */
    public void applyPassiveSkills(Pet pet) {
        if (! autoUsePassiveSkills) return;

        for (PetSkill skill : pet.getSkills()) {
            if (skill. isPassive() && skill.isEnabled()) {
                SkillEffect effect = skill.createEffect();
                effect. setSourceEntityId(pet.getPetId());
                effect. setOwnerPlayerId(pet.getOwnerId());

                // 패시브 효과 적용
                Map<String, Double> effects = skill.getAllEffects();
                for (Map.Entry<String, Double> entry : effects.entrySet()) {
                    pet.addBonusStat(entry.  getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * 패시브 스킬 제거
     */
    public void removePassiveSkills(Pet pet) {
        for (PetSkill skill : pet.getSkills()) {
            if (skill. isPassive()) {
                Map<String, Double> effects = skill.getAllEffects();
                for (Map.Entry<String, Double> entry : effects.entrySet()) {
                    pet.addBonusStat(entry.  getKey(), -entry.getValue());
                }
            }
        }
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadSettings();
    }

    // ===== Getter =====

    public double getSkillDamageMultiplier() {
        return skillDamageMultiplier;
    }

    public double getSkillHealingMultiplier() {
        return skillHealingMultiplier;
    }

    public int getMaxSkillLevel() {
        return maxSkillLevel;
    }

    public Map<String, PetSkill> getSkillTemplates() {
        return Collections. unmodifiableMap(skillTemplates);
    }
}