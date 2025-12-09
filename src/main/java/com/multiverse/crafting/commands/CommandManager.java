package com.multiverse.crafting.commands;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.managers.CraftingDataManager;
import com.multiverse.crafting.managers.CraftingManager;
import com.multiverse.crafting.managers.CraftingSkillManager;
import com.multiverse.crafting.managers.CraftingStationManager;
import com.multiverse.crafting.managers.RecipeManager;
import com.multiverse.crafting.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final AdminCommand adminCommand;
    private final CraftCommand craftCommand;

    public CommandManager(CraftingCore plugin,
                          CraftingManager craftingManager,
                          RecipeManager recipeManager,
                          CraftingStationManager stationManager,
                          CraftingSkillManager skillManager,
                          CraftingDataManager dataManager) {
        this.adminCommand = new AdminCommand(plugin, recipeManager, skillManager);
        this.craftCommand = new CraftCommand(craftingManager, recipeManager, stationManager, skillManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("admin")) {
            return adminCommand.handle(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return craftCommand.handle(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof Player)) return out;

        if (args.length == 1) {
            out.addAll(Arrays.asList("list", "info", "make", "skill", "station", "guide", "admin"));
            return filter(out, args[0]);
        }

        if (args[0].equalsIgnoreCase("admin")) {
            if (args.length == 2) {
                out.addAll(Arrays.asList("give", "skill", "reload"));
                return filter(out, args[1]);
            }
            if (args[1].equalsIgnoreCase("give")) {
                if (args.length == 3) {
                    out.addAll(sender.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
                    return filter(out, args[2]);
                }
                if (args.length == 4) {
                    out.add("<레시피ID>");
                    return filter(out, args[3]);
                }
            }
            if (args[1].equalsIgnoreCase("skill")) {
                if (args.length == 3) {
                    out.addAll(sender.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
                    return filter(out, args[2]);
                }
                if (args.length == 4) {
                    out.addAll(Arrays.stream(com.multiverse.crafting.models.enums.CraftingType.values())
                            .map(Enum::name).collect(Collectors.toList()));
                    return filter(out, args[3]);
                }
                if (args.length == 5) {
                    out.addAll(Arrays.asList("set", "add"));
                    return filter(out, args[4]);
                }
                if (args.length == 6) {
                    out.add("<값>");
                    return filter(out, args[5]);
                }
            }
        } else {
            // player commands
            switch (args[0].toLowerCase()) {
                case "list":
                    if (args.length == 2) {
                        out.addAll(Arrays.stream(com.multiverse.crafting.models.enums.CraftingType.values())
                                .map(Enum::name).collect(Collectors.toList()));
                        return filter(out, args[1]);
                    }
                    break;
                case "info":
                case "make":
                case "guide":
                    if (args.length == 2) {
                        out.add("<레시피ID>");
                        return filter(out, args[1]);
                    }
                    break;
                case "station":
                    if (args.length == 2) {
                        out.add("place");
                        return filter(out, args[1]);
                    }
                    if (args.length == 3 && args[1].equalsIgnoreCase("place")) {
                        out.addAll(Arrays.stream(com.multiverse.crafting.models.enums.CraftingStationType.values())
                                .map(Enum::name).collect(Collectors.toList()));
                        return filter(out, args[2]);
                    }
                    break;
                default:
                    break;
            }
        }

        return out;
    }

    private List<String> filter(List<String> src, String token) {
        if (token == null || token.isEmpty()) return src;
        String lower = token.toLowerCase();
        return src.stream().filter(s -> s.toLowerCase().startsWith(lower)).collect(Collectors.toList());
    }
}