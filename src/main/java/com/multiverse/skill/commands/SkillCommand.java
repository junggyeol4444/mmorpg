package com.multiverse.skill. commands;

import com.multiverse. skill.SkillCore;
import com.multiverse. skill.data.models. Skill;
import com.multiverse.skill.data.models.PlayerSkillData;
import com.multiverse.skill.managers.SkillManager;
import com.multiverse.skill.managers. SkillLearningManager;
import com.multiverse.skill.utils.MessageUtils;
import org.bukkit. Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java. util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillCommand implements CommandExecutor, TabCompleter {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final SkillLearningManager learningManager;

    public SkillCommand(SkillCore plugin, SkillManager skillManager, SkillLearningManager learningManager) {
        this. plugin = plugin;
        this. skillManager = skillManager;
        this.learningManager = learningManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "list" -> handleList(player);
            case "info" -> handleInfo(player, args);
            case "use" -> handleUse(player, args);
            case "tree" -> handleTree(player, args);
            case "preset" -> handlePreset(player, args);
            case "reset" -> handleReset(player, args);
            default -> sendHelpMessage(player);
        }

        return true;
    }

    /**
     * /skill list - 보유한 스킬 목록 표시
     */
    private void handleList(Player player) {
        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        
        if (skillData. getSkills().isEmpty()) {
            MessageUtils.sendMessage(player, "§e습득한 스킬이 없습니다.");
            return;
        }

        MessageUtils.sendMessage(player, "§b=== 보유 스킬 목록 ===");
        skillData.getSkills().forEach((skillId, learnedSkill) -> {
            Skill skill = skillManager.getSkill(skillId);
            if (skill != null) {
                MessageUtils.sendMessage(player, 
                    String.format("§e%s §7- Lv. %d (경험치: %d)",
                        skill.getName(),
                        learnedSkill.getLevel(),
                        learnedSkill.getExperience()));
            }
        });
        MessageUtils.sendMessage(player, String.format("§b사용 가능 포인트: §e%d", 
            skillData.getAvailableSkillPoints()));
    }

    /**
     * /skill info <스킬ID> - 스킬 정보 표시
     */
    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(player, "§c사용법: /skill info <스킬ID>");
            return;
        }

        String skillId = args[1];
        Skill skill = skillManager.getSkill(skillId);

        if (skill == null) {
            MessageUtils.sendMessage(player, "§c해당 스킬을 찾을 수 없습니다.");
            return;
        }

        MessageUtils.sendMessage(player, "§b=== 스킬 정보 ===");
        MessageUtils.sendMessage(player, "§e이름: §f" + skill.getName());
        MessageUtils.sendMessage(player, "§e설명: §f" + skill. getDescription());
        MessageUtils. sendMessage(player, "§e타입: §f" + skill. getType(). getDisplayName());
        MessageUtils.sendMessage(player, String.format("§e최대 레벨: §f%d", skill.getMaxLevel()));
        MessageUtils.sendMessage(player, String.format("§e쿨다운: §f%. 1f초", skill.getBaseCooldown() / 1000.0));
    }

    /**
     * /skill use <스킬ID> - 스킬 사용
     */
    private void handleUse(Player player, String[] args) {
        if (args. length < 2) {
            MessageUtils.sendMessage(player, "§c사용법: /skill use <스킬ID>");
            return;
        }

        String skillId = args[1];
        Skill skill = skillManager.getSkill(skillId);

        if (skill == null) {
            MessageUtils.sendMessage(player, "§c해당 스킬을 찾을 수 없습니다.");
            return;
        }

        PlayerSkillData skillData = plugin.getPlayerDataLoader().loadPlayerData(player.getUniqueId());
        
        if (! skillData.getSkills().containsKey(skillId)) {
            MessageUtils.sendMessage(player, "§c이 스킬을 습득하지 않았습니다.");
            return;
        }

        // 스킬 사용 로직
        plugin.getCastManager().startCast(player, skill, player.getTargetEntity(20, true));
    }

    /**
     * /skill tree [트리ID] - 스킬 트리 GUI 열기
     */
    private void handleTree(Player player, String[] args) {
        MessageUtils.sendMessage(player, "§e스킬 트리 UI는 구현 예정입니다.");
    }

    /**
     * /skill preset <save|load|list> - 스킬 프리셋 관리
     */
    private void handlePreset(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(player, "§c사용법: /skill preset <save|load|list>");
            return;
        }

        String presetCommand = args[1].toLowerCase();
        MessageUtils.sendMessage(player, "§e스킬 프리셋 기능은 구현 예정입니다.");
    }

    /**
     * /skill reset <스킬ID|tree|all> - 스킬 초기화
     */
    private void handleReset(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(player, "§c사용법: /skill reset <스킬ID|tree|all>");
            return;
        }

        String resetTarget = args[1].toLowerCase();

        if (resetTarget.equals("all")) {
            learningManager.resetAllSkills(player, true);
            MessageUtils.sendMessage(player, "§a모든 스킬이 초기화되었습니다!");
        } else {
            learningManager.resetSkill(player, resetTarget, true);
            MessageUtils.sendMessage(player, "§a스킬이 초기화되었습니다!");
        }
    }

    /**
     * 도움말 메시지 표시
     */
    private void sendHelpMessage(Player player) {
        MessageUtils.sendMessage(player, "§b=== SkillCore 명령어 ===");
        MessageUtils.sendMessage(player, "§e/skill list §7- 보유한 스킬 목록");
        MessageUtils.sendMessage(player, "§e/skill info <스킬ID> §7- 스킬 정보 조회");
        MessageUtils.sendMessage(player, "§e/skill use <스킬ID> §7- 스킬 사용");
        MessageUtils. sendMessage(player, "§e/skill tree [트리ID] §7- 스킬 트리 열기");
        MessageUtils.sendMessage(player, "§e/skill preset <save|load|list> §7- 프리셋 관리");
        MessageUtils.sendMessage(player, "§e/skill reset <스킬ID|tree|all> §7- 스킬 초기화");
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                      @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("list", "info", "use", "tree", "preset", "reset"));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "info", "use", "reset" -> {
                    // 스킬 목록 자동완성
                    skillManager.getAllSkills().forEach(skill -> 
                        completions.add(skill.getSkillId())
                    );
                }
                case "preset" -> {
                    completions.addAll(Arrays.asList("save", "load", "list"));
                }
            }
        }

        return completions;
    }
}