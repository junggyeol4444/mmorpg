package com.multiverse.pet.manager;

import com. multiverse.pet. PetCore;
import com. multiverse.pet. model.Pet;
import com. multiverse.pet. model.PetRarity;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit. scheduler.BukkitTask;

import java. util.*;
import java.util.concurrent. ConcurrentHashMap;
import java. util.stream.Collectors;

/**
 * 펫 랭킹 매니저 클래스
 * 펫 랭킹, 플레이어 랭킹 관리
 */
public class PetRankingManager {

    private final PetCore plugin;

    // 랭킹 캐시
    private final Map<RankingType, List<RankingEntry>> rankingCache;

    // 플레이어 레이팅
    private final Map<UUID, Integer> playerRatings;

    // 랭킹 업데이트 태스크
    private BukkitTask updateTask;

    // 설정 값
    private int topRankingSize;
    private int updateIntervalMinutes;
    private int defaultRating;
    private int minRating;
    private int maxRating;

    /**
     * 생성자
     */
    public PetRankingManager(PetCore plugin) {
        this. plugin = plugin;
        this.rankingCache = new ConcurrentHashMap<>();
        this.playerRatings = new ConcurrentHashMap<>();
        loadSettings();
        initializeRankings();
        startUpdateTask();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.topRankingSize = plugin.getConfigManager().getBattleSettings().getTopRankingSize();
        this.updateIntervalMinutes = plugin. getConfigManager().getBattleSettings().getRankingUpdateInterval();
        this.defaultRating = plugin. getConfigManager().getBattleSettings().getDefaultRating();
        this.minRating = plugin.getConfigManager().getBattleSettings().getMinRating();
        this.maxRating = plugin.getConfigManager().getBattleSettings().getMaxRating();
    }

    /**
     * 랭킹 초기화
     */
    private void initializeRankings() {
        for (RankingType type : RankingType.values()) {
            rankingCache.put(type, new ArrayList<>());
        }
    }

    /**
     * 업데이트 태스크 시작
     */
    private void startUpdateTask() {
        long intervalTicks = updateIntervalMinutes * 60L * 20L;
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, 
            this::updateAllRankings, intervalTicks, intervalTicks);
        
        // 초기 업데이트
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::updateAllRankings);
    }

    /**
     * 업데이트 태스크 중지
     */
    public void stopUpdateTask() {
        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask. cancel();
        }
    }

    // ===== 랭킹 업데이트 =====

    /**
     * 모든 랭킹 업데이트
     */
    public void updateAllRankings() {
        updatePetLevelRanking();
        updatePetPowerRanking();
        updatePetWinRateRanking();
        updatePetKillsRanking();
        updatePlayerRatingRanking();
        updatePlayerPetCountRanking();
        updatePlayerTotalLevelRanking();
    }

    /**
     * 펫 레벨 랭킹 업데이트
     */
    private void updatePetLevelRanking() {
        List<RankingEntry> entries = new ArrayList<>();

        for (Player player :  Bukkit.getOnlinePlayers()) {
            List<Pet> pets = plugin.getPetManager().getAllPets(player. getUniqueId());
            for (Pet pet : pets) {
                entries.add(new RankingEntry(
                    player.getUniqueId(),
                    player.getName(),
                    pet.getPetId(),
                    pet.getPetName(),
                    pet. getLevel(),
                    pet.getRarity()
                ));
            }
        }

        // 오프라인 플레이어 데이터도 포함 (필요시)
        // ... 

        entries.sort((a, b) -> Integer.compare(b. getValue(), a.getValue()));
        
        List<RankingEntry> topEntries = entries. stream()
                .limit(topRankingSize)
                .collect(Collectors.toList());

        // 순위 설정
        for (int i = 0; i < topEntries.size(); i++) {
            topEntries.get(i).setRank(i + 1);
        }

        rankingCache.put(RankingType. PET_LEVEL, topEntries);
    }

    /**
     * 펫 전투력 랭킹 업데이트
     */
    private void updatePetPowerRanking() {
        List<RankingEntry> entries = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            List<Pet> pets = plugin.getPetManager().getAllPets(player.getUniqueId());
            for (Pet pet : pets) {
                int power = (int) (pet.getTotalStat("attack") + pet.getTotalStat("defense") + 
                                   pet.getTotalStat("health") / 10);
                entries.add(new RankingEntry(
                    player.getUniqueId(),
                    player.getName(),
                    pet. getPetId(),
                    pet.getPetName(),
                    power,
                    pet.getRarity()
                ));
            }
        }

        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        List<RankingEntry> topEntries = entries.stream()
                .limit(topRankingSize)
                .collect(Collectors.toList());

        for (int i = 0; i < topEntries.size(); i++) {
            topEntries.get(i).setRank(i + 1);
        }

        rankingCache.put(RankingType.PET_POWER, topEntries);
    }

    /**
     * 펫 승률 랭킹 업데이트
     */
    private void updatePetWinRateRanking() {
        List<RankingEntry> entries = new ArrayList<>();

        for (Player player :  Bukkit.getOnlinePlayers()) {
            List<Pet> pets = plugin.getPetManager().getAllPets(player.getUniqueId());
            for (Pet pet : pets) {
                int totalBattles = pet. getBattleWins() + pet.getBattleLosses();
                if (totalBattles >= 10) { // 최소 10전 이상
                    int winRate = (int) ((double) pet.getBattleWins() / totalBattles * 100);
                    RankingEntry entry = new RankingEntry(
                        player.getUniqueId(),
                        player.getName(),
                        pet.getPetId(),
                        pet.getPetName(),
                        winRate,
                        pet.getRarity()
                    );
                    entry.setExtraInfo("(" + pet.getBattleWins() + "승 " + pet.getBattleLosses() + "패)");
                    entries.add(entry);
                }
            }
        }

        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        List<RankingEntry> topEntries = entries.stream()
                .limit(topRankingSize)
                .collect(Collectors. toList());

        for (int i = 0; i < topEntries.size(); i++) {
            topEntries. get(i).setRank(i + 1);
        }

        rankingCache.put(RankingType.PET_WIN_RATE, topEntries);
    }

    /**
     * 펫 킬수 랭킹 업데이트
     */
    private void updatePetKillsRanking() {
        List<RankingEntry> entries = new ArrayList<>();

        for (Player player :  Bukkit.getOnlinePlayers()) {
            List<Pet> pets = plugin. getPetManager().getAllPets(player. getUniqueId());
            for (Pet pet : pets) {
                if (pet.getKillCount() > 0) {
                    entries.add(new RankingEntry(
                        player.getUniqueId(),
                        player. getName(),
                        pet.getPetId(),
                        pet.getPetName(),
                        pet. getKillCount(),
                        pet. getRarity()
                    ));
                }
            }
        }

        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        List<RankingEntry> topEntries = entries.stream()
                .limit(topRankingSize)
                .collect(Collectors.toList());

        for (int i = 0; i < topEntries.size(); i++) {
            topEntries.get(i).setRank(i + 1);
        }

        rankingCache. put(RankingType.PET_KILLS, topEntries);
    }

    /**
     * 플레이어 레이팅 랭킹 업데이트
     */
    private void updatePlayerRatingRanking() {
        List<RankingEntry> entries = new ArrayList<>();

        for (Map.Entry<UUID, Integer> entry : playerRatings.entrySet()) {
            String playerName = Bukkit.getOfflinePlayer(entry. getKey()).getName();
            if (playerName != null) {
                entries. add(new RankingEntry(
                    entry.getKey(),
                    playerName,
                    null,
                    null,
                    entry.getValue(),
                    null
                ));
            }
        }

        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        List<RankingEntry> topEntries = entries. stream()
                .limit(topRankingSize)
                .collect(Collectors.toList());

        for (int i = 0; i < topEntries. size(); i++) {
            topEntries.get(i).setRank(i + 1);
        }

        rankingCache.put(RankingType. PLAYER_RATING, topEntries);
    }

    /**
     * 플레이어 펫 보유수 랭킹 업데이트
     */
    private void updatePlayerPetCountRanking() {
        List<RankingEntry> entries = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            int petCount = plugin. getPetManager().getAllPets(player. getUniqueId()).size();
            if (petCount > 0) {
                entries.add(new RankingEntry(
                    player. getUniqueId(),
                    player.getName(),
                    null,
                    null,
                    petCount,
                    null
                ));
            }
        }

        entries.sort((a, b) -> Integer.compare(b. getValue(), a.getValue()));
        
        List<RankingEntry> topEntries = entries.stream()
                .limit(topRankingSize)
                .collect(Collectors.toList());

        for (int i = 0; i < topEntries.size(); i++) {
            topEntries.get(i).setRank(i + 1);
        }

        rankingCache.put(RankingType.PLAYER_PET_COUNT, topEntries);
    }

    /**
     * 플레이어 총 펫 레벨 랭킹 업데이트
     */
    private void updatePlayerTotalLevelRanking() {
        List<RankingEntry> entries = new ArrayList<>();

        for (Player player :  Bukkit.getOnlinePlayers()) {
            List<Pet> pets = plugin.getPetManager().getAllPets(player.getUniqueId());
            int totalLevel = pets.stream().mapToInt(Pet::getLevel).sum();
            if (totalLevel > 0) {
                entries.add(new RankingEntry(
                    player.getUniqueId(),
                    player. getName(),
                    null,
                    null,
                    totalLevel,
                    null
                ));
            }
        }

        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        List<RankingEntry> topEntries = entries.stream()
                .limit(topRankingSize)
                .collect(Collectors.toList());

        for (int i = 0; i < topEntries.size(); i++) {
            topEntries.get(i).setRank(i + 1);
        }

        rankingCache. put(RankingType.PLAYER_TOTAL_LEVEL, topEntries);
    }

    // ===== 랭킹 조회 =====

    /**
     * 랭킹 가져오기
     */
    public List<RankingEntry> getRanking(RankingType type) {
        return new ArrayList<>(rankingCache.getOrDefault(type, new ArrayList<>()));
    }

    /**
     * 랭킹 가져오기 (페이지)
     */
    public List<RankingEntry> getRanking(RankingType type, int page, int pageSize) {
        List<RankingEntry> fullRanking = getRanking(type);
        
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, fullRanking.size());
        
        if (startIndex >= fullRanking.size()) {
            return new ArrayList<>();
        }
        
        return fullRanking.subList(startIndex, endIndex);
    }

    /**
     * 플레이어의 랭킹 순위 가져오기
     */
    public int getPlayerRank(RankingType type, UUID playerId) {
        List<RankingEntry> ranking = getRanking(type);
        
        for (RankingEntry entry : ranking) {
            if (entry.getPlayerId().equals(playerId)) {
                return entry.getRank();
            }
        }
        
        return -1; // 랭킹에 없음
    }

    /**
     * 펫의 랭킹 순위 가져오기
     */
    public int getPetRank(RankingType type, UUID petId) {
        List<RankingEntry> ranking = getRanking(type);
        
        for (RankingEntry entry : ranking) {
            if (entry.getPetId() != null && entry.getPetId().equals(petId)) {
                return entry.getRank();
            }
        }
        
        return -1;
    }

    // ===== 레이팅 관리 =====

    /**
     * 플레이어 레이팅 가져오기
     */
    public int getPlayerRating(UUID playerId) {
        return playerRatings.getOrDefault(playerId, defaultRating);
    }

    /**
     * 플레이어 레이팅 설정
     */
    public void setPlayerRating(UUID playerId, int rating) {
        rating = Math.max(minRating, Math. min(rating, maxRating));
        playerRatings.put(playerId, rating);
        plugin.getPlayerDataCoreHook().setRating(playerId, rating);
    }

    /**
     * 플레이어 레이팅 변경
     */
    public void addPlayerRating(UUID playerId, int change) {
        int current = getPlayerRating(playerId);
        setPlayerRating(playerId, current + change);
    }

    /**
     * 레이팅 기반 랭크 타이틀
     */
    public String getRankTitle(int rating) {
        if (rating >= 2500) return "&c&l전설";
        if (rating >= 2200) return "&6&l마스터";
        if (rating >= 1900) return "&5&l다이아몬드";
        if (rating >= 1600) return "&b&l플래티넘";
        if (rating >= 1300) return "&e&l골드";
        if (rating >= 1000) return "&7&l실버";
        return "&f&l브론즈";
    }

    /**
     * 레이팅 기반 랭크 색상
     */
    public String getRankColor(int rating) {
        if (rating >= 2500) return "&c";
        if (rating >= 2200) return "&6";
        if (rating >= 1900) return "&5";
        if (rating >= 1600) return "&b";
        if (rating >= 1300) return "&e";
        if (rating >= 1000) return "&7";
        return "&f";
    }

    // ===== 랭킹 표시 =====

    /**
     * 랭킹 보드 표시
     */
    public void showRanking(Player player, RankingType type, int page) {
        List<RankingEntry> entries = getRanking(type, page, 10);
        
        if (entries. isEmpty()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("ranking.empty"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("§6===== ").append(type.getDisplayName()).append(" 랭킹 =====\n\n");

        for (RankingEntry entry : entries) {
            sb.append(formatRankingEntry(entry, type)).append("\n");
        }

        // 자신의 순위 표시
        int myRank = getPlayerRank(type, player.getUniqueId());
        if (myRank > 0) {
            sb.append("\n§7내 순위: §e").append(myRank).append("위");
        }

        MessageUtil.sendMessage(player, sb.toString());
    }

    /**
     * 랭킹 항목 포맷팅
     */
    private String formatRankingEntry(RankingEntry entry, RankingType type) {
        StringBuilder sb = new StringBuilder();
        
        // 순위 색상
        String rankColor;
        switch (entry.getRank()) {
            case 1: rankColor = "&6"; break;
            case 2: rankColor = "&7"; break;
            case 3: rankColor = "&c"; break;
            default: rankColor = "&f"; break;
        }
        
        sb.append(rankColor).append("#").append(entry.getRank()).append(" ");
        
        if (type. isPetRanking()) {
            // 펫 랭킹
            sb.append("§f").append(entry.getPlayerName()).append("§7의 ");
            if (entry.getRarity() != null) {
                sb.append(entry.getRarity().getColorCode());
            }
            sb.append(entry.getPetName());
        } else {
            // 플레이어 랭킹
            sb.append("§f").append(entry.getPlayerName());
        }
        
        sb.append(" §7- ");
        
        switch (type) {
            case PET_LEVEL: 
                sb.append("§eLv. ").append(entry.getValue());
                break;
            case PET_POWER: 
                sb.append("§c전투력 ").append(entry.getValue());
                break;
            case PET_WIN_RATE:
                sb.append("§a").append(entry.getValue()).append("%");
                if (entry.getExtraInfo() != null) {
                    sb.append(" §7").append(entry.getExtraInfo());
                }
                break;
            case PET_KILLS: 
                sb.append("§4").append(entry.getValue()).append(" 킬");
                break;
            case PLAYER_RATING: 
                sb.append(getRankColor(entry.getValue())).append(entry.getValue()).append(" ");
                sb.append(getRankTitle(entry. getValue()));
                break;
            case PLAYER_PET_COUNT: 
                sb.append("§b").append(entry.getValue()).append("마리");
                break;
            case PLAYER_TOTAL_LEVEL:
                sb.append("§e총 Lv.").append(entry.getValue());
                break;
        }
        
        return sb.toString();
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadSettings();
    }

    /**
     * 종료 처리
     */
    public void shutdown() {
        stopUpdateTask();
    }

    // ===== 내부 클래스/열거형 =====

    /**
     * 랭킹 타입
     */
    public enum RankingType {
        PET_LEVEL("펫 레벨", true),
        PET_POWER("펫 전투력", true),
        PET_WIN_RATE("펫 승률", true),
        PET_KILLS("펫 킬 수", true),
        PLAYER_RATING("플레이어 레이팅", false),
        PLAYER_PET_COUNT("펫 보유수", false),
        PLAYER_TOTAL_LEVEL("총 펫 레벨", false);

        private final String displayName;
        private final boolean petRanking;

        RankingType(String displayName, boolean petRanking) {
            this.displayName = displayName;
            this.petRanking = petRanking;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isPetRanking() {
            return petRanking;
        }
    }

    /**
     * 랭킹 항목
     */
    public static class RankingEntry {
        private int rank;
        private final UUID playerId;
        private final String playerName;
        private final UUID petId;
        private final String petName;
        private final int value;
        private final PetRarity rarity;
        private String extraInfo;

        public RankingEntry(UUID playerId, String playerName, UUID petId, String petName, 
                           int value, PetRarity rarity) {
            this. playerId = playerId;
            this. playerName = playerName;
            this. petId = petId;
            this. petName = petName;
            this. value = value;
            this.rarity = rarity;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public UUID getPlayerId() {
            return playerId;
        }

        public String getPlayerName() {
            return playerName;
        }

        public UUID getPetId() {
            return petId;
        }

        public String getPetName() {
            return petName;
        }

        public int getValue() {
            return value;
        }

        public PetRarity getRarity() {
            return rarity;
        }

        public String getExtraInfo() {
            return extraInfo;
        }

        public void setExtraInfo(String extraInfo) {
            this.extraInfo = extraInfo;
        }
    }
}