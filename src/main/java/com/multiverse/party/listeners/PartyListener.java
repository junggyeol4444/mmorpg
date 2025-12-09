package com. multiverse.party. listeners;

import com. multiverse.party. PartyCore;
import com.multiverse.party.events.*;
import com.multiverse.party.models.Party;
import com.multiverse. party.models.enums.PartyDisbandReason;
import com.multiverse.party.models. enums.PartyRole;
import org. bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit. event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit. event.Listener;

import java.util.UUID;

public class PartyListener implements Listener {

    private final PartyCore plugin;

    public PartyListener(PartyCore plugin) {
        this.plugin = plugin;
    }

    // ==================== 파티 생성 ====================
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onPartyCreate(PartyCreateEvent event) {
        Player creator = event.getCreator();
        Party party = event.getParty();

        // 플레이어 데이터 업데이트
        plugin.getDataManager().savePlayerData(creator. getUniqueId(),
                plugin.getDataManager().loadPlayerData(creator. getUniqueId()));

        // 로그
        plugin.getLogger().info(creator.getName() + "님이 파티를 생성했습니다.  " +
                "(ID: " + party.getPartyId().toString().substring(0, 8) + ")");

        // 통계 업데이트
        plugin. getPartyStatisticsManager().initializeStatistics(party);
    }

    // ==================== 파티 해체 ====================
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPartyDisband(PartyDisbandEvent event) {
        Party party = event.getParty();
        PartyDisbandReason reason = event. getReason();

        // 모든 멤버의 파티 데이터 초기화
        for (UUID memberUUID : party.getMembers()) {
            var playerData = plugin.getDataManager().loadPlayerData(memberUUID);
            if (playerData != null) {
                playerData.setCurrentParty(null);
                plugin. getDataManager().savePlayerData(memberUUID, playerData);
            }

            // 버프 제거
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null) {
                plugin.getPartyBuffManager().removeBuffsFromPlayer(member);
            }
        }

        // 파티 관련 GUI 닫기
        plugin.getGuiManager().closeAllGUIsForParty(party. getPartyId());

        // 파티 모집 공고 삭제
        plugin.getDataManager().deleteListing(party.getPartyId());

        // 로그
        String reasonStr = reason != null ? reason.name() : "UNKNOWN";
        plugin.getLogger().info("파티가 해체되었습니다. " +
                "(ID: " + party. getPartyId().toString().substring(0, 8) + 
                ", 사유: " + reasonStr + ")");
    }

    // ==================== 멤버 가입 ====================
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMemberJoin(PartyMemberJoinEvent event) {
        Party party = event.getParty();
        Player player = event.getPlayer();

        // 플레이어 데이터 업데이트
        var playerData = plugin. getDataManager().loadPlayerData(player. getUniqueId());
        if (playerData != null) {
            playerData.setCurrentParty(party. getPartyId());
            playerData.setPartiesJoined(playerData.getPartiesJoined() + 1);
            playerData.setTotalParties(playerData. getTotalParties() + 1);
            playerData.setLastPartyTime(System.currentTimeMillis());
            plugin.getDataManager().savePlayerData(player.getUniqueId(), playerData);
        }

        // 멤버 통계 초기화
        plugin.getPartyStatisticsManager().initializeMemberStats(party, player. getUniqueId());

        // 버프 적용
        plugin. getPartyBuffManager().applyBuffsToPlayer(player, party);

        // 인원수 버프 업데이트
        plugin.getPartyBuffManager().updateMemberCountBuffs(party);

        // 로그
        plugin.getLogger().info(player.getName() + "님이 파티에 참가했습니다.  " +
                "(파티:  " + party.getPartyId().toString().substring(0, 8) + ")");
    }

    // ==================== 멤버 탈퇴 ====================
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onMemberLeave(PartyMemberLeaveEvent event) {
        Party party = event.getParty();
        UUID playerUUID = event. getPlayerUUID();
        String playerName = event. getPlayerName();

        // 플레이어 데이터 업데이트
        var playerData = plugin.getDataManager().loadPlayerData(playerUUID);
        if (playerData != null) {
            playerData.setCurrentParty(null);
            plugin.getDataManager().savePlayerData(playerUUID, playerData);
        }

        // 버프 제거
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            plugin.getPartyBuffManager().removeBuffsFromPlayer(player);
        }

        // 인원수 버프 업데이트
        plugin.getPartyBuffManager().updateMemberCountBuffs(party);

        // 파티가 비었으면 해체
        if (party.getMembers().isEmpty() || 
            (party.getMembers().size() == 1 && party. getMembers().contains(playerUUID))) {
            // 해체는 PartyManager에서 처리
        }

        // 로그
        plugin.getLogger().info(playerName + "님이 파티를 떠났습니다. " +
                "(파티: " + party. getPartyId().toString().substring(0, 8) + 
                ", 사유: " + event.getReason().name() + ")");
    }

    // ==================== 파티 레벨업 ====================
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPartyLevelUp(PartyLevelUpEvent event) {
        Party party = event.getParty();
        int newLevel = event. getNewLevel();
        int oldLevel = event.getOldLevel();

        // 레벨 버프 업데이트
        plugin.getPartyBuffManager().updateLevelBuffs(party);

        // 스킬 포인트 추가
        int skillPointsPerLevel = plugin.getConfig().getInt("party-level. skill-points-per-level", 1);
        int bonusPoints = event.getBonusSkillPoints();
        int totalPoints = (newLevel - oldLevel) * skillPointsPerLevel + bonusPoints;

        if (party.getPartyLevel() != null) {
            party.getPartyLevel().setSkillPoints(
                    party.getPartyLevel().getSkillPoints() + totalPoints);
        }

        // 파티원들에게 알림
        String levelUpMessage = plugin.getMessageUtil().getMessage("party. level-up",
                "%level%", String.valueOf(newLevel),
                "%points%", String.valueOf(totalPoints));
        plugin.getPartyChatManager().sendNotification(party, levelUpMessage);

        // 해금된 기능 알림
        if (event.hasUnlocks()) {
            for (String buff : event.getUnlockedBuffs()) {
                plugin.getPartyChatManager().sendNotification(party,
                        plugin. getMessageUtil().getMessage("party.buff-unlocked", "%buff%", buff));
            }
            for (String skill : event.getUnlockedSkills()) {
                plugin. getPartyChatManager().sendNotification(party,
                        plugin.getMessageUtil().getMessage("party.skill-unlocked", "%skill%", skill));
            }
        }

        // 데이터 저장
        plugin. getDataManager().saveParty(party);

        // 로그
        plugin.getLogger().info("파티 레벨업!  " +
                "(파티: " + party. getPartyId().toString().substring(0, 8) + 
                ", " + oldLevel + " -> " + newLevel + ")");
    }

    // ==================== 리더 변경 ====================
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeaderChange(PartyLeaderChangeEvent event) {
        Party party = event.getParty();
        UUID newLeaderUUID = event. getNewLeaderUUID();
        String newLeaderName = event.getNewLeaderName();

        // 역할 업데이트
        party.getRoles().put(newLeaderUUID, PartyRole.LEADER);
        party.setLeaderId(newLeaderUUID);

        // 이전 리더를 부리더로
        UUID oldLeaderUUID = event.getOldLeaderUUID();
        if (oldLeaderUUID != null && party.getMembers().contains(oldLeaderUUID)) {
            party.getRoles().put(oldLeaderUUID, PartyRole. OFFICER);
        }

        // 데이터 저장
        plugin.getDataManager().saveParty(party);

        // 로그
        plugin.getLogger().info("파티 리더 변경! " +
                "(파티: " + party. getPartyId().toString().substring(0, 8) + 
                ", 새 리더: " + newLeaderName + ")");
    }

    // ==================== 초대 ====================
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onPartyInvite(PartyInviteEvent event) {
        Player inviter = event.getInviter();
        Player target = event.getTarget();
        Party party = event.getParty();

        // GUI로 초대 표시 (설정에 따라)
        boolean useGUI = plugin. getConfig().getBoolean("invite. use-gui", true);
        if (useGUI) {
            plugin.getGuiManager().openPartyInviteGUI(target, party, inviter. getUniqueId());
        }

        // 로그
        plugin. getLogger().fine(inviter.getName() + "이(가) " + target.getName() + 
                "을(를) 파티에 초대했습니다.");
    }

    // ==================== 추방 ====================
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPartyKick(PartyKickEvent event) {
        Party party = event.getParty();
        UUID targetUUID = event. getTargetUUID();
        String targetName = event.getTargetName();

        // 버프 제거
        Player target = Bukkit.getPlayer(targetUUID);
        if (target != null) {
            plugin.getPartyBuffManager().removeBuffsFromPlayer(target);
        }

        // 플레이어 데이터 업데이트
        var playerData = plugin. getDataManager().loadPlayerData(targetUUID);
        if (playerData != null) {
            playerData.setCurrentParty(null);
            plugin.getDataManager().savePlayerData(targetUUID, playerData);
        }

        // 인원수 버프 업데이트
        plugin.getPartyBuffManager().updateMemberCountBuffs(party);

        // 로그
        plugin.getLogger().info(targetName + "이(가) 파티에서 추방되었습니다. " +
                "(파티:  " + party.getPartyId().toString().substring(0, 8) + ")");
    }

    // ==================== 경험치 획득 ====================
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpGain(PartyExpGainEvent event) {
        Party party = event.getParty();
        long finalExp = event.getFinalExp();

        // 파티 경험치 추가
        plugin.getPartyLevelManager().addPartyExp(party, finalExp);

        // 통계 업데이트
        switch (event.getSource()) {
            case MONSTER_KILL:
                plugin.getPartyStatisticsManager().recordMonsterKill(party, event.getTriggerPlayer());
                break;
            case BOSS_KILL: 
                plugin.getPartyStatisticsManager().recordBossKill(party, event.getTriggerPlayer());
                break;
            case DUNGEON_CLEAR:
                plugin. getPartyStatisticsManager().recordDungeonClear(party, event.getSourceId());
                break;
            case QUEST_COMPLETE: 
                plugin.getPartyStatisticsManager().recordQuestComplete(party, event. getSourceId());
                break;
        }
    }

    // ==================== 버프 변경 ====================
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBuffChange(PartyBuffChangeEvent event) {
        Party party = event.getParty();

        // 버프 추가 시 모든 멤버에게 적용
        if (event.isBuffAdded()) {
            for (UUID memberUUID : party. getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null) {
                    plugin.getPartyBuffManager().applyBuffToPlayer(member, event.getBuff());
                }
            }
        }

        // 버프 제거/만료 시 모든 멤버에서 제거
        if (event.isBuffRemoved() || event.isBuffExpired()) {
            for (UUID memberUUID : party.getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null) {
                    plugin.getPartyBuffManager().removeBuffFromPlayer(member, event.getBuff());
                }
            }
        }

        // 데이터 저장
        plugin.getDataManager().saveParty(party);
    }
}