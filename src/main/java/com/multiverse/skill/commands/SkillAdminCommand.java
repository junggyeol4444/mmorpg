package com.multiverse.skill.commands;

import com.multiverse.skill.SkillCore;
import com.multiverse. skill.managers.SkillLearningManager;
import com.multiverse.skill.utils.MessageUtils;
import org.bukkit. Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkillAdminCommand implements CommandExecutor {

    private final SkillCore plugin;
    private final SkillLearningManager learningManager;

    public SkillAdminCommand(SkillCore plugin, SkillLearningManager learningManager) {
        this. plugin = plugin;
        this. learningManager = learningManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {

        if (! sender.hasPermission("skill.admin")) {
            MessageUtils.sendMessage(sender, "§c권한이 없습니다.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c사용법: /skill admin <give|points|reload>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "give" -> handleGiveSkill(sender, args);
            case "points" -> handlePoints(sender, args);
            case "reload" -> handleReload(sender);
            default -> sender.sendMessage("§c알 수 없는 명령어입니다.");
        }

        return true;
    }

    /**
     * /skill admin give <플레이어> <스킬ID> [레벨] - 스킬 지급
     */
    private void handleGiveSkill(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender. sendMessage("§c사용법: /skill admin give <플레이어> <스킬ID> [레벨]");
            return;
        }

        String playerName = args[1];
        String skillId = args[2];
        int level = args. length > 3 ? Integer. parseInt(args[3]) : 1;

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            MessageUtils.sendMessage(sender, "§c플레이어를 찾을 수 없습니다.");
            return;
        }

        learningManager.learnSkill(player, skillId);
        for (int i = 1; i < level; i++) {
            learningManager.upgradeSkill(player, skillId);
        }

        MessageUtils.sendMessage(sender, "§a스킬이 지급되었습니다.");
        MessageUtils.sendMessage(player, String.format("§a관리자에게 §e%s§a 스킬을 받았습니다!", skillId));
    }

    /**
     * /skill admin points <플레이어> <add|set> <값> - 포인트 관리
     */
    private void handlePoints(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§c사용법: /skill admin points <플레이어> <add|set> <값>");
            return;
        }

        String playerName = args[1];
        String operation = args[2].toLowerCase();
        int value = Integer.parseInt(args[3]);

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            MessageUtils.sendMessage(sender, "§c플레이어를 찾을 수 없습니다.");
            return;
        }

        if (operation.equals("add")) {
            learningManager.addSkillPoints(player, value);
            MessageUtils.sendMessage(sender, String.format("§a%d포인트를 추가했습니다.", value));
        } else if (operation.equals("set")) {
            learningManager.setSkillPoints(player, value);
            MessageUtils.sendMessage(sender, String.format("§a포인트를 %d로 설정했습니다.", value));
        }
    }

    /**
     * /skill admin reload - 설정 다시 로드
     */
    private void handleReload(CommandSender sender) {
        plugin.reloadConfig();
        MessageUtils.sendMessage(sender, "§a설정이 다시 로드되었습니다!");
    }
}