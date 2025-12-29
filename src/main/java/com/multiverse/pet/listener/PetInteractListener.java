package com.multiverse. pet.listener;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.entity.PetEntity;
import com.multiverse.pet.manager.PetCareManager;
import com.multiverse.pet.model.Pet;
import com.multiverse. pet.util.MessageUtil;
import org.bukkit.Material;
import org. bukkit.entity.Entity;
import org. bukkit.entity. Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit. event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit. event.player.PlayerInteractAtEntityEvent;
import org.bukkit. event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory. EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java. util.Map;
import java.util. UUID;

/**
 * 펫 상호작용 관련 리스너
 * 펫 클릭, 먹이주기, 앉히기 등 처리
 */
public class PetInteractListener implements Listener {

    private final PetCore plugin;

    // 더블클릭 감지용
    private final Map<UUID, Long> lastClickTime;
    private final Map<UUID, UUID> lastClickedPet;
    private static final long DOUBLE_CLICK_THRESHOLD = 300; // 밀리초

    public PetInteractListener(PetCore plugin) {
        this.plugin = plugin;
        this. lastClickTime = new HashMap<>();
        this.lastClickedPet = new HashMap<>();
    }

    // ===== 펫 우클릭 상호작용 =====

    /**
     * 펫 우클릭 상호작용
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPetInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot. HAND) {
            return;
        }

        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        if (! plugin.getPetEntityManager().isPetEntity(entity)) {
            return;
        }

        PetEntity petEntity = plugin.getPetEntityManager().getPetEntityByEntity(entity);
        if (petEntity == null) return;

        Pet pet = petEntity. getPet();

        // 주인인지 확인
        if (! petEntity.getOwnerPlayerId().equals(player.getUniqueId())) {
            // 다른 사람의 펫 정보 보기
            showOtherPetInfo(player, pet, petEntity);
            event.setCancelled(true);
            return;
        }

        // 손에 든 아이템 확인
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (handItem != null && handItem. getType() != Material.AIR) {
            // 먹이 주기 시도
            if (tryFeedPet(player, pet, handItem)) {
                event.setCancelled(true);
                return;
            }

            // 장난감 사용 시도
            if (tryPlayWithPet(player, pet, handItem)) {
                event. setCancelled(true);
                return;
            }
        }

        // 웅크리기 + 우클릭 = 앉기 토글
        if (player.isSneaking()) {
            petEntity.toggleSit();
            event.setCancelled(true);
            return;
        }

        // 일반 우클릭 = 펫 메뉴 열기
        plugin.getGUIManager().openPetInfoMenu(player, pet);
        event.setCancelled(true);
    }

    /**
     * 펫 우클릭 상호작용 (ArmorStand 등)
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPetInteractAt(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Entity entity = event.getRightClicked();

        if (!plugin. getPetEntityManager().isPetEntity(entity)) {
            return;
        }

        // PlayerInteractEntityEvent에서 처리하므로 취소
        event. setCancelled(true);
    }

    // ===== 펫 좌클릭 (때리기) =====

    /**
     * 펫 좌클릭 처리
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPetHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Entity entity = event.getEntity();
        Player player = (Player) event.getDamager();

        if (!plugin.getPetEntityManager().isPetEntity(entity)) {
            return;
        }

        PetEntity petEntity = plugin.getPetEntityManager().getPetEntityByEntity(entity);
        if (petEntity == null) return;

        // 주인이 자신의 펫을 때리면
        if (petEntity.getOwnerPlayerId().equals(player.getUniqueId())) {
            event.setCancelled(true);

            // 더블클릭 감지
            UUID playerId = player.getUniqueId();
            UUID petId = petEntity.getPet().getPetId();
            long currentTime = System.currentTimeMillis();

            if (lastClickedPet. containsKey(playerId) && 
                lastClickedPet.get(playerId).equals(petId) &&
                currentTime - lastClickTime.getOrDefault(playerId, 0L) < DOUBLE_CLICK_THRESHOLD) {

                // 더블클릭 = 따라오기 토글
                petEntity.toggleFollow();
                lastClickTime.remove(playerId);
                lastClickedPet.remove(playerId);
            } else {
                // 싱글클릭 기록
                lastClickTime. put(playerId, currentTime);
                lastClickedPet.put(playerId, petId);

                // 간단한 정보 표시
                showQuickInfo(player, petEntity. getPet());
            }
        }
    }

    // ===== 먹이주기 =====

    /**
     * 펫에게 먹이 주기 시도
     */
    private boolean tryFeedPet(Player player, Pet pet, ItemStack item) {
        String itemId = item.getType().name();
        PetCareManager. FoodEffect effect = plugin.getPetCareManager().getFoodEffect(itemId);

        if (effect == null) {
            return false;
        }

        // 이미 배부른지 확인
        if (pet.getHunger() >= 100) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.already-full")
                    .replace("{name}", pet. getPetName()));
            return true;
        }

        // 먹이 주기
        return plugin.getPetCareManager().feedPet(player, pet, itemId);
    }

    // ===== 장난감 =====

    /**
     * 펫과 놀아주기 시도
     */
    private boolean tryPlayWithPet(Player player, Pet pet, ItemStack item) {
        String itemId = item.getType().name();
        PetCareManager.ToyEffect effect = plugin. getPetCareManager().getToyEffect(itemId);

        if (effect == null) {
            return false;
        }

        // 놀아주기
        return plugin.getPetCareManager().playWithPet(player, pet, itemId);
    }

    // ===== 정보 표시 =====

    /**
     * 간단한 정보 표시 (싱글클릭)
     */
    private void showQuickInfo(Player player, Pet pet) {
        StringBuilder sb = new StringBuilder();

        sb.append(pet.getRarity().getColorCode().replace("&", "§"));
        sb.append(pet.getPetName());
        sb.append(" §7Lv. ").append(pet.getLevel());
        sb.append(" §c❤").append(String.format("%. 0f", pet.getHealth()));
        sb.append("/").append(String.format("%.0f", pet.getMaxHealth()));

        if (pet.getHunger() < 30) {
            sb.append(" §e🍖").append(String.format("%.0f", pet.getHunger())).append("%");
        }

        if (pet.getHappiness() < 30) {
            sb.append(" §d😢").append(String.format("%.0f", pet.getHappiness())).append("%");
        }

        player.sendActionBar(sb.toString());
    }

    /**
     * 다른 사람의 펫 정보 표시
     */
    private void showOtherPetInfo(Player player, Pet pet, PetEntity petEntity) {
        Player owner = petEntity.getOwner();
        String ownerName = owner != null ? owner.getName() : "알 수 없음";

        StringBuilder sb = new StringBuilder();
        sb.append("\n§6§l===== ").append(ownerName).append("의 펫 =====\n\n");

        sb.append(pet.getRarity().getColorCode().replace("&", "§"));
        sb.append("§l").append(pet.getPetName()).append("\n");
        sb.append("§7종족: §f").append(pet.getSpeciesId()).append("\n");
        sb.append("§7레벨: §f").append(pet.getLevel()).append("\n");
        sb.append("§7희귀도: ").append(pet.getRarity().getColoredName()).append("\n");

        if (pet. getType() != null) {
            sb.append("§7타입: §f").append(pet.getType().getDisplayName()).append("\n");
        }

        MessageUtil.sendMessage(player, sb.toString());
    }

    // ===== 클릭 데이터 정리 =====

    /**
     * 오래된 클릭 데이터 정리
     */
    public void cleanupClickData() {
        long currentTime = System. currentTimeMillis();
        lastClickTime.entrySet().removeIf(entry -> 
                currentTime - entry. getValue() > DOUBLE_CLICK_THRESHOLD * 10);
        
        lastClickedPet.keySet().retainAll(lastClickTime.keySet());
    }
}