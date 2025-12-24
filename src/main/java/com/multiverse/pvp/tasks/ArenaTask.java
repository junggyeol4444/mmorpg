package com.multiverse. pvp.tasks;

import com.multiverse.pvp. PvPCore;
import com.multiverse.pvp.data.PvPArena;
import com.multiverse.pvp.enums.ArenaStatus;
import com.multiverse.pvp.utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit. scheduler.BukkitRunnable;

import java.util.UUID;

public class ArenaTask extends BukkitRunnable {

    private final PvPCore plugin;

    public ArenaTask(PvPCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 태스크 시작
     */
    public void start() {
        // 매 초마다 실행
        this. runTaskTimer(plugin, 20L, 20L);
        plugin.getLogger().info("아레나 태스크 시작");
    }

    @Override
    public void run() {
        for (PvPArena arena : plugin.getArenaManager().getAllArenas()) {
            processArena(arena);
        }
    }

    /**
     * 아레나 처리
     */
    private void processArena(PvPArena arena) {
        if (arena.getStatus() != ArenaStatus. ACTIVE) {
            return;
        }

        // 시간 체크
        long elapsedTime = arena.getElapsedTime();
        long matchDuration = arena.getMatchDuration();

        // 남은 시간 알림
        long remaining = matchDuration - elapsedTime;
        
        if (remaining == 60) {
            broadcastToArena(arena, "&e남은 시간:  &f1분");
        } else if (remaining == 30) {
            broadcastToArena(arena, "&e남은 시간:  &f30초");
        } else if (remaining <= 10 && remaining > 0) {
            broadcastToArena(arena, "&c남은 시간: &f" + remaining + "초");
            
            // 카운트다운 효과음
            for (UUID playerId : arena. getPlayers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    player.playSound(player. getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                }
            }
        }

        // 시간 초과
        if (remaining <= 0) {
            handleTimeOut(arena);
            return;
        }

        // 최소 인원 체크
        if (arena.getAlivePlayers().size() < 2 && arena.getType().getMinPlayers() >= 2) {
            // 1명만 남으면 승리 처리
            if (arena.getAlivePlayers().size() == 1) {
                UUID winnerId = arena.getAlivePlayers().get(0);
                plugin.getArenaManager().endArena(arena, winnerId);
            } else if (arena.getAlivePlayers().isEmpty()) {
                plugin.getArenaManager().endArena(arena, null);
            }
        }

        // 점령전/언덕의 왕 - 점령 체크
        if (arena.getType() == com.multiverse.pvp.enums. ArenaType.CAPTURE_POINT ||
            arena.getType() == com.multiverse.pvp.enums.ArenaType.KING_OF_HILL) {
            processCapturePoint(arena);
        }
    }

    /**
     * 시간 초과 처리
     */
    private void handleTimeOut(PvPArena arena) {
        broadcastToArena(arena, "&c&l시간 초과!");

        // 타입별 승자 결정
        UUID winnerId = null;
        String winningTeam = null;

        switch (arena.getType()) {
            case DUEL_1V1:
            case BATTLE_ROYALE: 
            case KING_OF_HILL:
                // 킬 수로 승자 결정
                winnerId = arena.getWinnerByKills();
                break;

            case TEAM_DEATHMATCH:
            case CAPTURE_POINT: 
                // 팀 킬 수 또는 점수로 승자 결정
                winningTeam = arena.getWinningTeam();
                break;
        }

        plugin.getArenaManager().endArena(arena, winnerId);
    }

    /**
     * 점령 포인트 처리
     */
    private void processCapturePoint(PvPArena arena) {
        // 점령 로직 구현
        // 중앙 지점에 있는 플레이어/팀 확인
        // 점령 점수 증가
        // 목표 점수 도달 시 승리

        // 간단한 구현 예시
        var center = arena.getCenter();
        if (center == null) {
            return;
        }

        double captureRadius = 5.0; // 점령 반경

        for (UUID playerId : arena. getAlivePlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) continue;

            double distance = player.getLocation().distance(center);
            if (distance <= captureRadius) {
                // 점령 중
                // 점수 추가 로직
            }
        }
    }

    /**
     * 아레나 메시지 브로드캐스트
     */
    private void broadcastToArena(PvPArena arena, String message) {
        plugin.getArenaManager().broadcastToArena(arena, message);
    }

    /**
     * 태스크 중지
     */
    public void stop() {
        if (!isCancelled()) {
            cancel();
        }
    }
}