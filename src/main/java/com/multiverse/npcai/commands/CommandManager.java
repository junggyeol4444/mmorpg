package com.multiverse.npcai.commands;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.models.enums.*;
import org.bukkit.command.Command;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NPC 명령어 및 어드민 명령어 탭 완성 구현
 */
public class CommandManager implements TabCompleter {

    private final NPCAICore plugin;

    public CommandManager(NPCAICore plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // npcai 명령어
        if (label.equalsIgnoreCase("npcai")) {
            if (args.length == 1) {
                return Arrays.asList("admin");
            }
            if (args[0].equalsIgnoreCase("admin")) {
                if (args.length == 2) {
                    return Arrays.asList(
                        "create", "remove", "settype", "setdialogue", "setbehavior", "setpatrol",
                        "reputation", "shop", "trainer", "reload"
                    );
                } else if (args.length == 3) {
                    String sub = args[1].toLowerCase();
                    switch (sub) {
                        case "create":
                            return Arrays.stream(NPCType.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
                        case "settype":
                            return Arrays.stream(NPCType.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
                        case "setbehavior":
                            return Arrays.stream(BehaviorType.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
                        case "shop":
                            return Arrays.asList("create", "additem");
                        case "trainer":
                            return Arrays.asList("create", "addskill");
                        default:
                            return Collections.emptyList();
                    }
                } else if (args.length == 4 && args[1].equalsIgnoreCase("shop") && args[2].equalsIgnoreCase("create")) {
                    return Arrays.stream(ShopType.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
                } else if (args.length == 4 && args[1].equalsIgnoreCase("trainer") && args[2].equalsIgnoreCase("create")) {
                    return Arrays.stream(TrainerType.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
                }
            }
        }

        // npc 명령어
        if (label.equalsIgnoreCase("npc")) {
            if (args.length == 1) {
                return Arrays.asList("info", "reputation", "gift", "skills");
            }
        }

        return Collections.emptyList();
    }
}