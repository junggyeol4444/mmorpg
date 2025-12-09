package com.multiverse.party.  listeners;

import com.multiverse.party.PartyCore;
import com.  multiverse.party.  events.PartyExpGainEvent;
import com.  multiverse.party.  models.Party;
import com.multiverse.  party. models.enums.ExpDistribution;
import org.bukkit.  Bukkit;
import org.bukkit. Location;
import org. bukkit.  entity.Entity;
import org. bukkit.  entity.LivingEntity;
import org.bukkit.  entity.Monster;
import org.bukkit.  entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.  event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. entity.EntityDeathEvent;
import org.bukkit. event.player.PlayerExpChangeEvent;

import java.util.*;

public class ExpShareListener implements Listener {

    private final PartyCore plugin;
    private final Map<UUID, Long> lastExpTime;
    private final Map<UUID, Long> lastDamageContribution;

    public ExpShareListener(PartyCore plugin) {
        this.plugin = plugin;
        this.lastExpTime = new HashMap<>();
        this.lastDamageContribution = new HashMap<>();
    }

    // ==================== 몬스터 처치 경험치 ====================
    @EventHandler(priority = EventPriority. NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        if (! plugin.getConfig().getBoolean("exp-share.enabled", true)) return;

        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        
        if (killer == null) return;
        if (!(entity instanceof Monster)) return;

        Party party = plugin.getPartyManager().getPlayerParty(killer);
        if (party == null) return;

        // 기본 경험치
        int baseExp = event.getDroppedExp();
        if (baseExp <= 0) return;

        // 파티 경험치 범위 확인
        double shareRange = plugin.getConfig().getDouble("exp-share.range", 50.0);
        Location deathLocation = entity. getLocation();

        // 범위 내 파티원 확인
        List<Player> nearbyMembers = getNearbyPartyMembers(party, deathLocation, shareRange);
        if (nearbyMembers.isEmpty()) return;

        // 보스인지 확인
        boolean isBoss = isBossEntity(entity);
        PartyExpGainEvent. ExpSource source = isBoss ? 
                PartyExpGainEvent.ExpSource. BOSS_KILL : 
                PartyExpGainEvent.ExpSource.MONSTER_KILL;

        // 경험치 배율 계산
        double partyExpMultiplier = plugin.getConfig().getDouble("exp-share.party-exp-multiplier", 1.0);
        double buffBonus = plugin.getPartyBuffManager().getExpBonus(party);
        double totalMultiplier = partyExpMultiplier * (1.0 + buffBonus);

        // 이벤트 발생
        PartyExpGainEvent expEvent = new PartyExpGainEvent(
                party, baseExp, source, deathLocation, killer. getUniqueId());
        expEvent.setBonusMultiplier(totalMultiplier);

        Bukkit.getPluginManager().callEvent(expEvent);

        if (expEvent.isCancelled()) return;

        // 경험치 분배
        distributeExp(party, nearbyMembers, expEvent.getFinalExp(), killer. getUniqueId());

        // 원래 경험치 드롭 취소 (분배했으므로)
        if (plugin.getConfig().getBoolean("exp-share.cancel-vanilla-drop", true)) {
            event.setDroppedExp(0);
        }
    }

    // ==================== 바닐라 경험치 획득 공유 ====================
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        if (!plugin.getConfig().getBoolean("exp-share.share-all-exp", false)) return;

        Player player = event.getPlayer();
        int amount = event.getAmount();
        
        if (amount <= 0) return;

        Party party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) return;

        // 중복 방지 (EntityDeathEvent에서 이미 처리된 경우)
        long currentTime = System. currentTimeMillis();
        Long lastTime = lastExpTime. get(player.getUniqueId());
        if (lastTime != null && currentTime - lastTime < 100) {
            return;
        }
        lastExpTime. put(player.getUniqueId(), currentTime);

        // 범위 내 파티원 확인
        double shareRange = plugin. getConfig().getDouble("exp-share. range", 50.0);
        List<Player> nearbyMembers = getNearbyPartyMembers(party, player. getLocation(), shareRange);
        
        if (nearbyMembers.size() <= 1) return; // 본인만 있으면 분배 불필요

        // 경험치 분배
        distributeExp(party, nearbyMembers, amount, player.getUniqueId());

        // 원래 경험치 취소
        event.setAmount(0);
    }

    // ==================== 경험치 분배 로직 ====================
    private void distributeExp(Party party, List<Player> members, long totalExp, UUID triggerPlayer) {
        if (members. isEmpty() || totalExp <= 0) return;

        ExpDistribution distribution = party.getExpDistribution();
        Map<UUID, Long> expMap = new HashMap<>();

        switch (distribution) {
            case EQUAL:
                distributeEqual(members, totalExp, expMap);
                break;
            case LEVEL_BASED:
                distributeLevelBased(members, totalExp, expMap);
                break;
            case CONTRIBUTION:
                distributeContribution(party, members, totalExp, triggerPlayer, expMap);
                break;
            default:
                distributeEqual(members, totalExp, expMap);
        }

        // 경험치 지급
        for (Map.Entry<UUID, Long> entry : expMap.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                int exp = (int) Math.max(1, entry.getValue());
                giveExpToPlayer(player, exp);

                // 알림 (설정에 따라)
                if (plugin.getConfig().getBoolean("exp-share.show-notification", true)) {
                    plugin.getActionBarUtil().sendActionBar(player,
                            plugin.getMessageUtil().getMessage("exp. gained", "%exp%", String.valueOf(exp)));
                }
            }
        }
    }

    // ==================== 균등 분배 ====================
    private void distributeEqual(List<Player> members, long totalExp, Map<UUID, Long> expMap) {
        long expPerMember = totalExp / members.size();
        long remainder = totalExp % members.size();

        for (int i = 0; i < members.size(); i++) {
            Player member = members.get(i);
            long exp = expPerMember;
            if (i < remainder) {
                exp++; // 나머지 분배
            }
            expMap.put(member.getUniqueId(), exp);
        }
    }

    // ==================== 레벨 기반 분배 ====================
    private void distributeLevelBased(List<Player> members, long totalExp, Map<UUID, Long> expMap) {
        // 총 레벨 계산
        int totalLevel = 0;
        Map<UUID, Integer> playerLevels = new HashMap<>();

        for (Player member : members) {
            int level = member.getLevel();
            if (plugin.getIntegrationManager().isPlayerDataCoreEnabled()) {
                // PlayerDataCore에서 레벨 가져오기
                level = plugin.getIntegrationManager().getPlayerDataCoreIntegration()
                        .getPlayerLevel(member);
            }
            playerLevels. put(member.getUniqueId(), level);
            totalLevel += level;
        }

        if (totalLevel == 0) {
            distributeEqual(members, totalExp, expMap);
            return;
        }

        // 레벨 비율로 분배
        long distributed = 0;
        for (int i = 0; i < members.size(); i++) {
            Player member = members.get(i);
            int level = playerLevels.get(member.getUniqueId());
            
            long exp;
            if (i == members.size() - 1) {
                exp = totalExp - distributed; // 마지막 멤버는 나머지 전부
            } else {
                exp = Math.round((double) level / totalLevel * totalExp);
                distributed += exp;
            }
            
            expMap.put(member.getUniqueId(), Math.max(1, exp));
        }
    }

    // ==================== 기여도 기반 분배 ====================
    private void distributeContribution(Party party, List<Player> members, long totalExp, 
                                        UUID triggerPlayer, Map<UUID, Long> expMap) {
        // 기여도 맵 (데미지, 힐 등 기반)
        Map<UUID, Double> contributions = new HashMap<>();
        double totalContribution = 0;

        for (Player member : members) {
            var memberStats = plugin.getPartyStatisticsManager().getMemberStats(party, member. getUniqueId());
            double contribution = 0;
            
            if (memberStats != null) {
                contribution = memberStats.getDamageDealt() + 
                              (memberStats.getHealingDone() * 0.5); // 힐은 50% 가중치
            }
            
            // 트리거 플레이어 보너스
            if (member.getUniqueId().equals(triggerPlayer)) {
                contribution += totalExp * 0.1; // 10% 보너스
            }
            
            // 최소 기여도 보장
            contribution = Math.max(1, contribution);
            
            contributions.put(member.getUniqueId(), contribution);
            totalContribution += contribution;
        }

        if (totalContribution == 0) {
            distributeEqual(members, totalExp, expMap);
            return;
        }

        // 기여도 비율로 분배
        long distributed = 0;
        List<UUID> memberIds = new ArrayList<>(contributions.keySet());
        
        for (int i = 0; i < memberIds.size(); i++) {
            UUID memberId = memberIds.get(i);
            double contribution = contributions.get(memberId);
            
            long exp;
            if (i == memberIds.size() - 1) {
                exp = totalExp - distributed;
            } else {
                exp = Math.round(contribution / totalContribution * totalExp);
                distributed += exp;
            }
            
            expMap. put(memberId, Math.max(1, exp));
        }
    }

    // ==================== 유틸리티 ====================
    private List<Player> getNearbyPartyMembers(Party party, Location location, double range) {
        List<Player> nearbyMembers = new ArrayList<>();
        double rangeSquared = range * range;

        for (UUID memberUUID : party. getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member == null || ! member.isOnline()) continue;
            if (! member.getWorld().equals(location.getWorld())) continue;
            
            if (member.getLocation().distanceSquared(location) <= rangeSquared) {
                nearbyMembers.add(member);
            }
        }

        return nearbyMembers;
    }

    private boolean isBossEntity(LivingEntity entity) {
        // 커스텀 보스 확인 (MythicMobs 등)
        if (plugin.getIntegrationManager().isMythicMobsEnabled()) {
            return plugin.getIntegrationManager().getMythicMobsIntegration().isBoss(entity);
        }

        // 바닐라 보스 확인
        switch (entity.getType()) {
            case ENDER_DRAGON: 
            case WITHER: 
            case ELDER_GUARDIAN:
            case WARDEN:
                return true;
            default: 
                return false;
        }
    }

    private void giveExpToPlayer(Player player, int exp) {
        if (plugin.getIntegrationManager().isPlayerDataCoreEnabled()) {
            // PlayerDataCore 경험치 시스템 사용
            plugin. getIntegrationManager().getPlayerDataCoreIntegration()
                    .addExperience(player, exp);
        } else {
            // 바닐라 경험치
            player.giveExp(exp);
        }
    }

    // ==================== 데미지 기여도 추적 ====================
    public void recordDamageContribution(UUID playerUUID, long damage) {
        lastDamageContribution.merge(playerUUID, damage, Long::sum);
    }

    public void clearContributions() {
        lastDamageContribution.clear();
    }
}