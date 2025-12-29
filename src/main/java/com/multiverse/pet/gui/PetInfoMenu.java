package com.multiverse.pet.gui;

import com.multiverse. pet.PetCore;
import com. multiverse.pet. gui.holder.PetMenuHolder;
import com. multiverse.pet. model.Pet;
import com.multiverse.pet.model. skill.PetSkill;
import com.multiverse.pet.util. ItemBuilder;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity. Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java. util.List;
import java.util. Map;
import java.util. UUID;

/**
 * 펫 상세 정보 메뉴 GUI
 * 개별 펫의 상세 정보 및 관리
 */
public class PetInfoMenu {

    private final PetCore plugin;
    private static final int MENU_SIZE = 54;

    // 현재 보고 있는 펫 저장
    private final Map<UUID, UUID> viewingPet = new java.util.HashMap<>();

    public PetInfoMenu(PetCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 펫 정보 메뉴 열기
     */
    public void open(Player player, Pet pet) {
        viewingPet.put(player.getUniqueId(), pet.getPetId());

        String title = pet.getRarity().getColorCode() + "§l" + pet.getPetName() + " §7정보";

        PetMenuHolder holder = new PetMenuHolder(plugin, PetMenuHolder.MenuType. PET_INFO);
        Inventory inventory = Bukkit.createInventory(holder, MENU_SIZE, title);

        setupMenuItems(inventory, player, pet);

        player.openInventory(inventory);
    }

    /**
     * 메뉴 아이템 설정
     */
    private void setupMenuItems(Inventory inventory, Player player, Pet pet) {
        // 배경
        ItemStack background = new ItemBuilder(Material. BLACK_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 0; i < MENU_SIZE; i++) {
            inventory.setItem(i, background);
        }

        // === 펫 기본 정보 (중앙 상단) ===
        inventory.setItem(4, createMainInfoItem(pet));

        // === 스탯 영역 (왼쪽) ===
        inventory.setItem(19, createStatItem(pet, "health", "§c❤ 체력", Material.RED_DYE));
        inventory.setItem(20, createStatItem(pet, "attack", "§6⚔ 공격력", Material.IRON_SWORD));
        inventory. setItem(28, createStatItem(pet, "defense", "§9🛡 방어력", Material.IRON_CHESTPLATE));
        inventory.setItem(29, createStatItem(pet, "speed", "§a💨 속도", Material.FEATHER));

        // === 상태 영역 (중앙) ===
        inventory.setItem(22, createStatusItem(pet, "hunger", "§6🍖 배고픔", pet.getHunger()));
        inventory.setItem(31, createStatusItem(pet, "happiness", "§d😊 행복도", pet.getHappiness()));

        // === 스킬 영역 (오른쪽) ===
        List<PetSkill> skills = pet. getSkills();
        int[] skillSlots = {24, 25, 33, 34};
        for (int i = 0; i < skillSlots.length; i++) {
            if (i < skills.size()) {
                inventory. setItem(skillSlots[i], createSkillItem(skills.get(i)));
            } else {
                inventory.setItem(skillSlots[i], new ItemBuilder(Material. GRAY_DYE)
                        .name("§7빈 스킬 슬롯")
                        .lore("§7스킬을 배우면 여기에 표시됩니다.")
                        . build());
            }
        }

        // === 하단 액션 버튼 ===

        // 소환/해제
        if (pet.isActive()) {
            inventory.setItem(37, new ItemBuilder(Material. ENDER_EYE)
                    .name("§c§l펫 해제")
                    .lore("§7현재 소환된 펫을 해제합니다.")
                    .build());
        } else if (pet.getStatus().canBeSummoned()) {
            inventory.setItem(37, new ItemBuilder(Material. ENDER_PEARL)
                    . name("§a§l펫 소환")
                    .lore("§7이 펫을 소환합니다.")
                    .build());
        } else {
            inventory.setItem(37, new ItemBuilder(Material. BARRIER)
                    . name("§c§l소환 불가")
                    .lore("§7상태:  " + pet.getStatus().getDisplayName())
                    .build());
        }

        // 이름 변경
        inventory. setItem(38, new ItemBuilder(Material. NAME_TAG)
                .name("§e§l이름 변경")
                .lore(
                        "§7펫의 이름을 변경합니다.",
                        "",
                        pet.isNameLocked() ? "§c이름 변경 잠금" : "§e클릭하여 변경"
                )
                .build());

        // 스킬 관리
        inventory.setItem(39, new ItemBuilder(Material.ENCHANTED_BOOK)
                .name("§b§l스킬 관리")
                .lore(
                        "§7스킬을 확인하고 강화합니다.",
                        "",
                        "§7보유 스킬: §f" + skills.size() + "개",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // 진화
        boolean canEvolve = plugin.getEvolutionManager().hasAvailableEvolution(pet);
        inventory.setItem(40, new ItemBuilder(canEvolve ? Material.NETHER_STAR : Material.COAL)
                .name("§d§l진화")
                .lore(
                        canEvolve ? "§a진화 가능!" : "§7진화 조건을 확인하세요.",
                        "",
                        "§7현재 단계: §f" + pet.getEvolutionStage() + "단계",
                        "",
                        "§e클릭하여 진화 정보 보기"
                )
                .glow(canEvolve)
                .build());

        // 장비
        inventory.setItem(41, new ItemBuilder(Material. DIAMOND_CHESTPLATE)
                .name("§9§l장비")
                .lore(
                        "§7펫 장비를 관리합니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // 해방 (삭제)
        inventory.setItem(43, new ItemBuilder(Material.LAVA_BUCKET)
                .name("§c§l펫 해방")
                .lore(
                        "§c이 펫을 영구적으로 해방합니다.",
                        "§c이 작업은 되돌릴 수 없습니다! ",
                        "",
                        "§e§lShift+클릭으로 해방"
                )
                .build());

        // === 추가 정보 (상단) ===

        // 전투 기록
        inventory.setItem(1, new ItemBuilder(Material.IRON_SWORD)
                .name("§c전투 기록")
                .lore(
                        "§a승리:  §f" + pet.getBattleWins(),
                        "§c패배: §f" + pet.getBattleLosses(),
                        "§7처치: §f" + pet.getKillCount(),
                        "",
                        getWinRateString(pet)
                )
                .build());

        // 경험치 정보
        inventory. setItem(7, new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .name("§a경험치")
                .lore(
                        "§7레벨:  §f" + pet.getLevel(),
                        "§7경험치: §f" + pet. getExperience() + "/" + pet.getExpToNext(),
                        "",
                        getExpBar(pet)
                )
                .build());

        // 뒤로가기
        inventory.setItem(45, new ItemBuilder(Material.ARROW)
                .name("§7뒤로가기")
                .lore("§7보관함으로 돌아갑니다.")
                .build());

        // 새로고침
        inventory. setItem(53, new ItemBuilder(Material.SUNFLOWER)
                .name("§e새로고침")
                .lore("§7정보를 새로고침합니다.")
                .build());
    }

    /**
     * 메인 정보 아이템 생성
     */
    private ItemStack createMainInfoItem(Pet pet) {
        List<String> lore = new ArrayList<>();
        lore.add("§7종족:  §f" + pet. getSpeciesId());
        lore. add("§7타입: §f" + (pet.getType() != null ? pet.getType().getDisplayName() : "없음"));
        lore.add("§7희귀도: " + pet.getRarity().getColoredName());
        lore.add("");
        lore.add("§7레벨: §f" + pet. getLevel());
        lore.add("§7진화 단계: §f" + pet.getEvolutionStage() + "단계");
        lore. add("");
        lore. add("§7상태: " + pet.getStatus().getDisplayName());

        if (pet.isFavorite()) {
            lore. add("");
            lore. add("§e★ 즐겨찾기");
        }

        if (pet.isMutation()) {
            lore.add("§d✦ 변이 개체");
        }

        return new ItemBuilder(Material.PLAYER_HEAD)
                .name(pet.getRarity().getColorCode() + "§l" + pet.getPetName())
                .lore(lore)
                .glow(pet.getRarity().ordinal() >= 4) // LEGENDARY 이상
                .build();
    }

    /**
     * 스탯 아이템 생성
     */
    private ItemStack createStatItem(Pet pet, String statKey, String displayName, Material material) {
        double baseStat = pet.getBaseStats().getOrDefault(statKey, 0.0);
        double bonusStat = pet. getBonusStats().getOrDefault(statKey, 0.0);
        double totalStat = pet.getTotalStat(statKey);

        return new ItemBuilder(material)
                .name(displayName)
                .lore(
                        "§7기본:  §f" + String.format("%.1f", baseStat),
                        "§7보너스: §a+" + String.format("%.1f", bonusStat),
                        "",
                        "§7총합: §f" + String.format("%. 1f", totalStat)
                )
                .build();
    }

    /**
     * 상태 아이템 생성
     */
    private ItemStack createStatusItem(Pet pet, String type, String displayName, double value) {
        Material material;
        String colorCode;

        if (value >= 70) {
            material = Material.LIME_DYE;
            colorCode = "§a";
        } else if (value >= 30) {
            material = Material.YELLOW_DYE;
            colorCode = "§e";
        } else {
            material = Material.RED_DYE;
            colorCode = "§c";
        }

        return new ItemBuilder(material)
                .name(displayName)
                .lore(
                        colorCode + String.format("%. 0f", value) + "%",
                        "",
                        getStatusBar(value)
                )
                .build();
    }

    /**
     * 스킬 아이템 생성
     */
    private ItemStack createSkillItem(PetSkill skill) {
        List<String> lore = new ArrayList<>();
        lore.add("§7" + skill.getDescription());
        lore.add("");
        lore. add("§7레벨: §f" + skill.getCurrentLevel() + "/" + skill.getMaxLevel());

        if (skill. isPassive()) {
            lore.add("§b[패시브]");
        } else {
            lore. add("§7쿨타임: §f" + skill. getCooldown() + "초");
            if (skill.isOnCooldown()) {
                lore. add("§c남은 쿨타임: " + skill.getRemainingCooldownSeconds() + "초");
            }
        }

        Material material = skill.isPassive() ? Material.BOOK : Material.ENCHANTED_BOOK;

        return new ItemBuilder(material)
                .name("§b" + skill.getName())
                .lore(lore)
                .glow(! skill.isOnCooldown())
                .build();
    }

    /**
     * 승률 문자열
     */
    private String getWinRateString(Pet pet) {
        int total = pet.getBattleWins() + pet.getBattleLosses();
        if (total == 0) {
            return "§7승률: §f-";
        }
        double rate = (double) pet.getBattleWins() / total * 100;
        return "§7승률:  §f" + String.format("%.1f", rate) + "%";
    }

    /**
     * 경험치 바
     */
    private String getExpBar(Pet pet) {
        double percent = plugin.getPetLevelManager().getExpPercentage(pet);
        int filled = (int) (percent / 10);

        StringBuilder bar = new StringBuilder("§8[");
        for (int i = 0; i < 10; i++) {
            bar.append(i < filled ? "§a█" : "§7░");
        }
        bar.append("§8] §f").append(String.format("%.1f", percent)).append("%");

        return bar.toString();
    }

    /**
     * 상태 바
     */
    private String getStatusBar(double value) {
        int filled = (int) (value / 10);
        String color = value >= 70 ? "§a" : (value >= 30 ? "§e" : "§c");

        StringBuilder bar = new StringBuilder("§8[");
        for (int i = 0; i < 10; i++) {
            bar.append(i < filled ? color + "█" : "§7░");
        }
        bar. append("§8]");

        return bar.toString();
    }

    /**
     * 클릭 이벤트 처리
     */
    public void handleClick(InventoryClickEvent event) {
        event. setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        ClickType clickType = event.getClick();

        UUID petId = viewingPet.get(player.getUniqueId());
        if (petId == null) return;

        Pet pet = plugin.getPetManager().getPetById(player.getUniqueId(), petId);
        if (pet == null) {
            player.closeInventory();
            return;
        }

        switch (slot) {
            case 37: // 소환/해제
                if (pet. isActive()) {
                    plugin.getPetManager().unsummonPet(player, pet.getPetId());
                } else if (pet.getStatus().canBeSummoned()) {
                    plugin.getPetManager().summonPet(player, pet.getPetId());
                }
                open(player, pet);
                break;

            case 38: // 이름 변경
                if (! pet.isNameLocked()) {
                    player.closeInventory();
                    plugin.getMessageUtil().sendMessage(player, plugin.getConfigManager().getMessage("gui.enter-new-name"));
                    // 채팅 입력 대기 로직 필요
                }
                break;

            case 39: // 스킬 관리
                plugin.getGUIManager().openSkillMenu(player, pet);
                break;

            case 40: // 진화
                plugin.getGUIManager().openEvolutionMenu(player, pet);
                break;

            case 41: // 장비
                plugin.getGUIManager().openEquipmentMenu(player, pet);
                break;

            case 43: // 해방
                if (clickType == ClickType. SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                    plugin.getPetStorageManager().releasePet(player, pet.getPetId(), true);
                    plugin.getGUIManager().openStorageMenu(player);
                }
                break;

            case 45: // 뒤로가기
                plugin.getGUIManager().openStorageMenu(player);
                break;

            case 53: // 새로고침
                pet = plugin.getPetManager().getPetById(player. getUniqueId(), petId);
                if (pet != null) {
                    open(player, pet);
                }
                break;
        }
    }

    /**
     * 보고 있는 펫 ID 가져오기
     */
    public UUID getViewingPetId(UUID playerId) {
        return viewingPet.get(playerId);
    }

    /**
     * 정리
     */
    public void cleanup(UUID playerId) {
        viewingPet.remove(playerId);
    }
}