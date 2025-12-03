package com.multiverse.npcai.managers;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.models.NPCReaction;
import com.multiverse.npcai.models.enums.ReactionTrigger;
import com.multiverse.npcai.models.NPCData;
import com.multiverse.npcai.models.DialogueCondition;
import com.multiverse.npcai.models.DialogueAction;
import com.multiverse.npcai.managers.AIBehaviorManager;
import com.multiverse.npcai.managers.DialogueManager;
import com.multiverse.npcai.managers.ReputationManager;
import com.multiverse.npcai.utils.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * NPC 반응 트리거 및 실행 로직
 */
public class ReactionManager {

    private final NPCAICore plugin;
    private final AIBehaviorManager aiBehaviorManager;
    private final DialogueManager dialogueManager;
    private final ReputationManager reputationManager;
    private final ConfigUtil config;

    // NPC별 반응 목록 (캐시)
    private final Map<Integer, List<NPCReaction>> reactionMap = new HashMap<>();

    public ReactionManager(NPCAICore plugin, AIBehaviorManager aiBehaviorManager, DialogueManager dialogueManager, ReputationManager reputationManager, ConfigUtil config) {
        this.plugin = plugin;
        this.aiBehaviorManager = aiBehaviorManager;
        this.dialogueManager = dialogueManager;
        this.reputationManager = reputationManager;
        this.config = config;
    }

    // 반응 등록
    public void addReaction(int npcId, NPCReaction reaction) {
        reactionMap.computeIfAbsent(npcId, k -> new ArrayList<>()).add(reaction);
    }

    // 반응 조회
    public List<NPCReaction> getReactions(int npcId, ReactionTrigger trigger) {
        List<NPCReaction> lst = reactionMap.getOrDefault(npcId, Collections.emptyList());
        List<NPCReaction> result = new ArrayList<>();
        for (NPCReaction r : lst) {
            if (r.getTrigger() == trigger) result.add(r);
        }
        return result;
    }

    // 반응 트리거 처리
    public void triggerReaction(int npcId, ReactionTrigger trigger, Player player) {
        List<NPCReaction> reactions = getReactions(npcId, trigger);
        NPCData npc = plugin.getNPCManager().getNPC(npcId);
        for (NPCReaction reaction : reactions) {
            if (!checkConditions(player, npc, reaction.getConditions())) continue;
            executeReaction(npc, reaction, player);
        }
    }

    // 반응 실행
    public void executeReaction(NPCData npc, NPCReaction reaction, Player player) {
        // 대사 또는 이모트 표시
        if (reaction.getReaction() != null && !reaction.getReaction().isEmpty()) {
            say(npc.getNpcId(), reaction.getReaction(), 10.0);
        }
        // 액션
        if (reaction.getAction() != null) {
            reaction.getAction().execute(player, npc);
        }
        // 이모트
        // showEmote 등은 구현 필요
    }

    // 이모트(감정표현 등)
    public void showEmote(int npcId, String emote) {
        // Citizens API, ProtocolLib 등 연동(이모트 보여주기)
        // 예시: 말풍선, 애니메이션 등
    }

    // 반경 내 대사 전파
    public void say(int npcId, String message, double radius) {
        NPCData npc = plugin.getNPCManager().getNPC(npcId);
        if (npc == null) return;
        Player nearest = findNearestPlayer(npc.getLocation(), radius);
        if (nearest != null) nearest.sendMessage("§e" + npc.getName() + ": " + message);
    }

    // 조건 그룹 체크
    private boolean checkConditions(Player player, NPCData npc, List<DialogueCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) return true;
        for (DialogueCondition cond : conditions) {
            if (!cond.check(player, npc)) return false;
        }
        return true;
    }

    // 가장 가까운 플레이어 찾기 (단순 구현)
    private Player findNearestPlayer(org.bukkit.Location loc, double radius) {
        double closest = Double.MAX_VALUE;
        Player closestPlayer = null;
        for (Player p : loc.getWorld().getPlayers()) {
            double dist = p.getLocation().distance(loc);
            if (dist < closest && dist <= radius) {
                closest = dist;
                closestPlayer = p;
            }
        }
        return closestPlayer;
    }
}