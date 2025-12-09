package com.multiverse.party.commands;

import com. multiverse.party. PartyCore;
import com. multiverse.party. models.Party;
import org.bukkit.command.Command;
import org.bukkit. command.CommandExecutor;
import org. bukkit.command. CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java. util.List;

public class PartyChatCommand implements CommandExecutor, TabCompleter {

    private final PartyCore plugin;

    public PartyChatCommand(PartyCore plugin) {
        this. plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 플레이어만 사용 가능
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("general.player-only"));
            return true;
        }

        Player player = (Player) sender;

        // 파티 시스템 활성화 확인
        if (!plugin.getConfig().getBoolean("party.enabled", true)) {
            player.sendMessage(plugin.getMessageUtil().getMessage("general.disabled"));
            return true;
        }

        // 파티 채팅 활성화 확인
        if (!plugin. getConfig().getBoolean("chat.enabled", true)) {
            player. sendMessage(plugin. getMessageUtil().getMessage("chat.disabled"));
            return true;
        }

        // 권한 확인
        if (!player.hasPermission("party.player.chat")) {
            player.sendMessage(plugin.getMessageUtil().getMessage("general.no-permission"));
            return true;
        }

        // 파티 확인
        Party party = plugin.getPartyManager().getPlayerParty(player);
        if (party == null) {
            player. sendMessage(plugin. getMessageUtil().getMessage("party.not-in-party"));
            return true;
        }

        // 메시지 확인
        if (args.length == 0) {
            player.sendMessage(plugin.getMessageUtil().getMessage("usage.p-chat"));
            return true;
        }

        // 메시지 조합
        String message = String.join(" ", args);

        // 빈 메시지 확인
        if (message.trim().isEmpty()) {
            player.sendMessage(plugin.getMessageUtil().getMessage("chat.empty-message"));
            return true;
        }

        // 메시지 길이 제한
        int maxLength = plugin. getConfig().getInt("chat.max-length", 256);
        if (message.length() > maxLength) {
            player.sendMessage(plugin.getMessageUtil().getMessage("chat.message-too-long",
                    "%max%", String.valueOf(maxLength)));
            return true;
        }

        // 쿨다운 확인
        if (plugin.getPartyChatManager().isOnCooldown(player)) {
            long remaining = plugin.getPartyChatManager().getRemainingCooldown(player);
            player.sendMessage(plugin.getMessageUtil().getMessage("chat.cooldown",
                    "%time%", String.valueOf(remaining)));
            return true;
        }

        // 파티 채팅 전송
        plugin. getPartyChatManager().sendPartyMessage(player, message);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // 파티 채팅은 탭 완성 없음
        return new ArrayList<>();
    }
}