package com.multiverse.pet.listener;

import com. multiverse.pet. PetCore;
import com.multiverse.pet.entity. PetEntity;
import com.multiverse.pet.model. Pet;
import com.multiverse.pet.model.PetStatus;
import com.multiverse.pet.util.MessageUtil;
import org. bukkit.Bukkit;
import org.bukkit.Material;
import org. bukkit.entity. Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. block.Action;
import org. bukkit.event. player.PlayerInteractEvent;
import org.bukkit.event.player. PlayerItemConsumeEvent;
import org.bukkit.inventory. EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util. UUID;

/**
 * 펫 케어 관련 리스너
 * 자동 먹이주기, 케어 알림 등
 */
public class PetCareListener implements Listener {

    private final PetCore plugin;

    public PetCareListener(PetCore plugin) {
        this.plugin = plugin;
    }

    // ===== 자동 먹이주기 (주인이 음식 먹을 때) =====

    /**
     * 플레이어가 음식 먹을 때 펫에게도 먹이
     */
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        if (! plugin.getConfigManager().getCareSettings().isAutoFeedWithOwner()) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player. getUniqueId();
        ItemStack item = event.getItem();

        // 음식인지 확인
        if (! item.getType().isEdible()) {
            return;
        }

        // 활성 펫 확인
        List<PetEntity> activePets = plugin.getPetManager().getActivePets(playerId);
        if (activePets.isEmpty()) {
            return;
        }

        String foodId = item.getType().name();

        // 펫 음식인지 확인
        if (plugin.getPetCareManager().getFoodEffect(foodId) == null) {
            return;
        }

        // 활성 펫에게 먹이 (배고픈 펫만)
        for (PetEntity petEntity : activePets) {
            Pet pet = petEntity. getPet();

            if (pet.getHunger() < 80) {
                // 플레이어가 먹는 음식의 일부 효과를 펫에게
                double hungerRestore = plugin.getPetCareManager().getFoodEffect(foodId).getHungerRestore() * 0.5;
                pet. increaseHunger(hungerRestore);

                // 저장
                plugin.getPetManager().savePetData(playerId, pet);

                // 이펙트
                plugin.getPetEntityManager().playHappyEffect(pet. getPetId());
            }
        }
    }

    // ===== 펫 음식 아이템 사용 =====

    /**
     * 펫 전용 음식 아이템 우클릭
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPetFoodUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event. getItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        // ItemCore 커스텀 아이템 확인
        if (plugin.hasItemCore()) {
            String customItemId = plugin.getItemCoreHook().getCustomItemId(item);
            if (customItemId != null && customItemId.startsWith("pet_food_")) {
                event.setCancelled(true);
                handleCustomPetFood(player, item, customItemId);
                return;
            }
        }
    }

    /**
     * 커스텀 펫 음식 처리
     */
    private void handleCustomPetFood(Player player, ItemStack item, String customItemId) {
        UUID playerId = player. getUniqueId();

        // 활성 펫에게 먹이기
        PetEntity activePet = plugin. getPetManager().getActivePet(playerId);

        if (activePet == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.no-active-pet"));
            return;
        }

        Pet pet = activePet.getPet();

        if (pet.getHunger() >= 100) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("care.already-full")
                    .replace("{name}", pet.getPetName()));
            return;
        }

        // 음식 효과 적용
        if (plugin.getPetCareManager().feedPet(player, pet, customItemId)) {
            // 아이템 소비는 CareManager에서 처리됨
        }
    }

    // ===== 케어 상태 경고 =====

    /**
     * 주기적인 케어 상태 체크 (PetCareTask에서 호출)
     */
    public void checkAllPetsCare() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            checkPlayerPetsCare(player);
        }
    }

    /**
     * 플레이어 펫들의 케어 상태 체크
     */
    private void checkPlayerPetsCare(Player player) {
        UUID playerId = player.getUniqueId();
        List<PetEntity> activePets = plugin.getPetManager().getActivePets(playerId);

        for (PetEntity petEntity : activePets) {
            Pet pet = petEntity. getPet();

            // 배고픔 경고
            if (pet.getHunger() <= 20 && pet.getHunger() > 10) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.hunger-warning")
                        .replace("{name}", pet.getPetName())
                        .replace("{hunger}", String.format("%.0f", pet.getHunger())));
            } else if (pet. getHunger() <= 10 && pet.getHunger() > 0) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.hunger-critical")
                        . replace("{name}", pet.getPetName()));
            }

            // 행복도 경고
            if (pet.getHappiness() <= 20 && pet.getHappiness() > 10) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.happiness-warning")
                        .replace("{name}", pet. getPetName())
                        .replace("{happiness}", String.format("%.0f", pet.getHappiness())));
            } else if (pet.getHappiness() <= 10) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.happiness-critical")
                        .replace("{name}", pet. getPetName()));
            }

            // 체력 경고
            if (pet.getHealth() <= pet.getMaxHealth() * 0.2 && pet.getHealth() > 0) {
                MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("care.health-warning")
                        .replace("{name}", pet.getPetName())
                        . replace("{health}", String.format("%.0f", pet.getHealth())));
            }
        }
    }

    // ===== 펫 치료 아이템 =====

    /**
     * 펫 치료 아이템 사용
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onHealItemUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event. getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event. getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;

        // 기본 치료 아이템
        if (item.getType() == Material.GOLDEN_APPLE || item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            if (player.isSneaking()) {
                // 웅크리기 + 우클릭 = 펫에게 사용
                event. setCancelled(true);
                healActivePet(player, item);
            }
        }

        // 커스텀 치료 아이템
        if (plugin. hasItemCore()) {
            String customItemId = plugin.getItemCoreHook().getCustomItemId(item);
            if (customItemId != null && customItemId.startsWith("pet_heal_")) {
                event. setCancelled(true);
                handleCustomHealItem(player, item, customItemId);
            }
        }
    }

    /**
     * 활성 펫 치료
     */
    private void healActivePet(Player player, ItemStack item) {
        UUID playerId = player.getUniqueId();
        PetEntity activePet = plugin.getPetManager().getActivePet(playerId);

        if (activePet == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.no-active-pet"));
            return;
        }

        Pet pet = activePet.getPet();

        if (pet.getHealth() >= pet.getMaxHealth()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.already-healthy")
                    . replace("{name}", pet.getPetName()));
            return;
        }

        // 치료량 결정
        double healAmount;
        if (item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            healAmount = pet.getMaxHealth(); // 완전 회복
        } else {
            healAmount = pet.getMaxHealth() * 0.5; // 50% 회복
        }

        // 치료
        plugin.getPetCareManager().healPet(player, pet, healAmount);

        // 아이템 소비
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }

    /**
     * 커스텀 치료 아이템 처리
     */
    private void handleCustomHealItem(Player player, ItemStack item, String customItemId) {
        UUID playerId = player.getUniqueId();
        PetEntity activePet = plugin.getPetManager().getActivePet(playerId);

        if (activePet == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.no-active-pet"));
            return;
        }

        Pet pet = activePet.getPet();

        // 아이템 ID에서 치료량 추출
        double healAmount = pet.getMaxHealth() * 0.3; // 기본 30%

        if (customItemId.contains("_small_")) {
            healAmount = pet.getMaxHealth() * 0.2;
        } else if (customItemId. contains("_medium_")) {
            healAmount = pet.getMaxHealth() * 0.5;
        } else if (customItemId.contains("_large_")) {
            healAmount = pet. getMaxHealth() * 0.8;
        } else if (customItemId.contains("_full_")) {
            healAmount = pet. getMaxHealth();
        }

        // 치료
        if (plugin.getPetCareManager().healPet(player, pet, healAmount)) {
            // 아이템 소비
            plugin.getItemCoreHook().removeItem(player, customItemId, 1);
        }
    }

    // ===== 펫 부활 =====

    /**
     * 기절한 펫 부활 처리
     */
    public boolean revivePet(Player player, Pet pet) {
        if (pet. getStatus() != PetStatus. FAINTED) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.not-fainted"));
            return false;
        }

        // 부활 비용
        double cost = plugin.getConfigManager().getCareSettings().getReviveCost();

        return plugin.getPetCareManager().revivePet(player, pet, cost);
    }
}