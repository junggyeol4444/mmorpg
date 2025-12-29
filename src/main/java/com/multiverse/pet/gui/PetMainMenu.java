package com. multiverse.pet.gui;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. entity.PetEntity;
import com. multiverse.pet. gui.holder.PetMenuHolder;
import com. multiverse.pet. model.Pet;
import com. multiverse.pet. util.ItemBuilder;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity.Player;
import org.bukkit. event.inventory.InventoryClickEvent;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java. util.List;
import java.util. UUID;

/**
 * 펫 메인 메뉴 GUI
 * 펫 시스템의 메인 허브 메뉴
 */
public class PetMainMenu {

    private final PetCore plugin;
    private static final int MENU_SIZE = 54;
    private static final String MENU_TITLE = "§6§l펫 메뉴";

    public PetMainMenu(PetCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 메인 메뉴 열기
     */
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(
                new PetMenuHolder(plugin, PetMenuHolder.MenuType.MAIN_MENU),
                MENU_SIZE,
                MENU_TITLE
        );

        // 메뉴 아이템 배치
        setupMenuItems(inventory, player);

        player.openInventory(inventory);
    }

    /**
     * 메뉴 아이템 설정
     */
    private void setupMenuItems(Inventory inventory, Player player) {
        UUID playerId = player.getUniqueId();

        // 배경 채우기
        ItemStack background = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 0; i < MENU_SIZE; i++) {
            inventory.setItem(i, background);
        }

        // === 상단 정보 영역 ===

        // 활성 펫 정보 (가운데)
        PetEntity activePet = plugin. getPetManager().getActivePet(playerId);
        if (activePet != null) {
            inventory.setItem(4, createActivePetItem(activePet. getPet()));
        } else {
            inventory.setItem(4, createNoActivePetItem());
        }

        // 펫 수 정보 (왼쪽)
        int petCount = plugin.getPetManager().getAllPets(playerId).size();
        int maxPets = plugin.getPetStorageManager().getStorageCapacity(playerId);
        inventory.setItem(2, new ItemBuilder(Material. CHEST)
                .name("§e보유 펫")
                .lore(
                        "§7보유:  §f" + petCount + "§7/§f" + maxPets + "마리",
                        "",
                        "§e클릭하여 보관함 열기"
                )
                .build());

        // 레이팅 정보 (오른쪽)
        int rating = plugin.getPetRankingManager().getPlayerRating(playerId);
        String rankTitle = plugin.getPetRankingManager().getRankTitle(rating);
        inventory. setItem(6, new ItemBuilder(Material.DIAMOND_SWORD)
                .name("§c전투 레이팅")
                .lore(
                        "§7레이팅: §f" + rating,
                        "§7랭크: " + rankTitle,
                        "",
                        "§e클릭하여 랭킹 보기"
                )
                .build());

        // === 메인 메뉴 버튼들 ===

        // 펫 보관함
        inventory.setItem(20, new ItemBuilder(Material. ENDER_CHEST)
                .name("§6§l펫 보관함")
                .lore(
                        "§7보유한 모든 펫을 확인합니다.",
                        "",
                        "§7보유 펫: §f" + petCount + "마리",
                        "",
                        "§e클릭하여 열기"
                )
                .glow(true)
                .build());

        // 펫 소환/해제
        if (activePet != null) {
            inventory. setItem(21, new ItemBuilder(Material. ENDER_EYE)
                    .name("§a§l펫 해제")
                    .lore(
                            "§7현재 소환된 펫을 해제합니다.",
                            "",
                            "§7활성 펫:  §f" + activePet.getPet().getPetName(),
                            "",
                            "§e클릭하여 해제"
                    )
                    .build());
        } else {
            inventory.setItem(21, new ItemBuilder(Material. ENDER_PEARL)
                    . name("§a§l펫 소환")
                    .lore(
                            "§7펫을 소환합니다.",
                            "",
                            "§e클릭하여 소환할 펫 선택"
                    )
                    .build());
        }

        // 펫 스킬
        inventory.setItem(22, new ItemBuilder(Material. ENCHANTED_BOOK)
                .name("§b§l스킬 관리")
                .lore(
                        "§7펫의 스킬을 확인하고 강화합니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .glow(true)
                .build());

        // 펫 진화
        inventory.setItem(23, new ItemBuilder(Material. NETHER_STAR)
                .name("§d§l펫 진화")
                .lore(
                        "§7펫을 진화시킵니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .glow(true)
                .build());

        // 펫 장비
        inventory. setItem(24, new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .name("§9§l펫 장비")
                .lore(
                        "§7펫에게 장비를 장착합니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // === 하단 메뉴 버튼들 ===

        // 펫 케어
        inventory.setItem(29, new ItemBuilder(Material.GOLDEN_APPLE)
                .name("§e§l펫 케어")
                .lore(
                        "§7펫에게 먹이를 주고 돌봅니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // 펫 교배
        inventory.setItem(30, new ItemBuilder(Material. EGG)
                .name("§5§l펫 교배")
                .lore(
                        "§7두 펫을 교배하여 새로운 펫을 얻습니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // 펫 대결
        inventory.setItem(31, new ItemBuilder(Material. IRON_SWORD)
                .name("§c§l펫 대결")
                .lore(
                        "§7다른 플레이어와 펫 대결을 합니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // 펫 알/소환서
        int eggCount = plugin.getPetAcquisitionManager().getPlayerEggs(playerId).size();
        inventory.setItem(32, new ItemBuilder(Material.DRAGON_EGG)
                .name("§6§l알 & 소환서")
                .lore(
                        "§7보유한 알과 소환서를 확인합니다.",
                        "",
                        "§7보유 알: §f" + eggCount + "개",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // 랭킹
        inventory.setItem(33, new ItemBuilder(Material. GOLD_INGOT)
                .name("§e§l랭킹")
                .lore(
                        "§7펫 랭킹을 확인합니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // === 하단 정보/닫기 ===

        // 도움말
        inventory. setItem(48, new ItemBuilder(Material.BOOK)
                .name("§a§l도움말")
                .lore(
                        "§7펫 시스템 사용법을 확인합니다.",
                        "",
                        "§e클릭하여 보기"
                )
                .build());

        // 설정
        inventory.setItem(49, new ItemBuilder(Material. COMPARATOR)
                .name("§7§l설정")
                .lore(
                        "§7펫 관련 설정을 변경합니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // 닫기
        inventory.setItem(50, new ItemBuilder(Material.BARRIER)
                .name("§c§l닫기")
                .lore("§7메뉴를 닫습니다.")
                .build());
    }

    /**
     * 활성 펫 아이템 생성
     */
    private ItemStack createActivePetItem(Pet pet) {
        Material material = getMaterialForPet(pet);

        return new ItemBuilder(material)
                .name(pet. getRarity().getColorCode() + "§l" + pet.getPetName())
                .lore(
                        "§7종족: §f" + pet.getSpeciesId(),
                        "§7레벨: §f" + pet. getLevel(),
                        "§7희귀도: " + pet.getRarity().getColoredName(),
                        "",
                        "§c❤ 체력: §f" + String.format("%.0f", pet.getHealth()) + "/" + String.format("%.0f", pet.getMaxHealth()),
                        "§6🍖 배고픔: §f" + String.format("%. 0f", pet. getHunger()) + "%",
                        "§d😊 행복도: §f" + String.format("%.0f", pet.getHappiness()) + "%",
                        "",
                        "§e클릭하여 상세 정보 보기"
                )
                .glow(true)
                .build();
    }

    /**
     * 활성 펫 없음 아이템 생성
     */
    private ItemStack createNoActivePetItem() {
        return new ItemBuilder(Material.GRAY_DYE)
                .name("§7§l소환된 펫 없음")
                .lore(
                        "§7현재 소환된 펫이 없습니다.",
                        "",
                        "§e클릭하여 펫 소환하기"
                )
                .build();
    }

    /**
     * 펫에 맞는 Material 가져오기
     */
    private Material getMaterialForPet(Pet pet) {
        if (pet.getEntityType() == null) {
            return Material.WOLF_SPAWN_EGG;
        }

        switch (pet.getEntityType()) {
            case WOLF:
                return Material.WOLF_SPAWN_EGG;
            case CAT:
                return Material.CAT_SPAWN_EGG;
            case PARROT:
                return Material.PARROT_SPAWN_EGG;
            case HORSE:
                return Material.HORSE_SPAWN_EGG;
            case RABBIT:
                return Material.RABBIT_SPAWN_EGG;
            case FOX:
                return Material.FOX_SPAWN_EGG;
            case OCELOT:
                return Material.OCELOT_SPAWN_EGG;
            case IRON_GOLEM:
                return Material.IRON_BLOCK;
            case BLAZE:
                return Material.BLAZE_SPAWN_EGG;
            case ENDER_DRAGON:
                return Material.DRAGON_EGG;
            case WITHER: 
                return Material. WITHER_SKELETON_SKULL;
            default:
                return Material.PLAYER_HEAD;
        }
    }

    /**
     * 클릭 이벤트 처리
     */
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 2: // 보유 펫
            case 20: // 펫 보관함
                plugin.getGUIManager().openStorageMenu(player);
                break;

            case 4: // 활성 펫 정보
                PetEntity activePet = plugin.getPetManager().getActivePet(player.getUniqueId());
                if (activePet != null) {
                    plugin.getGUIManager().openPetInfoMenu(player, activePet. getPet());
                } else {
                    plugin.getGUIManager().openStorageMenu(player);
                }
                break;

            case 6: // 레이팅/랭킹
            case 33: // 랭킹
                plugin.getGUIManager().openRankingMenu(player);
                break;

            case 21: // 펫 소환/해제
                if (plugin.getPetManager().hasActivePet(player.getUniqueId())) {
                    plugin.getPetManager().unsummonAllPets(player);
                    open(player); // 새로고침
                } else {
                    plugin.getGUIManager().openStorageMenu(player);
                }
                break;

            case 22: // 스킬 관리
                if (plugin.getPetManager().hasActivePet(player.getUniqueId())) {
                    PetEntity pet = plugin.getPetManager().getActivePet(player.getUniqueId());
                    plugin.getGUIManager().openSkillMenu(player, pet. getPet());
                } else {
                    plugin.getMessageUtil().sendMessage(player, plugin.getConfigManager().getMessage("gui.select-pet-first"));
                }
                break;

            case 23: // 펫 진화
                plugin.getGUIManager().openEvolutionMenu(player);
                break;

            case 24: // 펫 장비
                if (plugin.getPetManager().hasActivePet(player.getUniqueId())) {
                    PetEntity pet = plugin.getPetManager().getActivePet(player.getUniqueId());
                    plugin.getGUIManager().openEquipmentMenu(player, pet.getPet());
                } else {
                    plugin.getMessageUtil().sendMessage(player, plugin.getConfigManager().getMessage("gui.select-pet-first"));
                }
                break;

            case 29: // 펫 케어
                plugin. getGUIManager().openCareMenu(player);
                break;

            case 30: // 펫 교배
                plugin. getGUIManager().openBreedingMenu(player);
                break;

            case 31: // 펫 대결
                plugin.getGUIManager().openBattleMenu(player);
                break;

            case 32: // 알 & 소환서
                plugin. getGUIManager().openEggMenu(player);
                break;

            case 48: // 도움말
                player.closeInventory();
                player.performCommand("pet help");
                break;

            case 49: // 설정
                plugin.getGUIManager().openSettingsMenu(player);
                break;

            case 50: // 닫기
                player.closeInventory();
                break;
        }
    }
}