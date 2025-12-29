package com.multiverse.pet.manager;

import com. multiverse.pet. PetCore;
import com. multiverse.pet. model.Pet;
import com.multiverse. pet.model.PetStatus;
import com.multiverse. pet.util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity.Player;
import org.bukkit. inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java. util.*;

/**
 * 펫 케어 매니저 클래스
 * 펫 배고픔, 행복도, 체력 관리
 */
public class PetCareManager {

    private final PetCore plugin;

    // 케어 태스크
    private BukkitTask careTask;

    // 설정 값
    private double hungerDecreaseRate;
    private double happinessDecreaseRate;
    private double healthRegenRate;
    private double lowHungerThreshold;
    private double lowHappinessThreshold;
    private double criticalHungerThreshold;
    private double criticalHappinessThreshold;
    private boolean enableHungerSystem;
    private boolean enableHappinessSystem;
    private boolean enableHealthRegen;
    private int careTickInterval;

    // 음식 효과
    private final Map<String, FoodEffect> foodEffects;

    // 장난감 효과
    private final Map<String, ToyEffect> toyEffects;

    /**
     * 생성자
     */
    public PetCareManager(PetCore plugin) {
        this.plugin = plugin;
        this.foodEffects = new HashMap<>();
        this.toyEffects = new HashMap<>();
        loadSettings();
        loadFoodEffects();
        loadToyEffects();
        startCareTask();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.hungerDecreaseRate = plugin. getConfigManager().getCareSettings().getHungerDecreaseRate();
        this.happinessDecreaseRate = plugin.getConfigManager().getCareSettings().getHappinessDecreaseRate();
        this.healthRegenRate = plugin. getConfigManager().getCareSettings().getHealthRegenRate();
        this.lowHungerThreshold = plugin.getConfigManager().getCareSettings().getLowHungerThreshold();
        this.lowHappinessThreshold = plugin.getConfigManager().getCareSettings().getLowHappinessThreshold();
        this.criticalHungerThreshold = plugin.getConfigManager().getCareSettings().getCriticalHungerThreshold();
        this.criticalHappinessThreshold = plugin.getConfigManager().getCareSettings().getCriticalHappinessThreshold();
        this.enableHungerSystem = plugin.getConfigManager().getCareSettings().isEnableHungerSystem();
        this.enableHappinessSystem = plugin.getConfigManager().getCareSettings().isEnableHappinessSystem();
        this.enableHealthRegen = plugin.getConfigManager().getCareSettings().isEnableHealthRegen();
        this.careTickInterval = plugin.getConfigManager().getCareSettings().getCareTickInterval();
    }

    /**
     * 음식 효과 로드
     */
    private void loadFoodEffects() {
        registerFood("BREAD", 10, 5, 0);
        registerFood("COOKED_BEEF", 25, 10, 0);
        registerFood("COOKED_PORKCHOP", 25, 10, 0);
        registerFood("COOKED_CHICKEN", 20, 8, 0);
        registerFood("COOKED_MUTTON", 20, 8, 0);
        registerFood("COOKED_RABBIT", 20, 8, 0);
        registerFood("COOKED_SALMON", 20, 10, 0);
        registerFood("COOKED_COD", 15, 8, 0);
        registerFood("GOLDEN_APPLE", 50, 30, 20);
        registerFood("ENCHANTED_GOLDEN_APPLE", 100, 50, 50);
        registerFood("GOLDEN_CARROT", 40, 25, 10);
        registerFood("CAKE", 35, 20, 0);
        registerFood("COOKIE", 5, 10, 0);
        registerFood("PUMPKIN_PIE", 30, 15, 0);
        registerFood("APPLE", 10, 5, 0);
        registerFood("MELON_SLICE", 8, 5, 0);
        registerFood("SWEET_BERRIES", 5, 8, 0);
    }

    /**
     * 장난감 효과 로드
     */
    private void loadToyEffects() {
        registerToy("STICK", 10, 60);
        registerToy("BONE", 20, 120);
        registerToy("FEATHER", 15, 90);
        registerToy("STRING", 12, 60);
        registerToy("SLIME_BALL", 25, 120);
        registerToy("SNOWBALL", 15, 60);
    }

    /**
     * 케어 태스크 시작
     */
    private void startCareTask() {
        careTask = Bukkit.getScheduler().runTaskTimer(plugin, this::processCare,
                careTickInterval * 20L, careTickInterval * 20L);
    }

    /**
     * 케어 태스크 중지
     */
    public void stopCareTask() {
        if (careTask != null && !careTask.isCancelled()) {
            careTask. cancel();
        }
    }

    // ===== 케어 처리 =====

    /**
     * 케어 처리 (주기적 호출)
     */
    private void processCare() {
        for (Player player :  Bukkit.getOnlinePlayers()) {
            List<Pet> pets = plugin.getPetManager().getAllPets(player. getUniqueId());

            for (Pet pet : pets) {
                processPetCare(player, pet);
            }
        }
    }

    /**
     * 개별 펫 케어 처리
     */
    private void processPetCare(Player owner, Pet pet) {
        boolean needsSave = false;

        // 활성 펫만 배고픔/행복도 감소
        if (pet.isActive()) {
            // 배고픔 감소
            if (enableHungerSystem) {
                double hungerDecrease = hungerDecreaseRate;

                if (pet. getBehavior().isActive()) {
                    hungerDecrease *= 1.5;
                }

                pet.decreaseHunger(hungerDecrease);
                needsSave = true;

                checkHungerWarning(owner, pet);
            }

            // 행복도 감소
            if (enableHappinessSystem) {
                double happinessDecrease = happinessDecreaseRate;

                if (pet. getHunger() < lowHungerThreshold) {
                    happinessDecrease *= 2. 0;
                }

                pet.decreaseHappiness(happinessDecrease);
                needsSave = true;

                checkHappinessWarning(owner, pet);
            }

            // 체력 재생
            if (enableHealthRegen && pet.getHealth() < pet.getMaxHealth()) {
                if (pet.getHunger() >= lowHungerThreshold && pet.getHappiness() >= lowHappinessThreshold) {
                    double regen = healthRegenRate;

                    if (pet.getHappiness() >= 80) {
                        regen *= 1.5;
                    }

                    pet. heal(regen);
                    needsSave = true;
                }
            }

            applyStatusEffects(pet);
        }

        if (needsSave) {
            plugin. getPetManager().savePetData(owner.getUniqueId(), pet);
        }
    }

    /**
     * 배고픔 경고 체크
     */
    private void checkHungerWarning(Player owner, Pet pet) {
        if (pet.getHunger() <= criticalHungerThreshold && pet.getHunger() > 0) {
            if (pet.getHunger() <= criticalHungerThreshold &&
                    pet.getHunger() + hungerDecreaseRate > criticalHungerThreshold) {
                MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("care.critical-hunger")
                        .replace("{name}", pet.getPetName()));
            }
        } else if (pet. getHunger() <= lowHungerThreshold) {
            if (pet.getHunger() <= lowHungerThreshold &&
                    pet.getHunger() + hungerDecreaseRate > lowHungerThreshold) {
                MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("care.low-hunger")
                        . replace("{name}", pet.getPetName()));
            }
        }

        if (pet. getHunger() <= 0 && pet.isActive()) {
            MessageUtil.sendMessage(owner, plugin. getConfigManager().getMessage("care.starving")
                    . replace("{name}", pet.getPetName()));
            plugin.getPetManager().unsummonPet(owner, pet. getPetId());
        }
    }

    /**
     * 행복도 경고 체크
     */
    private void checkHappinessWarning(Player owner, Pet pet) {
        if (pet.getHappiness() <= criticalHappinessThreshold && pet. getHappiness() > 0) {
            if (pet.getHappiness() <= criticalHappinessThreshold &&
                    pet.getHappiness() + happinessDecreaseRate > criticalHappinessThreshold) {
                MessageUtil. sendMessage(owner, plugin.getConfigManager().getMessage("care.critical-happiness")
                        .replace("{name}", pet.getPetName()));
            }
        } else if (pet.getHappiness() <= lowHappinessThreshold) {
            if (pet. getHappiness() <= lowHappinessThreshold &&
                    pet.getHappiness() + happinessDecreaseRate > lowHappinessThreshold) {
                MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("care.low-happiness")
                        . replace("{name}", pet.getPetName()));
            }
        }
    }

    /**
     * 상태 효과 적용
     */
    private void applyStatusEffects(Pet pet) {
        if (pet.getHunger() < criticalHungerThreshold) {
            pet.addBonusStat("attack", -pet.getBaseStats().getOrDefault("attack", 0.0) * 0.3);
            pet.addBonusStat("speed", -pet.getBaseStats().getOrDefault("speed", 0.0) * 0.3);
        }

        if (pet. getHappiness() >= 80) {
            pet.addBonusStat("exp_bonus", 10. 0);
        } else if (pet. getHappiness() < criticalHappinessThreshold) {
            pet.addBonusStat("exp_bonus", -20.0);
        }
    }

    // ===== 먹이 주기 =====

    /**
     * 펫에게 먹이 주기
     */
    public boolean feedPet(Player player, Pet pet, String foodId) {
        FoodEffect effect = foodEffects. get(foodId. toUpperCase());
        if (effect == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.invalid-food"));
            return false;
        }

        if (pet.getHunger() >= 100) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.already-full")
                    .replace("{name}", pet. getPetName()));
            return false;
        }

        if (! consumeItem(player, foodId)) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("care.no-food"));
            return false;
        }

        pet.increaseHunger(effect.getHungerRestore());
        pet.increaseHappiness(effect.getHappinessBonus());

        if (effect.getHealthRestore() > 0) {
            pet. heal(effect.getHealthRestore());
        }

        plugin.getPetManager().savePetData(player.getUniqueId(), pet);

        MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("care.fed")
                .replace("{name}", pet.getPetName())
                .replace("{food}", foodId)
                .replace("{hunger}", String.format("%.1f", effect.getHungerRestore())));

        return true;
    }

    /**
     * 최적의 음식으로 먹이 주기
     */
    public boolean feedPetAuto(Player player, Pet pet) {
        String bestFood = null;
        double bestValue = 0;

        for (Map.Entry<String, FoodEffect> entry : foodEffects. entrySet()) {
            if (hasItem(player, entry. getKey())) {
                double value = entry.getValue().getHungerRestore() + entry.getValue().getHappinessBonus();
                if (value > bestValue) {
                    bestValue = value;
                    bestFood = entry.getKey();
                }
            }
        }

        if (bestFood == null) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("care.no-food"));
            return false;
        }

        return feedPet(player, pet, bestFood);
    }

    // ===== 놀아주기 =====

    /**
     * 펫과 놀아주기
     */
    public boolean playWithPet(Player player, Pet pet, String toyId) {
        if (! pet.isActive()) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("care.not-active")
                    .replace("{name}", pet. getPetName()));
            return false;
        }

        if (isOnPlayCooldown(pet)) {
            long remaining = getPlayCooldownRemaining(pet);
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.play-cooldown")
                    . replace("{time}", String.valueOf(remaining / 1000)));
            return false;
        }

        double happinessGain;
        int cooldown;

        if (toyId != null) {
            ToyEffect effect = toyEffects. get(toyId. toUpperCase());
            if (effect == null) {
                MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("care.invalid-toy"));
                return false;
            }

            if (! consumeItem(player, toyId)) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.no-toy"));
                return false;
            }

            happinessGain = effect.getHappinessBonus();
            cooldown = effect.getCooldown();
        } else {
            happinessGain = 5;
            cooldown = 60;
        }

        pet.increaseHappiness(happinessGain);
        pet.decreaseHunger(2);
        pet.setPlayCooldownEnd(System.currentTimeMillis() + (cooldown * 1000L));

        plugin.getPetManager().savePetData(player.getUniqueId(), pet);

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.played")
                .replace("{name}", pet. getPetName())
                .replace("{happiness}", String.format("%.1f", happinessGain)));

        plugin.getPetEntityManager().playHappyEffect(pet. getPetId());

        return true;
    }

    // ===== 치료 =====

    /**
     * 펫 치료
     */
    public boolean healPet(Player player, Pet pet, double healAmount) {
        if (pet.getHealth() >= pet.getMaxHealth()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.already-healthy")
                    . replace("{name}", pet.getPetName()));
            return false;
        }

        double actualHeal;
        if (healAmount <= 0) {
            actualHeal = pet. getMaxHealth() - pet.getHealth();
        } else {
            actualHeal = Math.min(healAmount, pet.getMaxHealth() - pet.getHealth());
        }

        pet. heal(actualHeal);
        plugin.getPetManager().savePetData(player.getUniqueId(), pet);

        MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("care.healed")
                .replace("{name}", pet.getPetName())
                .replace("{amount}", String. format("%.1f", actualHeal)));

        return true;
    }

    /**
     * 펫 부활
     */
    public boolean revivePet(Player player, Pet pet, double cost) {
        if (pet.getHealth() > 0) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.not-fainted"));
            return false;
        }

        if (cost > 0 && !plugin.getPlayerDataCoreHook().hasGold(player.getUniqueId(), cost)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.not-enough-gold")
                    .replace("{cost}", String.format("%.0f", cost)));
            return false;
        }

        if (cost > 0) {
            plugin.getPlayerDataCoreHook().withdrawGold(player.getUniqueId(), cost);
        }

        pet.setHealth(pet.getMaxHealth() * 0.5);
        pet.setStatus(PetStatus. STORED);

        plugin.getPetManager().savePetData(player.getUniqueId(), pet);

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.revived")
                .replace("{name}", pet.getPetName()));

        return true;
    }

    // ===== 쿨다운 =====

    /**
     * 놀이 쿨다운 확인
     */
    public boolean isOnPlayCooldown(Pet pet) {
        return System. currentTimeMillis() < pet.getPlayCooldownEnd();
    }

    /**
     * 놀이 쿨다운 남은 시간
     */
    public long getPlayCooldownRemaining(Pet pet) {
        return Math.max(0, pet. getPlayCooldownEnd() - System.currentTimeMillis());
    }

    // ===== 아이템 관련 =====

    /**
     * 아이템 보유 확인
     */
    private boolean hasItem(Player player, String itemId) {
        try {
            Material material = Material.valueOf(itemId.toUpperCase());
            return player.getInventory().contains(material);
        } catch (IllegalArgumentException e) {
            // ItemCore 커스텀 아이템
            if (plugin.hasItemCore()) {
                return plugin.getItemCoreHook().hasItem(player, itemId, 1);
            }
            return false;
        }
    }

    /**
     * 아이템 소비
     */
    private boolean consumeItem(Player player, String itemId) {
        try {
            Material material = Material.valueOf(itemId.toUpperCase());
            ItemStack[] contents = player.getInventory().getContents();

            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item != null && item.getType() == material) {
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().setItem(i, null);
                    }
                    return true;
                }
            }
            return false;
        } catch (IllegalArgumentException e) {
            // ItemCore 커스텀 아이템
            if (plugin. hasItemCore()) {
                return plugin. getItemCoreHook().removeItem(player, itemId, 1);
            }
            return false;
        }
    }

    // ===== 음식/장난감 등록 =====

    /**
     * 음식 효과 등록
     */
    public void registerFood(String itemId, double hungerRestore, double happinessBonus, double healthRestore) {
        foodEffects.put(itemId.toUpperCase(), new FoodEffect(hungerRestore, happinessBonus, healthRestore));
    }

    /**
     * 장난감 효과 등록
     */
    public void registerToy(String itemId, double happinessBonus, int cooldown) {
        toyEffects. put(itemId. toUpperCase(), new ToyEffect(happinessBonus, cooldown));
    }

    /**
     * 음식 효과 가져오기
     */
    public FoodEffect getFoodEffect(String itemId) {
        return foodEffects. get(itemId. toUpperCase());
    }

    /**
     * 장난감 효과 가져오기
     */
    public ToyEffect getToyEffect(String itemId) {
        return toyEffects. get(itemId. toUpperCase());
    }

    /**
     * 모든 음식 목록
     */
    public Set<String> getAllFoods() {
        return Collections.unmodifiableSet(foodEffects.keySet());
    }

    /**
     * 모든 장난감 목록
     */
    public Set<String> getAllToys() {
        return Collections.unmodifiableSet(toyEffects.keySet());
    }

    /**
     * 케어가 필요한 펫 목록
     */
    public List<Pet> getPetsNeedingCare(UUID playerId) {
        List<Pet> needsCare = new ArrayList<>();
        List<Pet> allPets = plugin. getPetManager().getAllPets(playerId);

        for (Pet pet : allPets) {
            if (pet.getHunger() < lowHungerThreshold ||
                pet.getHappiness() < lowHappinessThreshold ||
                pet.getHealth() < pet.getMaxHealth() * 0.5) {
                needsCare.add(pet);
            }
        }

        return needsCare;
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadSettings();
        foodEffects.clear();
        toyEffects.clear();
        loadFoodEffects();
        loadToyEffects();
    }

    /**
     * 종료 처리
     */
    public void shutdown() {
        stopCareTask();
    }

    // ===== 내부 클래스 =====

    /**
     * 음식 효과
     */
    public static class FoodEffect {
        private final double hungerRestore;
        private final double happinessBonus;
        private final double healthRestore;

        public FoodEffect(double hungerRestore, double happinessBonus, double healthRestore) {
            this.hungerRestore = hungerRestore;
            this. happinessBonus = happinessBonus;
            this.healthRestore = healthRestore;
        }

        public double getHungerRestore() {
            return hungerRestore;
        }

        public double getHappinessBonus() {
            return happinessBonus;
        }

        public double getHealthRestore() {
            return healthRestore;
        }
    }

    /**
     * 장난감 효과
     */
    public static class ToyEffect {
        private final double happinessBonus;
        private final int cooldown;

        public ToyEffect(double happinessBonus, int cooldown) {
            this. happinessBonus = happinessBonus;
            this.cooldown = cooldown;
        }

        public double getHappinessBonus() {
            return happinessBonus;
        }

        public int getCooldown() {
            return cooldown;
        }
    }
}