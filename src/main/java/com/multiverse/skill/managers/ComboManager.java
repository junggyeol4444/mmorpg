package com. multiverse.skill.managers;

import com.multiverse.skill.SkillCore;
import com.multiverse. skill.data.models.PlayerComboState;
import com.multiverse. skill.data.models.SkillCombo;
import com.multiverse. skill.events. SkillComboCompleteEvent;
import com.multiverse.skill.utils.MessageUtils;
import org.bukkit. Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent. ConcurrentHashMap;

public class ComboManager {

    private final SkillCore plugin;
    private final Map<String, SkillCombo> combos = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerComboState> playerComboStates = new ConcurrentHashMap<>();

    public ComboManager(SkillCore plugin) {
        this.plugin = plugin;
        loadCombos();
    }

    /**
     * 모든 콤보 로드
     */
    private void loadCombos() {
        plugin.getComboDataLoader().loadAllCombos().forEach(combo ->
            combos.put(combo.getComboId(), combo)
        );
        plugin.getLogger().info("✅ " + combos.size() + "개의 콤보가 로드되었습니다.");
    }

    /**
     * 콤보 등록
     */
    public void registerCombo(SkillCombo combo) {
        combos.put(combo.getComboId(), combo);
    }

    /**
     * 모든 콤보 조회
     */
    public Collection<SkillCombo> getAllCombos() {
        return new ArrayList<>(combos.values());
    }

    /**
     * 스킬 사용 시 콤보 체크
     */
    public void onSkillUse(Player player, String skillId) {
        UUID playerUUID = player.getUniqueId();

        // 플레이어 콤보 상태 초기화 (없으면)
        if (!playerComboStates.containsKey(playerUUID)) {
            playerComboStates. put(playerUUID, new PlayerComboState());
        }

        PlayerComboState state = playerComboStates.get(playerUUID);

        // 시간 제한 체크 (3초)
        if (state.getLastSkillTime() > 0) {
            long timeSinceLastSkill = System.currentTimeMillis() - state.getLastSkillTime();
            if (timeSinceLastSkill > 3000) {
                state.getCurrentSequence().clear();
            }
        }

        // 현재 시퀀스에 스킬 추가
        state.getCurrentSequence().add(skillId);
        state.setLastSkillTime(System.currentTimeMillis());

        // 콤보 매칭 체크
        SkillCombo matchedCombo = checkComboMatch(state.getCurrentSequence());
        if (matchedCombo != null) {
            completeCombo(player, matchedCombo);
            state.setMatchingComboId(matchedCombo.getComboId());
            state.getCurrentSequence().clear();
        }
    }

    /**
     * 콤보 매칭 체크
     */
    public SkillCombo checkComboMatch(List<String> sequence) {
        for (SkillCombo combo : combos.values()) {
            if (combo.getSkillSequence().equals(sequence)) {
                return combo;
            }
        }
        return null;
    }

    /**
     * 콤보 완성 여부 확인
     */
    public boolean isComboComplete(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerComboState state = playerComboStates.get(playerUUID);
        return state != null && state.getMatchingComboId() != null && ! state.getMatchingComboId().isEmpty();
    }

    /**
     * 콤보 완성 처리
     */
    private void completeCombo(Player player, SkillCombo combo) {
        // 콤보 완성 이벤트 발생
        SkillComboCompleteEvent event = new SkillComboCompleteEvent(player, combo. getComboId());
        Bukkit.getPluginManager(). callEvent(event);

        // 피니셔 스킬 활성화
        if (combo.getFinisherSkillId() != null && !combo.getFinisherSkillId().isEmpty()) {
            MessageUtils.sendMessage(player, String.format("§a콤보 완성!  §e%s§a - 피니셔 사용 가능!", combo.getName()));
        } else {
            MessageUtils.sendMessage(player, String.format("§a콤보 완성! §e%s§a!", combo.getName()));
        }

        // 보너스 효과 적용
        if (combo.getHasBonus() && combo.getDamageBonus() > 0) {
            MessageUtils.sendMessage(player, String.format("§e데미지 보너스: §a+%. 0f%%", combo.getDamageBonus() * 100));
        }
    }

    /**
     * 콤보 초기화
     */
    public void resetCombo(Player player) {
        UUID playerUUID = player. getUniqueId();
        PlayerComboState state = playerComboStates.get(playerUUID);

        if (state != null) {
            state.getCurrentSequence().clear();
            state.setMatchingComboId("");
            state.setLastSkillTime(0);
        }
    }

    /**
     * 피니셔 활성화
     */
    public void enableFinisher(Player player, String finisherSkillId) {
        UUID playerUUID = player. getUniqueId();
        PlayerComboState state = playerComboStates.get(playerUUID);

        if (state != null && state.getMatchingComboId() != null) {
            SkillCombo combo = combos.get(state.getMatchingComboId());
            if (combo != null && combo.getFinisherSkillId(). equals(finisherSkillId)) {
                MessageUtils.sendMessage(player, "§a피니셔를 사용할 수 있습니다!");
            }
        }
    }

    /**
     * 피니셔 사용 가능 여부
     */
    public boolean canUseFinisher(Player player, String skillId) {
        UUID playerUUID = player.getUniqueId();
        PlayerComboState state = playerComboStates. get(playerUUID);

        if (state == null || state.getMatchingComboId() == null) {
            return false;
        }

        SkillCombo combo = combos.get(state.getMatchingComboId());
        return combo != null && combo.getFinisherSkillId(). equals(skillId);
    }

    /**
     * 플레이어 콤보 상태 정리 (로그아웃 시)
     */
    public void cleanupPlayer(Player player) {
        playerComboStates.remove(player.getUniqueId());
    }

    /**
     * 콤보 통계
     */
    public Map<String, Object> getComboStats() {
        Map<String, Object> stats = new HashMap<>();
        stats. put("total_combos", combos.size());
        stats. put("active_players", playerComboStates.size());
        return stats;
    }
}