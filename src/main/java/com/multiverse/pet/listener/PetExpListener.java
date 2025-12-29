package com.multiverse.pet.listener;

import com.multiverse. pet.PetCore;
import com. multiverse.pet. api.event.PetLevelUpEvent;
import com.multiverse.pet.entity.PetEntity;
import com.multiverse.pet.model.Pet;
import com.multiverse.pet.model.PetType;
import com. multiverse.pet. util.MessageUtil;
import org.bukkit.entity.Entity;
import org. bukkit.entity. LivingEntity;
import org.bukkit.entity.Player;
import org. bukkit.event. EventHandler;
import org.bukkit. event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit. event.entity.EntityDeathEvent;
import org.bukkit. event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util. List;
import java.util. UUID;

/**
 * 펫 경험치 획득 관련 리스너
 * 몬스터 처치, 채집, 낚시 등에서 경험치 획득
 */
public class PetExpListener implements Listener {

    private final PetCore plugin;

    public PetExpListener(PetCore plugin) {
        this.plugin = plugin;
    }

    // ===== 몬스터 처치 경험치 =====

    /**
     * 주인이 몬스터를 처치할 때 펫 경험치
     */
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onEntityKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer == null) {
            return;
        }

        UUID playerId = killer.getUniqueId();

        // 활성 펫 확인
        List<PetEntity> activePets = plugin.getPetManager().getActivePets(playerId);
        if (activePets.isEmpty()) {
            return;
        }

        // 기본 경험치 계산
        int baseExp = calculateMobExp(entity);
        if (baseExp <= 0) {
            return;
        }

        // 각 펫에게 경험치 분배
        for (PetEntity petEntity : activePets) {
            Pet pet = petEntity. getPet();

            // 전투형 펫 보너스
            double multiplier = 1.0;
            if (pet.getType() == PetType.COMBAT) {
                multiplier = 1.5;
            }

            // 거리 보너스 (가까울수록 더 많은 경험치)
            double distance = petEntity.getEntity().getLocation().distance(entity.getLocation());
            if (distance <= 5) {
                multiplier *= 1.2;
            } else if (distance <= 10) {
                multiplier *= 1.0;
            } else if (distance <= 20) {
                multiplier *= 0.8;
            } else {
                multiplier *= 0.5;
            }

            // 행복도 보너스
            if (pet.getHappiness() >= 80) {
                multiplier *= 1.1;
            } else if (pet. getHappiness() < 30) {
                multiplier *= 0.8;
            }

            int finalExp = (int) (baseExp * multiplier);

            // 경험치 추가
            if (finalExp > 0) {
                plugin.getPetLevelManager().addExperience(pet, finalExp, killer);
            }
        }
    }

    /**
     * 몬스터 경험치 계산
     */
    private int calculateMobExp(LivingEntity entity) {
        double baseExp = 10;

        // 체력에 비례
        baseExp += entity.getMaxHealth() * 0.3;

        // 엔티티 타입별 보정
        switch (entity.getType()) {
            case WITHER:
            case ENDER_DRAGON:
                baseExp *= 10;
                break;
            case ELDER_GUARDIAN:
            case WARDEN:
                baseExp *= 5;
                break;
            case RAVAGER:
            case EVOKER:
            case VINDICATOR:
                baseExp *= 3;
                break;
            case BLAZE:
            case GHAST:
            case WITCH:
            case PIGLIN_BRUTE:
                baseExp *= 2;
                break;
            case ZOMBIE:
            case SKELETON:
            case SPIDER:
            case CREEPER:
                baseExp *= 1;
                break;
            case SLIME:
            case SILVERFISH:
                baseExp *= 0.5;
                break;
            default:
                break;
        }

        return (int) baseExp;
    }

    // ===== 채집 경험치 =====

    /**
     * 블록 파괴 시 채집 펫 경험치
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player. getUniqueId();

        // 활성 펫 확인
        List<PetEntity> activePets = plugin.getPetManager().getActivePets(playerId);
        if (activePets.isEmpty()) {
            return;
        }

        // 채집 경험치 계산
        int baseExp = calculateBlockExp(event.getBlock().getType());
        if (baseExp <= 0) {
            return;
        }

        // 채집형 펫에게 경험치
        for (PetEntity petEntity : activePets) {
            Pet pet = petEntity.getPet();

            // 채집형 펫만 또는 감소된 경험치
            double multiplier = 0.3; // 기본 30%
            if (pet.getType() == PetType. GATHERING) {
                multiplier = 1.5; // 채집형은 150%
            }

            // 행복도 보너스
            if (pet.getHappiness() >= 80) {
                multiplier *= 1.1;
            }

            int finalExp = (int) (baseExp * multiplier);

            if (finalExp > 0) {
                plugin.getPetLevelManager().addExperience(pet, finalExp, player);
            }
        }
    }

    /**
     * 블록 경험치 계산
     */
    private int calculateBlockExp(org.bukkit.Material material) {
        switch (material) {
            // 광석
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE: 
                return 50;
            case EMERALD_ORE: 
            case DEEPSLATE_EMERALD_ORE:
                return 45;
            case ANCIENT_DEBRIS: 
                return 60;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE: 
            case NETHER_GOLD_ORE: 
                return 20;
            case IRON_ORE: 
            case DEEPSLATE_IRON_ORE: 
                return 15;
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                return 10;
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE: 
                return 18;
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE: 
                return 12;
            case COAL_ORE: 
            case DEEPSLATE_COAL_ORE: 
                return 5;
            case NETHER_QUARTZ_ORE: 
                return 8;

            // 나무
            case OAK_LOG:
            case BIRCH_LOG: 
            case SPRUCE_LOG: 
            case JUNGLE_LOG:
            case ACACIA_LOG: 
            case DARK_OAK_LOG:
            case MANGROVE_LOG: 
            case CHERRY_LOG:
            case CRIMSON_STEM:
            case WARPED_STEM:
                return 3;

            // 농작물
            case WHEAT: 
            case CARROTS:
            case POTATOES: 
            case BEETROOTS:
                return 2;
            case MELON:
            case PUMPKIN: 
                return 4;
            case SUGAR_CANE: 
            case BAMBOO:
                return 1;
            case NETHER_WART:
                return 3;

            default:
                return 0;
        }
    }

    // ===== 낚시 경험치 =====

    /**
     * 낚시 시 펫 경험치
     */
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // 활성 펫 확인
        List<PetEntity> activePets = plugin.getPetManager().getActivePets(playerId);
        if (activePets.isEmpty()) {
            return;
        }

        // 낚시 경험치
        Entity caught = event.getCaught();
        int baseExp = 10;

        if (caught != null) {
            // 잡은 아이템에 따라 경험치 조정
            if (caught instanceof org.bukkit.entity.Item) {
                org.bukkit.entity.Item item = (org.bukkit. entity.Item) caught;
                org.bukkit.Material type = item.getItemStack().getType();

                switch (type) {
                    case COD:
                    case SALMON:
                        baseExp = 8;
                        break;
                    case TROPICAL_FISH: 
                    case PUFFERFISH: 
                        baseExp = 15;
                        break;
                    case BOW:
                    case FISHING_ROD:
                    case NAME_TAG:
                    case SADDLE:
                        baseExp = 25;
                        break;
                    case ENCHANTED_BOOK:
                        baseExp = 40;
                        break;
                    case NAUTILUS_SHELL: 
                        baseExp = 35;
                        break;
                    default:
                        baseExp = 5;
                        break;
                }
            }
        }

        // 펫에게 경험치 분배
        for (PetEntity petEntity : activePets) {
            Pet pet = petEntity.getPet();

            double multiplier = 0.5;
            if (pet.getType() == PetType. GATHERING) {
                multiplier = 1.2;
            }

            int finalExp = (int) (baseExp * multiplier);

            if (finalExp > 0) {
                plugin.getPetLevelManager().addExperience(pet, finalExp, player);
            }
        }
    }

    // ===== 플레이어 경험치 연동 =====

    /**
     * 플레이어 경험치 획득 시 펫도 일부 획득
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        int amount = event.getAmount();

        if (amount <= 0) {
            return;
        }

        // 활성 펫 확인
        List<PetEntity> activePets = plugin. getPetManager().getActivePets(playerId);
        if (activePets. isEmpty()) {
            return;
        }

        // 플레이어 경험치의 일부를 펫에게
        double shareRate = plugin.getConfigManager().getLevelSettings().getPlayerExpShareRate();
        if (shareRate <= 0) {
            return;
        }

        int sharedExp = (int) (amount * shareRate);
        if (sharedExp <= 0) {
            return;
        }

        // 분배
        int expPerPet = Math. max(1, sharedExp / activePets.size());

        for (PetEntity petEntity : activePets) {
            Pet pet = petEntity.getPet();
            plugin.getPetLevelManager().addExperience(pet, expPerPet, player);
        }
    }

    // ===== 레벨업 이벤트 처리 =====

    /**
     * 펫 레벨업 이벤트
     */
    @EventHandler(priority = EventPriority. MONITOR)
    public void onPetLevelUp(PetLevelUpEvent event) {
        Player player = event.getPlayer();
        Pet pet = event.getPet();
        int oldLevel = event.getOldLevel();
        int newLevel = event.getNewLevel();

        // 레벨업 이펙트
        if (pet.isActive()) {
            plugin.getPetEntityManager().playLevelUpEffect(pet. getPetId());
        }

        // 알림
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet. level-up")
                .replace("{name}", pet.getPetName())
                .replace("{level}", String.valueOf(newLevel)));

        // 스킬 포인트 획득 알림
        int skillPoints = newLevel - oldLevel; // 레벨당 1포인트
        if (skillPoints > 0) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.skill-points-gained")
                    .replace("{points}", String.valueOf(skillPoints)));
        }

        // 진화 가능 알림
        if (plugin.getEvolutionManager().hasAvailableEvolution(pet)) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.evolution-available")
                    .replace("{name}", pet.getPetName()));
        }

        // 새 스킬 해금 확인
        List<String> newSkills = plugin.getPetSkillManager().getNewUnlockableSkills(pet, oldLevel, newLevel);
        if (!newSkills.isEmpty()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.new-skills-available")
                    . replace("{skills}", String.join(", ", newSkills)));
        }
    }
}