package com.multiverse.pet.gui;

import com.multiverse.pet.PetCore;
import com.multiverse. pet.gui.holder.PetMenuHolder;
import com. multiverse.pet. model.Pet;
import com.multiverse.pet.model. equipment.PetEquipSlot;
import com.multiverse.pet.model.equipment.PetEquipmentData;
import com. multiverse.pet. util.ItemBuilder;
import org.bukkit.Bukkit;
import org. bukkit.Material;
import org.bukkit.entity.Player;
import org. bukkit.event. inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java. util.*;

/**
 * 펫 장비 메뉴 GUI
 * 펫 장비 장착, 해제, 확인
 */
public class PetEquipmentMenu {

    private final PetCore plugin;
    private static final int MENU_SIZE = 54;

    // 현재 보고 있는 펫
    private final Map<UUID, UUID> viewingPet = new HashMap<>();

    public PetEquipmentMenu(PetCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 장비 메뉴 열기
     */
    public void open(Player player, Pet pet) {
        viewingPet.put(player.getUniqueId(), pet.getPetId());

        String title = "§9§l" + pet.getPetName() + " §7장비";

        PetMenuHolder holder = new PetMenuHolder(plugin, PetMenuHolder.MenuType. EQUIPMENT);
        Inventory inventory = Bukkit.createInventory(holder, MENU_SIZE, title);

        setupMenuItems(inventory, player, pet);

        player.openInventory(inventory);
    }

    /**
     * 메뉴 아이템 설정
     */
    private void setupMenuItems(Inventory inventory, Player player, Pet pet) {
        // 배경
        ItemStack background = new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 0; i < MENU_SIZE; i++) {
            inventory.setItem(i, background);
        }

        Map<PetEquipSlot, PetEquipmentData> equipment = pet. getEquipment();

        // === 펫 정보 (중앙) ===
        inventory.setItem(13, createPetDisplayItem(pet));

        // === 장비 슬롯 배치 (펫 주변) ===
        
        // 머리 (상단)
        inventory.setItem(4, createEquipSlotItem(PetEquipSlot.HEAD, equipment.get(PetEquipSlot. HEAD)));

        // 목걸이 (좌상단)
        inventory.setItem(11, createEquipSlotItem(PetEquipSlot. NECK, equipment. get(PetEquipSlot. NECK)));

        // 갑옷 (우상단)
        inventory.setItem(15, createEquipSlotItem(PetEquipSlot. ARMOR, equipment.get(PetEquipSlot.ARMOR)));

        // 무기 (좌측)
        inventory.setItem(20, createEquipSlotItem(PetEquipSlot. WEAPON, equipment.get(PetEquipSlot.WEAPON)));

        // 방패 (우측)
        inventory.setItem(24, createEquipSlotItem(PetEquipSlot. SHIELD, equipment. get(PetEquipSlot. SHIELD)));

        // 발 (하단)
        inventory.setItem(22, createEquipSlotItem(PetEquipSlot. FEET, equipment. get(PetEquipSlot. FEET)));

        // 악세서리 1 (좌하단)
        inventory.setItem(29, createEquipSlotItem(PetEquipSlot. ACCESSORY_1, equipment.get(PetEquipSlot.ACCESSORY_1)));

        // 악세서리 2 (우하단)
        inventory.setItem(33, createEquipSlotItem(PetEquipSlot. ACCESSORY_2, equipment.get(PetEquipSlot.ACCESSORY_2)));

        // === 장비 스탯 요약 (오른쪽) ===
        inventory.setItem(16, createStatSummaryItem(pet));

        // === 인벤토리 영역 (하단) - 장착할 아이템 선택 ===
        inventory. setItem(36, new ItemBuilder(Material. CHEST)
                .name("§e장비 인벤토리")
                .lore(
                        "§7장착 가능한 장비를 표시합니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // === 하단 버튼 ===

        // 모든 장비 해제
        inventory. setItem(47, new ItemBuilder(Material.BARRIER)
                .name("§c모든 장비 해제")
                .lore(
                        "§7장착된 모든 장비를 해제합니다.",
                        "",
                        "§eShift+클릭으로 해제"
                )
                .build());

        // 장비 세트 정보
        inventory.setItem(49, new ItemBuilder(Material. BOOK)
                .name("§e장비 세트")
                .lore(
                        "§7활성화된 세트 효과를 확인합니다.",
                        "",
                        "§e클릭하여 확인"
                )
                .build());

        // 자동 장착
        inventory.setItem(51, new ItemBuilder(Material. HOPPER)
                .name("§a최적 장비 자동 장착")
                .lore(
                        "§7인벤토리에서 최적의 장비를",
                        "§7자동으로 장착합니다.",
                        "",
                        "§e클릭하여 실행"
                )
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
     * 펫 표시 아이템
     */
    private ItemStack createPetDisplayItem(Pet pet) {
        List<String> lore = new ArrayList<>();
        lore.add(pet.getRarity().getColoredName());
        lore.add("§7레벨: §f" + pet. getLevel());
        lore.add("");
        
        // 장비로 인한 스탯 증가 표시
        Map<String, Double> equipStats = calculateEquipmentStats(pet);
        if (!equipStats.isEmpty()) {
            lore.add("§e§l[ 장비 스탯 ]");
            for (Map.Entry<String, Double> stat : equipStats.entrySet()) {
                if (stat.getValue() != 0) {
                    lore.add("§7" + stat.getKey() + ": §a+" + String.format("%.1f", stat.getValue()));
                }
            }
        }

        return new ItemBuilder(Material.ARMOR_STAND)
                .name(pet.getRarity().getColorCode() + "§l" + pet.getPetName())
                .lore(lore)
                .build();
    }

    /**
     * 장비 슬롯 아이템 생성
     */
    private ItemStack createEquipSlotItem(PetEquipSlot slot, PetEquipmentData equipment) {
        if (equipment != null && equipment.getItemId() != null) {
            // 장착된 장비 표시
            List<String> lore = new ArrayList<>();
            lore.add("§7" + slot.getDisplayName());
            lore.add("");

            // 스탯 보너스
            if (!equipment. getStatBonuses().isEmpty()) {
                for (Map.Entry<String, Double> stat : equipment.getStatBonuses().entrySet()) {
                    String color = stat.getValue() >= 0 ? "§a+" : "§c";
                    lore.add("§7" + stat.getKey() + ": " + color + String. format("%.1f", stat.getValue()));
                }
                lore.add("");
            }

            // 등급
            lore.add("§7등급: " + equipment.getRarity().getColoredName());

            // 내구도
            if (equipment.getDurability() < equipment.getMaxDurability()) {
                double durPercent = (double) equipment.getDurability() / equipment.getMaxDurability() * 100;
                String durColor = durPercent > 50 ? "§a" : (durPercent > 20 ? "§e" : "§c");
                lore.add("§7내구도: " + durColor + equipment.getDurability() + "/" + equipment.getMaxDurability());
            }

            lore.add("");
            lore.add("§e좌클릭:  상세 정보");
            lore. add("§e우클릭: 장비 해제");

            Material material = getEquipmentMaterial(slot, equipment);

            return new ItemBuilder(material)
                    .name(equipment.getRarity().getColorCode() + equipment.getDisplayName())
                    .lore(lore)
                    .glow(equipment.getRarity().ordinal() >= 3) // RARE 이상
                    .build();
        } else {
            // 빈 슬롯
            return new ItemBuilder(getEmptySlotMaterial(slot))
                    .name("§7" + slot.getDisplayName() + " §8(비어있음)")
                    .lore(
                            "§7장비가 장착되지 않았습니다.",
                            "",
                            "§e클릭하여 장비 선택"
                    )
                    . build();
        }
    }

    /**
     * 스탯 요약 아이템
     */
    private ItemStack createStatSummaryItem(Pet pet) {
        Map<String, Double> equipStats = calculateEquipmentStats(pet);
        Map<String, Double> totalStats = pet.calculateTotalStats();

        List<String> lore = new ArrayList<>();
        lore.add("§e§l[ 기본 스탯 ]");
        lore.add("§c⚔ 공격력: §f" + String. format("%.1f", pet.getBaseStats().getOrDefault("attack", 0. 0)));
        lore.add("§9🛡 방어력: §f" + String. format("%.1f", pet.getBaseStats().getOrDefault("defense", 0.0)));
        lore.add("§a💨 속도: §f" + String.format("%.1f", pet.getBaseStats().getOrDefault("speed", 0.0)));
        lore.add("");

        if (!equipStats.isEmpty()) {
            lore.add("§b§l[ 장비 보너스 ]");
            for (Map.Entry<String, Double> stat : equipStats.entrySet()) {
                if (stat.getValue() != 0) {
                    String color = stat.getValue() >= 0 ? "§a+" : "§c";
                    lore.add("§7" + stat.getKey() + ": " + color + String.format("%.1f", stat.getValue()));
                }
            }
            lore.add("");
        }

        lore.add("§6§l[ 최종 스탯 ]");
        lore.add("§c⚔ 공격력: §f" + String.format("%. 1f", totalStats.getOrDefault("attack", 0.0)));
        lore. add("§9🛡 방어력: §f" + String.format("%.1f", totalStats.getOrDefault("defense", 0.0)));
        lore.add("§a💨 속도: §f" + String.format("%.1f", totalStats.getOrDefault("speed", 0.0)));

        return new ItemBuilder(Material.DIAMOND)
                .name("§b스탯 요약")
                .lore(lore)
                .build();
    }

    /**
     * 장비 스탯 계산
     */
    private Map<String, Double> calculateEquipmentStats(Pet pet) {
        Map<String, Double> stats = new HashMap<>();

        for (PetEquipmentData equipment : pet. getEquipment().values()) {
            if (equipment != null) {
                for (Map.Entry<String, Double> stat : equipment.getStatBonuses().entrySet()) {
                    stats.merge(stat.getKey(), stat.getValue(), Double::sum);
                }
            }
        }

        return stats;
    }

    /**
     * 장비 Material 가져오기
     */
    private Material getEquipmentMaterial(PetEquipSlot slot, PetEquipmentData equipment) {
        switch (slot) {
            case HEAD:  return Material.DIAMOND_HELMET;
            case NECK: return Material. GOLDEN_CARROT;
            case ARMOR: return Material.DIAMOND_CHESTPLATE;
            case WEAPON: return Material. DIAMOND_SWORD;
            case SHIELD: return Material. SHIELD;
            case FEET: return Material. DIAMOND_BOOTS;
            case ACCESSORY_1:
            case ACCESSORY_2: return Material.EMERALD;
            default: return Material.IRON_INGOT;
        }
    }

    /**
     * 빈 슬롯 Material
     */
    private Material getEmptySlotMaterial(PetEquipSlot slot) {
        switch (slot) {
            case HEAD:  return Material.LEATHER_HELMET;
            case NECK:  return Material.STRING;
            case ARMOR: return Material. LEATHER_CHESTPLATE;
            case WEAPON: return Material.WOODEN_SWORD;
            case SHIELD: return Material. OAK_PLANKS;
            case FEET: return Material.LEATHER_BOOTS;
            case ACCESSORY_1:
            case ACCESSORY_2: return Material. GRAY_DYE;
            default: return Material. BARRIER;
        }
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
        ClickType clickType = event. getClick();

        UUID petId = viewingPet.get(player.getUniqueId());
        if (petId == null) return;

        Pet pet = plugin. getPetManager().getPetById(player.getUniqueId(), petId);
        if (pet == null) {
            player.closeInventory();
            return;
        }

        // 장비 슬롯 매핑
        Map<Integer, PetEquipSlot> slotMapping = new HashMap<>();
        slotMapping.put(4, PetEquipSlot.HEAD);
        slotMapping.put(11, PetEquipSlot.NECK);
        slotMapping.put(15, PetEquipSlot.ARMOR);
        slotMapping.put(20, PetEquipSlot.WEAPON);
        slotMapping.put(24, PetEquipSlot.SHIELD);
        slotMapping.put(22, PetEquipSlot.FEET);
        slotMapping.put(29, PetEquipSlot.ACCESSORY_1);
        slotMapping. put(33, PetEquipSlot.ACCESSORY_2);

        // 장비 슬롯 클릭
        if (slotMapping. containsKey(slot)) {
            PetEquipSlot equipSlot = slotMapping.get(slot);
            PetEquipmentData equipment = pet.getEquipment().get(equipSlot);

            if (clickType == ClickType.RIGHT) {
                // 장비 해제
                if (equipment != null) {
                    if (plugin.getPetEquipmentManager().unequipItem(player, pet, equipSlot)) {
                        open(player, pet);
                    }
                }
            } else {
                // 장비 선택 메뉴 열기
                plugin.getGUIManager().openEquipmentSelectMenu(player, pet, equipSlot);
            }
            return;
        }

        switch (slot) {
            case 36: // 장비 인벤토리
                plugin.getGUIManager().openEquipmentInventoryMenu(player, pet);
                break;

            case 47: // 모든 장비 해제
                if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                    for (PetEquipSlot eqSlot : PetEquipSlot.values()) {
                        plugin.getPetEquipmentManager().unequipItem(player, pet, eqSlot);
                    }
                    open(player, pet);
                }
                break;

            case 49: // 세트 정보
                showSetBonusInfo(player, pet);
                break;

            case 51: // 자동 장착
                plugin.getPetEquipmentManager().autoEquipBest(player, pet);
                open(player, pet);
                break;

            case 45: // 뒤로가기
                plugin.getGUIManager().openPetInfoMenu(player, pet);
                break;

            case 53: // 새로고침
                open(player, pet);
                break;
        }
    }

    /**
     * 세트 보너스 정보 표시
     */
    private void showSetBonusInfo(Player player, Pet pet) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n§9§l===== 장비 세트 효과 =====\n\n");

        Map<String, Integer> setPieces = plugin.getPetEquipmentManager().getEquippedSetPieces(pet);

        if (setPieces. isEmpty()) {
            sb.append("§7장착된 세트 장비가 없습니다.\n");
        } else {
            for (Map.Entry<String, Integer> set : setPieces. entrySet()) {
                String setName = set. getKey();
                int pieces = set.getValue();

                sb.append("§e").append(setName).append(" §7(").append(pieces).append("개)\n");

                // 세트 보너스 표시
                Map<Integer, Map<String, Double>> setBonuses = 
                        plugin.getPetEquipmentManager().getSetBonuses(setName);

                for (Map.Entry<Integer, Map<String, Double>> bonus :  setBonuses.entrySet()) {
                    String status = pieces >= bonus.getKey() ? "§a✓" : "§c✗";
                    sb.append("  ").append(status).append(" §7").append(bonus.getKey()).append("세트:  ");

                    for (Map.Entry<String, Double> stat :  bonus.getValue().entrySet()) {
                        sb.append(stat.getKey()).append("+").append(String.format("%.0f", stat.getValue())).append(" ");
                    }
                    sb.append("\n");
                }
                sb.append("\n");
            }
        }

        plugin.getMessageUtil().sendMessage(player, sb.toString());
    }

    /**
     * 정리
     */
    public void cleanup(UUID playerId) {
        viewingPet.remove(playerId);
    }
}