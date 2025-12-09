package com.multiverse.crafting.commands;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.managers.CraftingSkillManager;
import com.multiverse.crafting.managers.RecipeManager;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.enums.CraftingType;
import com.multiverse.crafting.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand {

    private final CraftingCore plugin;
    private final RecipeManager recipeManager;
    private final CraftingSkillManager skillManager;

    public AdminCommand(CraftingCore plugin, RecipeManager recipeManager, CraftingSkillManager skillManager) {
        this.plugin = plugin;
        this.recipeManager = recipeManager;
        this.skillManager = skillManager;
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (!sender.hasPermission("crafting.admin")) {
            sender.sendMessage(MessageUtil.color("&c권한이 없습니다."));
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "give": return handleGive(sender, args);
            case "skill": return handleSkill(sender, args);
            case "reload": return handleReload(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        // /craft admin give <플레이어> <레시피ID>
        if (args.length < 3) {
            sender.sendMessage(MessageUtil.color("&c사용법: /craft admin give <플레이어> <레시피ID>"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtil.color("&c플레이어를 찾을 수 없습니다."));
            return true;
        }
        String recipeId = args[2];
        CraftingRecipe recipe = recipeManager.getRecipe(recipeId);
        if (recipe == null) {
            sender.sendMessage(MessageUtil.color("&c레시피를 찾을 수 없습니다: " + recipeId));
            return true;
        }
        recipeManager.learnRecipe(target, recipeId, false);
        sender.sendMessage(MessageUtil.color("&a" + target.getName() + "에게 레시피를 지급했습니다: " + recipeId));
        return true;
    }

    private boolean handleSkill(CommandSender sender, String[] args) {
        // /craft admin skill <플레이어> <타입> <set|add> <값>
        if (args.length < 5) {
            sender.sendMessage(MessageUtil.color("&c사용법: /craft admin skill <플레이어> <타입> <set|add> <값>"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtil.color("&c플레이어를 찾을 수 없습니다."));
            return true;
        }
        CraftingType type;
        try {
            type = CraftingType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(MessageUtil.color("&c존재하지 않는 타입입니다."));
            return true;
        }
        String mode = args[3].toLowerCase();
        int value;
        try {
            value = Integer.parseInt(args[4]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(MessageUtil.color("&c숫자를 입력하세요."));
            return true;
        }

        if (mode.equals("set")) {
            skillManager.setLevel(target, type, value);
            sender.sendMessage(MessageUtil.color("&a" + target.getName() + "의 " + type + " 레벨을 " + value + "(으)로 설정했습니다."));
        } else if (mode.equals("add")) {
            skillManager.addExperience(target, type, value);
            sender.sendMessage(MessageUtil.color("&a" + target.getName() + "의 " + type + " 경험치를 +" + value + " 만큼 추가했습니다."));
        } else {
            sender.sendMessage(MessageUtil.color("&c모드(set|add)만 가능합니다."));
        }
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        plugin.reloadAllConfigs();
        plugin.getRecipeManager().loadBuiltinRecipes();
        sender.sendMessage(MessageUtil.color("&a설정과 레시피를 리로드했습니다."));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(MessageUtil.color("&e/craft admin give <플레이어> <레시피ID>"));
        sender.sendMessage(MessageUtil.color("&e/craft admin skill <플레이어> <타입> <set|add> <값>"));
        sender.sendMessage(MessageUtil.color("&e/craft admin reload"));
    }
}