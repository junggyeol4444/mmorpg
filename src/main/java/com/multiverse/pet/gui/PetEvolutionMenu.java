package com.multiverse.pet.gui;

import com.multiverse.pet.PetCore;
import com.multiverse. pet.gui.holder.PetMenuHolder;
import com.multiverse. pet.model.Pet;
import com. multiverse.pet. model.evolution.PetEvolution;
import com. multiverse.pet. util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * 펫 진화 메뉴 GUI
 * 진화 가능 여부 확인 및 진화 실행
 */
public class PetEvolutionMenu {

    private final PetCore plugin;
    private static final int MENU_SIZE = 54;

    // 현재 보고 있는 펫
    private final Map<UUID, UUID> viewingPet = new HashMap<>();
    // 선택한 진화
    private final Map<UUID, String> selectedEvolution = new HashMap<>();

    public PetEvolutionMenu(PetCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 진화 메뉴 열기
     */
    public void open(Player player) {
        // 활성 펫 또는 첫 번째 펫
        Pet pet = null;
        if (plugin.getPetManager().hasActivePet(player. getUniqueId())) {
            pet = plugin.getPetManager().getActivePet(player. getUniqueId()).getPet();
        } else {
            List<Pet> pets = plugin.getPetManager().getAllPets(player.getUniqueId());
            if (!pets.isEmpty()) {
                pet = pets.get(0);
            }
        }

        if (pet != null) {
            open(player, pet);
        } else {
            plugin.getMessageUtil().sendMessage(player, 
                plugin. getConfigManager().getMessage("pet.no-pets"));
        }
    }

    /**
     * 특정 펫의 진화 메뉴 열기
     */
    public void open(Player player, Pet pet) {
        viewingPet.put(player.getUniqueId(), pet.getPetId());
        selectedEvolution. remove(player.getUniqueId());

        String title = "§d§l" + pet.getPetName() + " §7진화";

        PetMenuHolder holder = new PetMenuHolder(plugin, PetMenuHolder.MenuType.EVOLUTION);
        Inventory inventory = Bukkit.createInventory(holder, MENU_SIZE, title);

        setupMenuItems(inventory, player, pet);

        player.openInventory(inventory);
    }

    /**
     * 메뉴 아이템 설정
     */
    private void setupMenuItems(Inventory inventory, Player player, Pet pet) {
        // 배경
        ItemStack background = new ItemBuilder(Material. PURPLE_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 0; i < MENU_SIZE; i++) {
            inventory.setItem(i, background);
        }

        // === 현재 펫 정보 (왼쪽) ===
        inventory.setItem(10, createCurrentPetItem(pet));

        // 화살표
        inventory.setItem(13, new ItemBuilder(Material. ARROW)
                .name("§d→ 진화 →")
                .build());

        // === 진화 옵션들 (오른쪽) ===
        List<PetEvolution> availableEvolutions = plugin.getEvolutionManager()
                .getAvailableEvolutions(pet, player);
        List<PetEvolution> allEvolutions = plugin. getEvolutionManager()
                .getEvolutionsForSpecies(pet.getSpeciesId());

        int[] evolutionSlots = {15, 16, 24, 25, 33, 34};
        int slotIndex = 0;

        // 가능한 진화
        for (PetEvolution evolution : availableEvolutions) {
            if (slotIndex < evolutionSlots. length) {
                inventory. setItem(evolutionSlots[slotIndex], createEvolutionItem(evolution, pet, player, true));
                slotIndex++;
            }
        }

        // 불가능한 진화 (조건 미충족)
        for (PetEvolution evolution : allEvolutions) {
            if (! availableEvolutions.contains(evolution) && slotIndex < evolutionSlots.length) {
                inventory. setItem(evolutionSlots[slotIndex], createEvolutionItem(evolution, pet, player, false));
                slotIndex++;
            }
        }

        // 빈 슬롯
        while (slotIndex < evolutionSlots.length) {
            inventory.setItem(evolutionSlots[slotIndex], new ItemBuilder(Material. LIGHT_GRAY_STAINED_GLASS_PANE)
                    .name("§7진화 경로 없음")
                    .build());
            slotIndex++;
        }

        // === 선택된 진화 상세 정보 (하단) ===
        String selectedEvoId = selectedEvolution. get(player.getUniqueId());
        if (selectedEvoId != null) {
            PetEvolution evo = plugin.getEvolutionManager().getEvolution(selectedEvoId);
            if (evo != null) {
                setupSelectedEvolutionInfo(inventory, evo, pet, player);
            }
        } else {
            inventory.setItem(40, new ItemBuilder(Material.PAPER)
                    . name("§e진화를 선택하세요")
                    .lore("§7위의 진화 옵션을 클릭하여 선택하세요.")
                    .build());
        }

        // === 하단 버튼 ===

        // 펫 변경
        inventory.setItem(37, new ItemBuilder(Material.CHEST)
                .name("§e다른 펫 선택")
                .lore("§7진화할 펫을 변경합니다.")
                .build());

        // 뒤로가기
        inventory.setItem(45, new ItemBuilder(Material. ARROW)
                .name("§7뒤로가기")
                .build());

        // 새로고침
        inventory.setItem(53, new ItemBuilder(Material. SUNFLOWER)
                .name("§e새로고침")
                .build());
    }

    /**
     * 현재 펫 아이템
     */
    private ItemStack createCurrentPetItem(Pet pet) {
        List<String> lore = new ArrayList<>();
        lore.add("§7종족:  §f" + pet. getSpeciesId());
        lore. add("§7레벨: §f" + pet.getLevel());
        lore.add("§7희귀도: " + pet.getRarity().getColoredName());
        lore.add("");
        lore. add("§7진화 단계: §f" + pet.getEvolutionStage() + "단계");
        lore.add("");

        // 주요 스탯
        lore.add("§c⚔ 공격력: §f" + String.format("%.0f", pet.getTotalStat("attack")));
        lore.add("§9🛡 방어력: §f" + String. format("%.0f", pet.getTotalStat("defense")));
        lore.add("§a💨 속도: §f" + String.format("%.0f", pet.getTotalStat("speed")));

        return new ItemBuilder(Material.PLAYER_HEAD)
                .name(pet.getRarity().getColorCode() + "§l" + pet.getPetName())
                .lore(lore)
                .glow(true)
                .build();
    }

    /**
     * 진화 옵션 아이템
     */
    private ItemStack createEvolutionItem(PetEvolution evolution, Pet pet, Player player, boolean available) {
        List<String> lore = new ArrayList<>();

        // 진화 대상
        String targetSpecies = evolution. getToSpeciesId();
        var species = plugin.getSpeciesCache().getSpecies(targetSpecies);
        String speciesName = species != null ? species.getName() : targetSpecies;

        lore.add("§7진화 후: §f" + speciesName);
        lore.add("§7단계: §f" + evolution.getToStage() + "단계");
        lore. add("");

        // 성공 확률
        lore.add("§e성공률: §f" + String.format("%. 1f", evolution. getSuccessChance()) + "%");
        lore.add("");

        // 조건
        lore.add("§e§l[ 조건 ]");
        lore.add("§7레벨: " + (pet.getLevel() >= evolution.getRequiredLevel() ? "§a✓" : "§c✗") + 
                " §f" + evolution.getRequiredLevel() + " §7(현재:  " + pet.getLevel() + ")");

        // 비용
        if (evolution.getGoldCost() > 0) {
            boolean hasGold = plugin.getPlayerDataCoreHook().hasGold(player.getUniqueId(), evolution.getGoldCost());
            lore.add("§7골드:  " + (hasGold ? "§a✓" : "§c✗") + " §6" + String.format("%.0f", evolution.getGoldCost()));
        }

        // 필요 아이템
        for (PetEvolution.ItemRequirement item : evolution.getRequiredItems()) {
            boolean hasItem = plugin. hasItemCore() && 
                    plugin.getItemCoreHook().hasItem(player, item.getItemId(), item.getAmount());
            lore.add("§7아이템: " + (hasItem ? "§a✓" : "§c✗") + " §f" + item.getItemId() + " x" + item.getAmount());
        }

        lore.add("");

        // 스탯 보너스
        if (! evolution.getStatBonuses().isEmpty()) {
            lore.add("§a§l[ 스탯 보너스 ]");
            for (Map.Entry<String, Double> bonus : evolution.getStatBonuses().entrySet()) {
                lore.add("§7" + bonus.getKey() + ": §a+" + String.format("%.0f", bonus.getValue()));
            }
            lore.add("");
        }

        // 새 스킬
        if (! evolution.getNewSkills().isEmpty()) {
            lore. add("§b§l[ 새 스킬 ]");
            for (String skillId : evolution.getNewSkills()) {
                lore. add("§7- §f" + skillId);
            }
            lore.add("");
        }

        // 클릭 안내
        if (available) {
            lore.add("§a진화 가능!");
            lore.add("§e클릭하여 선택");
        } else {
            List<String> unmet = evolution.getUnmetConditions(pet, null, 0, null, null, true, null);
            lore.add("§c조건 미충족:");
            for (String condition : unmet) {
                lore.add("§c- " + condition);
            }
        }

        Material material = available ? Material.NETHER_STAR :  Material.COAL;

        ItemBuilder builder = new ItemBuilder(material)
                .name((available ? "§a" : "§c") + speciesName + " §7(" + evolution.getToStage() + "단계)")
                .lore(lore);

        if (available) {
            builder.glow(true);
        }

        return builder. build();
    }

    /**
     * 선택된 진화 정보 설정
     */
    private void setupSelectedEvolutionInfo(Inventory inventory, PetEvolution evolution, Pet pet, Player player) {
        // 확인 정보
        var species = plugin.getSpeciesCache().getSpecies(evolution.getToSpeciesId());
        String speciesName = species != null ? species. getName() : evolution.getToSpeciesId();

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore. add("§f" + pet.getPetName() + " §7→ §a" + speciesName);
        lore.add("");
        lore. add("§e성공률: §f" + String. format("%.1f", evolution.getSuccessChance()) + "%");
        lore.add("");
        lore.add("§c§l실패 시 진화석만 소모됩니다.");
        lore.add("");
        lore. add("§e§lShift+클릭으로 진화!");

        inventory.setItem(40, new ItemBuilder(Material.END_CRYSTAL)
                .name("§d§l" + speciesName + "(으)로 진화")
                .lore(lore)
                .glow(true)
                .build());

        // 취소 버튼
        inventory.setItem(43, new ItemBuilder(Material.BARRIER)
                .name("§c선택 취소")
                .lore("§7다른 진화를 선택합니다.")
                .build());
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

        UUID petId = viewingPet.get(player.getUniqueId());
        if (petId == null) return;

        Pet pet = plugin.getPetManager().getPetById(player.getUniqueId(), petId);
        if (pet == null) {
            player.closeInventory();
            return;
        }

        // 진화 옵션 슬롯
        int[] evolutionSlots = {15, 16, 24, 25, 33, 34};
        List<PetEvolution> allEvolutions = new ArrayList<>();
        allEvolutions.addAll(plugin.getEvolutionManager().getAvailableEvolutions(pet, player));

        for (PetEvolution evo : plugin.getEvolutionManager().getEvolutionsForSpecies(pet.getSpeciesId())) {
            if (! allEvolutions.contains(evo)) {
                allEvolutions.add(evo);
            }
        }

        for (int i = 0; i < evolutionSlots.length; i++) {
            if (evolutionSlots[i] == slot && i < allEvolutions.size()) {
                PetEvolution evolution = allEvolutions. get(i);

                // 가능한 진화만 선택 가능
                if (plugin.getEvolutionManager().getAvailableEvolutions(pet, player).contains(evolution)) {
                    selectedEvolution.put(player.getUniqueId(), evolution.getEvolutionId());
                    open(player, pet);
                } else {
                    plugin.getMessageUtil().sendMessage(player, 
                        plugin.getConfigManager().getMessage("evolution.not-available"));
                }
                return;
            }
        }

        switch (slot) {
            case 37:  // 펫 변경
                plugin.getGUIManager().openEvolutionPetSelectMenu(player);
                break;

            case 40: // 진화 실행
                String evoId = selectedEvolution.get(player.getUniqueId());
                if (evoId != null && (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT)) {
                    if (plugin.getEvolutionManager().evolve(player, pet, evoId)) {
                        selectedEvolution.remove(player.getUniqueId());
                        // 진화 후 새로운 펫 정보로 열기
                        pet = plugin.getPetManager().getPetById(player.getUniqueId(), petId);
                        if (pet != null) {
                            open(player, pet);
                        }
                    }
                }
                break;

            case 43: // 선택 취소
                selectedEvolution. remove(player.getUniqueId());
                open(player, pet);
                break;

            case 45: // 뒤로가기
                plugin.getGUIManager().openMainMenu(player);
                break;

            case 53: // 새로고침
                open(player, pet);
                break;
        }
    }

    /**
     * 정리
     */
    public void cleanup(UUID playerId) {
        viewingPet.remove(playerId);
        selectedEvolution.remove(playerId);
    }
}