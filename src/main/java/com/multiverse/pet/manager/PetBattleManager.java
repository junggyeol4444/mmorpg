package com.multiverse.pet. manager;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. api.event.PetBattleStartEvent;
import com.multiverse.pet.api.event. PetBattleEndEvent;
import com.multiverse. pet.model.Pet;
import com. multiverse.pet. model.PetStatus;
import com. multiverse.pet. model.battle.*;
import com.multiverse.pet.model.skill.PetSkill;
import com. multiverse.pet. util.MessageUtil;
import org. bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java. util.*;
import java.util.concurrent. ConcurrentHashMap;

/**
 * 펫 배틀 매니저 클래스
 * 펫 대결 생성, 진행, 종료 관리
 */
public class PetBattleManager {

    private final PetCore plugin;

    // 활성 배틀 (배틀ID -> 배틀)
    private final Map<UUID, PetBattle> activeBattles;

    // 플레이어별 현재 배틀
    private final Map<UUID, UUID> playerBattles;

    // 대결 요청 (요청자 -> 요청 데이터)
    private final Map<UUID, BattleRequest> battleRequests;

    // 배틀 타이머
    private BukkitTask battleTickTask;

    // 설정 값
    private int requestTimeout;
    private int turnTimeout;
    private int maxTurns;
    private double baseDamageMultiplier;
    private double criticalDamageMultiplier;
    private int baseExpReward;
    private int baseRatingChange;
    private boolean allowSpectators;

    /**
     * 생성자
     */
    public PetBattleManager(PetCore plugin) {
        this. plugin = plugin;
        this.activeBattles = new ConcurrentHashMap<>();
        this.playerBattles = new ConcurrentHashMap<>();
        this.battleRequests = new ConcurrentHashMap<>();
        loadSettings();
        startBattleTickTask();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        this.requestTimeout = plugin.getConfigManager().getBattleSettings().getRequestTimeout();
        this.turnTimeout = plugin.getConfigManager().getBattleSettings().getTurnTimeout();
        this.maxTurns = plugin. getConfigManager().getBattleSettings().getMaxTurns();
        this.baseDamageMultiplier = plugin.getConfigManager().getBattleSettings().getDamageMultiplier();
        this.criticalDamageMultiplier = plugin.getConfigManager().getBattleSettings().getCriticalMultiplier();
        this.baseExpReward = plugin.getConfigManager().getBattleSettings().getBaseExpReward();
        this.baseRatingChange = plugin. getConfigManager().getBattleSettings().getBaseRatingChange();
        this.allowSpectators = plugin.getConfigManager().getBattleSettings().isAllowSpectators();
    }

    /**
     * 배틀 틱 태스크 시작
     */
    private void startBattleTickTask() {
        battleTickTask = Bukkit. getScheduler().runTaskTimer(plugin, this:: tickBattles, 20L, 20L);
    }

    /**
     * 배틀 틱 태스크 중지
     */
    public void stopBattleTickTask() {
        if (battleTickTask != null && !battleTickTask. isCancelled()) {
            battleTickTask.cancel();
        }
    }

    // ===== 대결 요청 =====

    /**
     * 대결 요청
     *
     * @param challenger 도전자
     * @param target 대상
     * @param challengerPet 도전자 펫
     * @param type 대결 타입
     * @return 요청 성공 여부
     */
    public boolean requestBattle(Player challenger, Player target, Pet challengerPet, BattleType type) {
        UUID challengerId = challenger.getUniqueId();
        UUID targetId = target.getUniqueId();

        // 자기 자신에게 요청 불가
        if (challengerId.equals(targetId)) {
            MessageUtil.sendMessage(challenger, plugin.getConfigManager().getMessage("battle.cannot-self"));
            return false;
        }

        // 이미 배틀 중인지 확인
        if (isInBattle(challengerId)) {
            MessageUtil.sendMessage(challenger, plugin.getConfigManager().getMessage("battle.already-in-battle"));
            return false;
        }

        if (isInBattle(targetId)) {
            MessageUtil.sendMessage(challenger, plugin.getConfigManager().getMessage("battle.target-in-battle"));
            return false;
        }

        // 펫 상태 확인
        if (!canBattle(challengerPet)) {
            MessageUtil.sendMessage(challenger, plugin.getConfigManager().getMessage("battle.pet-cannot-battle")
                    .replace("{reason}", getCannotBattleReason(challengerPet)));
            return false;
        }

        // 이미 요청 중인지 확인
        if (battleRequests.containsKey(challengerId)) {
            MessageUtil.sendMessage(challenger, plugin.getConfigManager().getMessage("battle.already-requested"));
            return false;
        }

        // 대결 타입별 조건 확인
        if (challengerPet.getLevel() < type.getMinPetLevel()) {
            MessageUtil.sendMessage(challenger, plugin.getConfigManager().getMessage("battle.level-too-low")
                    .replace("{level}", String.valueOf(type.getMinPetLevel())));
            return false;
        }

        // 입장료 확인
        double entryFee = type.getEntryFee();
        if (entryFee > 0 && ! plugin.getPlayerDataCoreHook().hasGold(challengerId, entryFee)) {
            MessageUtil.sendMessage(challenger, plugin.getConfigManager().getMessage("battle.not-enough-gold")
                    .replace("{cost}", String.format("%. 0f", entryFee)));
            return false;
        }

        // 요청 생성
        BattleRequest request = new BattleRequest(challengerId, targetId, challengerPet. getPetId(), type);
        battleRequests.put(challengerId, request);

        // 대상에게 알림
        MessageUtil.sendMessage(target, plugin.getConfigManager().getMessage("battle.request-received")
                .replace("{player}", challenger.getName())
                .replace("{pet}", challengerPet.getPetName())
                .replace("{type}", type. getDisplayName()));

        MessageUtil.sendMessage(challenger, plugin.getConfigManager().getMessage("battle.request-sent")
                .replace("{player}", target.getName()));

        // 타임아웃 스케줄러
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (battleRequests.containsKey(challengerId)) {
                battleRequests.remove(challengerId);
                Player p = Bukkit.getPlayer(challengerId);
                if (p != null) {
                    MessageUtil.sendMessage(p, plugin.getConfigManager().getMessage("battle.request-timeout"));
                }
            }
        }, requestTimeout * 20L);

        return true;
    }

    /**
     * 대결 요청 수락
     *
     * @param target 대상 (수락자)
     * @param challenger 도전자
     * @param targetPet 대상의 펫
     * @return 수락 성공 여부
     */
    public boolean acceptBattle(Player target, Player challenger, Pet targetPet) {
        UUID targetId = target.getUniqueId();
        UUID challengerId = challenger.getUniqueId();

        // 요청 확인
        BattleRequest request = battleRequests.get(challengerId);
        if (request == null || ! request.getTargetId().equals(targetId)) {
            MessageUtil.sendMessage(target, plugin.getConfigManager().getMessage("battle.no-request"));
            return false;
        }

        // 펫 상태 확인
        if (!canBattle(targetPet)) {
            MessageUtil.sendMessage(target, plugin.getConfigManager().getMessage("battle.pet-cannot-battle")
                    .replace("{reason}", getCannotBattleReason(targetPet)));
            return false;
        }

        // 레벨 확인
        if (targetPet.getLevel() < request.getType().getMinPetLevel()) {
            MessageUtil.sendMessage(target, plugin.getConfigManager().getMessage("battle.level-too-low")
                    .replace("{level}", String. valueOf(request.getType().getMinPetLevel())));
            return false;
        }

        // 입장료 확인
        double entryFee = request.getType().getEntryFee();
        if (entryFee > 0 && ! plugin.getPlayerDataCoreHook().hasGold(targetId, entryFee)) {
            MessageUtil.sendMessage(target, plugin. getConfigManager().getMessage("battle.not-enough-gold")
                    .replace("{cost}", String.format("%.0f", entryFee)));
            return false;
        }

        // 요청 제거
        battleRequests.remove(challengerId);

        // 도전자 펫 가져오기
        Pet challengerPet = plugin.getPetManager().getPetById(challengerId, request.getPetId());
        if (challengerPet == null) {
            MessageUtil.sendMessage(target, plugin.getConfigManager().getMessage("battle.challenger-pet-not-found"));
            return false;
        }

        // 배틀 시작
        return startBattle(challenger, target, challengerPet, targetPet, request.getType());
    }

    /**
     * 대결 요청 거절
     */
    public boolean declineBattle(Player target, Player challenger) {
        UUID challengerId = challenger.getUniqueId();
        UUID targetId = target.getUniqueId();

        BattleRequest request = battleRequests.get(challengerId);
        if (request == null || !request.getTargetId().equals(targetId)) {
            MessageUtil.sendMessage(target, plugin.getConfigManager().getMessage("battle.no-request"));
            return false;
        }

        battleRequests.remove(challengerId);

        MessageUtil.sendMessage(target, plugin.getConfigManager().getMessage("battle.declined"));
        MessageUtil.sendMessage(challenger, plugin.getConfigManager().getMessage("battle.request-declined")
                .replace("{player}", target.getName()));

        return true;
    }

    // ===== 배틀 시작 =====

    /**
     * PvP 배틀 시작
     */
    public boolean startBattle(Player player1, Player player2, Pet pet1, Pet pet2, BattleType type) {
        UUID player1Id = player1.getUniqueId();
        UUID player2Id = player2.getUniqueId();

        // 배틀 생성
        PetBattle battle = new PetBattle(player1Id, player2Id, pet1.getPetId(), pet2.getPetId(), type);
        battle.setPlayer1Name(player1.getName());
        battle.setPlayer2Name(player2.getName());
        battle.setPet1Name(pet1.getPetName());
        battle.setPet2Name(pet2.getPetName());
        battle.setMaxTurns(type.getMaxTurns());
        battle.setTurnTimeLimit(type.getTurnTimeLimit());
        battle.setAllowSpectators(type.allowsSpectators() && allowSpectators);

        // 이벤트 발생
        PetBattleStartEvent event = new PetBattleStartEvent(player1, player2, pet1, pet2, battle);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        // 입장료 차감
        double entryFee = type.getEntryFee();
        if (entryFee > 0) {
            plugin. getPlayerDataCoreHook().withdrawGold(player1Id, entryFee);
            plugin.getPlayerDataCoreHook().withdrawGold(player2Id, entryFee);
        }

        // 펫 상태 변경
        pet1.setStatus(PetStatus. BATTLING);
        pet2.setStatus(PetStatus. BATTLING);

        // 활성 펫이면 해제
        if (pet1.isActive()) {
            plugin.getPetManager().unsummonPet(player1, pet1.getPetId());
        }
        if (pet2.isActive()) {
            plugin.getPetManager().unsummonPet(player2, pet2.getPetId());
        }

        // 배틀 등록
        activeBattles.put(battle.getBattleId(), battle);
        playerBattles.put(player1Id, battle. getBattleId());
        playerBattles.put(player2Id, battle. getBattleId());

        // 배틀 시작
        double pet1MaxHP = pet1.getTotalStat("health");
        double pet2MaxHP = pet2.getTotalStat("health");
        battle.start(pet1MaxHP, pet2MaxHP);

        // 저장
        plugin.getPetManager().savePetData(player1Id, pet1);
        plugin.getPetManager().savePetData(player2Id, pet2);

        // 알림
        MessageUtil.sendMessage(player1, plugin.getConfigManager().getMessage("battle.started")
                .replace("{opponent}", player2.getName())
                .replace("{pet}", pet2.getPetName()));

        MessageUtil.sendMessage(player2, plugin. getConfigManager().getMessage("battle.started")
                .replace("{opponent}", player1.getName())
                .replace("{pet}", pet1.getPetName()));

        // 첫 턴 알림
        notifyCurrentTurn(battle);

        return true;
    }

    /**
     * AI 배틀 시작 (연습 모드)
     */
    public boolean startAIBattle(Player player, Pet pet, int difficulty) {
        UUID playerId = player.getUniqueId();

        if (isInBattle(playerId)) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("battle.already-in-battle"));
            return false;
        }

        if (!canBattle(pet)) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("battle.pet-cannot-battle")
                    .replace("{reason}", getCannotBattleReason(pet)));
            return false;
        }

        // AI 배틀 생성
        PetBattle battle = new PetBattle(playerId, pet.getPetId(), difficulty);
        battle.setPlayer1Name(player.getName());
        battle.setPet1Name(pet.getPetName());
        battle.setType(BattleType. PRACTICE);

        // AI 펫 스탯 생성
        double aiHP = 100 + (difficulty * 50) + (pet.getLevel() * 5);
        battle.setPet2MaxHP(aiHP);

        // 펫 상태 변경
        pet.setStatus(PetStatus.BATTLING);

        if (pet.isActive()) {
            plugin.getPetManager().unsummonPet(player, pet. getPetId());
        }

        // 배틀 등록
        activeBattles.put(battle.getBattleId(), battle);
        playerBattles.put(playerId, battle.getBattleId());

        // 배틀 시작
        double petMaxHP = pet. getTotalStat("health");
        battle.start(petMaxHP, aiHP);

        // 저장
        plugin.getPetManager().savePetData(playerId, pet);

        // 알림
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("battle.ai-started")
                .replace("{difficulty}", String.valueOf(difficulty)));

        return true;
    }

    // ===== 배틀 행동 =====

    /**
     * 공격 행동
     */
    public boolean performAttack(Player player) {
        PetBattle battle = getPlayerBattle(player. getUniqueId());
        if (battle == null || !battle.isPlayerTurn(player. getUniqueId())) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("battle.not-your-turn"));
            return false;
        }

        Pet pet = getPlayerBattlePet(player. getUniqueId(), battle);
        if (pet == null) return false;

        // 데미지 계산
        double baseDamage = pet.getTotalStat("attack") * baseDamageMultiplier;

        // 치명타 확인
        double critChance = pet.getTotalStat("critical_chance");
        boolean isCrit = Math.random() * 100 < critChance;
        if (isCrit) {
            baseDamage *= criticalDamageMultiplier;
        }

        // 빗나감 확인
        Pet opponentPet = getOpponentPet(battle, player. getUniqueId());
        double accuracy = 95 - (opponentPet != null ? opponentPet.getTotalStat("evasion") : 0);
        boolean isMiss = Math. random() * 100 > accuracy;

        // 턴 기록
        BattleTurn turn = new BattleTurn(battle.getCurrentTurn(), player.getUniqueId(), pet.getPetId(),
                BattleTurn. TurnAction.ATTACK);

        int targetPetNumber = battle.getPlayer1Id().equals(player.getUniqueId()) ? 2 : 1;

        if (isMiss) {
            turn.setMiss(true);
            turn.setDamageDealt(0);
            battle.addLog(pet.getPetName() + "의 공격이 빗나갔습니다!");
        } else {
            turn.setDamageDealt(baseDamage);
            turn.setCritical(isCrit);

            // 데미지 적용
            boolean knockout = battle.applyDamage(targetPetNumber, baseDamage);

            if (knockout) {
                // 배틀 종료
                endBattle(battle, player. getUniqueId());
                return true;
            }
        }

        battle.recordTurn(turn);

        // 턴 진행
        battle.nextTurn();

        // AI 배틀이면 AI 턴 처리
        if (battle.isAIBattle()) {
            processAITurn(battle);
        } else {
            notifyCurrentTurn(battle);
        }

        return true;
    }

    /**
     * 스킬 사용
     */
    public boolean performSkill(Player player, String skillId) {
        PetBattle battle = getPlayerBattle(player.getUniqueId());
        if (battle == null || !battle.isPlayerTurn(player.getUniqueId())) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("battle.not-your-turn"));
            return false;
        }

        Pet pet = getPlayerBattlePet(player.getUniqueId(), battle);
        if (pet == null) return false;

        PetSkill skill = pet.getSkill(skillId);
        if (skill == null) {
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("battle. skill-not-found"));
            return false;
        }

        if (! skill.canUse(pet. getLevel(), pet.getHunger())) {
            MessageUtil.sendMessage(player, "&c" + skill.getCannotUseReason(pet. getLevel(), pet.getHunger()));
            return false;
        }

        // 스킬 사용
        skill.use();

        // 턴 기록
        BattleTurn turn = new BattleTurn(battle.getCurrentTurn(), player.getUniqueId(), pet.getPetId(),
                BattleTurn.TurnAction.SKILL);
        turn.setSkillId(skillId);
        turn.setSkillName(skill.getName());

        // 스킬 효과 적용
        double damage = skill.getEffectValue("damage") * baseDamageMultiplier;
        double healing = skill.getEffectValue("healing");

        int targetPetNumber = battle.getPlayer1Id().equals(player.getUniqueId()) ? 2 : 1;
        int selfPetNumber = battle. getPlayer1Id().equals(player.getUniqueId()) ? 1 : 2;

        if (damage > 0) {
            turn.setDamageDealt(damage);
            boolean knockout = battle.applyDamage(targetPetNumber, damage);
            if (knockout) {
                endBattle(battle, player.getUniqueId());
                return true;
            }
        }

        if (healing > 0) {
            turn.setHealingDone(healing);
            battle. applyHealing(selfPetNumber, healing);
        }

        battle.recordTurn(turn);
        battle.addLog(pet.getPetName() + "이(가) [" + skill.getName() + "] 스킬을 사용했습니다!");

        // 턴 진행
        battle.nextTurn();

        if (battle.isAIBattle()) {
            processAITurn(battle);
        } else {
            notifyCurrentTurn(battle);
        }

        return true;
    }

    /**
     * 방어 행동
     */
    public boolean performDefend(Player player) {
        PetBattle battle = getPlayerBattle(player.getUniqueId());
        if (battle == null || !battle.isPlayerTurn(player. getUniqueId())) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("battle.not-your-turn"));
            return false;
        }

        Pet pet = getPlayerBattlePet(player.getUniqueId(), battle);
        if (pet == null) return false;

        // 턴 기록
        BattleTurn turn = new BattleTurn(battle.getCurrentTurn(), player.getUniqueId(), pet.getPetId(),
                BattleTurn.TurnAction.DEFEND);
        turn.setDefendAction(30); // 30% 데미지 감소

        battle.recordTurn(turn);
        battle.addLog(pet.getPetName() + "이(가) 방어 태세를 취했습니다!");

        // 턴 진행
        battle.nextTurn();

        if (battle.isAIBattle()) {
            processAITurn(battle);
        } else {
            notifyCurrentTurn(battle);
        }

        return true;
    }

    /**
     * 항복
     */
    public boolean surrender(Player player) {
        PetBattle battle = getPlayerBattle(player. getUniqueId());
        if (battle == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("battle.not-in-battle"));
            return false;
        }

        // 상대방 승리
        UUID winnerId;
        if (battle.isAIBattle()) {
            winnerId = null; // AI 승리
        } else {
            winnerId = battle.getPlayer1Id().equals(player.getUniqueId())
                    ? battle.getPlayer2Id()
                    :  battle.getPlayer1Id();
        }

        endBattle(battle, winnerId);

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("battle.surrendered"));

        return true;
    }

    // ===== AI 턴 처리 =====

    /**
     * AI 턴 처리
     */
    private void processAITurn(PetBattle battle) {
        if (! battle.isActive()) return;

        // 1초 후 AI 행동
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (! battle.isActive()) return;

            int difficulty = battle.getAiDifficulty();
            double actionRoll = Math.random() * 100;

            double damage;

            if (actionRoll < 60 + difficulty * 5) {
                // 공격
                damage = 10 + difficulty * 5 + Math.random() * 10;
                boolean knockout = battle.applyDamage(1, damage);
                battle.addLog("AI가 공격하여 " + String.format("%.1f", damage) + " 데미지를 입혔습니다.");

                if (knockout) {
                    endBattle(battle, null); // AI 승리
                    return;
                }
            } else if (actionRoll < 80) {
                // 방어
                battle.addLog("AI가 방어 태세를 취했습니다.");
            } else {
                // 스킬 사용
                damage = 15 + difficulty * 7 + Math.random() * 15;
                boolean knockout = battle.applyDamage(1, damage);
                battle.addLog("AI가 스킬을 사용하여 " + String.format("%. 1f", damage) + " 데미지를 입혔습니다.");

                if (knockout) {
                    endBattle(battle, null);
                    return;
                }
            }

            battle.nextTurn();
            notifyCurrentTurn(battle);

        }, 20L);
    }

    // ===== 배틀 종료 =====

    /**
     * 배틀 종료
     */
    private void endBattle(PetBattle battle, UUID winnerId) {
        // 배틀 종료 처리
        BattleResult. ResultType resultType;
        if (winnerId == null && ! battle.isAIBattle()) {
            resultType = BattleResult.ResultType.DRAW;
        } else if (winnerId == null && battle.isAIBattle()) {
            resultType = BattleResult.ResultType. LOSE;
        } else {
            resultType = BattleResult.ResultType.WIN;
        }

        battle.endBattle(winnerId, resultType);

        // 결과 생성
        BattleResult result = BattleResult.fromBattle(battle);
        battle.calculateRewards(baseExpReward, baseRatingChange);

        // 펫 상태 복구
        UUID player1Id = battle.getPlayer1Id();
        UUID player2Id = battle.getPlayer2Id();

        Pet pet1 = plugin.getPetManager().getPetById(player1Id, battle.getPet1Id());
        if (pet1 != null) {
            pet1.setStatus(PetStatus. STORED);
            plugin. getPetManager().savePetData(player1Id, pet1);
        }

        if (! battle.isAIBattle() && player2Id != null) {
            Pet pet2 = plugin.getPetManager().getPetById(player2Id, battle.getPet2Id());
            if (pet2 != null) {
                pet2.setStatus(PetStatus.STORED);
                plugin.getPetManager().savePetData(player2Id, pet2);
            }
        }

        // 보상 지급
        applyBattleRewards(battle, result);

        // 이벤트 발생
        Player player1 = Bukkit.getPlayer(player1Id);
        Player player2 = battle.isAIBattle() ? null : Bukkit. getPlayer(player2Id);

        if (player1 != null || player2 != null) {
            PetBattleEndEvent event = new PetBattleEndEvent(player1, player2, battle, result);
            Bukkit. getPluginManager().callEvent(event);
        }

        // 결과 알림
        sendBattleResult(battle, result);

        // 정리
        cleanupBattle(battle);
    }

    /**
     * 배틀 보상 적용
     */
    private void applyBattleRewards(PetBattle battle, BattleResult result) {
        UUID winnerId = result.getWinnerId();
        UUID loserId = result.getLoserId();

        // 경험치 지급
        if (winnerId != null) {
            Pet winnerPet = plugin. getPetManager().getPetById(winnerId, result.getWinnerPetId());
            if (winnerPet != null) {
                plugin.getPetLevelManager().addBattleExp(winnerPet, battle.getWinnerExp(), true);
                winnerPet.incrementBattleWins();
                plugin.getPetManager().savePetData(winnerId, winnerPet);
            }
        }

        if (loserId != null && !battle.isAIBattle()) {
            Pet loserPet = plugin. getPetManager().getPetById(loserId, result.getLoserPetId());
            if (loserPet != null) {
                plugin.getPetLevelManager().addBattleExp(loserPet, battle.getLoserExp(), false);
                loserPet. incrementBattleLosses();
                plugin.getPetManager().savePetData(loserId, loserPet);
            }
        }

        // 레이팅 변경 (랭킹전만)
        if (battle.getType().isAffectsRanking() && winnerId != null && loserId != null) {
            plugin.getPlayerDataCoreHook().addRating(winnerId, battle.getRatingChange());
            plugin.getPlayerDataCoreHook().addRating(loserId, -battle.getRatingChange());
        }
    }

    /**
     * 배틀 결과 전송
     */
    private void sendBattleResult(PetBattle battle, BattleResult result) {
        Player player1 = Bukkit.getPlayer(battle.getPlayer1Id());

        if (player1 != null) {
            MessageUtil. sendMessage(player1, result.getSummary());
        }

        if (! battle.isAIBattle()) {
            Player player2 = Bukkit. getPlayer(battle. getPlayer2Id());
            if (player2 != null) {
                MessageUtil. sendMessage(player2, result.getSummary());
            }
        }
    }

    /**
     * 배틀 정리
     */
    private void cleanupBattle(PetBattle battle) {
        activeBattles.remove(battle.getBattleId());
        playerBattles. remove(battle.getPlayer1Id());
        if (! battle.isAIBattle()) {
            playerBattles.remove(battle. getPlayer2Id());
        }
    }

    // ===== 틱 처리 =====

    /**
     * 배틀 틱 처리 (매초 호출)
     */
    private void tickBattles() {
        for (PetBattle battle : activeBattles.values()) {
            if (!battle.isActive()) continue;

            // 턴 타임아웃 체크
            if (battle.isTurnTimeout()) {
                handleTurnTimeout(battle);
            }
        }
    }

    /**
     * 턴 타임아웃 처리
     */
    private void handleTurnTimeout(PetBattle battle) {
        UUID currentPlayerId = battle.getCurrentTurnPlayerId();

        if (battle.isAIBattle() && ! currentPlayerId.equals(battle.getPlayer1Id())) {
            // AI 턴이면 AI 행동
            processAITurn(battle);
            return;
        }

        Player player = Bukkit.getPlayer(currentPlayerId);
        if (player != null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("battle.turn-timeout"));
        }

        // 자동으로 방어 행동
        Pet pet = getPlayerBattlePet(currentPlayerId, battle);
        if (pet != null) {
            BattleTurn turn = new BattleTurn(battle.getCurrentTurn(), currentPlayerId, pet. getPetId(),
                    BattleTurn.TurnAction. TIMEOUT);
            battle. recordTurn(turn);
        }

        battle.nextTurn();

        if (battle.isAIBattle()) {
            processAITurn(battle);
        } else {
            notifyCurrentTurn(battle);
        }
    }

    /**
     * 현재 턴 알림
     */
    private void notifyCurrentTurn(PetBattle battle) {
        if (! battle.isActive()) return;

        UUID currentPlayerId = battle. getCurrentTurnPlayerId();
        Player currentPlayer = Bukkit.getPlayer(currentPlayerId);

        if (currentPlayer != null) {
            MessageUtil.sendMessage(currentPlayer, plugin.getConfigManager().getMessage("battle.your-turn")
                    .replace("{time}", String.valueOf(battle.getTurnTimeLimit())));
        }

        // 상대방에게도 알림
        if (! battle.isAIBattle()) {
            UUID opponentId = battle.getPlayer1Id().equals(currentPlayerId)
                    ? battle.getPlayer2Id()
                    :  battle.getPlayer1Id();
            Player opponent = Bukkit. getPlayer(opponentId);
            if (opponent != null) {
                MessageUtil.sendMessage(opponent, plugin.getConfigManager().getMessage("battle.opponent-turn")
                        .replace("{player}", currentPlayer != null ? currentPlayer. getName() : "상대방"));
            }
        }
    }

    // ===== 관전 =====

    /**
     * 배틀 관전
     */
    public boolean spectate(Player spectator, UUID battleId) {
        PetBattle battle = activeBattles.get(battleId);
        if (battle == null) {
            MessageUtil.sendMessage(spectator, plugin. getConfigManager().getMessage("battle.not-found"));
            return false;
        }

        if (!battle. isAllowSpectators()) {
            MessageUtil.sendMessage(spectator, plugin.getConfigManager().getMessage("battle.spectate-disabled"));
            return false;
        }

        battle.addSpectator(spectator. getUniqueId());
        MessageUtil.sendMessage(spectator, plugin. getConfigManager().getMessage("battle.spectating")
                .replace("{player1}", battle.getPlayer1Name())
                .replace("{player2}", battle.getPlayer2Name()));

        return true;
    }

    /**
     * 관전 중지
     */
    public boolean stopSpectating(Player spectator) {
        for (PetBattle battle : activeBattles.values()) {
            if (battle.isSpectator(spectator. getUniqueId())) {
                battle.removeSpectator(spectator. getUniqueId());
                MessageUtil.sendMessage(spectator, plugin. getConfigManager().getMessage("battle.stop-spectating"));
                return true;
            }
        }
        return false;
    }

    // ===== 유틸리티 =====

    /**
     * 배틀 중인지 확인
     */
    public boolean isInBattle(UUID playerId) {
        return playerBattles.containsKey(playerId);
    }

    /**
     * 플레이어의 현재 배틀 가져오기
     */
    public PetBattle getPlayerBattle(UUID playerId) {
        UUID battleId = playerBattles.get(playerId);
        return battleId != null ?  activeBattles. get(battleId) : null;
    }

    /**
     * 배틀에서 플레이어의 펫 가져오기
     */
    private Pet getPlayerBattlePet(UUID playerId, PetBattle battle) {
        UUID petId;
        if (battle.getPlayer1Id().equals(playerId)) {
            petId = battle.getPet1Id();
        } else {
            petId = battle.getPet2Id();
        }
        return plugin.getPetManager().getPetById(playerId, petId);
    }

    /**
     * 상대 펫 가져오기
     */
    private Pet getOpponentPet(PetBattle battle, UUID playerId) {
        if (battle.isAIBattle()) {
            return null;
        }

        UUID opponentId = battle.getPlayer1Id().equals(playerId)
                ? battle.getPlayer2Id()
                : battle.getPlayer1Id();
        UUID opponentPetId = battle.getPlayer1Id().equals(playerId)
                ? battle.getPet2Id()
                : battle.getPet1Id();

        return plugin.getPetManager().getPetById(opponentId, opponentPetId);
    }

    /**
     * 배틀 가능 여부 확인
     */
    public boolean canBattle(Pet pet) {
        if (pet == null) return false;
        if (pet. getStatus() != PetStatus. STORED && pet.getStatus() != PetStatus. ACTIVE) return false;
        if (pet.getHealth() <= 0) return false;
        if (pet.getHunger() <= 10) return false;
        return true;
    }

    /**
     * 배틀 불가 이유
     */
    public String getCannotBattleReason(Pet pet) {
        if (pet == null) return "펫을 찾을 수 없습니다. ";
        if (pet.getStatus() != PetStatus. STORED && pet. getStatus() != PetStatus.ACTIVE)
            return "현재 상태에서 배틀할 수 없습니다.  (" + pet.getStatus().getDisplayName() + ")";
        if (pet.getHealth() <= 0) return "체력이 0입니다.";
        if (pet. getHunger() <= 10) return "배고픔이 너무 낮습니다. ";
        return null;
    }

    /**
     * 활성 배틀 목록 가져오기
     */
    public Collection<PetBattle> getActiveBattles() {
        return Collections.unmodifiableCollection(activeBattles.values());
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
        stopBattleTickTask();

        // 모든 활성 배틀 강제 종료
        for (PetBattle battle : new ArrayList<>(activeBattles.values())) {
            battle.endBattle(null, BattleResult.ResultType.DRAW);
            cleanupBattle(battle);
        }
    }

    // ===== 내부 클래스 =====

    /**
     * 배틀 요청 데이터
     */
    public static class BattleRequest {
        private final UUID challengerId;
        private final UUID targetId;
        private final UUID petId;
        private final BattleType type;
        private final long requestTime;

        public BattleRequest(UUID challengerId, UUID targetId, UUID petId, BattleType type) {
            this. challengerId = challengerId;
            this. targetId = targetId;
            this. petId = petId;
            this. type = type;
            this.requestTime = System.currentTimeMillis();
        }

        public UUID getChallengerId() {
            return challengerId;
        }

        public UUID getTargetId() {
            return targetId;
        }

        public UUID getPetId() {
            return petId;
        }

        public BattleType getType() {
            return type;
        }

        public long getRequestTime() {
            return requestTime;
        }
    }
}