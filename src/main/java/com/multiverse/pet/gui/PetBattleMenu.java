package com.multiverse.pet.gui;

import com.multiverse.pet.PetCore;
import com.multiverse. pet.gui.holder.PetMenuHolder;
import com. multiverse.pet. model.Pet;
import com.multiverse.pet.model. battle.BattleType;
import com. multiverse.pet. util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * 펫 배틀 메뉴 GUI
 * 대결 모드 선택, 대결 요청
 */
public class PetBattleMenu {

    private final PetCore plugin;
    private static final int MENU_SIZE = 54;

    public PetBattleMenu(PetCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 배틀 메뉴 열기
     */
    public void open(Player player) {
        String title = "§c§l펫 대결";

        PetMenuHolder holder = new PetMenuHolder(plugin, PetMenuHolder. MenuType.BATTLE);
        Inventory inventory = Bukkit.createInventory(holder, MENU_SIZE, title);

        setupMenuItems(inventory, player);

        player.openInventory(inventory);
    }

    /**
     * 메뉴 아이템 설정
     */
    private void setupMenuItems(Inventory inventory, Player player) {
        UUID playerId = player.getUniqueId();

        // 배경
        ItemStack background = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 0; i < MENU_SIZE; i++) {
            inventory. setItem(i, background);
        }

        // === 레이팅 정보 (상단) ===
        int rating = plugin.getPetRankingManager().getPlayerRating(playerId);
        String rankTitle = plugin.getPetRankingManager().getRankTitle(rating);
        String rankColor = plugin.getPetRankingManager().getRankColor(rating);

        inventory.setItem(4, new ItemBuilder(Material.DIAMOND_SWORD)
                .name("§c§l나의 레이팅")
                .lore(
                        "§7레이팅: §f" + rating,
                        "§7랭크: " + rankTitle,
                        "",
                        "§7승:  §a" + getPlayerWins(playerId),
                        "§7패:  §c" + getPlayerLosses(playerId),
                        "",
                        "§e클릭하여 랭킹 보기"
                )
                .glow(true)
                .build());

        // === 대결 모드 (중앙) ===

        // AI 대결 (쉬움)
        inventory.setItem(19, new ItemBuilder(Material.ZOMBIE_HEAD)
                .name("§a§lAI 대결 (쉬움)")
                .lore(
                        "§7난이도 1의 AI와 대결합니다.",
                        "",
                        "§7보상: §e낮음",
                        "§7레이팅 변화: §7없음",
                        "",
                        "§e클릭하여 시작"
                )
                .build());

        // AI 대결 (보통)
        inventory.setItem(20, new ItemBuilder(Material.SKELETON_SKULL)
                .name("§e§lAI 대결 (보통)")
                .lore(
                        "§7난이도 3의 AI와 대결합니다.",
                        "",
                        "§7보상: §e보통",
                        "§7레이팅 변화: §7없음",
                        "",
                        "§e클릭하여 시작"
                )
                .build());

        // AI 대결 (어려움)
        inventory.setItem(21, new ItemBuilder(Material.WITHER_SKELETON_SKULL)
                .name("§c§lAI 대결 (어려움)")
                .lore(
                        "§7난이도 5의 AI와 대결합니다.",
                        "",
                        "§7보상: §e높음",
                        "§7레이팅 변화: §7없음",
                        "",
                        "§e클릭하여 시작"
                )
                .build());

        // 친선 대결
        inventory.setItem(23, new ItemBuilder(Material.GOLDEN_SWORD)
                .name("§6§l친선 대결")
                .lore(
                        "§7다른 플레이어와 친선 대결을 합니다.",
                        "",
                        "§7레이팅 변화: §7없음",
                        "§7펫 손상: §7없음",
                        "",
                        "§e클릭하여 플레이어 선택"
                )
                .build());

        // 랭크 대결
        inventory. setItem(24, new ItemBuilder(Material.NETHERITE_SWORD)
                .name("§c§l랭크 대결")
                .lore(
                        "§7레이팅을 걸고 대결합니다.",
                        "",
                        "§7레이팅 변화: §a승리 +20 §c/ 패배 -15",
                        "§7추가 보상: §e있음",
                        "",
                        "§e클릭하여 매칭 시작"
                )
                .glow(true)
                .build());

        // 도전 대결
        inventory. setItem(25, new ItemBuilder(Material.DIAMOND)
                .name("§b§l도전 대결")
                .lore(
                        "§7특별 보상을 걸고 대결합니다.",
                        "",
                        "§7참가비: §6100 골드",
                        "§7승리 보상:  §6200 골드 + 아이템",
                        "",
                        "§e클릭하여 매칭 시작"
                )
                .build());

        // === 내 펫 선택 ===
        inventory. setItem(31, new ItemBuilder(Material.CHEST)
                .name("§e대결 펫 선택")
                .lore(
                        "§7대결에 사용할 펫을 선택합니다.",
                        "",
                        "§e클릭하여 선택"
                )
                .build());

        // === 최근 대결 기록 ===
        inventory.setItem(37, createRecentBattlesItem(playerId));

        // === 대결 규칙 ===
        inventory. setItem(43, new ItemBuilder(Material.BOOK)
                .name("§e대결 규칙")
                .lore(
                        "§7§l[기본 규칙]",
                        "§7- 턴 기반 전투",
                        "§7- 최대 30턴",
                        "§7- 턴당 30초 제한",
                        "",
                        "§7§l[행동]",
                        "§7- 공격:  기본 데미지",
                        "§7- 스킬: 특수 효과",
                        "§7- 방어: 데미지 50% 감소",
                        "§7- 항복: 즉시 패배",
                        "",
                        "§7§l[승패 조건]",
                        "§7- 상대 펫 체력 0 = 승리",
                        "§7- 시간 초과 = 체력 비율 판정"
                )
                .build());

        // === 진행 중인 대결 확인 ===
        if (plugin.getPetBattleManager().isInBattle(playerId)) {
            inventory.setItem(22, new ItemBuilder(Material.REDSTONE_TORCH)
                    . name("§c§l대결 진행 중!")
                    .lore(
                            "§7현재 대결이 진행 중입니다.",
                            "",
                            "§e클릭하여 대결로 돌아가기"
                    )
                    .glow(true)
                    .build());
        }

        // === 대기 중인 도전 ===
        List<UUID> pendingChallenges = getPendingChallenges(playerId);
        if (!pendingChallenges.isEmpty()) {
            List<String> challengers = new ArrayList<>();
            for (UUID challengerId : pendingChallenges) {
                Player challenger = Bukkit.getPlayer(challengerId);
                if (challenger != null) {
                    challengers.add("§7- §f" + challenger.getName());
                }
            }

            inventory.setItem(40, new ItemBuilder(Material.BELL)
                    . name("§e§l대결 요청 " + pendingChallenges.size() + "건")
                    . lore(challengers. toArray(new String[0]))
                    .glow(true)
                    .build());
        }

        // === 하단 버튼 ===

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
     * 최근 대결 기록 아이템
     */
    private ItemStack createRecentBattlesItem(UUID playerId) {
        List<String> lore = new ArrayList<>();
        lore. add("");

        // 최근 5경기 결과 (BattleManager에서 가져오기)
        List<String> recentResults = plugin.getPetBattleManager().getRecentBattleResults(playerId, 5);

        if (recentResults.isEmpty()) {
            lore.add("§7최근 대결 기록이 없습니다.");
        } else {
            for (String result : recentResults) {
                lore.add(result);
            }
        }

        lore.add("");
        lore.add("§e클릭하여 전체 기록 보기");

        return new ItemBuilder(Material.WRITABLE_BOOK)
                .name("§e최근 대결 기록")
                .lore(lore)
                .build();
    }

    /**
     * 대기 중인 도전 가져오기
     */
    private List<UUID> getPendingChallenges(UUID playerId) {
        // BattleManager에서 구현
        return new ArrayList<>();
    }

    /**
     * 플레이어 승리 횟수
     */
    private int getPlayerWins(UUID playerId) {
        return plugin.getPetBattleManager().getPlayerWins(playerId);
    }

    /**
     * 플레이어 패배 횟수
     */
    private int getPlayerLosses(UUID playerId) {
        return plugin.getPetBattleManager().getPlayerLosses(playerId);
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

        // 대결 중이면 다른 대결 시작 불가
        if (plugin.getPetBattleManager().isInBattle(playerId)) {
            if (slot == 22) {
                // 대결로 돌아가기
                player.closeInventory();
                plugin.getMessageUtil().sendMessage(player, 
                    plugin.getConfigManager().getMessage("battle.returning"));
            } else {
                plugin.getMessageUtil().sendMessage(player, 
                    plugin.getConfigManager().getMessage("battle.already-in-battle"));
            }
            return;
        }

        // 대결 펫 확인
        Pet battlePet = getBattlePet(player);

        switch (slot) {
            case 4: // 랭킹 보기
                plugin.getGUIManager().openRankingMenu(player);
                break;

            case 19: // AI 쉬움
                if (battlePet != null) {
                    startAIBattle(player, battlePet, 1);
                } else {
                    plugin.getMessageUtil().sendMessage(player, 
                        plugin.getConfigManager().getMessage("battle.select-pet-first"));
                }
                break;

            case 20: // AI 보통
                if (battlePet != null) {
                    startAIBattle(player, battlePet, 3);
                } else {
                    plugin.getMessageUtil().sendMessage(player, 
                        plugin.getConfigManager().getMessage("battle.select-pet-first"));
                }
                break;

            case 21: // AI 어려움
                if (battlePet != null) {
                    startAIBattle(player, battlePet, 5);
                } else {
                    plugin.getMessageUtil().sendMessage(player, 
                        plugin.getConfigManager().getMessage("battle.select-pet-first"));
                }
                break;

            case 23: // 친선 대결
                plugin.getGUIManager().openBattlePlayerSelectMenu(player, BattleType. FRIENDLY);
                break;

            case 24: // 랭크 대결
                if (battlePet != null) {
                    plugin.getPetBattleManager().startRankedMatchmaking(player, battlePet);
                    player.closeInventory();
                } else {
                    plugin.getMessageUtil().sendMessage(player, 
                        plugin.getConfigManager().getMessage("battle. select-pet-first"));
                }
                break;

            case 25: // 도전 대결
                if (battlePet != null) {
                    plugin.getPetBattleManager().startChallengeMatchmaking(player, battlePet);
                    player.closeInventory();
                } else {
                    plugin.getMessageUtil().sendMessage(player, 
                        plugin.getConfigManager().getMessage("battle. select-pet-first"));
                }
                break;

            case 31: // 펫 선택
                plugin.getGUIManager().openBattlePetSelectMenu(player);
                break;

            case 37: // 대결 기록
                showBattleHistory(player);
                break;

            case 40: // 대결 요청
                plugin.getGUIManager().openBattleChallengesMenu(player);
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
     * 대결 펫 가져오기
     */
    private Pet getBattlePet(Player player) {
        // 활성 펫 또는 선택된 대결 펫
        if (plugin.getPetManager().hasActivePet(player. getUniqueId())) {
            return plugin.getPetManager().getActivePet(player.getUniqueId()).getPet();
        }

        // 첫 번째 대결 가능한 펫
        for (Pet pet : plugin.getPetManager().getAllPets(player.getUniqueId())) {
            if (plugin.getPetBattleManager().canBattle(pet)) {
                return pet;
            }
        }

        return null;
    }

    /**
     * AI 대결 시작
     */
    private void startAIBattle(Player player, Pet pet, int difficulty) {
        player.closeInventory();
        plugin.getPetBattleManager().startAIBattle(player, pet, difficulty);
    }

    /**
     * 대결 기록 표시
     */
    private void showBattleHistory(Player player) {
        List<String> history = plugin.getPetBattleManager().getBattleHistory(player.getUniqueId(), 10);

        StringBuilder sb = new StringBuilder();
        sb.append("\n§c§l===== 대결 기록 =====\n\n");

        if (history.isEmpty()) {
            sb. append("§7대결 기록이 없습니다.\n");
        } else {
            for (String record : history) {
                sb.append(record).append("\n");
            }
        }

        plugin.getMessageUtil().sendMessage(player, sb.toString());
    }
}