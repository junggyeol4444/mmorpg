package com.multiverse.party. api;

import com.multiverse.party.PartyCore;
import com.multiverse. party.models.Party;
import com. multiverse.party. models.PartyBuff;
import com.multiverse.party.models.PartyStatistics;
import com.multiverse.party.models.MemberStatistics;
import com.multiverse.party.models.enums.ExpDistribution;
import com.multiverse.party.models.enums.LootDistribution;
import com.multiverse.party.models.enums.PartyRole;
import org.bukkit.Location;
import org. bukkit.entity.Player;

import java.util. List;
import java.util. Map;
import java.util. UUID;

public class PartyAPI {

    private static PartyCore plugin;
    
    private PartyAPI() {
    }
    
    public static void init(PartyCore instance) {
        plugin = instance;
    }
    
    private static void checkInitialized() {
        if (plugin == null) {
            throw new IllegalStateException("PartyAPI가 초기화되지 않았습니다!");
        }
    }
    
    // ==================== 파티 생성/삭제 ====================
    
    public static Party createParty(Player leader) {
        checkInitialized();
        return plugin.getPartyManager().createParty(leader);
    }
    
    public static Party createParty(Player leader, String partyName) {
        checkInitialized();
        return plugin.getPartyManager().createParty(leader, partyName);
    }
    
    public static boolean disbandParty(UUID partyId) {
        checkInitialized();
        Party party = plugin.getPartyManager().getParty(partyId);
        if (party == null) return false;
        plugin.getPartyManager().disbandParty(party);
        return true;
    }
    
    public static boolean disbandParty(Party party) {
        checkInitialized();
        if (party == null) return false;
        plugin.getPartyManager().disbandParty(party);
        return true;
    }
    
    // ==================== 파티 조회 ====================
    
    public static Party getParty(UUID partyId) {
        checkInitialized();
        return plugin.getPartyManager().getParty(partyId);
    }
    
    public static Party getPlayerParty(Player player) {
        checkInitialized();
        return plugin.getPartyManager().getPlayerParty(player);
    }
    
    public static Party getPlayerParty(UUID playerUUID) {
        checkInitialized();
        return plugin.getPartyManager().getPlayerParty(playerUUID);
    }
    
    public static boolean isInParty(Player player) {
        checkInitialized();
        return plugin.getPartyManager().isInParty(player);
    }
    
    public static boolean isInParty(UUID playerUUID) {
        checkInitialized();
        return plugin.getPartyManager().isInParty(playerUUID);
    }
    
    public static boolean isInSameParty(Player player1, Player player2) {
        checkInitialized();
        return plugin. getPartyManager().isInSameParty(player1, player2);
    }
    
    public static boolean isInSameParty(UUID player1, UUID player2) {
        checkInitialized();
        return plugin.getPartyManager().isInSameParty(player1, player2);
    }
    
    public static List<Party> getAllParties() {
        checkInitialized();
        return plugin.getPartyManager().getAllParties();
    }
    
    public static List<Party> getPublicParties() {
        checkInitialized();
        return plugin.getPartyFinder().getPublicParties();
    }
    
    // ==================== 파티 멤버 관리 ====================
    
    public static boolean invitePlayer(Party party, Player inviter, Player target) {
        checkInitialized();
        return plugin.getPartyInviteManager().sendInvite(party, inviter, target);
    }
    
    public static boolean invitePlayer(Party party, Player target) {
        checkInitialized();
        return plugin.getPartyInviteManager().sendInvite(party, target);
    }
    
    public static boolean addMember(Party party, Player player) {
        checkInitialized();
        return plugin.getPartyManager().addMember(party, player);
    }
    
    public static boolean removeMember(Party party, UUID playerUUID) {
        checkInitialized();
        return plugin.getPartyManager().removeMember(party, playerUUID);
    }
    
    public static boolean kickMember(Party party, UUID kicker, UUID target) {
        checkInitialized();
        return plugin. getPartyManager().kickMember(party, kicker, target);
    }
    
    public static List<UUID> getPartyMembers(Party party) {
        checkInitialized();
        if (party == null) return List.of();
        return party.getMembers();
    }
    
    public static List<Player> getOnlinePartyMembers(Party party) {
        checkInitialized();
        return plugin.getPartyManager().getOnlineMembers(party);
    }
    
    public static int getPartySize(Party party) {
        checkInitialized();
        if (party == null) return 0;
        return party.getMembers().size();
    }
    
    public static boolean isPartyFull(Party party) {
        checkInitialized();
        if (party == null) return true;
        return party.getMembers().size() >= party.getMaxMembers();
    }
    
    // ==================== 파티 역할 ====================
    
    public static PartyRole getPlayerRole(Party party, UUID playerUUID) {
        checkInitialized();
        return plugin.getPartyRoleManager().getRole(party, playerUUID);
    }
    
    public static boolean setPlayerRole(Party party, UUID playerUUID, PartyRole role) {
        checkInitialized();
        return plugin.getPartyRoleManager().setRole(party, playerUUID, role);
    }
    
    public static boolean isPartyLeader(Party party, UUID playerUUID) {
        checkInitialized();
        return plugin.getPartyRoleManager().isLeader(party, playerUUID);
    }
    
    public static boolean isPartyOfficer(Party party, UUID playerUUID) {
        checkInitialized();
        return plugin.getPartyRoleManager().isOfficer(party, playerUUID);
    }
    
    public static boolean transferLeadership(Party party, UUID newLeader) {
        checkInitialized();
        return plugin.getPartyRoleManager().transferLeadership(party, newLeader);
    }
    
    public static boolean promoteToOfficer(Party party, UUID playerUUID) {
        checkInitialized();
        return plugin.getPartyRoleManager().promoteToOfficer(party, playerUUID);
    }
    
    public static boolean demoteToMember(Party party, UUID playerUUID) {
        checkInitialized();
        return plugin.getPartyRoleManager().demoteToMember(party, playerUUID);
    }
    
    // ==================== 파티 버프 ====================
    
    public static List<PartyBuff> getActiveBuffs(Party party) {
        checkInitialized();
        return plugin.getPartyBuffManager().getActiveBuffs(party);
    }
    
    public static double getExpBonus(Party party) {
        checkInitialized();
        return plugin.getPartyBuffManager().getExpBonus(party);
    }
    
    public static double getDamageBonus(Party party) {
        checkInitialized();
        return plugin.getPartyBuffManager().getDamageBonus(party);
    }
    
    public static double getDefenseBonus(Party party) {
        checkInitialized();
        return plugin. getPartyBuffManager().getDefenseBonus(party);
    }
    
    public static double getCriticalBonus(Party party) {
        checkInitialized();
        return plugin.getPartyBuffManager().getCriticalBonus(party);
    }
    
    public static double getSpeedBonus(Party party) {
        checkInitialized();
        return plugin. getPartyBuffManager().getSpeedBonus(party);
    }
    
    public static double getHealthBonus(Party party) {
        checkInitialized();
        return plugin.getPartyBuffManager().getHealthBonus(party);
    }
    
    public static boolean hasBuffInRange(Player player, Party party) {
        checkInitialized();
        return plugin.getPartyBuffManager().hasBuffInRange(player, party);
    }
    
    public static void applyBuffs(Party party) {
        checkInitialized();
        plugin.getPartyBuffManager().applyBuffs(party);
    }
    
    public static void removeBuffs(Party party) {
        checkInitialized();
        plugin.getPartyBuffManager().removeBuffs(party);
    }
    
    // ==================== 경험치 공유 ====================
    
    public static void distributeExp(Party party, long totalExp, Location source) {
        checkInitialized();
        plugin.getExpShareManager().distributeExp(party, totalExp, source);
    }
    
    public static Map<UUID, Long> calculateExpShare(Party party, long totalExp) {
        checkInitialized();
        ExpDistribution distribution = party.getExpDistribution();
        return plugin.getExpShareManager().calculateExpShare(party, totalExp, distribution);
    }
    
    public static List<UUID> getMembersInRange(Party party, Location source, double range) {
        checkInitialized();
        return plugin.getExpShareManager().getMembersInRange(party, source, range);
    }
    
    public static void setExpDistribution(Party party, ExpDistribution distribution) {
        checkInitialized();
        party.setExpDistribution(distribution);
    }
    
    public static ExpDistribution getExpDistribution(Party party) {
        checkInitialized();
        return party.getExpDistribution();
    }
    
    // ==================== 아이템 분배 ====================
    
    public static void setLootDistribution(Party party, LootDistribution distribution) {
        checkInitialized();
        party.setLootDistribution(distribution);
    }
    
    public static LootDistribution getLootDistribution(Party party) {
        checkInitialized();
        return party. getLootDistribution();
    }
    
    // ==================== 파티 레벨 ====================
    
    public static int getPartyLevel(Party party) {
        checkInitialized();
        return plugin.getPartyLevelManager().getPartyLevel(party);
    }
    
    public static long getPartyExp(Party party) {
        checkInitialized();
        return plugin.getPartyLevelManager().getPartyExp(party);
    }
    
    public static long getPartyExpToNextLevel(Party party) {
        checkInitialized();
        return plugin.getPartyLevelManager().getExpToNextLevel(party);
    }
    
    public static void addPartyExp(Party party, long amount) {
        checkInitialized();
        plugin.getPartyLevelManager().addPartyExp(party, amount);
    }
    
    public static int getPartySkillPoints(Party party) {
        checkInitialized();
        return plugin.getPartyLevelManager().getAvailableSkillPoints(party);
    }
    
    public static boolean hasPartySkill(Party party, String skillId) {
        checkInitialized();
        return plugin.getPartySkillManager().hasSkill(party, skillId);
    }
    
    public static boolean learnPartySkill(Party party, String skillId) {
        checkInitialized();
        return plugin.getPartySkillManager().learnSkill(party, skillId);
    }
    
    public static boolean usePartySkill(Party party, Player user, String skillId) {
        checkInitialized();
        return plugin.getPartySkillManager().useSkill(party, user, skillId);
    }
    
    // ==================== 파티 채팅 ====================
    
    public static void sendPartyMessage(Player sender, String message) {
        checkInitialized();
        plugin.getPartyChatManager().sendPartyMessage(sender, message);
    }
    
    public static void sendPartyAnnouncement(Party party, String message) {
        checkInitialized();
        plugin.getPartyChatManager().sendPartyAnnouncement(party, message);
    }
    
    public static void sendPartyNotification(Party party, String message) {
        checkInitialized();
        plugin.getPartyChatManager().sendNotification(party, message);
    }
    
    // ==================== 파티 통계 ====================
    
    public static PartyStatistics getPartyStatistics(Party party) {
        checkInitialized();
        return plugin.getPartyStatisticsManager().getStatistics(party);
    }
    
    public static MemberStatistics getMemberStatistics(Party party, UUID playerUUID) {
        checkInitialized();
        return plugin.getPartyStatisticsManager().getMemberStats(party, playerUUID);
    }
    
    public static void recordMonsterKill(Party party, UUID killer) {
        checkInitialized();
        plugin.getPartyStatisticsManager().recordMonsterKill(party, killer);
    }
    
    public static void recordBossKill(Party party, UUID killer) {
        checkInitialized();
        plugin.getPartyStatisticsManager().recordBossKill(party, killer);
    }
    
    public static void recordDamage(Party party, UUID player, double damage) {
        checkInitialized();
        plugin.getPartyStatisticsManager().recordDamage(party, player, damage);
    }
    
    public static void recordHealing(Party party, UUID player, double healing) {
        checkInitialized();
        plugin.getPartyStatisticsManager().recordHealing(party, player, healing);
    }
    
    public static void recordDungeonClear(Party party, String dungeonId) {
        checkInitialized();
        plugin.getPartyStatisticsManager().recordDungeonClear(party, dungeonId);
    }
    
    public static UUID calculateMVP(Party party) {
        checkInitialized();
        return plugin.getPartyStatisticsManager().calculateMVP(party);
    }
    
    // ==================== 파티 찾기 ====================
    
    public static List<Party> searchParties(String query) {
        checkInitialized();
        return plugin.getPartyFinder().searchParties(query);
    }
    
    public static void queueForMatching(Player player, String dungeonId) {
        checkInitialized();
        plugin.getPartyFinder().queueForMatching(player, dungeonId);
    }
    
    public static void cancelQueue(Player player) {
        checkInitialized();
        plugin.getPartyFinder().cancelQueue(player);
    }
    
    public static boolean isInQueue(Player player) {
        checkInitialized();
        return plugin.getPartyFinder().isInQueue(player);
    }
    
    // ==================== 파티 퀘스트 ====================
    
    public static boolean acceptPartyQuest(Party party, String questId) {
        checkInitialized();
        return plugin.getPartyQuestManager().acceptQuest(party, questId);
    }
    
    public static void updateQuestProgress(Party party, String questId, String objectiveId, int amount) {
        checkInitialized();
        plugin.getPartyQuestManager().updateProgress(party, questId, objectiveId, amount);
    }
    
    public static boolean hasActiveQuest(Party party, String questId) {
        checkInitialized();
        return plugin.getPartyQuestManager().hasActiveQuest(party, questId);
    }
    
    // ==================== 기여도 ====================
    
    public static Map<UUID, Double> getContributions(Party party) {
        checkInitialized();
        return plugin.getContributionManager().getContributions(party);
    }
    
    public static double getPlayerContribution(Party party, UUID playerUUID) {
        checkInitialized();
        return plugin. getContributionManager().getPlayerContribution(party, playerUUID);
    }
    
    public static void resetContributions(Party party) {
        checkInitialized();
        plugin.getContributionManager().resetContributions(party);
    }
    
    // ==================== 유틸리티 ====================
    
    public static boolean isPartyEnabled() {
        checkInitialized();
        return plugin.getConfig().getBoolean("party.enabled", true);
    }
    
    public static int getDefaultMaxPartySize() {
        checkInitialized();
        return plugin.getConfig().getInt("party. size.default-max", 5);
    }
    
    public static double getBuffRange() {
        checkInitialized();
        return plugin.getConfig().getDouble("buffs.range", 50.0);
    }
    
    public static double getExpShareRange() {
        checkInitialized();
        return plugin. getConfig().getDouble("exp-share.range", 50.0);
    }
}