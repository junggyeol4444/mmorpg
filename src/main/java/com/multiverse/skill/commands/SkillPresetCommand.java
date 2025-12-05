package com.multiverse.skill.commands;

import com.multiverse. skill.SkillCore;
import com.multiverse. skill.data.models.PlayerSkillData;
import com.multiverse. skill.data.models.SkillPreset;
import com. multiverse.skill.utils.MessageUtils;
import org. bukkit.command.Command;
import org.bukkit. command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations. NotNull;

import java.util.HashMap;
import java.util.Map;

public class SkillPresetCommand implements CommandExecutor {

    private final SkillCore plugin;

    public SkillPresetCommand(SkillCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "§c이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            sendHelpMessage(player);
            return true;
        }

        String presetCommand = args[0].toLowerCase();
        String presetName = args. length > 1 ? args[1] : null;

        switch (presetCommand) {
            case "save" -> handleSavePreset(player, presetName);
            case "load" -> handleLoadPreset(player, presetName);
            case "list" -> handleListPresets(player);
            case "delete" -> handleDeletePreset(player, presetName);
            default -> sendHelpMessage(player);
        }

        return true;
    }

    /**
     * /skill preset save <이름> - 현재 스킬 배치를 프리셋으로 저장
     */
    private void handleSavePreset(Player player, String presetName) {
        if (presetName == null) {
            MessageUtils.sendMessage(player, "§c사용법: /skill preset save <이름>");
            return;
        }

        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player. getUniqueId());
        
        // 기존 프리셋 확인
        for (SkillPreset preset : skillData.getPresets()) {
            if (preset.getName().equalsIgnoreCase(presetName)) {
                MessageUtils.sendMessage(player, "§c같은 이름의 프리셋이 이미 존재합니다!");
                return;
            }
        }

        // 새로운 프리셋 생성
        SkillPreset newPreset = new SkillPreset();
        newPreset.setName(presetName);
        
        // 현재 핫바 상태 저장 (예시)
        Map<Integer, String> hotbar = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            // 실제로는 플레이어의 핫바 스킬 정보를 저장해야 함
            hotbar.put(i, "empty");
        }
        newPreset.setHotbar(hotbar);

        skillData.getPresets().add(newPreset);
        plugin.getPlayerDataLoader().savePlayerData(player. getUniqueId());

        MessageUtils.sendMessage(player, String.format("§a프리셋 '§e%s§a'이(가) 저장되었습니다!", presetName));
    }

    /**
     * /skill preset load <이름> - 저장된 프리셋 불러오기
     */
    private void handleLoadPreset(Player player, String presetName) {
        if (presetName == null) {
            MessageUtils. sendMessage(player, "§c사용법: /skill preset load <이름>");
            return;
        }

        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        
        SkillPreset foundPreset = null;
        for (SkillPreset preset : skillData.getPresets()) {
            if (preset.getName(). equalsIgnoreCase(presetName)) {
                foundPreset = preset;
                break;
            }
        }

        if (foundPreset == null) {
            MessageUtils.sendMessage(player, "§c해당 프리셋을 찾을 수 없습니다.");
            return;
        }

        // 프리셋 적용 (핫바 스킬 배치 적용 등)
        int presetIndex = skillData.getPresets().indexOf(foundPreset);
        skillData.setActivePresetIndex(presetIndex);
        plugin.getPlayerDataLoader().savePlayerData(player.getUniqueId());

        MessageUtils.sendMessage(player, String.format("§a프리셋 '§e%s§a'이(가) 불러워졌습니다!", presetName));
    }

    /**
     * /skill preset list - 저장된 프리셋 목록 표시
     */
    private void handleListPresets(Player player) {
        PlayerSkillData skillData = plugin. getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        
        if (skillData.getPresets().isEmpty()) {
            MessageUtils.sendMessage(player, "§e저장된 프리셋이 없습니다.");
            return;
        }

        MessageUtils. sendMessage(player, "§b=== 저장된 프리셋 ===");
        for (int i = 0; i < skillData.getPresets().size(); i++) {
            SkillPreset preset = skillData.getPresets().get(i);
            String marker = i == skillData.getActivePresetIndex() ? "§a★ " : "  ";
            MessageUtils.sendMessage(player, String.format("%s§e%s", marker, preset.getName()));
        }
    }

    /**
     * /skill preset delete <이름> - 프리셋 삭제
     */
    private void handleDeletePreset(Player player, String presetName) {
        if (presetName == null) {
            MessageUtils.sendMessage(player, "§c사용법: /skill preset delete <이름>");
            return;
        }

        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        
        SkillPreset presetToDelete = null;
        for (SkillPreset preset : skillData.getPresets()) {
            if (preset.getName().equalsIgnoreCase(presetName)) {
                presetToDelete = preset;
                break;
            }
        }

        if (presetToDelete == null) {
            MessageUtils.sendMessage(player, "§c해당 프리셋을 찾을 수 없습니다.");
            return;
        }

        skillData.getPresets().remove(presetToDelete);
        
        // 활성 프리셋이 삭제된 경우 처리
        if (skillData. getActivePresetIndex() >= skillData.getPresets().size()) {
            skillData.setActivePresetIndex(Math.max(0, skillData. getPresets().size() - 1));
        }

        plugin.getPlayerDataLoader(). savePlayerData(player.getUniqueId());
        MessageUtils.sendMessage(player, String.format("§a프리셋 '§e%s§a'이(가) 삭제되었습니다!", presetName));
    }

    /**
     * 도움말 메시지 표시
     */
    private void sendHelpMessage(Player player) {
        MessageUtils.sendMessage(player, "§b=== 스킬 프리셋 명령어 ===");
        MessageUtils.sendMessage(player, "§e/skill preset save <이름> §7- 현재 배치 저장");
        MessageUtils.sendMessage(player, "§e/skill preset load <이름> §7- 프리셋 불러오기");
        MessageUtils.sendMessage(player, "§e/skill preset list §7- 프리셋 목록");
        MessageUtils.sendMessage(player, "§e/skill preset delete <이름> §7- 프리셋 삭제");
    }
}