package com.multiverse. pet.listener;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.model.Pet;
import com. multiverse.pet. model.acquisition.PetEgg;
import com. multiverse.pet. util.MessageUtil;
import org.bukkit.Location;
import org. bukkit.Material;
import org. bukkit. Particle;
import org.bukkit.Sound;
import org. bukkit.block.Block;
import org.bukkit. entity.Player;
import org.bukkit.event.EventHandler;
import org. bukkit.event. EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. block.Action;
import org. bukkit.event. player.PlayerInteractEvent;
import org. bukkit.inventory. EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta. ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.List;
import java. util.UUID;

/**
 * 펫 알 관련 리스너
 * 알 사용, 부화 처리
 */
public class PetEggListener implements Listener {

    private final PetCore plugin;

    // 네임스페이스 키
    private final NamespacedKey petEggKey;
    private final NamespacedKey petEggIdKey;
    private final NamespacedKey petEggTypeKey;

    public PetEggListener(PetCore plugin) {
        this.plugin = plugin;
        this.petEggKey = new NamespacedKey(plugin, "pet_egg");
        this.petEggIdKey = new NamespacedKey(plugin, "pet_egg_id");
        this.petEggTypeKey = new NamespacedKey(plugin, "pet_egg_type");
    }

    // ===== 알 아이템 사용 =====

    /**
     * 알 아이템 우클릭
     */
    @EventHandler(priority = EventPriority. HIGH)
    public void onEggUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        // 펫 알 아이템인지 확인
        if (!isPetEggItem(item)) {
            return;
        }

        event.setCancelled(true);

        // 알 데이터 가져오기
        PetEgg egg = getEggFromItem(item);
        if (egg == null) {
            // 새 알 생성
            egg = createEggFromItem(item, player. getUniqueId());
        }

        // 웅크리기 + 우클릭 = 알 정보 보기
        if (player.isSneaking()) {
            showEggInfo(player, egg);
            return;
        }

        // 일반 우클릭 = 부화 시작
        handleEggActivation(player, egg, item);
    }

    /**
     * 알 활성화/부화 처리
     */
    private void handleEggActivation(Player player, PetEgg egg, ItemStack item) {
        UUID playerId = player. getUniqueId();

        // 이미 부화 중인지 확인
        if (egg.isHatching()) {
            // 부화 진행 상황 표시
            showHatchingProgress(player, egg);
            return;
        }

        // 부화 준비 완료인지 확인
        if (egg. isReadyToHatch()) {
            // 즉시 부화
            hatchEgg(player, egg, item);
            return;
        }

        // 부화 조건 확인
        String biome = player.getLocation().getBlock().getBiome().name();
        boolean isDay = player.getWorld().getTime() < 13000;

        if (! egg.canHatch(player.getLevel(), biome, isDay)) {
            String reason = egg.getCannotHatchReason(player.getLevel(), biome, isDay);
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.cannot-hatch")
                    .replace("{reason}", reason));
            return;
        }

        // 인벤토리에서 알 목록에 추가
        if (plugin.getPetAcquisitionManager().addEgg(player, egg)) {
            // 아이템 소비
            if (item.getAmount() > 1) {
                item. setAmount(item. getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }

            // 부화 시작
            plugin.getPetAcquisitionManager().startHatching(player, egg. getEggId());

            // 이펙트
            player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, 
                    player. getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        }
    }

    /**
     * 알 즉시 부화
     */
    private void hatchEgg(Player player, PetEgg egg, ItemStack item) {
        // 보관함 여유 확인
        if (plugin.getPetStorageManager().isStorageFull(player. getUniqueId())) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.storage-full"));
            return;
        }

        // 종족 결정
        String speciesId = egg. determineSpecies();
        if (speciesId == null) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("egg.hatch-failed"));
            return;
        }

        // 펫 생성
        Pet pet = createPetFromEgg(egg, speciesId, player.getUniqueId());

        // 보관함에 추가
        if (plugin.getPetManager().addNewPet(player. getUniqueId(), pet)) {
            // 아이템 소비
            if (item. getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }

            // 이펙트
            playHatchEffect(player. getLocation());

            // 알림
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.hatched")
                    .replace("{egg}", egg.getName())
                    . replace("{pet}", pet.getPetName())
                    .replace("{rarity}", pet.getRarity().getDisplayName()));

            // 변이 알림
            if (pet.isMutation()) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("egg.mutation"));
            }
        }
    }

    /**
     * 알에서 펫 생성
     */
    private Pet createPetFromEgg(PetEgg egg, String speciesId, UUID ownerId) {
        var species = plugin.getSpeciesCache().getSpecies(speciesId);

        Pet pet = new Pet();
        pet.setOwnerId(ownerId);
        pet.setSpeciesId(speciesId);
        pet.setPetName(species != null ? species.getName() : speciesId);
        pet.setType(species != null ?  species.getType() : null);
        pet.setRarity(egg.determineRarity());
        pet.setLevel(1);

        if (species != null) {
            pet. setEntityType(species.getEntityType());
            pet.setBaseStats(species.getAllStatsAtLevel(1));
        }

        pet.setMaxHealth(pet.getTotalStat("health"));
        pet.setHealth(pet.getMaxHealth());
        pet.setHunger(100);
        pet.setHappiness(100);

        // 알 보너스 스탯
        for (var entry : egg.getStatBonuses().entrySet()) {
            double current = pet.getBaseStats().getOrDefault(entry.getKey(), 0.0);
            pet.setBaseStat(entry. getKey(), current + entry.getValue());
        }

        // 변이 확인
        if (egg.rollMutation(5. 0)) {
            pet.setMutation(true);
            for (String stat : pet.getBaseStats().keySet()) {
                double current = pet.getBaseStats().get(stat);
                pet. setBaseStat(stat, current * 1.1);
            }
        }

        return pet;
    }

    // ===== 정보 표시 =====

    /**
     * 알 정보 표시
     */
    private void showEggInfo(Player player, PetEgg egg) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n§6§l===== ").append(egg.getName()).append(" =====\n\n");

        sb.append("§e타입: §f").append(egg.getEggType()).append("\n");
        sb.append("§e부화 시간: §f").append(egg.getHatchTimeFormatted()).append("\n");

        // 가능한 종족
        List<String> possibleSpecies = egg.getPossibleSpecies();
        if (!possibleSpecies.isEmpty()) {
            sb.append("\n§e가능한 종족:\n");
            for (String speciesId : possibleSpecies) {
                var species = plugin.getSpeciesCache().getSpecies(speciesId);
                String name = species != null ?  species.getName() : speciesId;
                sb.append("§7- §f").append(name).append("\n");
            }
        }

        // 희귀도 확률
        sb.append("\n§e희귀도 확률:\n");
        var rarityChances = egg.getRarityChances();
        for (var entry : rarityChances.entrySet()) {
            if (entry.getValue() > 0) {
                sb.append("§7- ").append(entry.getKey().getColoredName());
                sb.append("§7: §f").append(String.format("%.1f", entry.getValue())).append("%\n");
            }
        }

        // 부화 조건
        sb.append("\n§e부화 조건:\n");
        if (egg.getRequiredBiome() != null) {
            sb.append("§7- 바이옴: §f").append(egg.getRequiredBiome()).append("\n");
        }
        if (egg. isRequiresDaytime()) {
            sb.append("§7- 낮에만 부화\n");
        }
        if (egg. isRequiresNighttime()) {
            sb.append("§7- 밤에만 부화\n");
        }
        if (egg. getRequiredPlayerLevel() > 0) {
            sb.append("§7- 플레이어 레벨: §f").append(egg.getRequiredPlayerLevel()).append(" 이상\n");
        }

        // 스탯 보너스
        if (! egg.getStatBonuses().isEmpty()) {
            sb. append("\n§e스탯 보너스:\n");
            for (var entry : egg.getStatBonuses().entrySet()) {
                sb.append("§7- ").append(entry.getKey()).append(": §a+");
                sb.append(String.format("%.1f", entry.getValue())).append("\n");
            }
        }

        MessageUtil.sendMessage(player, sb.toString());
    }

    /**
     * 부화 진행 상황 표시
     */
    private void showHatchingProgress(Player player, PetEgg egg) {
        double progress = egg.getHatchProgress();
        String remaining = egg.getRemainingHatchTimeFormatted();

        StringBuilder sb = new StringBuilder();
        sb.append("\n§6§l===== 부화 진행 중 =====\n\n");
        sb.append("§e").append(egg.getName()).append("\n");
        sb.append("§7진행률: ").append(getProgressBar(progress)).append(" §f");
        sb.append(String.format("%. 1f", progress)).append("%\n");
        sb.append("§7남은 시간: §f").append(remaining).append("\n");

        MessageUtil.sendMessage(player, sb.toString());
    }

    /**
     * 진행률 바 생성
     */
    private String getProgressBar(double percent) {
        int filled = (int) (percent / 10);
        StringBuilder bar = new StringBuilder("§8[");

        for (int i = 0; i < 10; i++) {
            if (i < filled) {
                bar.append("§a█");
            } else {
                bar.append("§7░");
            }
        }

        bar.append("§8]");
        return bar.toString();
    }

    // ===== 이펙트 =====

    /**
     * 부화 이펙트 재생
     */
    private void playHatchEffect(Location location) {
        location.getWorld().spawnParticle(Particle. TOTEM, location.add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.2);
        location.getWorld().spawnParticle(Particle. FIREWORKS_SPARK, location, 30, 0.5, 0.5, 0.5, 0.1);
        location.getWorld().playSound(location, Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }

    // ===== 유틸리티 =====

    /**
     * 펫 알 아이템인지 확인
     */
    private boolean isPetEggItem(ItemStack item) {
        if (! item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.has(petEggKey, PersistentDataType.BYTE);
    }

    /**
     * 아이템에서 알 데이터 가져오기
     */
    private PetEgg getEggFromItem(ItemStack item) {
        if (!item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        String eggIdStr = container.get(petEggIdKey, PersistentDataType.STRING);
        if (eggIdStr == null) {
            return null;
        }

        try {
            UUID eggId = UUID. fromString(eggIdStr);
            // 플레이어 알 목록에서 찾기 (구현 필요)
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 아이템에서 새 알 생성
     */
    private PetEgg createEggFromItem(ItemStack item, UUID ownerId) {
        if (!item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        String eggType = container.getOrDefault(petEggTypeKey, PersistentDataType.STRING, "normal");

        PetEgg egg = new PetEgg();
        egg.setOwnerId(ownerId);
        egg.setEggType(eggType);
        egg.setName(meta.hasDisplayName() ? meta.getDisplayName() : eggType + " 알");
        egg.setHatchTime(plugin.getPetAcquisitionManager().getBaseHatchTime());

        return egg;
    }

    /**
     * 펫 알 아이템 생성
     */
    public ItemStack createEggItem(PetEgg egg) {
        ItemStack item = new ItemStack(Material.DRAGON_EGG);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6" + egg.getName());

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(petEggKey, PersistentDataType.BYTE, (byte) 1);
            container.set(petEggIdKey, PersistentDataType.STRING, egg.getEggId().toString());
            container.set(petEggTypeKey, PersistentDataType.STRING, egg.getEggType());

            item.setItemMeta(meta);
        }

        return item;
    }
}