package com.multiverse.pvp.managers;

import com. multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPRanking;
import com.multiverse.pvp.data.PvPStatistics;
import com.multiverse.pvp.data.PvPTitle;
import com. multiverse.pvp.enums.PvPTier;
import com.multiverse.pvp. enums.TitleCategory;
import com.multiverse.pvp.utils.MessageUtil;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TitleManager {

    private final PvPCore plugin;
    private final Map<String, PvPTitle> registeredTitles;
    private final Map<UUID, Set<String>> unlockedTitles;
    private final Map<UUID, String> activeTitles;

    private boolean enabled;
    private boolean bonusesEnabled;

    public TitleManager(PvPCore plugin) {
        this.plugin = plugin;
        this. registeredTitles = new HashMap<>();
        this.unlockedTitles = new ConcurrentHashMap<>();
        this.activeTitles = new ConcurrentHashMap<>();
        loadConfig();
        registerDefaultTitles();
    }

    private void loadConfig() {
        this.enabled = plugin.getConfig().getBoolean("titles.enabled", true);
        this.bonusesEnabled = plugin.getConfig().getBoolean("titles.bonuses. enabled", true);
    }

    /**
     * 기본 칭호 등록
     */
    private void registerDefaultTitles() {
        // 킬 칭호
        registerTitle(PvPTitle.builder("warrior")
                .displayName("전사")
                .category(TitleCategory. KILLS)
                .requiredKills(100)
                .color("&7")
                .prefix("&7[전사]")
                .rarity(PvPTitle.TitleRarity. COMMON)
                .description("100 킬 달성")
                .unlockDescription("100명의 적을 처치하세요.")
                .build());

        registerTitle(PvPTitle.builder("veteran")
                .displayName("베테랑")
                .category(TitleCategory.KILLS)
                .requiredKills(500)
                .color("&a")
                .prefix("&a[베테랑]")
                .rarity(PvPTitle.TitleRarity.UNCOMMON)
                .description("500 킬 달성")
                .unlockDescription("500명의 적을 처치하세요.")
                .bonus("kill_exp", 0.05)
                .build());

        registerTitle(PvPTitle.builder("elite")
                .displayName("엘리트")
                .category(TitleCategory. KILLS)
                .requiredKills(1000)
                .color("&9")
                .prefix("&9[엘리트]")
                .rarity(PvPTitle.TitleRarity.RARE)
                .description("1000 킬 달성")
                .unlockDescription("1000명의 적을 처치하세요.")
                .bonus("kill_exp", 0.1)
                .bonus("kill_money", 0.05)
                .build());

        registerTitle(PvPTitle.builder("champion")
                .displayName("챔피언")
                .category(TitleCategory. KILLS)
                .requiredKills(5000)
                .color("&6")
                .prefix("&6[챔피언]")
                .rarity(PvPTitle.TitleRarity.EPIC)
                .description("5000 킬 달성")
                .unlockDescription("5000명의 적을 처치하세요.")
                .bonus("kill_exp", 0.15)
                .bonus("kill_money", 0.1)
                .build());

        // 승리 칭호
        registerTitle(PvPTitle.builder("victor")
                .displayName("승리자")
                .category(TitleCategory. WINS)
                .requiredWins(10)
                .color("&e")
                .prefix("&e[승리자]")
                .rarity(PvPTitle. TitleRarity. COMMON)
                .description("10승 달성")
                .unlockDescription("10번 승리하세요.")
                .build());

        registerTitle(PvPTitle. builder("conqueror")
                .displayName("정복자")
                .category(TitleCategory.WINS)
                .requiredWins(50)
                .color("&c")
                .prefix("&c[정복자]")
                .rarity(PvPTitle. TitleRarity. UNCOMMON)
                .description("50승 달성")
                .unlockDescription("50번 승리하세요.")
                .bonus("win_exp", 0.05)
                .build());

        registerTitle(PvPTitle.builder("dominator")
                .displayName("지배자")
                .category(TitleCategory.WINS)
                .requiredWins(100)
                .color("&5")
                .prefix("&5[지배자]")
                .rarity(PvPTitle. TitleRarity. RARE)
                .description("100승 달성")
                .unlockDescription("100번 승리하세요.")
                .bonus("win_exp", 0.1)
                .bonus("win_money", 0.05)
                .build());

        // 스트릭 칭호
        registerTitle(PvPTitle.builder("slayer")
                .displayName("학살자")
                .category(TitleCategory.STREAK)
                .requiredStreak(10)
                .color("&4")
                .prefix("&4[학살자]")
                .rarity(PvPTitle. TitleRarity. RARE)
                .description("10 연속 킬 달성")
                .unlockDescription("10 연속 킬을 달성하세요.")
                .bonus("streak_bonus", 0.1)
                .build());

        registerTitle(PvPTitle.builder("executioner")
                .displayName("처형자")
                .category(TitleCategory.STREAK)
                .requiredStreak(50)
                .color("&d")
                .prefix("&d[처형자]")
                .rarity(PvPTitle. TitleRarity. LEGENDARY)
                .description("50 연속 킬 달성")
                .unlockDescription("50 연속 킬을 달성하세요.")
                .bonus("streak_bonus", 0.2)
                .build());

        // 티어 칭호
        registerTitle(PvPTitle. builder("master_of_combat")
                .displayName("전투의 달인")
                .category(TitleCategory.TIER)
                .requiredTier(PvPTier.MASTER)
                .color("&d")
                .prefix("&d[전투의 달인]")
                .rarity(PvPTitle.TitleRarity.LEGENDARY)
                .description("마스터 티어 달성")
                .unlockDescription("마스터 티어에 도달하세요.")
                .bonus("all_exp", 0.1)
                .bonus("all_money", 0.1)
                .build());

        registerTitle(PvPTitle.builder("gladiator")
                .displayName("검투사")
                .category(TitleCategory. TIER)
                .requiredRank(1)
                .color("&6&l")
                .prefix("&6&l[검투사]")
                .rarity(PvPTitle.TitleRarity.MYTHIC)
                .description("시즌 1위 달성")
                .unlockDescription("시즌 랭킹 1위를 달성하세요.")
                .bonus("all_exp", 0.2)
                .bonus("all_money", 0.2)
                .build());
    }

    /**
     * 칭호 등록
     */
    public void registerTitle(PvPTitle title) {
        registeredTitles.put(title.getTitleId(), title);
    }

    /**
     * 칭호 조회
     */
    public PvPTitle getTitle(String titleId) {
        return registeredTitles. get(titleId);
    }

    /**
     * 모든 칭호 조회
     */
    public List<PvPTitle> getAllTitles() {
        return new ArrayList<>(registeredTitles. values());
    }

    /**
     * 카테고리별 칭호 조회
     */
    public List<PvPTitle> getTitlesByCategory(TitleCategory category) {
        List<PvPTitle> result = new ArrayList<>();
        for (PvPTitle title :  registeredTitles.values()) {
            if (title. getCategory() == category) {
                result.add(title);
            }
        }
        return result;
    }

    /**
     * 칭호 해금 체크
     */
    public void checkTitleUnlock(Player player) {
        if (! enabled) {
            return;
        }

        PvPRanking ranking = plugin.getRankingManager().getRanking(player);
        PvPStatistics stats = plugin. getStatisticsManager().getStatistics(player);

        if (ranking == null || stats == null) {
            return;
        }

        int kills = stats.getTotalKills();
        int wins = stats.getTotalWins();
        int streak = stats.getLongestKillStreak();
        PvPTier tier = ranking.getTier();
        int rank = plugin.getRankingManager().getPlayerRank(player);

        for (PvPTitle title : registeredTitles.values()) {
            if (! title.isEnabled()) {
                continue;
            }

            if (hasTitle(player, title. getTitleId())) {
                continue;
            }

            if (title. meetsAllRequirements(kills, wins, streak, tier, rank)) {
                unlockTitle(player, title. getTitleId());
            }
        }
    }

    /**
     * 칭호 해금
     */
    public void unlockTitle(Player player, String titleId) {
        if (!enabled) {
            return;
        }

        PvPTitle title = registeredTitles. get(titleId);
        if (title == null) {
            return;
        }

        Set<String> playerTitles = unlockedTitles.computeIfAbsent(
                player.getUniqueId(), k -> new HashSet<>());

        if (playerTitles.contains(titleId)) {
            return;
        }

        playerTitles.add(titleId);

        // 해금 메시지
        MessageUtil.sendMessage(player, "&6&l새로운 칭호 해금!");
        MessageUtil. sendMessage(player, "&e" + title.getFormattedName() + " &7- " + title.getDescription());

        // 효과음
        player.playSound(player.getLocation(),
                org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

        // 첫 칭호면 자동 장착
        if (getActiveTitle(player) == null) {
            setActiveTitle(player, titleId);
        }
    }

    /**
     * 활성 칭호 설정
     */
    public void setActiveTitle(Player player, String titleId) {
        if (!enabled) {
            return;
        }

        if (titleId == null) {
            activeTitles.remove(player. getUniqueId());
            MessageUtil.sendMessage(player, "&e칭호를 해제했습니다.");
            return;
        }

        if (! hasTitle(player, titleId)) {
            MessageUtil.sendMessage(player, "&c해금되지 않은 칭호입니다.");
            return;
        }

        PvPTitle title = registeredTitles.get(titleId);
        if (title == null) {
            return;
        }

        activeTitles.put(player.getUniqueId(), titleId);
        MessageUtil.sendMessage(player, "&a칭호가 " + title.getFormattedName() + "&a(으)로 변경되었습니다.");
    }

    /**
     * 활성 칭호 조회
     */
    public String getActiveTitle(Player player) {
        return activeTitles.get(player. getUniqueId());
    }

    /**
     * 활성 칭호 데이터 조회
     */
    public PvPTitle getActiveTitleData(Player player) {
        String titleId = getActiveTitle(player);
        if (titleId == null) {
            return null;
        }
        return registeredTitles.get(titleId);
    }

    /**
     * 해금된 칭호 목록 조회
     */
    public List<PvPTitle> getUnlockedTitles(Player player) {
        Set<String> titleIds = unlockedTitles.get(player.getUniqueId());
        if (titleIds == null || titleIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<PvPTitle> result = new ArrayList<>();
        for (String titleId : titleIds) {
            PvPTitle title = registeredTitles. get(titleId);
            if (title != null) {
                result. add(title);
            }
        }

        return result;
    }

    /**
     * 칭호 보유 여부 확인
     */
    public boolean hasTitle(Player player, String titleId) {
        Set<String> playerTitles = unlockedTitles. get(player.getUniqueId());
        return playerTitles != null && playerTitles. contains(titleId);
    }

    /**
     * 칭호 보너스 조회
     */
    public double getTitleBonus(Player player, String bonusType) {
        if (!enabled || ! bonusesEnabled) {
            return 0.0;
        }

        PvPTitle activeTitle = getActiveTitleData(player);
        if (activeTitle == null) {
            return 0.0;
        }

        return activeTitle.getBonus(bonusType);
    }

    /**
     * 플레이어 이름 포맷팅 (칭호 포함)
     */
    public String formatPlayerName(Player player) {
        PvPTitle activeTitle = getActiveTitleData(player);
        if (activeTitle == null) {
            return player.getName();
        }

        return activeTitle. formatPlayerName(player. getName());
    }

    /**
     * 플레이어 데이터 로드
     */
    public void loadPlayerData(UUID playerId, Set<String> titles, String activeTitle) {
        if (titles != null && !titles. isEmpty()) {
            unlockedTitles.put(playerId, new HashSet<>(titles));
        }
        if (activeTitle != null) {
            activeTitles.put(playerId, activeTitle);
        }
    }

    /**
     * 플레이어 데이터 언로드
     */
    public void unloadPlayerData(UUID playerId) {
        // 데이터는 유지 (저장용)
    }

    /**
     * 플레이어의 해금된 칭호 ID 목록
     */
    public Set<String> getUnlockedTitleIds(UUID playerId) {
        Set<String> titles = unlockedTitles.get(playerId);
        return titles != null ?  new HashSet<>(titles) : new HashSet<>();
    }

    /**
     * 플레이어의 활성 칭호 ID
     */
    public String getActiveTitleId(UUID playerId) {
        return activeTitles. get(playerId);
    }

    public void reload() {
        loadConfig();
    }
}