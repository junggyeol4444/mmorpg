package com.multiverse.crafting.commands;

import com.multiverse.crafting.managers.CraftingManager;
import com.multiverse.crafting.managers.CraftingSkillManager;
import com.multiverse.crafting.managers.RecipeManager;
import com.multiverse.crafting.managers.CraftingStationManager;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.enums.CraftingType;
import com.multiverse.crafting.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CraftCommand {

    private final CraftingManager craftingManager;
    private final RecipeManager recipeManager;
    private final CraftingStationManager stationManager;
    private final CraftingSkillManager skillManager;

    public CraftCommand(CraftingManager craftingManager,
                        RecipeManager recipeManager,
                        CraftingStationManager stationManager,
                        CraftingSkillManager skillManager) {
        this.craftingManager = craftingManager;
        this.recipeManager = recipeManager;
        this.stationManager = stationManager;
        this.skillManager = skillManager;
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "list":
                return handleList(player, args);
            case "info":
                return handleInfo(player, args);
            case "make":
                return handleMake(player, args);
            case "skill":
                return handleSkill(player);
            case "station":
                return handleStation(player, args);
            case "guide":
                return handleGuide(player, args);
            default:
                sendHelp(player);
                return true;
        }
    }

    private boolean handleList(Player player, String[] args) {
        CraftingType filter = null;
        if (args.length >= 2) {
            try {
                filter = CraftingType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        List<CraftingRecipe> known = recipeManager.getKnownRecipes(player);
        if (filter != null) {
            known = known.stream().filter(r -> r.getType() == filter).collect(Collectors.toList());
        }
        player.sendMessage(MessageUtil.color("&e=== 사용 가능 레시피 ==="));
        known.forEach(r -> player.sendMessage(MessageUtil.color("&7- &f" + r.getRecipeId() + " &8(" + r.getName() + ")")));
        return true;
    }

    private boolean handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageUtil.color("&c사용법: /craft info <레시피ID>"));
            return true;
        }
        CraftingRecipe recipe = recipeManager.getRecipe(args[1]);
        if (recipe == null) {
            player.sendMessage(MessageUtil.color("&c레시피를 찾을 수 없습니다."));
            return true;
        }
        player.sendMessage(MessageUtil.color("&e[" + recipe.getName() + "]"));
        player.sendMessage(MessageUtil.color("&7ID: " + recipe.getRecipeId()));
        player.sendMessage(MessageUtil.color("&7타입: " + recipe.getType() + " / 카테고리: " + recipe.getCategory()));
        player.sendMessage(MessageUtil.color("&7필요 레벨: " + recipe.getRequiredLevel() + ", 스킬레벨: " + recipe.getRequiredSkillLevel()));
        player.sendMessage(MessageUtil.color("&7기본 성공률: " + recipe.getSuccessRate() + "%, 시간: " + recipe.getCraftingTime() + "s"));
        return true;
    }

    private boolean handleMake(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageUtil.color("&c사용법: /craft make <레시피ID> [개수]"));
            return true;
        }
        String recipeId = args[1];
        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {}
        }
        CraftingRecipe recipe = recipeManager.getRecipe(recipeId);
        if (recipe == null) {
            player.sendMessage(MessageUtil.color("&c레시피를 찾을 수 없습니다."));
            return true;
        }
        craftingManager.startCrafting(player, recipe, amount);
        return true;
    }

    private boolean handleSkill(Player player) {
        player.sendMessage(MessageUtil.color("&e=== 제작 스킬 ==="));
        for (CraftingType type : CraftingType.values()) {
            int level = skillManager.getLevel(player, type);
            long exp = skillManager.getExperience(player, type);
            player.sendMessage(MessageUtil.color("&7" + type + ": Lv." + level + " (" + exp + " exp)"));
        }
        return true;
    }

    private boolean handleStation(Player player, String[] args) {
        // /craft station place <타입>
        if (args.length >= 3 && args[1].equalsIgnoreCase("place")) {
            stationManager.placeStation(player, player.getLocation(), args[2]);
            return true;
        }
        player.sendMessage(MessageUtil.color("&c사용법: /craft station place <타입>"));
        return true;
    }

    private boolean handleGuide(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageUtil.color("&c사용법: /craft guide <레시피ID>"));
            return true;
        }
        craftingManager.openGuide(player, args[1]);
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(MessageUtil.color("&e/craft list [타입]"));
        player.sendMessage(MessageUtil.color("&e/craft info <레시피ID>"));
        player.sendMessage(MessageUtil.color("&e/craft make <레시피ID> [개수]"));
        player.sendMessage(MessageUtil.color("&e/craft skill"));
        player.sendMessage(MessageUtil.color("&e/craft station place <타입>"));
        player.sendMessage(MessageUtil.color("&e/craft guide <레시피ID>"));
    }
}