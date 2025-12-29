package com.multiverse.pet.gui;

import com.multiverse. pet.PetCore;
import com. multiverse.pet. gui.holder.PetMenuHolder;
import com.multiverse.pet.model.Pet;
import com.multiverse. pet.model.skill.PetSkill;
import com.multiverse.pet.model. skill.SkillType;
import com.multiverse.pet.util.ItemBuilder;
import org.bukkit. Bukkit;
import org.bukkit. Material;
import org. bukkit.entity. Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * 펫 스킬 관리 메뉴 GUI
 * 스킬 확인, 사용, 강화
 */
public class PetSkillMenu {

    private final PetCore plugin;
    private static final int MENU_SIZE = 54;

    // 현재 보고 있는 펫
    private final Map<UUID, UUID> viewingPet = new HashMap<>();

    public PetSkillMenu(PetCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 스킬 메뉴 열기
     */
    public void open(Player player, Pet pet) {
        viewingPet.put(player.getUniqueId(), pet.getPetId());

        String title = "§b§l" + pet.getPetName() + " §7스킬";

        PetMenuHolder holder = new PetMenuHolder(plugin, PetMenuHolder.MenuType.SKILL);
        Inventory inventory = Bukkit.createInventory(holder, MENU_SIZE, title);

        setupMenuItems(inventory, player, pet);

        player.openInventory(inventory);
    }

    /**
     * 메뉴 아이템 설정
     */
    private void setupMenuItems(Inventory inventory, Player player, Pet pet) {
        // 배경
        ItemStack background = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 0; i < MENU_SIZE; i++) {
            inventory.setItem(i, background);
        }

        List<PetSkill> skills = pet. getSkills();
        int maxSlots = pet.getRarity().getSkillSlots();

        // === 펫 정보 (상단 중앙) ===
        inventory.setItem(4, createPetInfoItem(pet));

        // === 스킬 포인트 정보 ===
        inventory.setItem(0, new ItemBuilder(Material. EXPERIENCE_BOTTLE)
                .name("§a스킬 포인트")
                .lore(
                        "§7보유:  §f" + pet.getSkillPoints() + " 포인트",
                        "",
                        "§7레벨업 시 1포인트 획득",
                        "§7스킬 강화에 사용됩니다."
                )
                .build());

        // === 스킬 슬롯 ===
        // 액티브 스킬 (상단)
        int[] activeSlots = {10, 11, 12, 13, 14, 15, 16};
        // 패시브 스킬 (중단)
        int[] passiveSlots = {28, 29, 30, 31, 32, 33, 34};

        List<PetSkill> activeSkills = new ArrayList<>();
        List<PetSkill> passiveSkills = new ArrayList<>();

        for (PetSkill skill :  skills) {
            if (skill.isPassive()) {
                passiveSkills. add(skill);
            } else {
                activeSkills. add(skill);
            }
        }

        // 액티브 스킬 배치
        inventory.setItem(9, new ItemBuilder(Material. DIAMOND_SWORD)
                .name("§c§l액티브 스킬")
                .lore("§7직접 사용하는 스킬입니다.")
                .build());

        for (int i = 0; i < activeSlots.length; i++) {
            if (i < activeSkills.size()) {
                inventory.setItem(activeSlots[i], createSkillItem(activeSkills.get(i), pet));
            } else if (i < maxSlots / 2) {
                inventory.setItem(activeSlots[i], createEmptySlotItem());
            } else {
                inventory.setItem(activeSlots[i], createLockedSlotItem());
            }
        }

        // 패시브 스킬 배치
        inventory.setItem(27, new ItemBuilder(Material. BOOK)
                .name("§b§l패시브 스킬")
                .lore("§7자동으로 적용되는 스킬입니다.")
                .build());

        for (int i = 0; i < passiveSlots.length; i++) {
            if (i < passiveSkills.size()) {
                inventory.setItem(passiveSlots[i], createSkillItem(passiveSkills.get(i), pet));
            } else if (i < maxSlots / 2) {
                inventory.setItem(passiveSlots[i], createEmptySlotItem());
            } else {
                inventory.setItem(passiveSlots[i], createLockedSlotItem());
            }
        }

        // === 스킬 정보 영역 (오른쪽) ===
        inventory.setItem(24, new ItemBuilder(Material. WRITABLE_BOOK)
                .name("§e스킬 도움말")
                .lore(
                        "§7§l[스킬 사용법]",
                        "§7좌클릭:  스킬 사용 (액티브)",
                        "§7우클릭: 스킬 상세 정보",
                        "§7Shift+클릭: 스킬 강화",
                        "",
                        "§7§l[스킬 타입]",
                        "§c공격 - 적에게 피해",
                        "§b방어 - 방어력 증가",
                        "§a버프 - 아군 강화",
                        "§5디버프 - 적 약화",
                        "§d힐 - 체력 회복"
                )
                .build());

        // === 배울 수 있는 스킬 ===
        List<String> learnableSkills = plugin.getPetSkillManager().getLearnableSkills(pet);
        if (!learnableSkills.isEmpty()) {
            inventory.setItem(42, new ItemBuilder(Material. KNOWLEDGE_BOOK)
                    .name("§a§l새 스킬 배우기")
                    .lore(
                            "§7배울 수 있는 스킬:  §f" + learnableSkills.size() + "개",
                            "",
                            "§e클릭하여 확인"
                    )
                    .glow(true)
                    . build());
        } else {
            inventory.setItem(42, new ItemBuilder(Material.BARRIER)
                    . name("§7새 스킬 없음")
                    .lore("§7현재 배울 수 있는 스킬이 없습니다.")
                    .build());
        }

        // === 하단 버튼 ===

        // 스킬 초기화
        inventory.setItem(47, new ItemBuilder(Material. CAULDRON)
                .name("§c스킬 초기화")
                .lore(
                        "§7모든 스킬 강화를 초기화합니다.",
                        "§7스킬 포인트가 환불됩니다.",
                        "",
                        "§c비용: §f1000 골드",
                        "",
                        "§e클릭하여 초기화"
                )
                .build());

        // 스킬 프리셋
        inventory.setItem(49, new ItemBuilder(Material.COMPARATOR)
                .name("§e스킬 프리셋")
                .lore(
                        "§7스킬 구성을 저장/불러옵니다.",
                        "",
                        "§e클릭하여 열기"
                )
                .build());

        // 뒤로가기
        inventory.setItem(45, new ItemBuilder(Material.ARROW)
                .name("§7뒤로가기")
                .build());

        // 새로고침
        inventory.setItem(53, new ItemBuilder(Material.SUNFLOWER)
                .name("§e새로고침")
                .build());
    }

    /**
     * 펫 정보 아이템
     */
    private ItemStack createPetInfoItem(Pet pet) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .name(pet.getRarity().getColorCode() + pet.getPetName())
                .lore(
                        "§7레벨:  §f" + pet. getLevel(),
                        "§7스킬 슬롯: §f" + pet. getSkills().size() + "/" + pet.getRarity().getSkillSlots(),
                        "§7스킬 포인트: §f" + pet.getSkillPoints()
                )
                .build();
    }

    /**
     * 스킬 아이템 생성
     */
    private ItemStack createSkillItem(PetSkill skill, Pet pet) {
        List<String> lore = new ArrayList<>();

        // 타입
        SkillType type = skill.getType();
        if (type != null) {
            lore.add(getTypeColor(type) + "[" + type.getDisplayName() + "]");
        }
        lore.add("");

        // 설명
        lore.add("§7" + skill.getDescription());
        lore.add("");

        // 레벨
        lore.add("§7레벨: §f" + skill. getCurrentLevel() + "/" + skill.getMaxLevel());

        // 효과
        if (skill.getEffectValue("damage") > 0) {
            lore.add("§c⚔ 피해: §f" + String.format("%.0f", skill.getEffectValue("damage")));
        }
        if (skill.getEffectValue("healing") > 0) {
            lore.add("§a❤ 회복: §f" + String.format("%.0f", skill.getEffectValue("healing")));
        }
        if (skill.getEffectValue("duration") > 0) {
            lore.add("§b⏱ 지속: §f" + String. format("%.0f", skill.getEffectValue("duration")) + "초");
        }

        // 쿨다운
        if (! skill.isPassive()) {
            lore.add("");
            if (skill.isOnCooldown()) {
                lore. add("§c쿨타임: " + skill.getRemainingCooldownSeconds() + "초 남음");
            } else {
                lore.add("§a쿨타임: " + skill. getCooldown() + "초");
            }
        }

        // 강화 정보
        if (! skill.isMaxLevel()) {
            lore.add("");
            lore.add("§e§l[강화 정보]");
            lore.add("§7필요 포인트: §f" + skill. getUpgradeCost());
            if (pet. getSkillPoints() >= skill.getUpgradeCost()) {
                lore. add("§a강화 가능!");
            } else {
                lore. add("§c포인트 부족");
            }
        } else {
            lore.add("");
            lore.add("§6§l최대 레벨 달성!");
        }

        lore.add("");
        if (! skill.isPassive()) {
            lore.add("§e좌클릭: 스킬 사용");
        }
        lore.add("§e우클릭: 상세 정보");
        if (! skill.isMaxLevel()) {
            lore.add("§eShift+클릭: 강화");
        }

        Material material = getSkillMaterial(skill);

        ItemBuilder builder = new ItemBuilder(material)
                .name(getTypeColor(type) + skill.getName())
                .lore(lore);

        if (! skill.isOnCooldown() && ! skill.isPassive()) {
            builder.glow(true);
        }

        return builder.build();
    }

    /**
     * 빈 슬롯 아이템
     */
    private ItemStack createEmptySlotItem() {
        return new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .name("§7빈 스킬 슬롯")
                .lore("§7새 스킬을 배울 수 있습니다.")
                .build();
    }

    /**
     * 잠긴 슬롯 아이템
     */
    private ItemStack createLockedSlotItem() {
        return new ItemBuilder(Material. BARRIER)
                .name("§c잠긴 슬롯")
                .lore(
                        "§7더 높은 희귀도가 필요합니다.",
                        "§7진화로 희귀도를 올리세요."
                )
                .build();
    }

    /**
     * 타입별 색상
     */
    private String getTypeColor(SkillType type) {
        if (type == null) return "§f";

        switch (type) {
            case ATTACK:  return "§c";
            case DEFENSE: return "§b";
            case BUFF: return "§a";
            case DEBUFF: return "§5";
            case HEAL: return "§d";
            case GATHERING: return "§e";
            case SUPPORT: return "§9";
            case SPECIAL: 
            case ULTIMATE: return "§6";
            default: return "§f";
        }
    }

    /**
     * 스킬 Material
     */
    private Material getSkillMaterial(PetSkill skill) {
        if (skill.isPassive()) {
            return Material.BOOK;
        }

        SkillType type = skill.getType();
        if (type == null) return Material.PAPER;

        switch (type) {
            case ATTACK: return Material.IRON_SWORD;
            case DEFENSE:  return Material.SHIELD;
            case BUFF: return Material. GOLDEN_APPLE;
            case DEBUFF: return Material.POISONOUS_POTATO;
            case HEAL: return Material. GLISTERING_MELON_SLICE;
            case GATHERING: return Material. IRON_PICKAXE;
            case SUPPORT: return Material.TOTEM_OF_UNDYING;
            case SPECIAL:  return Material.BLAZE_POWDER;
            case ULTIMATE:  return Material.NETHER_STAR;
            default: return Material.PAPER;
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

        // 스킬 슬롯 확인
        int[] activeSlots = {10, 11, 12, 13, 14, 15, 16};
        int[] passiveSlots = {28, 29, 30, 31, 32, 33, 34};

        List<PetSkill> activeSkills = new ArrayList<>();
        List<PetSkill> passiveSkills = new ArrayList<>();

        for (PetSkill skill : pet.getSkills()) {
            if (skill. isPassive()) {
                passiveSkills. add(skill);
            } else {
                activeSkills. add(skill);
            }
        }

        // 액티브 스킬 클릭
        for (int i = 0; i < activeSlots.length; i++) {
            if (activeSlots[i] == slot && i < activeSkills. size()) {
                handleSkillClick(player, pet, activeSkills.get(i), clickType);
                return;
            }
        }

        // 패시브 스킬 클릭
        for (int i = 0; i < passiveSlots.length; i++) {
            if (passiveSlots[i] == slot && i < passiveSkills. size()) {
                handleSkillClick(player, pet, passiveSkills.get(i), clickType);
                return;
            }
        }

        // 기타 버튼
        switch (slot) {
            case 42:  // 새 스킬 배우기
                plugin.getGUIManager().openLearnSkillMenu(player, pet);
                break;

            case 47: // 스킬 초기화
                if (clickType == ClickType. SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
                    plugin.getPetSkillManager().resetSkills(player, pet);
                    open(player, pet);
                }
                break;

            case 49: // 스킬 프리셋
                plugin. getGUIManager().openSkillPresetMenu(player, pet);
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
     * 스킬 클릭 처리
     */
    private void handleSkillClick(Player player, Pet pet, PetSkill skill, ClickType clickType) {
        if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
            // 강화
            if (! skill.isMaxLevel()) {
                if (plugin.getPetSkillManager().upgradeSkill(player, pet, skill. getSkillId())) {
                    open(player, pet);
                }
            }
        } else if (clickType == ClickType.RIGHT) {
            // 상세 정보
            showSkillDetails(player, skill);
        } else {
            // 사용 (액티브만)
            if (!skill.isPassive() && pet.isActive()) {
                plugin.getPetSkillManager().useSkill(pet, skill. getSkillId(), null);
                open(player, pet);
            } else if (! pet.isActive()) {
                plugin.getMessageUtil().sendMessage(player, 
                    plugin.getConfigManager().getMessage("skill.pet-not-active"));
            }
        }
    }

    /**
     * 스킬 상세 정보 표시
     */
    private void showSkillDetails(Player player, PetSkill skill) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n§b§l===== ").append(skill.getName()).append(" =====\n\n");
        sb.append("§7").append(skill.getDescription()).append("\n\n");

        if (skill. getType() != null) {
            sb.append("§e타입: ").append(getTypeColor(skill. getType())).append(skill.getType().getDisplayName()).append("\n");
        }

        sb.append("§e레벨: §f").append(skill.getCurrentLevel()).append("/").append(skill.getMaxLevel()).append("\n");

        if (! skill.isPassive()) {
            sb.append("§e쿨타임:  §f").append(skill.getCooldown()).append("초\n");
        }

        sb.append("\n§e§l[ 효과 ]\n");
        for (Map.Entry<String, Double> effect : skill. getEffects().entrySet()) {
            sb. append("§7- ").append(effect.getKey()).append(": §f").append(String.format("%.1f", effect.getValue())).append("\n");
        }

        if (! skill.isMaxLevel()) {
            sb.append("\n§e§l[ 다음 레벨 ]\n");
            sb.append("§7필요 포인트: §f").append(skill.getUpgradeCost()).append("\n");
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