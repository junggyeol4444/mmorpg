package com.multiverse.npcai.managers;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.models.*;
import com.multiverse.npcai.utils.ConfigUtil;
import com.multiverse.npcai.data.DataManager;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * 대화 트리, 노드 조건, 선택지, 대화 진행/실행
 */
public class DialogueManager {

    private final NPCAICore plugin;
    private final DataManager dataManager;
    private final ReputationManager reputationManager;
    private final ConfigUtil config;

    // npcId -> DialogueNode 트리 (캐시)
    private final Map<Integer, Map<String, DialogueNode>> dialogueTrees = new HashMap<>();
    private final Map<Integer, String> startNodes = new HashMap<>();

    public DialogueManager(NPCAICore plugin, DataManager dataManager, ReputationManager reputationManager, ConfigUtil config) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.reputationManager = reputationManager;
        this.config = config;
    }

    // === 대화 관리 API ===
    public DialogueNode getNode(String nodeId) {
        for (Map<String, DialogueNode> tree : dialogueTrees.values()) {
            if (tree.containsKey(nodeId)) return tree.get(nodeId);
        }
        return null;
    }

    public DialogueNode getStartNode(int npcId) {
        String start = startNodes.get(npcId);
        if (start == null) return null;
        Map<String, DialogueNode> tree = dialogueTrees.get(npcId);
        if (tree == null) return null;
        return tree.get(start);
    }

    public void loadDialogueTree(int npcId, String filePath) {
        Map<String, DialogueNode> nodes = dataManager.loadDialogueTree(npcId, filePath);
        String startNode = dataManager.getDialogueStartNode(npcId, filePath);
        dialogueTrees.put(npcId, nodes);
        startNodes.put(npcId, startNode);
    }

    // === 대화 시작 ===
    public void startDialogue(Player player, NPCData npc) {
        DialogueNode node = getStartNode(npc.getNpcId());
        if (node == null) {
            player.sendMessage(config.getString("messages.interaction.busy").replace("{npc}", npc.getName()));
            return;
        }
        showNode(player, node);
    }

    public void showNode(Player player, DialogueNode node) {
        player.sendMessage("§e[NPC 대화] " + node.getText());
        List<DialogueOption> options = getAvailableOptions(player, null, node);
        for (int i = 0; i < options.size(); i++) {
            player.sendMessage("§f" + (i + 1) + ". " + options.get(i).getText());
        }
        // 선택지 클릭/입력은 GUI 또는 별도 처리
    }

    public void handleChoice(Player player, NPCData npc, int optionIndex) {
        DialogueNode node = getStartNode(npc.getNpcId());
        List<DialogueOption> options = getAvailableOptions(player, npc, node);
        if (optionIndex < 0 || optionIndex >= options.size()) {
            player.sendMessage("§c올바르지 않은 선택입니다.");
            return;
        }
        DialogueOption opt = options.get(optionIndex);
        if (!checkConditions(player, npc, opt.getConditions())) {
            player.sendMessage("§c조건을 만족하지 않습니다.");
            return;
        }
        DialogueNode next = getNode(opt.getNextNodeId());
        showNode(player, next);
        if (next.getAction() != null) {
            next.getAction().execute(player, npc);
        }
    }

    // === 조건 체크 ===
    public boolean checkConditions(Player player, NPCData npc, List<DialogueCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) return true;
        for (DialogueCondition cond : conditions) {
            if (!cond.check(player, npc)) return false;
        }
        return true;
    }

    public List<DialogueOption> getAvailableOptions(Player player, NPCData npc, DialogueNode node) {
        List<DialogueOption> result = new ArrayList<>();
        for (DialogueOption opt : node.getOptions()) {
            if (checkConditions(player, npc, opt.getConditions())) {
                result.add(opt);
            }
        }
        return result;
    }
}