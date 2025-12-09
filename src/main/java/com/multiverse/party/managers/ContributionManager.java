package com.multiverse.party.managers;

import com.multiverse.party.PartyCore;
import com.multiverse.party.models.Party;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 파티 내 개별 멤버의 행동(데미지, 힐 등)에 따른 기여도를 계산하여
 * 경험치 분배, 통계, MVP 선정 등에 활용하는 매니저입니다.
 */
public class ContributionManager {

    private final PartyCore plugin;
    // 파티별 멤버별 기여도 캐시 (파티ID -> (플레이어ID -> 기여도))
    private final Map<UUID, Map<UUID, ContributionStat>> partyContributions;

    public ContributionManager(PartyCore plugin) {
        this.plugin = plugin;
        this.partyContributions = new ConcurrentHashMap<>();
    }

    /** 기여도 통계 구조체 */
    public static class ContributionStat {
        public double damageDealt = 0;
        public double healingDone = 0;
        public int mvpCount = 0;
        public long lastActionTime = 0;

        public void addDamage(double amount, long time) {
            damageDealt += amount;
            lastActionTime = time;
        }

        public void addHealing(double amount, long time) {
            healingDone += amount;
            lastActionTime = time;
        }

        public void incrementMVP() {
            mvpCount++;
        }
    }

    // 데미지 기록
    public void recordDamage(Party party, UUID playerUUID, double amount) {
        if (party == null || playerUUID == null || amount <= 0) return;
        getOrCreateStat(party.getPartyId(), playerUUID).addDamage(amount, System.currentTimeMillis());
    }

    // 힐 기록
    public void recordHealing(Party party, UUID playerUUID, double amount) {
        if (party == null || playerUUID == null || amount <= 0) return;
        getOrCreateStat(party.getPartyId(), playerUUID).addHealing(amount, System.currentTimeMillis());
    }

    // 파티 해체/종료 시 캐시 제거
    public void clearContributions(Party party) {
        if (party == null) return;
        partyContributions.remove(party.getPartyId());
    }

    // 특정 파티 내 MVP 선정: 가장 높은 데미지 + 힐 기준
    public UUID findMVP(Party party) {
        if (party == null) return null;
        Map<UUID, ContributionStat> stats = partyContributions.get(party.getPartyId());
        if (stats == null || stats.isEmpty()) return null;

        UUID mvp = null;
        double bestScore = 0;

        for (Map.Entry<UUID, ContributionStat> entry : stats.entrySet()) {
            double score = entry.getValue().damageDealt + (entry.getValue().healingDone * 0.5);
            if (score > bestScore) {
                bestScore = score;
                mvp = entry.getKey();
            }
        }
        return mvp;
    }

    // 모든 기여도 정보 반환 (통계 UI 등)
    public Map<UUID, ContributionStat> getContributions(Party party) {
        if (party == null) return Collections.emptyMap();
        Map<UUID, ContributionStat> copy = new HashMap<>();
        Map<UUID, ContributionStat> stats = partyContributions.get(party.getPartyId());
        if (stats != null) {
            for (Map.Entry<UUID, ContributionStat> entry : stats.entrySet()) {
                copy.put(entry.getKey(), entry.getValue());
            }
        }
        return copy;
    }

    /** 
     * 경험치 분배용 점수 반환 (파티 멤버 리스트 기준) 
     * - 내부적으로 데미지/힐 합산 점수 기준, 없으면 최소값
     */
    public Map<UUID, Double> getExpShares(Party party, List<UUID> memberUUIDs) {
        Map<UUID, Double> shares = new HashMap<>();
        if (party == null || memberUUIDs == null) return shares;

        Map<UUID, ContributionStat> stats = partyContributions.get(party.getPartyId());
        for (UUID uuid : memberUUIDs) {
            double value = 1.0; // 최소값
            if (stats != null && stats.containsKey(uuid)) {
                ContributionStat stat = stats.get(uuid);
                value = stat.damageDealt + (stat.healingDone * 0.5);
            }
            shares.put(uuid, value);
        }
        return shares;
    }

    // 기여도 구조체 반환 (없으면 생성)
    private ContributionStat getOrCreateStat(UUID partyId, UUID playerUUID) {
        Map<UUID, ContributionStat> stats = partyContributions.computeIfAbsent(partyId, k -> new ConcurrentHashMap<>());
        return stats.computeIfAbsent(playerUUID, k -> new ContributionStat());
    }

    // 각종 로그, 디버깅, 주기적 클린업/자동 저장 등 필요 시 확장 가능
    public void logStats(Party party) {
        Map<UUID, ContributionStat> stats = getContributions(party);
        for (Map.Entry<UUID, ContributionStat> entry : stats.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            String name = player != null ? player.getName() : entry.getKey().toString();
            ContributionStat stat = entry.getValue();
            plugin.getLogger().info("[기여도] " + name +
                    " - Damage: " + stat.damageDealt +
                    ", Healing: " + stat.healingDone +
                    ", MVP: " + stat.mvpCount +
                    ", LastAction: " + stat.lastActionTime);
        }
    }
}