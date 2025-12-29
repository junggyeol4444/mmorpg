package com.multiverse.pet.listener;

import com.multiverse.pet.PetCore;
import com.multiverse. pet.api.event.PetCaptureEvent;
import com.multiverse. pet.model.acquisition. CaptureBall;
import com. multiverse.pet. util.MessageUtil;
import org.bukkit.Material;
import org. bukkit. Particle;
import org. bukkit.Sound;
import org. bukkit.entity. Entity;
import org. bukkit.entity. LivingEntity;
import org.bukkit.entity.Player;
import org. bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory. EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit. persistence.PersistentDataContainer;
import org.bukkit. persistence.PersistentDataType;
import org. bukkit. NamespacedKey;

import java.util. UUID;

/**
 * 펫 포획 관련 리스너
 * 포획구 사용, 포획 시도 처리
 */
public class PetCaptureListener implements Listener {

    private final PetCore plugin;
    
    // 네임스페이스 키
    private final NamespacedKey captureBallKey;
    private final NamespacedKey captureBallTypeKey;

    public PetCaptureListener(PetCore plugin) {
        this.plugin = plugin;
        this. captureBallKey = new NamespacedKey(plugin, "capture_ball");
        this.captureBallTypeKey = new NamespacedKey(plugin, "capture_ball_type");
    }

    // ===== 포획구 투척 =====

    /**
     * 투사체(포획구) 히트 이벤트
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCaptureBallHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) {
            return;
        }

        Snowball snowball = (Snowball) event.getEntity();

        // 포획구인지 확인
        if (!isCaptureBall(snowball)) {
            return;
        }

        // 발사자 확인
        if (!(snowball.getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) snowball.getShooter();
        Entity hitEntity = event.getHitEntity();

        // 엔티티에 맞지 않았으면 무시
        if (hitEntity == null) {
            // 바닥에 맞으면 포획구 아이템 드롭 (선택적)
            if (plugin.getConfigManager().getPetSettings().isDropFailedCaptureBall()) {
                dropCaptureBall(snowball);
            }
            return;
        }

        // LivingEntity만 포획 가능
        if (!(hitEntity instanceof LivingEntity)) {
            return;
        }

        LivingEntity target = (LivingEntity) hitEntity;

        // 포획 시도
        String ballType = getCaptureBallType(snowball);
        CaptureBall captureBall = createCaptureBallFromType(ballType);

        if (captureBall != null) {
            plugin.getPetAcquisitionManager().attemptCapture(player, target, captureBall);
        }
    }

    // ===== 포획구 직접 사용 (우클릭) =====

    /**
     * 엔티티에 포획구 직접 사용
     */
    @EventHandler(priority = EventPriority. HIGH, ignoreCancelled = true)
    public void onCaptureBallUse(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        // 포획구 아이템인지 확인
        CaptureBall captureBall = getCaptureBallFromItem(item);
        if (captureBall == null) {
            return;
        }

        Entity entity = event.getRightClicked();

        // LivingEntity만 포획 가능
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        event.setCancelled(true);

        LivingEntity target = (LivingEntity) entity;

        // 아이템 소비
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        // 포획 시도
        plugin.getPetAcquisitionManager().attemptCapture(player, target, captureBall);
    }

    // ===== 포획 이벤트 =====

    /**
     * 펫 포획 성공 이벤트
     */
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onPetCapture(PetCaptureEvent event) {
        Player player = event.getPlayer();
        
        // 성공 이펙트
        event.getTarget().getWorld().spawnParticle(
                Particle.VILLAGER_HAPPY,
                event.getTarget().getLocation().add(0, 1, 0),
                30, 0.5, 0.5, 0.5, 0.1
        );

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);

        // 알림
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("capture.success")
                .replace("{name}", event.getPet().getPetName())
                .replace("{rarity}", event.getPet().getRarity().getDisplayName()));

        // 디버그 로그
        if (plugin.isDebugMode()) {
            plugin.getLogger().info("[DEBUG] " + player.getName() + "이(가) " + 
                    event. getPet().getSpeciesId() + " 포획 성공");
        }
    }

    // ===== 유틸리티 =====

    /**
     * 투사체가 포획구인지 확인
     */
    private boolean isCaptureBall(Snowball snowball) {
        // 커스텀 데이터 확인
        PersistentDataContainer container = snowball.getPersistentDataContainer();
        return container.has(captureBallKey, PersistentDataType.BYTE);
    }

    /**
     * 포획구 타입 가져오기
     */
    private String getCaptureBallType(Snowball snowball) {
        PersistentDataContainer container = snowball. getPersistentDataContainer();
        return container.getOrDefault(captureBallTypeKey, PersistentDataType.STRING, "normal");
    }

    /**
     * 아이템에서 포획구 가져오기
     */
    private CaptureBall getCaptureBallFromItem(ItemStack item) {
        if (item == null || ! item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item. getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(captureBallKey, PersistentDataType.BYTE)) {
            return null;
        }

        String type = container.getOrDefault(captureBallTypeKey, PersistentDataType.STRING, "normal");
        return createCaptureBallFromType(type);
    }

    /**
     * 타입에서 포획구 생성
     */
    private CaptureBall createCaptureBallFromType(String type) {
        switch (type. toLowerCase()) {
            case "basic":
                return CaptureBall. createBasicBall();
            case "great":
                return CaptureBall. createGreatBall();
            case "ultra":
                return CaptureBall. createUltraBall();
            case "master":
                return CaptureBall. createMasterBall();
            case "normal":
            default:
                return CaptureBall.createBasicBall();
        }
    }

    /**
     * 포획구 아이템 드롭
     */
    private void dropCaptureBall(Snowball snowball) {
        String type = getCaptureBallType(snowball);
        CaptureBall captureBall = createCaptureBallFromType(type);

        if (captureBall != null) {
            ItemStack item = captureBall.toItemStack();
            snowball.getWorld().dropItemNaturally(snowball.getLocation(), item);
        }
    }

    /**
     * 포획구 아이템 생성
     */
    public ItemStack createCaptureBallItem(String type, int amount) {
        CaptureBall captureBall = createCaptureBallFromType(type);
        if (captureBall == null) {
            return null;
        }

        ItemStack item = captureBall.toItemStack();
        item.setAmount(amount);

        // 커스텀 데이터 설정
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container. set(captureBallKey, PersistentDataType. BYTE, (byte) 1);
            container. set(captureBallTypeKey, PersistentDataType.STRING, type);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 투척용 포획구 스노우볼 생성
     */
    public Snowball launchCaptureBall(Player player, String type) {
        Snowball snowball = player.launchProjectile(Snowball.class);

        // 커스텀 데이터 설정
        PersistentDataContainer container = snowball. getPersistentDataContainer();
        container.set(captureBallKey, PersistentDataType.BYTE, (byte) 1);
        container.set(captureBallTypeKey, PersistentDataType. STRING, type);

        // 이펙트
        player.playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1.0f, 0.8f);

        return snowball;
    }
}