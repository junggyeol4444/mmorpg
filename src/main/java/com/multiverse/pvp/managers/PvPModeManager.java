package com.multiverse.pvp.managers;

import com.multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.PvPMode;
import com. multiverse.pvp.data.PvPZone;
import com.multiverse.pvp.enums.PvPType;
import com. multiverse.pvp.enums.ZoneType;
import com.multiverse. pvp.utils. MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;

import java.util. HashMap;
import java. util.Map;
import java.util. UUID;

public class PvPModeManager {

    private final PvPCore plugin;
    private final Map<UUID, PvPMode> pvpModes;

    // 설정값
    private PvPType defaultMode;
    private int newPlayerProtectionDuration;
    private int levelProtection;
    private boolean levelDifferenceEnabled;
    private int maxLevelDifference;
    private int combatTagDuration;

    public PvPModeManager(PvPCore plugin) {
        this. plugin = plugin;
        this.pvpModes = new HashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        this.defaultMode = PvPType.fromString(
                plugin.getConfig().getString("pvp-mode.default-mode", "CONSENSUAL"));
        this.newPlayerProtectionDuration = plugin.getConfig().getInt(
                "pvp-mode.protection. new-player-duration", 86400);
        this.levelProtection = plugin.getConfig().getInt(
                "pvp-mode.protection.level-protection", 10);
        this.levelDifferenceEnabled = plugin.getConfig().getBoolean(
                "pvp-mode.level-difference.enabled", true);
        this.maxLevelDifference = plugin. getConfig().getInt(
                "pvp-mode.level-difference.max-difference", 10);
        this.combatTagDuration = plugin.getConfig().getInt(
                "pvp-mode.combat-tag-duration", 15);
    }

    /**
     * PvP 모드 데이터 가져오기 (없으면 생성)
     */
    public PvPMode getPvPMode(Player player) {
        return getPvPMode(player.getUniqueId());
    }

    public PvPMode getPvPMode(UUID playerId) {
        return pvpModes.computeIfAbsent(playerId, id -> {
            PvPMode mode = new PvPMode(id, defaultMode, false);
            return mode;
        });
    }

    /**
     * PvP 모드 설정
     */
    public void setPvPMode(Player player, PvPMode mode) {
        pvpModes.put(player.getUniqueId(), mode);
    }

    /**
     * PvP 활성화/비활성화
     */
    public void setPvPEnabled(Player player, boolean enabled) {
        PvPMode mode = getPvPMode(player);
        
        // 전투 중에는 변경 불가
        if (mode.isInCombat(combatTagDuration)) {
            MessageUtil.sendMessage(player, "&c전투 중에는 PvP 모드를 변경할 수 없습니다.");
            return;
        }
        
        mode.setEnabled(enabled);
        
        if (enabled) {
            // PvP 활성화 시 보호 종료
            mode.endProtection();
        }
    }

    /**
     * PvP 활성화 상태 확인
     */
    public boolean isPvPEnabled(Player player) {
        PvPMode mode = getPvPMode(player);
        
        // 항상 PvP 모드인 경우
        if (mode.getType() == PvPType.ALWAYS_ON) {
            return true;
        }
        
        // 지역 기반 PvP인 경우 지역 확인
        if (mode.getType() == PvPType.ZONE_BASED) {
            PvPZone zone = plugin.getZoneManager().getZone(player. getLocation());
            if (zone != null && zone.isPvPAllowed()) {
                return true;
            }
        }
        
        return mode.isEnabled();
    }

    /**
     * 공격 가능 여부 확인
     */
    public boolean canAttack(Player attacker, Player target) {
        // 자기 자신 공격 불가
        if (attacker.equals(target)) {
            return false;
        }

        // 듀얼 중인 경우 듀얼 상대만 공격 가능
        if (plugin.getDuelManager().isInDuel(attacker)) {
            return plugin.getDuelManager().isDuelOpponent(attacker, target);
        }

        // 아레나 내 공격 체크
        if (plugin.getArenaManager().isInArena(attacker)) {
            return canAttackInArena(attacker, target);
        }

        // 보호 상태 확인
        if (isProtected(attacker)) {
            return false;
        }
        if (isProtected(target)) {
            return false;
        }

        // 안전 지역 확인
        PvPZone attackerZone = plugin. getZoneManager().getZone(attacker.getLocation());
        PvPZone targetZone = plugin.getZoneManager().getZone(target.getLocation());
        
        if (attackerZone != null && attackerZone. getType() == ZoneType.SAFE) {
            return false;
        }
        if (targetZone != null && targetZone.getType() == ZoneType.SAFE) {
            return false;
        }

        // PvP 모드 타입별 확인
        PvPMode attackerMode = getPvPMode(attacker);
        PvPMode targetMode = getPvPMode(target);

        switch (attackerMode. getType()) {
            case ALWAYS_ON:
                return checkLevelDifference(attacker, target);
                
            case CONSENSUAL:
                if (! attackerMode.isEnabled() || !targetMode. isEnabled()) {
                    return false;
                }
                return checkLevelDifference(attacker, target);
                
            case ZONE_BASED:
                if (attackerZone == null || ! attackerZone. isPvPAllowed()) {
                    return false;
                }
                if (targetZone == null || !targetZone.isPvPAllowed()) {
                    return false;
                }
                return checkLevelDifference(attacker, target);
                
            case GUILD_WAR:
                return checkGuildWar(attacker, target);
                
            default:
                return false;
        }
    }

    /**
     * 아레나 내 공격 가능 여부
     */
    private boolean canAttackInArena(Player attacker, Player target) {
        // 둘 다 같은 아레나에 있어야 함
        if (! plugin.getArenaManager().isInArena(target)) {
            return false;
        }
        
        var attackerArena = plugin.getArenaManager().getPlayerArena(attacker);
        var targetArena = plugin.getArenaManager().getPlayerArena(target);
        
        if (attackerArena == null || ! attackerArena. equals(targetArena)) {
            return false;
        }
        
        // 아레나가 활성 상태인지 확인
        if (! attackerArena. getStatus().canFight()) {
            return false;
        }
        
        // 팀전인 경우 같은 팀 공격 불가
        if (attackerArena.isTeamBased()) {
            return ! attackerArena.isSameTeam(attacker. getUniqueId(), target.getUniqueId());
        }
        
        return true;
    }

    /**
     * 레벨 차이 확인
     */
    private boolean checkLevelDifference(Player attacker, Player target) {
        if (!levelDifferenceEnabled) {
            return true;
        }

        // PlayerDataCore에서 레벨 가져오기
        int attackerLevel = getPlayerLevel(attacker);
        int targetLevel = getPlayerLevel(target);

        // 레벨 보호 (저레벨 보호)
        if (targetLevel <= levelProtection) {
            return false;
        }

        int difference = Math.abs(attackerLevel - targetLevel);
        return difference <= maxLevelDifference;
    }

    /**
     * 길드 전쟁 확인
     */
    private boolean checkGuildWar(Player attacker, Player target) {
        if (!plugin.hasGuildCore()) {
            return false;
        }

        // GuildCore 연동 - 전쟁 중인 길드인지 확인
        try {
            var guildPlugin = Bukkit.getPluginManager().getPlugin("GuildCore");
            if (guildPlugin != null) {
                // GuildCore API를 통해 전쟁 상태 확인
                // 실제 구현은 GuildCore의 API에 따라 달라짐
                return isAtWarWithGuild(attacker, target);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("GuildCore 연동 오류: " + e.getMessage());
        }

        return false;
    }

    /**
     * 길드 전쟁 상태 확인 (GuildCore 연동)
     */
    private boolean isAtWarWithGuild(Player attacker, Player target) {
        // GuildCore API 호출
        // 실제 구현은 GuildCore 플러그인의 API 구조에 따름
        return false;
    }

    /**
     * 플레이어 레벨 가져오기 (PlayerDataCore 연동)
     */
    private int getPlayerLevel(Player player) {
        try {
            var playerDataPlugin = Bukkit. getPluginManager().getPlugin("PlayerDataCore");
            if (playerDataPlugin != null) {
                // PlayerDataCore API를 통해 레벨 조회
                // 실제 구현은 PlayerDataCore의 API에 따라 달라짐
                return getPlayerLevelFromCore(player);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("PlayerDataCore 연동 오류: " + e.getMessage());
        }
        return 1;
    }

    /**
     * PlayerDataCore에서 레벨 조회
     */
    private int getPlayerLevelFromCore(Player player) {
        // PlayerDataCore API 호출
        // 실제 구현은 PlayerDataCore 플러그인의 API 구조에 따름
        return player.getLevel(); // 임시로 바닐라 레벨 사용
    }

    /**
     * 공격 차단 사유 반환
     */
    public String getBlockReason(Player attacker, Player target) {
        if (attacker.equals(target)) {
            return "자기 자신을 공격할 수 없습니다.";
        }

        if (isProtected(attacker)) {
            return "보호 상태에서는 공격할 수 없습니다.";
        }

        if (isProtected(target)) {
            return "상대방이 보호 상태입니다.";
        }

        PvPZone attackerZone = plugin.getZoneManager().getZone(attacker. getLocation());
        PvPZone targetZone = plugin.getZoneManager().getZone(target.getLocation());

        if (attackerZone != null && attackerZone.getType() == ZoneType. SAFE) {
            return "안전 지역에서는 공격할 수 없습니다.";
        }

        if (targetZone != null && targetZone.getType() == ZoneType. SAFE) {
            return "상대방이 안전 지역에 있습니다. ";
        }

        PvPMode attackerMode = getPvPMode(attacker);
        PvPMode targetMode = getPvPMode(target);

        if (attackerMode. getType() == PvPType.CONSENSUAL) {
            if (!attackerMode. isEnabled()) {
                return "PvP 모드가 비활성화되어 있습니다.  /pvp toggle로 활성화하세요.";
            }
            if (!targetMode. isEnabled()) {
                return "상대방의 PvP 모드가 비활성화되어 있습니다. ";
            }
        }

        if (attackerMode. isBlacklisted(target. getUniqueId())) {
            return "해당 플레이어를 차단한 상태입니다. ";
        }

        if (targetMode.isBlacklisted(attacker. getUniqueId())) {
            return "상대방에게 차단당한 상태입니다.";
        }

        if (levelDifferenceEnabled) {
            int attackerLevel = getPlayerLevel(attacker);
            int targetLevel = getPlayerLevel(target);

            if (targetLevel <= levelProtection) {
                return "상대방은 레벨 " + levelProtection + " 이하로 보호됩니다.";
            }

            int difference = Math.abs(attackerLevel - targetLevel);
            if (difference > maxLevelDifference) {
                return "레벨 차이가 너무 큽니다. (최대 " + maxLevelDifference + " 레벨)";
            }
        }

        if (attackerMode. getType() == PvPType.GUILD_WAR) {
            return "길드 전쟁 상태가 아닙니다.";
        }

        return "알 수 없는 이유로 공격할 수 없습니다.";
    }

    /**
     * 보호 설정
     */
    public void setProtection(Player player, int durationSeconds) {
        PvPMode mode = getPvPMode(player);
        mode.setProtection(durationSeconds);
        
        MessageUtil.sendMessage(player, "&a" + (durationSeconds / 3600) + "시간 동안 PvP 보호가 적용됩니다.");
    }

    /**
     * 보호 상태 확인
     */
    public boolean isProtected(Player player) {
        PvPMode mode = getPvPMode(player);
        return mode.isNewPlayerProtected();
    }

    /**
     * 보호 종료
     */
    public void endProtection(Player player) {
        PvPMode mode = getPvPMode(player);
        mode.endProtection();
        
        MessageUtil.sendMessage(player, "&ePvP 보호가 종료되었습니다.");
    }

    /**
     * 블랙리스트에 추가
     */
    public void addToBlacklist(Player player, Player target) {
        PvPMode mode = getPvPMode(player);
        
        if (mode. isBlacklisted(target.getUniqueId())) {
            MessageUtil.sendMessage(player, "&c이미 차단된 플레이어입니다.");
            return;
        }
        
        mode. addToBlacklist(target.getUniqueId());
        MessageUtil.sendMessage(player, "&a" + target.getName() + "님을 PvP 차단 목록에 추가했습니다.");
    }

    /**
     * 블랙리스트에서 제거
     */
    public void removeFromBlacklist(Player player, Player target) {
        PvPMode mode = getPvPMode(player);
        
        if (! mode.isBlacklisted(target. getUniqueId())) {
            MessageUtil.sendMessage(player, "&c차단되지 않은 플레이어입니다.");
            return;
        }
        
        mode.removeFromBlacklist(target.getUniqueId());
        MessageUtil.sendMessage(player, "&a" + target.getName() + "님을 PvP 차단 목록에서 제거했습니다.");
    }

    /**
     * 블랙리스트 여부 확인
     */
    public boolean isBlacklisted(Player player, Player target) {
        PvPMode mode = getPvPMode(player);
        return mode.isBlacklisted(target.getUniqueId());
    }

    /**
     * 전투 태그 업데이트
     */
    public void updateCombatTag(Player player, Player attacker) {
        PvPMode mode = getPvPMode(player);
        mode.updateCombatTag(attacker.getUniqueId());
    }

    /**
     * 전투 중 여부 확인
     */
    public boolean isInCombat(Player player) {
        PvPMode mode = getPvPMode(player);
        return mode.isInCombat(combatTagDuration);
    }

    /**
     * 전투 태그 초기화
     */
    public void clearCombatTag(Player player) {
        PvPMode mode = getPvPMode(player);
        mode.clearCombatTag();
    }

    /**
     * PvP 타입 설정
     */
    public void setPvPType(Player player, PvPType type) {
        PvPMode mode = getPvPMode(player);
        mode.setType(type);
    }

    /**
     * PvP 타입 조회
     */
    public PvPType getPvPType(Player player) {
        PvPMode mode = getPvPMode(player);
        return mode.getType();
    }

    /**
     * 파티 PvP 허용 설정
     */
    public void setAllowPartyPvP(Player player, boolean allow) {
        PvPMode mode = getPvPMode(player);
        mode.setAllowPartyPvP(allow);
    }

    /**
     * 길드 PvP 허용 설정
     */
    public void setAllowGuildPvP(Player player, boolean allow) {
        PvPMode mode = getPvPMode(player);
        mode.setAllowGuildPvP(allow);
    }

    /**
     * 신규 플레이어 보호 적용
     */
    public void applyNewPlayerProtection(Player player) {
        if (newPlayerProtectionDuration > 0) {
            setProtection(player, newPlayerProtectionDuration);
        }
    }

    /**
     * 플레이어 데이터 로드
     */
    public void loadPlayerData(UUID playerId, PvPMode mode) {
        pvpModes.put(playerId, mode);
    }

    /**
     * 플레이어 데이터 언로드
     */
    public void unloadPlayerData(UUID playerId) {
        pvpModes.remove(playerId);
    }

    /**
     * 모든 PvP 모드 데이터 반환
     */
    public Map<UUID, PvPMode> getAllPvPModes() {
        return new HashMap<>(pvpModes);
    }

    /**
     * 설정 리로드
     */
    public void reload() {
        loadConfig();
    }
}