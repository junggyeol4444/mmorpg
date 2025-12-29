package com.multiverse.pet.listener;

import com. multiverse.pet. PetCore;
import com. multiverse.pet. api.event.PetBattleEndEvent;
import com.multiverse.pet.api.event. PetBattleStartEvent;
import com.multiverse. pet.model.Pet;
import com.multiverse. pet.model.battle.BattleResult;
import com.multiverse.pet.model. battle.PetBattle;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. Sound;
import org. bukkit.entity. Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. entity.EntityDamageByEntityEvent;
import org.bukkit. event.entity.EntityDamageEvent;
import org.bukkit.event.player. PlayerMoveEvent;
import org.bukkit. event.player.PlayerQuitEvent;
import org.bukkit. event.player.PlayerTeleportEvent;

import java.util.UUID;

/**
 * 펫 배틀 관련 리스너
 * 배틀 시작/종료, 배틀 중 이벤트 처리
 */
public class PetBattleListener implements Listener {

    private final PetCore plugin;

    public PetBattleListener(PetCore plugin) {
        this.plugin = plugin;
    }

    // ===== 배틀 시작/종료 이벤트 =====

    /**
     * 배틀 시작 이벤트
     */
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onBattleStart(PetBattleStartEvent event) {
        Player player1 = event.getPlayer1();
        Player player2 = event. getPlayer2();
        Pet pet1 = event.getPet1();
        Pet pet2 = event.getPet2();
        PetBattle battle = event.getBattle();

        // 시작 사운드
        playBattleStartSound(player1);
        if (player2 != null) {
            playBattleStartSound(player2);
        }

        // 시작 메시지
        String startMsg = plugin.getConfigManager().getMessage("battle.start-announcement")
                .replace("{player1}", player1.getName())
                .replace("{pet1}", pet1.getPetName())
                .replace("{player2}", player2 != null ? player2.getName() : "AI")
                .replace("{pet2}", pet2 != null ?  pet2.getPetName() : "훈련용 펫");

        MessageUtil.sendMessage(player1, startMsg);
        if (player2 != null) {
            MessageUtil. sendMessage(player2, startMsg);
        }

        // 관전자에게 알림
        if (battle.isAllowSpectators()) {
            broadcastToBattleSpectators(battle, startMsg);
        }

        // 디버그 로그
        if (plugin.isDebugMode()) {
            plugin.getLogger().info("[DEBUG] 배틀 시작:  " + battle.getBattleId());
        }
    }

    /**
     * 배틀 종료 이벤트
     */
    @EventHandler(priority = EventPriority. MONITOR)
    public void onBattleEnd(PetBattleEndEvent event) {
        Player player1 = event.getPlayer1();
        Player player2 = event.getPlayer2();
        PetBattle battle = event.getBattle();
        BattleResult result = event.getResult();

        // 종료 사운드
        if (player1 != null) {
            playBattleEndSound(player1, result. getWinnerId() != null && 
                    result. getWinnerId().equals(player1.getUniqueId()));
        }
        if (player2 != null) {
            playBattleEndSound(player2, result.getWinnerId() != null && 
                    result.getWinnerId().equals(player2.getUniqueId()));
        }

        // 결과 메시지
        String resultMsg = formatBattleResult(battle, result);
        
        if (player1 != null) {
            MessageUtil.sendMessage(player1, resultMsg);
        }
        if (player2 != null) {
            MessageUtil.sendMessage(player2, resultMsg);
        }

        // 관전자에게 알림
        broadcastToBattleSpectators(battle, resultMsg);

        // 보상 알림
        if (result.getWinnerId() != null) {
            Player winner = Bukkit.getPlayer(result.getWinnerId());
            if (winner != null) {
                MessageUtil.sendMessage(winner, plugin.getConfigManager().getMessage("battle.rewards")
                        .replace("{exp}", String.valueOf(battle.getWinnerExp()))
                        . replace("{rating}", String.valueOf(battle.getRatingChange())));
            }
        }

        // 디버그 로그
        if (plugin.isDebugMode()) {
            plugin.getLogger().info("[DEBUG] 배틀 종료: " + battle.getBattleId() + 
                    " - 승자: " + (result.getWinnerId() != null ? result.getWinnerId() : "없음"));
        }
    }

    // ===== 배틀 중 보호 =====

    /**
     * 배틀 중 플레이어 데미지 방지
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamageDuringBattle(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        UUID playerId = player. getUniqueId();

        // 배틀 중인지 확인
        if (!plugin.getPetBattleManager().isInBattle(playerId)) {
            return;
        }

        // 배틀 중 데미지 무시 (선택적)
        if (plugin.getConfigManager().getBattleSettings().isProtectDuringBattle()) {
            event.setCancelled(true);
        }
    }

    /**
     * 배틀 중 플레이어가 다른 엔티티 공격 방지
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerAttackDuringBattle(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        UUID playerId = player.getUniqueId();

        // 배틀 중인지 확인
        if (! plugin.getPetBattleManager().isInBattle(playerId)) {
            return;
        }

        // 배틀 중 외부 공격 방지
        if (plugin.getConfigManager().getBattleSettings().isPreventExternalAttack()) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("battle.cannot-attack-external"));
        }
    }

    /**
     * 배틀 중 이동 제한 (선택적)
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMoveDuringBattle(PlayerMoveEvent event) {
        if (! plugin.getConfigManager().getBattleSettings().isRestrictMovement()) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // 배틀 중인지 확인
        if (!plugin.getPetBattleManager().isInBattle(playerId)) {
            return;
        }

        // 블록 이동만 체크 (회전은 허용)
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
            event.getFrom().getBlockY() != event.getTo().getBlockY() ||
            event. getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            
            event.setTo(event.getFrom());
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("battle.cannot-move"));
        }
    }

    /**
     * 배틀 중 텔레포트 제한
     */
    @EventHandler(priority = EventPriority. HIGH, ignoreCancelled = true)
    public void onPlayerTeleportDuringBattle(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player. getUniqueId();

        // 배틀 중인지 확인
        if (!plugin.getPetBattleManager().isInBattle(playerId)) {
            return;
        }

        // 배틀 중 텔레포트 방지
        if (plugin.getConfigManager().getBattleSettings().isPreventTeleport()) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("battle.cannot-teleport"));
        }
    }

    /**
     * 배틀 중 로그아웃 처리
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuitDuringBattle(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // 배틀 중인지 확인
        if (!plugin. getPetBattleManager().isInBattle(playerId)) {
            return;
        }

        // 항복 처리
        plugin.getPetBattleManager().surrender(player);

        // 상대방에게 알림
        PetBattle battle = plugin.getPetBattleManager().getPlayerBattle(playerId);
        if (battle != null && ! battle.isAIBattle()) {
            UUID opponentId = battle.getPlayer1Id().equals(playerId) 
                    ? battle.getPlayer2Id() 
                    : battle.getPlayer1Id();
            Player opponent = Bukkit.getPlayer(opponentId);
            
            if (opponent != null) {
                MessageUtil.sendMessage(opponent, plugin. getConfigManager().getMessage("battle.opponent-disconnected")
                        .replace("{player}", player.getName()));
            }
        }
    }

    // ===== 유틸리티 =====

    /**
     * 배틀 시작 사운드 재생
     */
    private void playBattleStartSound(Player player) {
        if (player == null) return;
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.5f);
        player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 1.0f, 1.0f);
    }

    /**
     * 배틀 종료 사운드 재생
     */
    private void playBattleEndSound(Player player, boolean isWinner) {
        if (player == null) return;
        
        if (isWinner) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.5f, 1.5f);
        }
    }

    /**
     * 배틀 결과 포맷팅
     */
    private String formatBattleResult(PetBattle battle, BattleResult result) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\n§6§l===== 배틀 결과 =====\n\n");

        switch (result.getResultType()) {
            case WIN:
                String winnerName = result.getWinnerId() != null 
                        ? Bukkit. getOfflinePlayer(result. getWinnerId()).getName() 
                        : "AI";
                sb.append("§a§l승리: §f").append(winnerName).append("\n");
                break;
            case LOSE: 
                sb.append("§c§l패배\n");
                break;
            case DRAW:
                sb.append("§e§l무승부\n");
                break;
            case TIMEOUT:
                sb. append("§7§l시간 초과\n");
                break;
            case SURRENDER:
                sb.append("§c§l항복\n");
                break;
            case DISCONNECT:
                sb. append("§7§l연결 끊김\n");
                break;
        }

        sb.append("\n§7총 턴: §f").append(battle.getCurrentTurn());
        sb.append("\n§7진행 시간: §f").append(formatDuration(battle.getDuration()));

        // 통계
        sb.append("\n\n§e[").append(battle.getPlayer1Name()).append("의 ").append(battle.getPet1Name()).append("]");
        sb.append("\n§7- 남은 체력: §c").append(String.format("%.1f", battle.getPet1HP()));
        sb.append("/").append(String.format("%.1f", battle.getPet1MaxHP()));

        if (! battle.isAIBattle()) {
            sb. append("\n\n§e[").append(battle.getPlayer2Name()).append("의 ").append(battle.getPet2Name()).append("]");
            sb.append("\n§7- 남은 체력:  §c").append(String.format("%. 1f", battle. getPet2HP()));
            sb.append("/").append(String.format("%.1f", battle.getPet2MaxHP()));
        }

        return sb.toString();
    }

    /**
     * 시간 포맷팅
     */
    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds);
        } else {
            return String.format("%d초", seconds);
        }
    }

    /**
     * 관전자에게 메시지 전송
     */
    private void broadcastToBattleSpectators(PetBattle battle, String message) {
        if (battle == null) return;

        for (UUID spectatorId : battle. getSpectators()) {
            Player spectator = Bukkit.getPlayer(spectatorId);
            if (spectator != null) {
                MessageUtil.sendMessage(spectator, "§8[관전] " + message);
            }
        }
    }
}