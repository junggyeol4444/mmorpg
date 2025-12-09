package com.multiverse.party.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.Map;

/**
 * 메시지 포맷/채팅 유틸리티
 */
public class MessageUtil {

    private Map<String, String> messages; // key: 메시지코드, value: 실제 메시지

    public MessageUtil(Map<String, String> messages) {
        this.messages = messages;
    }

    /** 메시지 치환 및 색상 적용 */
    public String getMessage(String key, Object... kv) {
        String base = messages.getOrDefault(key, key);
        for (int i = 0; i + 1 < kv.length; i += 2) {
            base = base.replace(String.valueOf(kv[i]), String.valueOf(kv[i + 1]));
        }
        return ChatColor.translateAlternateColorCodes('&', base);
    }

    /** 플레이어/커맨드에 메시지 전송 */
    public void send(Player player, String key, Object... kv) {
        player.sendMessage(getMessage(key, kv));
    }
    public void send(CommandSender sender, String key, Object... kv) {
        sender.sendMessage(getMessage(key, kv));
    }
}