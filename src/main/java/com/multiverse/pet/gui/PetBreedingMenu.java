package com.multiverse.pet.gui;

import com.multiverse. pet.PetCore;
import com. multiverse.pet. gui.holder.PetMenuHolder;
import com.multiverse.pet.model.Pet;
import com.multiverse. pet.model.breeding. BreedingStatus;
import com. multiverse.pet. model.breeding.PetBreeding;
import com. multiverse.pet. util.ItemBuilder;
import org.bukkit.Bukkit;
import org. bukkit.Material;
import org. bukkit.entity.Player;
import org.bukkit. event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * 펫 교배 메뉴 GUI
 * 교배할 펫 선택, 진행 중인 교배 확인
 */
public class PetBreedingMenu {

    private final PetCore plugin;
    private static final int MENU_SIZE = 54;

    // 선택된 부모 펫
    private final Map<UUID, UUID> selectedParent1 = new HashMap<>();
    private final Map<UUID, UUID> selectedParent2 = new HashMap<>();

    public PetBreedingMenu(PetCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 교배 메뉴 열기
     */
    public void open(Player player) {
        selectedParent1.remove(player.getUniqueId());
        selectedParent2.remove(player. getUniqueId());

        String title = "§5§l펫 교배";

        PetMenuHolder holder = new PetMenuHolder(plugin, PetMenuHolder.MenuType.BREEDING);
        Inventory inventory = Bukkit.createInventory(holder, MENU_SIZE, title);

        setupMenuItems(inventory, player);

        player.openInventory(inventory);
    }

    /**
     * 메뉴 아이템 설정
     */
    private void setupMenuItems(Inventory inventory, Player player) {
        UUID playerId = player. getUniqueId();

        // 배경
        ItemStack background = new ItemBuilder(Material.MAGENTA_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 0; i < MENU_SIZE; i++) {
            inventory.setItem(i, background);
        }

        // === 교배 슬롯 (상단) ===

        // 부모 1 슬롯
        UUID parent1Id = selectedParent1.get(playerId);
        if (parent1Id != null) {
            Pet parent1 = plugin.getPetManager().getPetById(playerId, parent1Id);
            if (parent1 != null) {
                inventory.setItem(11, createSelectedPetItem(parent1, 1));
            }
        } else {
            inventory.setItem(11, createEmptyParentSlot(1));
        }

        // 하트 아이콘
        inventory. setItem(13, new ItemBuilder(Material. RED_DYE)
                .name("§c§l❤")
                .lore(
                        "§7두 펫을 선택하면",
                        "§7교배를 시작할 수 있습니다."
                )
                .build());

        // 부모 2 슬롯
        UUID parent2Id = selectedParent2.get(playerId);
        if (parent2Id != null) {
            Pet parent2 = plugin.getPetManager().getPetById(playerId, parent2Id);
            if (parent2 != null) {
                inventory.setItem(15, createSelectedPetItem(parent2, 2));
            }
        } else {
            inventory. setItem(15, createEmptyParentSlot(2));
        }

        // === 예상 결과 (양쪽 선택 시) ===
        if (parent1Id != null && parent2Id != null) {
            Pet parent1 = plugin.getPetManager().getPetById(playerId, parent1Id);
            Pet parent2 = plugin. getPetManager().getPetById(playerId, parent2Id);

            if (parent1 != null && parent2 != null) {
                inventory.setItem(22, createBreedingPreviewItem(parent1, parent2, player));

                // 교배 시작 버튼
                inventory.setItem(31, new ItemBuilder(Material. LIME_CONCRETE)
                        . name("§a§l교배 시작!")
                        .lore(
                                "§7두 펫의 교배를 시작합니다.",
                                "",
                                "§e클릭하여 시작"
                        )
                        .glow(true)
                        .build());
            }
        } else {
            inventory.setItem(22, new ItemBuilder(Material. GRAY_DYE)
                    .name("§7결과 미리보기")
                    .lore("§7두 펫을 선택하면 예상 결과가 표시됩니다.")
                    . build());

            inventory.setItem(31, new ItemBuilder(Material. GRAY_CONCRETE)
                    .name("§7§l교배 시작")
                    .lore("§7두 펫을 먼저 선택하세요.")
                    .build());
        }

        // === 진행 중인 교배 (하단) ===
        List<PetBreeding> activeBreedings = plugin. getBreedingManager().getPlayerBreedings(playerId);
        int maxBreedings = plugin.getBreedingManager().getMaxConcurrentBreedings();

        inventory.setItem(36, new ItemBuilder(Material. CLOCK)
                .name("§d§l진행 중인 교배")
                .lore(
                        "§7진행 중:  §f" + activeBreedings.size() + "/" + maxBreedings,
                        "",
                        "§e아래에서 확인"
                )
                .build());

        int[] breedingSlots = {37, 38, 39, 40, 41, 42, 43};
        for (int i = 0; i < breedingSlots.length; i++) {
            if (i < activeBreedings. size()) {
                inventory.setItem(breedingSlots[i], createBreedingStatusItem(activeBreedings.get(i), i + 1));
            } else if (i < maxBreedings) {
                inventory.setItem(breedingSlots[i], new ItemBuilder(Material. LIGHT_GRAY_STAINED_GLASS_PANE)
                        .name("§7빈 슬롯")
                        . lore("§7교배 가능")
                        .build());
            } else {
                inventory.setItem(breedingSlots[i], new ItemBuilder(Material. BARRIER)
                        . name("§c잠긴 슬롯")
                        .lore("§7추가 슬롯이 필요합니다.")
                        . build());
            }
        }

        // === 펫 선택 영역 버튼 ===
        inventory.setItem(2, new ItemBuilder(Material.CHEST)
                .name("§e부모 1 선택")
                .lore("§7클릭하여 첫 번째 펫 선택")
                .build());

        inventory.setItem(6, new ItemBuilder(Material.CHEST)
                .name("§e부모 2 선택")
                .lore("§7클릭하여 두 번째 펫 선택")
                .build());

        // === 정보 ===
        inventory. setItem(8, new ItemBuilder(Material.BOOK)
                .name("§e교배 안내")
                .lore(
                        "§7§l[교배 조건]",
                        "§7- 최소 레벨: §f" + plugin. getBreedingManager().getMinBreedingLevel(),
                        "§7- 최소 행복도: §f" + plugin.getBreedingManager().getMinBreedingHappiness() + "%",
                        "§7- 보관 상태여야 함",
                        "§7- 쿨다운 없어야 함",
                        "",
                        "§7§l[결과]",
                        "§7- 부모의 특성을 유전",
                        "§7- 희귀도에 따른 변이 확률",
                        "§7- 레벨 1부터 시작"
                )
                .build());

        // === 하단 버튼 ===

        // 선택 초기화
        inventory.setItem(47, new ItemBuilder(Material.BARRIER)
                .name("§c선택 초기화")
                .lore("§7선택한 펫을 초기화합니다.")
                .build());

        // 뒤로가기
        inventory. setItem(45, new ItemBuilder(Material.ARROW)
                .name("§7뒤로가기")
                .build());

        // 새로고침
        inventory. setItem(53, new ItemBuilder(Material.SUNFLOWER)
                .name("§e새로고침")
                .build());
    }

    /**
     * 빈 부모 슬롯 아이템
     */
    private ItemStack createEmptyParentSlot(int number) {
        return new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .name("§7부모 " + number + " §8(미선택)")
                .lore(
                        "§7교배할 펫을 선택하세요.",
                        "",
                        "§e클릭하여 선택"
                )
                .build();
    }

    /**
     * 선택된 펫 아이템
     */
    private ItemStack createSelectedPetItem(Pet pet, int number) {
        List<String> lore = new ArrayList<>();
        lore.add("§7부모 " + number);
        lore. add("");
        lore. add("§7종족:  §f" + pet. getSpeciesId());
        lore. add("§7레벨: §f" + pet.getLevel());
        lore.add("§7희귀도: " + pet.getRarity().getColoredName());
        lore.add("");

        // 교배 가능 여부
        if (plugin.getBreedingManager().canBreed(pet)) {
            lore.add("§a✓ 교배 가능");
        } else {
            lore.add("§c✗ 교배 불가");
            String reason = plugin.getBreedingManager().getCannotBreedReason(pet);
            lore.add("§c" + reason);
        }

        lore.add("");
        lore.add("§e우클릭:  선택 취소");

        return new ItemBuilder(Material.PLAYER_HEAD)
                .name(pet.getRarity().getColorCode() + pet.getPetName())
                .lore(lore)
                .glow(true)
                .build();
    }

    /**
     * 교배 미리보기 아이템
     */
    private ItemStack createBreedingPreviewItem(Pet parent1, Pet parent2, Player player) {
        List<String> lore = new ArrayList<>();

        // 비용
        double cost = plugin.getBreedingManager().calculateBreedingCost(parent1, parent2);
        boolean hasCost = plugin. getPlayerDataCoreHook().hasGold(player. getUniqueId(), cost);

        lore.add("§e§l[비용]");
        lore.add((hasCost ? "§a✓" : "§c✗") + " §6" + String.format("%.0f", cost) + " 골드");
        lore.add("");

        // 소요 시간
        long duration = plugin.getBreedingManager().calculateBreedingDuration(parent1, parent2);
        lore.add("§e§l[소요 시간]");
        lore. add("§f" + formatDuration(duration));
        lore.add("");

        // 예상 결과
        lore.add("§e§l[예상 결과]");
        
        // 가능한 종족
        List<String> possibleSpecies = plugin.getBreedingManager().getPossibleOffspringSpecies(parent1, parent2);
        lore.add("§7가능한 종족:");
        for (String speciesId : possibleSpecies) {
            var species = plugin.getSpeciesCache().getSpecies(speciesId);
            String name = species != null ? species.getName() : speciesId;
            lore.add("§7  - §f" + name);
        }
        lore.add("");

        // 변이 확률
        double mutationChance = plugin.getBreedingManager().calculateMutationChance(parent1, parent2);
        lore.add("§d변이 확률:  §f" + String.format("%.1f", mutationChance) + "%");

        return new ItemBuilder(Material.EGG)
                .name("§d§l예상 결과")
                .lore(lore)
                .glow(true)
                .build();
    }

    /**
     * 교배 상태 아이템
     */
    private ItemStack createBreedingStatusItem(PetBreeding breeding, int number) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore. add("§7" + breeding.getParent1SpeciesId() + " + " + breeding.getParent2SpeciesId());
        lore. add("");

        BreedingStatus status = breeding.getStatus();
        Material material;

        switch (status) {
            case IN_PROGRESS: 
                material = Material. CLOCK;
                lore.add("§e진행 중");
                lore.add("§7남은 시간:  §f" + breeding.getRemainingTimeFormatted());
                lore.add("");
                lore.add(getProgressBar(breeding. getProgress()));
                break;

            case AWAITING_COLLECTION:
                material = Material.LIME_DYE;
                lore.add("§a완료!  수령 대기 중");
                if (breeding.isMutation()) {
                    lore.add("§d✦ 변이 발생!");
                }
                lore.add("");
                lore.add("§e클릭하여 수령");
                break;

            case CANCELLED:
                material = Material.RED_DYE;
                lore. add("§c취소됨");
                break;

            case FAILED:
                material = Material. BARRIER;
                lore.add("§c실패");
                break;

            default:
                material = Material. GRAY_DYE;
                lore. add("§7" + status.getDisplayName());
                break;
        }

        lore.add("");
        if (status == BreedingStatus.IN_PROGRESS) {
            lore.add("§e우클릭: 취소");
        }

        return new ItemBuilder(material)
                .name("§d교배 #" + number)
                .lore(lore)
                .glow(status == BreedingStatus. AWAITING_COLLECTION)
                .build();
    }

    /**
     * 진행률 바
     */
    private String getProgressBar(double percent) {
        int filled = (int) (percent / 10);
        StringBuilder bar = new StringBuilder("§8[");

        for (int i = 0; i < 10; i++) {
            bar.append(i < filled ? "§d█" : "§7░");
        }

        bar.append("§8] §f").append(String.format("%.1f", percent)).append("%");
        return bar.toString();
    }

    /**
     * 시간 포맷팅
     */
    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String. format("%d초", seconds);
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
        UUID playerId = player. getUniqueId();
        int slot = event.getSlot();
        ClickType clickType = event.getClick();

        // 부모 슬롯 클릭
        if (slot == 2 || slot == 11) {
            // 부모 1 선택
            if (clickType == ClickType.RIGHT && selectedParent1.containsKey(playerId)) {
                selectedParent1.remove(playerId);
                open(player);
            } else {
                openPetSelectMenu(player, 1);
            }
            return;
        }

        if (slot == 6 || slot == 15) {
            // 부모 2 선택
            if (clickType == ClickType.RIGHT && selectedParent2.containsKey(playerId)) {
                selectedParent2.remove(playerId);
                open(player);
            } else {
                openPetSelectMenu(player, 2);
            }
            return;
        }

        // 교배 시작
        if (slot == 31) {
            UUID parent1Id = selectedParent1.get(playerId);
            UUID parent2Id = selectedParent2.get(playerId);

            if (parent1Id != null && parent2Id != null) {
                Pet parent1 = plugin. getPetManager().getPetById(playerId, parent1Id);
                Pet parent2 = plugin.getPetManager().getPetById(playerId, parent2Id);

                if (parent1 != null && parent2 != null) {
                    if (plugin.getBreedingManager().startBreeding(player, parent1, parent2)) {
                        selectedParent1.remove(playerId);
                        selectedParent2.remove(playerId);
                        open(player);
                    }
                }
            }
            return;
        }

        // 진행 중인 교배 슬롯
        int[] breedingSlots = {37, 38, 39, 40, 41, 42, 43};
        List<PetBreeding> activeBreedings = plugin.getBreedingManager().getPlayerBreedings(playerId);

        for (int i = 0; i < breedingSlots. length; i++) {
            if (breedingSlots[i] == slot && i < activeBreedings. size()) {
                PetBreeding breeding = activeBreedings. get(i);

                if (breeding.getStatus() == BreedingStatus. AWAITING_COLLECTION) {
                    // 수령
                    Pet offspring = plugin.getBreedingManager().collectOffspring(player, breeding. getBreedingId());
                    if (offspring != null) {
                        open(player);
                    }
                } else if (breeding.getStatus() == BreedingStatus.IN_PROGRESS && clickType == ClickType.RIGHT) {
                    // 취소
                    plugin.getBreedingManager().cancelBreeding(player, breeding.getBreedingId());
                    open(player);
                }
                return;
            }
        }

        switch (slot) {
            case 47:  // 선택 초기화
                selectedParent1.remove(playerId);
                selectedParent2.remove(playerId);
                open(player);
                break;

            case 45: // 뒤로가기
                plugin.getGUIManager().openMainMenu(player);
                break;

            case 53: // 새로고침
                open(player);
                break;
        }
    }

    /**
     * 펫 선택 메뉴 열기
     */
    private void openPetSelectMenu(Player player, int parentNumber) {
        plugin.getGUIManager().openBreedingPetSelectMenu(player, parentNumber);
    }

    /**
     * 부모 선택
     */
    public void selectParent(UUID playerId, UUID petId, int parentNumber) {
        if (parentNumber == 1) {
            selectedParent1.put(playerId, petId);
        } else {
            selectedParent2.put(playerId, petId);
        }
    }

    /**
     * 선택된 부모 가져오기
     */
    public UUID getSelectedParent(UUID playerId, int parentNumber) {
        return parentNumber == 1 ? selectedParent1. get(playerId) : selectedParent2.get(playerId);
    }

    /**
     * 정리
     */
    public void cleanup(UUID playerId) {
        selectedParent1.remove(playerId);
        selectedParent2.remove(playerId);
    }
}