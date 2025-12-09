package com.multiverse.party.gui;

import com.multiverse.party.PartyCore;
import com.multiverse. party.gui.GUIManager.GUIType;
import com.multiverse.party.gui.GUIManager.PartyGUIHolder;
import com.multiverse. party.models.Party;
import com. multiverse.party. models.PartySkill;
import com.multiverse.party.utils.ColorUtil;
import com.multiverse.party.utils. ItemUtil;
import org.bukkit. Bukkit;
import org.bukkit.Material;
import org.bukkit. entity.Player;
import org.bukkit.inventory.Inventory;
import org. bukkit.inventory. ItemStack;

import java.util.ArrayList;
import java. util.List;
import java.util. UUID;

public class PartySkillGUI implements PartyGUIHolder {

    private final PartyCore plugin;
    private final Player player;
    private final Party party;
    private final int page;
    private Inventory inventory;
    private List<PartySkill> allSkills;

    private static final int ITEMS_PER_PAGE = 21;
    private static final int[] SKILL_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public static final int SLOT_PREV_PAGE = 45;
    public static final int SLOT_INFO = 49;
    public static final int SLOT_NEXT_PAGE = 53;
    public static final int SLOT_BACK = 48;

    public PartySkillGUI(PartyCore plugin, Player player, Party party, int page) {
        this.plugin = plugin;
        this.player = player;
        this.party = party;
        this.page = Math.max(1, page);
        this.allSkills = new ArrayList<>();
    }

    public void open() {
        String title = ColorUtil.colorize(plugin.getConfigUtil().getGUIConfig()
                .getString("party-skill.title", "&8파티 스킬"));
        int rows = plugin.getConfigUtil().getGUIConfig().getInt("party-skill.rows", 6);

        inventory = Bukkit.createInventory(this, rows * 9, title);

        fillBackground();
        loadSkills();
        displaySkills();
        setNavigationItems();

        player.openInventory(inventory);
    }

    private void fillBackground() {
        ItemStack filler = ItemUtil. createItem(Material. GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < inventory. getSize(); i++) {
            inventory. setItem(i, filler);
        }

        ItemStack border = ItemUtil.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, border);
            inventory.setItem(inventory.getSize() - 9 + i, border);
        }
        for (int i = 0; i < inventory.getSize(); i += 9) {
            inventory.setItem(i, border);
            inventory.setItem(i + 8, border);
        }
    }

    private void loadSkills() {
        allSkills = new ArrayList<>(plugin.getSkillRegistry().getAllSkills());

        // 습득한 스킬 먼저, 그 다음 습득 가능한 스킬, 마지막으로 잠긴 스킬
        List<String> learnedSkillIds = party.getPartyLevel() != null ? 
                party.getPartyLevel().getLearnedSkills() : new ArrayList<>();
        int partyLevel = plugin. getPartyLevelManager().getPartyLevel(party);

        allSkills.sort((a, b) -> {
            boolean aLearned = learnedSkillIds. contains(a.getSkillId());
            boolean bLearned = learnedSkillIds.contains(b.getSkillId());
            boolean aCanLearn = ! aLearned && a.getRequiredLevel() <= partyLevel;
            boolean bCanLearn = ! bLearned && b.getRequiredLevel() <= partyLevel;

            if (aLearned && ! bLearned) return -1;
            if (!aLearned && bLearned) return 1;
            if (aCanLearn && !bCanLearn) return -1;
            if (! aCanLearn && bCanLearn) return 1;
            return Integer.compare(a. getRequiredLevel(), b.getRequiredLevel());
        });
    }

    private void displaySkills() {
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allSkills.size());

        List<String> learnedSkillIds = party.getPartyLevel() != null ?
                party. getPartyLevel().getLearnedSkills() : new ArrayList<>();

        for (int i = 0; i < SKILL_SLOTS.length; i++) {
            int skillIndex = startIndex + i;

            if (skillIndex < endIndex) {
                PartySkill skill = allSkills.get(skillIndex);
                boolean isLearned = learnedSkillIds.contains(skill.getSkillId());
                inventory.setItem(SKILL_SLOTS[i], createSkillItem(skill, isLearned));
            } else {
                inventory.setItem(SKILL_SLOTS[i], ItemUtil.createItem(Material.AIR, " "));
            }
        }
    }

    private ItemStack createSkillItem(PartySkill skill, boolean isLearned) {
        List<String> lore = new ArrayList<>();

        int partyLevel = plugin.getPartyLevelManager().getPartyLevel(party);
        int availablePoints = plugin.getPartyLevelManager().getAvailableSkillPoints(party);
        boolean canLearn = !isLearned && partyLevel >= skill.getRequiredLevel() && availablePoints >= skill.getCost();

        // 스킬 설명
        lore.add("&7" + skill.getDescription());
        lore.add("");

        // 요구사항
        String levelReqColor = partyLevel >= skill.getRequiredLevel() ? "&a" : "&c";
        lore.add("&7필요 레벨:  " + levelReqColor + skill.getRequiredLevel());

        String pointReqColor = availablePoints >= skill.getCost() ? "&a" : "&c";
        lore. add("&7필요 포인트: " + pointReqColor + skill. getCost());

        // 쿨다운
        if (skill.getCooldown() > 0) {
            lore. add("&7쿨다운:  &f" + skill.getCooldown() + "초");
        }

        lore.add("");

        // 효과
        if (skill.getEffects() != null && ! skill.getEffects().isEmpty()) {
            lore.add("&7효과:");
            for (String effect : skill.getEffects()) {
                lore.add("&a  • " + effect);
            }
            lore.add("");
        }

        // 상태 및 액션
        if (isLearned) {
            lore. add("&a✔ 습득 완료");
            lore.add("");
            lore.add("&e클릭하여 스킬 사용");
        } else if (canLearn) {
            lore.add("&e○ 습득 가능");
            lore.add("");
            lore. add("&a클릭하여 습득");
        } else {
            lore.add("&c✘ 습득 불가");
            if (partyLevel < skill.getRequiredLevel()) {
                lore.add("&7  (레벨 부족)");
            }
            if (availablePoints < skill.getCost()) {
                lore.add("&7  (포인트 부족)");
            }
        }

        Material material;
        if (isLearned) {
            material = Material.ENCHANTED_BOOK;
        } else if (canLearn) {
            material = Material.BOOK;
        } else {
            material = Material.BARRIER;
        }

        String nameColor = isLearned ? "&a" : (canLearn ? "&e" : "&c");
        return ItemUtil.createItem(material, nameColor + skill.getName(), lore);
    }

    private void setNavigationItems() {
        int totalPages = (int) Math.ceil((double) allSkills.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        // 이전 페이지
        if (page > 1) {
            List<String> lore = new ArrayList<>();
            lore. add("&7현재 페이지: " + page);
            lore.add("");
            lore. add("&e클릭하여 이전 페이지");
            inventory.setItem(SLOT_PREV_PAGE, ItemUtil. createItem(Material. ARROW, "&a이전 페이지", lore));
        } else {
            inventory.setItem(SLOT_PREV_PAGE, ItemUtil. createItem(Material. GRAY_STAINED_GLASS_PANE, " "));
        }

        // 다음 페이지
        if (page < totalPages) {
            List<String> lore = new ArrayList<>();
            lore. add("&7현재 페이지: " + page);
            lore. add("");
            lore.add("&e클릭하여 다음 페이지");
            inventory.setItem(SLOT_NEXT_PAGE, ItemUtil.createItem(Material.ARROW, "&a다음 페이지", lore));
        } else {
            inventory.setItem(SLOT_NEXT_PAGE, ItemUtil. createItem(Material. GRAY_STAINED_GLASS_PANE, " "));
        }

        // 정보
        List<String> infoLore = new ArrayList<>();
        int partyLevel = plugin. getPartyLevelManager().getPartyLevel(party);
        int availablePoints = plugin.getPartyLevelManager().getAvailableSkillPoints(party);
        List<String> learnedSkills = party.getPartyLevel() != null ? 
                party.getPartyLevel().getLearnedSkills() : new ArrayList<>();

        infoLore.add("&7페이지:  &f" + page + "/" + totalPages);
        infoLore.add("");
        infoLore.add("&7파티 레벨: &e" + partyLevel);
        infoLore.add("&7사용 가능 포인트: &a" + availablePoints);
        infoLore.add("&7습득한 스킬:  &f" + learnedSkills.size() + "/" + allSkills.size());

        inventory.setItem(SLOT_INFO, ItemUtil.createItem(Material. NETHER_STAR, "&e스킬 정보", infoLore));

        // 뒤로 가기
        List<String> backLore = new ArrayList<>();
        backLore.add("&7파티 메뉴로 돌아갑니다.");
        inventory.setItem(SLOT_BACK, ItemUtil. createItem(Material. DARK_OAK_DOOR, "&c뒤로 가기", backLore));
    }

    public PartySkill getSkillAtSlot(int slot) {
        for (int i = 0; i < SKILL_SLOTS.length; i++) {
            if (SKILL_SLOTS[i] == slot) {
                int skillIndex = (page - 1) * ITEMS_PER_PAGE + i;
                if (skillIndex < allSkills.size()) {
                    return allSkills.get(skillIndex);
                }
            }
        }
        return null;
    }

    public boolean isSkillSlot(int slot) {
        for (int skillSlot : SKILL_SLOTS) {
            if (skillSlot == slot) return true;
        }
        return false;
    }

    public void handleSkillClick(PartySkill skill) {
        if (skill == null) return;

        List<String> learnedSkillIds = party.getPartyLevel() != null ?
                party. getPartyLevel().getLearnedSkills() : new ArrayList<>();
        boolean isLearned = learnedSkillIds.contains(skill.getSkillId());

        if (isLearned) {
            // 스킬 사용
            boolean success = plugin.getPartySkillManager().useSkill(party, player, skill. getSkillId());
            if (success) {
                player.closeInventory();
            }
        } else {
            // 스킬 습득 시도
            int partyLevel = plugin.getPartyLevelManager().getPartyLevel(party);
            int availablePoints = plugin.getPartyLevelManager().getAvailableSkillPoints(party);

            if (partyLevel < skill.getRequiredLevel()) {
                player.sendMessage(plugin.getMessageUtil().getMessage("skill. level-required",
                        "%level%", String.valueOf(skill. getRequiredLevel())));
                return;
            }

            if (availablePoints < skill.getCost()) {
                player.sendMessage(plugin.getMessageUtil().getMessage("skill.not-enough-points"));
                return;
            }

            boolean success = plugin.getPartySkillManager().learnSkill(party, skill.getSkillId());
            if (success) {
                player.sendMessage(plugin.getMessageUtil().getMessage("skill.learned",
                        "%skill%", skill.getName()));
                plugin.getPartyChatManager().sendNotification(party,
                        plugin. getMessageUtil().getMessage("skill.party-learned",
                                "%player%", player.getName(),
                                "%skill%", skill. getName()));
                open(); // GUI 새로고침
            } else {
                player.sendMessage(plugin.getMessageUtil().getMessage("skill.learn-failed"));
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public GUIType getGUIType() {
        return GUIType.PARTY_SKILL;
    }

    @Override
    public UUID getPartyId() {
        return party.getPartyId();
    }

    public Party getParty() {
        return party;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return Math.max(1, (int) Math.ceil((double) allSkills.size() / ITEMS_PER_PAGE));
    }
}