package com.multiverse.guild.command;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.manager.GuildManager;
import com.multiverse.guild.model.Guild;
import com.multiverse.guild.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuildCommandExecutor implements CommandExecutor, TabCompleter {

    private final GuildCore plugin;
    private final GuildManager guildManager;

    public GuildCommandExecutor(GuildCore plugin) {
        this.plugin = plugin;
        this.guildManager = plugin.getGuildManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(Message.prefixed("&e/guild help"));
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "create" -> {
                if (args.length < 3) {
                    player.sendMessage(Message.prefixed("&c사용법: /guild create <이름> <태그>"));
                    return true;
                }
                guildManager.createGuild(player, args[1], args[2]);
            }
            case "disband" -> guildManager.disbandGuildCmd(player);
            case "invite" -> {
                if (args.length < 2) { player.sendMessage(Message.prefixed("&c/guild invite <플레이어>")); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                guildManager.inviteMemberCmd(player, target);
            }
            case "accept" -> guildManager.acceptInviteCmd(player);
            case "kick" -> {
                if (args.length < 2) { player.sendMessage(Message.prefixed("&c/guild kick <플레이어>")); return true; }
                guildManager.kickMemberCmd(player, args[1]);
            }
            case "leave" -> guildManager.leaveMember(player);
            case "promote" -> {
                if (args.length < 3) { player.sendMessage(Message.prefixed("&c/guild promote <플레이어> <계급>")); return true; }
                guildManager.setRankCmd(player, args[1], args[2], true);
            }
            case "demote" -> {
                if (args.length < 2) { player.sendMessage(Message.prefixed("&c/guild demote <플레이어>")); return true; }
                guildManager.demoteCmd(player, args[1]);
            }
            case "info" -> {
                Guild g;
                if (args.length >= 2) g = guildManager.getGuildByName(args[1]);
                else g = guildManager.getPlayerGuild(player.getUniqueId());
                guildManager.showInfo(player, g);
            }
            case "list" -> guildManager.listGuilds(player);
            case "chat", "g" -> {
                if (args.length < 2) { player.sendMessage(Message.prefixed("&c/guild chat <메시지>")); return true; }
                String msg = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                guildManager.guildChat(player, msg);
            }
            case "territory" -> guildManager.handleTerritorySub(player, args);
            case "war" -> guildManager.handleWarSub(player, args);
            case "ally" -> {
                if (args.length < 2) { player.sendMessage(Message.prefixed("&c/guild ally <길드>")); return true; }
                guildManager.handleAllySub(player, args[1]);
            }
            case "admin" -> {
                // Forward to admin executor
                return new GuildAdminCommandExecutor(plugin).onCommand(sender, cmd, label, java.util.Arrays.copyOfRange(args,1,args.length));
            }
            default -> player.sendMessage(Message.prefixed("&c알 수 없는 서브명령입니다."));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.addAll(List.of("create","disband","invite","accept","kick","leave","promote","demote","info","list","chat","war","ally","territory","admin"));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "invite","kick","promote","demote" -> {
                    for (Player p : Bukkit.getOnlinePlayers()) list.add(p.getName());
                }
                case "ally","info","war" -> list.addAll(guildManager.getGuildNames());
            }
        }
        return list;
    }
}