package com.multiverse. pet.gui;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.gui.holder.PetPaginatedHolder;
import com.multiverse.pet.model.Pet;
import com.multiverse.pet.model.PetRarity;
import com.multiverse.pet.model.PetStatus;
import com. multiverse.pet. model.storage.SortType;
import com.multiverse. pet.util.ItemBuilder;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org.bukkit.entity.Player;
import org.bukkit. event.inventory. ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 펫 보관함 메뉴 GUI
 * 보유한 모든 펫 목록 표시 및 관리
 */
public class PetStorageMenu {

    private final PetCore plugin;
    private static final int MENU_SIZE = 54;
    private static final String MENU_TITLE = "§6§l펫 보관함";
    private static final int PETS_PER_PAGE = 28; // 4x7

    public PetStorageMenu(PetCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 보관함 메뉴 열기
     */
    public void open(Player player) {
        open(player, 0);
    }

    /**
     * 보관함 메뉴 열기 (페이지 지정)
     */
    public void open(Player player, int page) {
        UUID playerId = player. getUniqueId();
        List<Pet> pets = plugin.getPetStorageManager().getFilteredAndSortedPets(playerId);

        int totalPages = (int) Math.ceil((double) pets.size() / PETS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        page = Math.max(0, Math.min(page, totalPages - 1));

        PetPaginatedHolder holder = new PetPaginatedHolder(
                plugin,
                PetPaginatedHolder.MenuType.STORAGE,
                page,
                totalPages
        );

        Inventory inventory = Bukkit.createInventory(
                holder,
                MENU_SIZE,
                MENU_TITLE + " §7(" + (page + 1) + "/" + totalPages + ")"
        );

        setupMenuItems(inventory, player, pets, page);

        player.openInventory(inventory);
    }

    /**
     * 메뉴 아이템 설정
     */
    private void setupMenuItems(Inventory inventory, Player player, List<Pet> pets, int page) {
        UUID playerId = player. getUniqueId();

        // 배경
        ItemStack background = new ItemBuilder(Material. GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 0; i < MENU_SIZE; i++) {
            inventory.setItem(i, background);
        }

        // 펫 슬롯 (10-16, 19-25, 28-34, 37-43)
        int[] petSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };

        int startIndex = page * PETS_PER_PAGE;
        int endIndex = Math.min(startIndex + PETS_PER_PAGE, pets.size());

        for (int i = 0; i < petSlots.length; i++) {
            int petIndex = startIndex + i;
            if (petIndex < endIndex) {
                Pet pet = pets.get(petIndex);
                inventory.setItem(petSlots[i], createPetItem(pet));
            } else {
                inventory.setItem(petSlots[i], new ItemBuilder(Material. LIGHT_GRAY_STAINED_GLASS_PANE)
                        .name("§7빈 슬롯")
                        . build());
            }
        }

        // === 하단 컨트롤 ===

        // 이전 페이지
        if (page > 0) {
            inventory.setItem(45, new ItemBuilder(Material.ARROW)
                    . name("§e이전 페이지")
                    . lore("§7" + page + "페이지로 이동")
                    .build());
        }

        // 정렬 버튼
        SortType currentSort = plugin.getPetStorageManager().getSortType(playerId);
        inventory.setItem(47, new ItemBuilder(Material.HOPPER)
                .name("§e정렬 방식")
                .lore(
                        "§7현재:  §f" + currentSort.getDisplayName(),
                        "",
                        "§e클릭하여 변경"
                )
                .build());

        // 필터 버튼
        boolean hasFilter = plugin.getPetStorageManager().hasActiveFilter(playerId);
        inventory.setItem(48, new ItemBuilder(hasFilter ? Material. LIME_DYE : Material. GRAY_DYE)
                .name("§e필터")
                .lore(
                        hasFilter ? "§a필터 적용 중" : "§7필터 없음",
                        "",
                        "§e좌클릭:  필터 설정",
                        "§e우클릭: 필터 초기화"
                )
                .build());

        // 정보
        int petCount = pets.size();
        int capacity = plugin.getPetStorageManager().getStorageCapacity(playerId);
        inventory.setItem(49, new ItemBuilder(Material.BOOK)
                .name("§6보관함 정보")
                .lore(
                        "§7보유 펫:  §f" + petCount + "§7/§f" + capacity,
                        "§7남은 슬롯: §f" + (capacity - petCount),
                        "",
                        "§e클릭하여 통계 보기"
                )
                .build());

        // 검색
        inventory.setItem(50, new ItemBuilder(Material.NAME_TAG)
                .name("§e검색")
                .lore(
                        "§7펫 이름으로 검색합니다.",
                        "",
                        "§e클릭하여 검색"
                )
                .build());

        // 다음 페이지
        int totalPages = (int) Math.ceil((double) pets.size() / PETS_PER_PAGE);
        if (page < totalPages - 1) {
            inventory.setItem(53, new ItemBuilder(Material.ARROW)
                    . name("§e다음 페이지")
                    . lore("§7" + (page + 2) + "페이지로 이동")
                    . build());
        }

        // 뒤로가기
        inventory.setItem(0, new ItemBuilder(Material.BARRIER)
                .name("§c뒤로가기")
                .lore("§7메인 메뉴로 돌아갑니다.")
                .build());
    }

    /**
     * 펫 아이템 생성
     */
    private ItemStack createPetItem(Pet pet) {
        Material material = getMaterialForRarity(pet.getRarity());

        List<String> lore = new ArrayList<>();
        lore.add("§7종족: §f" + pet.getSpeciesId());
        lore.add("§7레벨: §f" + pet.getLevel());
        lore. add("§7희귀도: " + pet.getRarity().getColoredName());
        lore.add("");

        // 상태 표시
        lore.add("§7상태: " + getStatusDisplay(pet));
        lore.add("");

        // 스탯 요약
        lore.add("§c❤ 체력: §f" + String.format("%. 0f", pet. getHealth()) + "/" + String.format("%.0f", pet.getMaxHealth()));
        lore.add("§6🍖 배고픔: §f" + String.format("%. 0f", pet. getHunger()) + "%");
        lore.add("§d😊 행복도: §f" + String. format("%.0f", pet.getHappiness()) + "%");
        lore.add("");

        // 즐겨찾기/활성 표시
        if (pet.isActive()) {
            lore.add("§a✦ 현재 소환 중");
        }
        if (pet. isFavorite()) {
            lore.add("§e★ 즐겨찾기");
        }
        lore.add("");

        // 클릭 안내
        lore.add("§e좌클릭:  소환/해제");
        lore.add("§e우클릭: 상세 정보");
        lore. add("§eShift+클릭: 즐겨찾기");

        ItemBuilder builder = new ItemBuilder(material)
                .name(pet. getRarity().getColorCode() + pet.getPetName())
                .lore(lore);

        if (pet.isActive() || pet.isFavorite()) {
            builder.glow(true);
        }

        return builder. build();
    }

    /**
     * 희귀도별 Material
     */
    private Material getMaterialForRarity(PetRarity rarity) {
        switch (rarity) {
            case COMMON: 
                return Material.WHITE_WOOL;
            case UNCOMMON:
                return Material.LIME_WOOL;
            case RARE:
                return Material.LIGHT_BLUE_WOOL;
            case EPIC:
                return Material.PURPLE_WOOL;
            case LEGENDARY: 
                return Material. ORANGE_WOOL;
            case MYTHIC:
                return Material.RED_WOOL;
            default:
                return Material.GRAY_WOOL;
        }
    }

    /**
     * 상태 표시 문자열
     */
    private String getStatusDisplay(Pet pet) {
        PetStatus status = pet.getStatus();

        switch (status) {
            case ACTIVE:
                return "§a소환 중";
            case STORED:
                return "§f보관 중";
            case BREEDING:
                return "§d교배 중";
            case BATTLING:
                return "§c대결 중";
            case FAINTED:
                return "§c기절";
            case TRAINING:
                return "§e훈련 중";
            default:
                return "§7" + status.getDisplayName();
        }
    }

    /**
     * 클릭 이벤트 처리
     */
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (!(event. getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event. getSlot();
        ClickType clickType = event.getClick();

        PetPaginatedHolder holder = (PetPaginatedHolder) event.getInventory().getHolder();
        int currentPage = holder.getCurrentPage();
        UUID playerId = player. getUniqueId();

        // 펫 슬롯 확인
        int[] petSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };

        int petSlotIndex = -1;
        for (int i = 0; i < petSlots.length; i++) {
            if (petSlots[i] == slot) {
                petSlotIndex = i;
                break;
            }
        }

        if (petSlotIndex >= 0) {
            // 펫 슬롯 클릭
            List<Pet> pets = plugin.getPetStorageManager().getFilteredAndSortedPets(playerId);
            int petIndex = currentPage * PETS_PER_PAGE + petSlotIndex;

            if (petIndex < pets.size()) {
                Pet pet = pets.get(petIndex);
                handlePetClick(player, pet, clickType);
            }
            return;
        }

        // 컨트롤 버튼
        switch (slot) {
            case 0: // 뒤로가기
                plugin.getGUIManager().openMainMenu(player);
                break;

            case 45: // 이전 페이지
                if (currentPage > 0) {
                    open(player, currentPage - 1);
                }
                break;

            case 47: // 정렬
                plugin.getPetStorageManager().cycleSortType(playerId);
                open(player, 0);
                break;

            case 48: // 필터
                if (clickType == ClickType.RIGHT) {
                    plugin.getPetStorageManager().clearFilter(playerId);
                    open(player, 0);
                } else {
                    plugin. getGUIManager().openFilterMenu(player);
                }
                break;

            case 49: // 정보
                showStorageStats(player);
                break;

            case 50: // 검색
                player.closeInventory();
                plugin.getMessageUtil().sendMessage(player, plugin.getConfigManager().getMessage("gui.enter-search-term"));
                // 채팅 입력 대기 로직 필요
                break;

            case 53: // 다음 페이지
                open(player, currentPage + 1);
                break;
        }
    }

    /**
     * 펫 클릭 처리
     */
    private void handlePetClick(Player player, Pet pet, ClickType clickType) {
        if (clickType == ClickType. SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
            // 즐겨찾기 토글
            plugin.getPetStorageManager().toggleFavorite(player. getUniqueId(), pet.getPetId());
            open(player, plugin.getPetStorageManager().getCurrentPage(player.getUniqueId()));
        } else if (clickType == ClickType.RIGHT) {
            // 상세 정보
            plugin.getGUIManager().openPetInfoMenu(player, pet);
        } else {
            // 소환/해제
            if (pet. isActive()) {
                plugin.getPetManager().unsummonPet(player, pet. getPetId());
            } else if (pet.getStatus().canBeSummoned()) {
                plugin.getPetManager().summonPet(player, pet.getPetId());
            } else {
                plugin.getMessageUtil().sendMessage(player, plugin. getConfigManager().getMessage("pet.cannot-summon")
                        . replace("{status}", pet.getStatus().getDisplayName()));
            }
            open(player, plugin.getPetStorageManager().getCurrentPage(player.getUniqueId()));
        }
    }

    /**
     * 보관함 통계 표시
     */
    private void showStorageStats(Player player) {
        var stats = plugin.getPetStorageManager().getStorageStats(player.getUniqueId());
        plugin.getMessageUtil().sendMessage(player, stats.getSummary());
    }
}