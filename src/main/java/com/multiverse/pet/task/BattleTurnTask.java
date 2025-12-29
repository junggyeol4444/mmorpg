package com.multiverse.pet.task;

import com.multiverse.pet.PetCore;
import com.multiverse. pet.model.battle.BattleStatus;
import com.multiverse.pet.model.battle.PetBattle;
import com. multiverse.pet. util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org. bukkit.entity. Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java. util.List;
import java.util. UUID;

/**
 * 배틀 턴 태스크
 * 배틀 턴 타임아웃 및 AI 턴 처리
 */
public class BattleTurnTask extends BukkitRunnable {

    private final PetCore plugin;

    // 설정
    private int turnTimeLimit;      // 턴 제한 시간 (초)
    private int warningTime;        // 경고 시간 (초)
    private int aiThinkingDelay;    // AI 생각 시간 (틱)

    public BattleTurnTask(PetCore plugin) {
        this. plugin = plugin;
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        var battleSettings = plugin.getConfigManager().getBattleSettings();
        this.turnTimeLimit = battleSettings.getTurnTimeLimit();
        this.warningTime = battleSettings.getTurnWarningTime();
        this.aiThinkingDelay = battleSettings.getAiThinkingDelay();
    }

    @Override
    public void run() {
        List<PetBattle> activeBattles = plugin.getPetBattleManager().getAllActiveBattles();

        for (PetBattle battle : new ArrayList<>(activeBattles)) {
            processBattle(battle);
        }
    }

    /**
     * 배틀 처리
     */
    private void processBattle(PetBattle battle) {
        // 이미 종료된 배틀 스킵
        if (battle.getStatus() != BattleStatus. IN_PROGRESS) {
            return;
        }

        // AI 배틀인 경우 AI 턴 처리
        if (battle. isAIBattle() && battle.isAITurn()) {
            processAITurn(battle);
            return;
        }

        // 턴 타임아웃 체크
        checkTurnTimeout(battle);
    }

    /**
     * AI 턴 처리
     */
    private void processAITurn(PetBattle battle) {
        // AI 생각 시간 체크
        long turnStartTime = battle.getCurrentTurnStartTime();
        long elapsed = System.currentTimeMillis() - turnStartTime;

        if (elapsed < aiThinkingDelay * 50) {
            // 아직 생각 중
            return;
        }

        // AI 행동 결정 및 실행
        executeAIAction(battle);
    }

    /**
     * AI 행동 실행
     */
    private void executeAIAction(PetBattle battle) {
        int difficulty = battle.getAIDifficulty();

        // 난이도에 따른 AI 전략
        String action = decideAIAction(battle, difficulty);

        switch (action) {
            case "attack":
                plugin.getPetBattleManager().executeAIAttack(battle);
                break;
            case "skill":
                String skillId = selectAISkill(battle, difficulty);
                if (skillId != null) {
                    plugin.getPetBattleManager().executeAISkill(battle, skillId);
                } else {
                    plugin.getPetBattleManager().executeAIAttack(battle);
                }
                break;
            case "defend":
                plugin. getPetBattleManager().executeAIDefend(battle);
                break;
            default:
                plugin. getPetBattleManager().executeAIAttack(battle);
                break;
        }

        // 플레이어에게 AI 행동 알림
        Player player = Bukkit.getPlayer(battle.getPlayer1Id());
        if (player != null) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("battle.ai-action")
                    .replace("{action}", getActionDisplayName(action)));
        }
    }

    /**
     * AI 행동 결정
     */
    private String decideAIAction(PetBattle battle, int difficulty) {
        double aiHealthPercent = battle. getPet2HP() / battle.getPet2MaxHP() * 100;
        double playerHealthPercent = battle.getPet1HP() / battle.getPet1MaxHP() * 100;

        // 난이도별 전략
        if (difficulty <= 2) {
            // 쉬움:  대부분 기본 공격
            if (Math.random() < 0.8) {
                return "attack";
            } else if (Math.random() < 0.5) {
                return "skill";
            } else {
                return "defend";
            }
        } else if (difficulty <= 4) {
            // 보통: 상황에 따른 판단
            if (aiHealthPercent < 30) {
                // 체력 낮으면 방어 or 힐 스킬
                return Math.random() < 0.4 ? "defend" : "skill";
            } else if (playerHealthPercent < 30) {
                // 상대 체력 낮으면 공격
                return Math.random() < 0.7 ? "attack" : "skill";
            } else {
                return Math.random() < 0.6 ?  "attack" : "skill";
            }
        } else {
            // 어려움: 최적 전략
            if (aiHealthPercent < 20) {
                return "defend";
            } else if (playerHealthPercent < 20) {
                // 마무리 공격
                return "skill";
            } else if (aiHealthPercent > 70 && playerHealthPercent > 70) {
                // 초반에는 스킬 적극 사용
                return Math.random() < 0.6 ? "skill" : "attack";
            } else {
                // 상황 판단
                return Math.random() < 0.5 ?  "skill" : "attack";
            }
        }
    }

    /**
     * AI 스킬 선택
     */
    private String selectAISkill(PetBattle battle, int difficulty) {
        // 배틀에서 사용 가능한 AI 스킬 목록
        List<String> availableSkills = battle.getAIAvailableSkills();

        if (availableSkills.isEmpty()) {
            return null;
        }

        // 난이도에 따른 스킬 선택
        if (difficulty <= 2) {
            // 쉬움: 랜덤
            return availableSkills.get((int) (Math.random() * availableSkills.size()));
        } else if (difficulty <= 4) {
            // 보통: 50% 최적, 50% 랜덤
            if (Math.random() < 0.5) {
                return selectOptimalSkill(battle, availableSkills);
            } else {
                return availableSkills.get((int) (Math.random() * availableSkills.size()));
            }
        } else {
            // 어려움:  항상 최적
            return selectOptimalSkill(battle, availableSkills);
        }
    }

    /**
     * 최적 스킬 선택
     */
    private String selectOptimalSkill(PetBattle battle, List<String> availableSkills) {
        double aiHealthPercent = battle.getPet2HP() / battle.getPet2MaxHP() * 100;

        // 체력 낮으면 힐 스킬 우선
        if (aiHealthPercent < 40) {
            for (String skillId : availableSkills) {
                if (skillId.contains("heal") || skillId.contains("recovery")) {
                    return skillId;
                }
            }
        }

        // 그 외에는 공격 스킬
        for (String skillId :  availableSkills) {
            if (skillId.contains("attack") || skillId.contains("strike") || skillId.contains("damage")) {
                return skillId;
            }
        }

        // 기본값
        return availableSkills.get(0);
    }

    /**
     * 턴 타임아웃 체크
     */
    private void checkTurnTimeout(PetBattle battle) {
        long turnStartTime = battle. getCurrentTurnStartTime();
        long elapsed = System.currentTimeMillis() - turnStartTime;
        int elapsedSeconds = (int) (elapsed / 1000);

        UUID currentPlayerId = battle.getCurrentTurnPlayerId();
        Player currentPlayer = Bukkit.getPlayer(currentPlayerId);

        // 경고 시간
        int remainingTime = turnTimeLimit - elapsedSeconds;

        if (remainingTime == warningTime && currentPlayer != null) {
            // 경고
            MessageUtil.sendMessage(currentPlayer, plugin.getConfigManager().getMessage("battle. turn-warning")
                    . replace("{seconds}", String.valueOf(remainingTime)));
            currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
        } else if (remainingTime == 5 && currentPlayer != null) {
            // 5초 경고
            MessageUtil.sendMessage(currentPlayer, plugin.getConfigManager().getMessage("battle.turn-warning")
                    . replace("{seconds}", "5"));
            currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.5f);
        }

        // 타임아웃
        if (elapsedSeconds >= turnTimeLimit) {
            handleTurnTimeout(battle, currentPlayerId);
        }
    }

    /**
     * 턴 타임아웃 처리
     */
    private void handleTurnTimeout(PetBattle battle, UUID timeoutPlayerId) {
        Player timeoutPlayer = Bukkit.getPlayer(timeoutPlayerId);

        // 타임아웃 플레이어 알림
        if (timeoutPlayer != null) {
            MessageUtil.sendMessage(timeoutPlayer, plugin.getConfigManager().getMessage("battle. turn-timeout"));
            timeoutPlayer.playSound(timeoutPlayer. getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }

        // 상대에게 알림
        UUID opponentId = battle. getOpponentId(timeoutPlayerId);
        Player opponent = Bukkit. getPlayer(opponentId);
        if (opponent != null) {
            MessageUtil.sendMessage(opponent, plugin. getConfigManager().getMessage("battle.opponent-timeout"));
        }

        // 자동 방어 또는 턴 스킵
        if (plugin.getConfigManager().getBattleSettings().isAutoDefendOnTimeout()) {
            plugin.getPetBattleManager().performDefend(timeoutPlayer);
        } else {
            plugin.getPetBattleManager().skipTurn(battle, timeoutPlayerId);
        }

        // 연속 타임아웃 시 패배 처리
        battle.incrementTimeoutCount(timeoutPlayerId);
        if (battle.getTimeoutCount(timeoutPlayerId) >= 3) {
            plugin.getPetBattleManager().forfeitDueToTimeout(battle, timeoutPlayerId);
        }
    }

    /**
     * 행동 표시 이름
     */
    private String getActionDisplayName(String action) {
        switch (action) {
            case "attack":  return "공격";
            case "skill": return "스킬";
            case "defend": return "방어";
            default: return action;
        }
    }

    /**
     * 설정 리로드
     */
    public void reloadSettings() {
        loadSettings();
    }

    /**
     * 태스크 시작
     */
    public void start() {
        // 1초(20틱)마다 실행
        this.runTaskTimer(plugin, 20L, 20L);
    }

    /**
     * 태스크 중지
     */
    public void stop() {
        try {
            this.cancel();
        } catch (IllegalStateException ignored) {
        }
    }
}