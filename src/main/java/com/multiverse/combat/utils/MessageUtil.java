package com. multiverse.combat.utils;

import org.bukkit.ChatColor;
import org.bukkit. entity.Player;
import com.multiverse.combat.CombatCore;
import java.util.HashMap;
import java.util.Map;

public class MessageUtil {
    
    private final CombatCore plugin;
    private final ConfigUtil configUtil;
    private final Map<String, String> messageCache;
    
    public MessageUtil(CombatCore plugin, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.configUtil = configUtil;
        this.messageCache = new HashMap<>();
        loadMessages();
    }
    
    private void loadMessages() {
        messageCache.put("prefix", configUtil.getString("messages.prefix", "§8[§c전투§8] "));
        messageCache.put("damage", configUtil.getString("messages. combat.damage", "§a{damage}§f의 데미지를 입혔습니다! "));
        messageCache.put("critical", configUtil.getString("messages. combat.critical", "§c§l크리티컬! "));
        messageCache.put("dodge", configUtil.getString("messages. combat.dodge", "§a§l회피!"));
        messageCache. put("skill. learned", configUtil.getString("messages. skill.learned", "§a스킬 §e{skill}§a을(를) 습득했습니다!"));
        messageCache.put("skill. upgraded", configUtil.getString("messages. skill.upgraded", "§a스킬 §e{skill}§a이(가) §6Lv.  {level}§a(으)로 성장했습니다!"));
        messageCache.put("skill. cooldown", configUtil.getString("messages.skill.cooldown", "§c쿨다운 중...  ({time}초)"));
        messageCache.put("pvp.enabled", configUtil.getString("messages. pvp.enabled", "§aPvP가 활성화되었습니다. "));
        messageCache.put("pvp.disabled", configUtil.getString("messages. pvp.disabled", "§cPvP가 비활성화되었습니다."));
        messageCache.put("pvp.kill", configUtil.getString("messages. pvp.kill", "§a{player}을(를) 처치했습니다!  (+{fame} 명성)"));
        messageCache.put("pvp.death", configUtil.getString("messages.pvp. death", "§c{player}에게 처치당했습니다. "));
        messageCache.put("status.applied", configUtil.getString("messages. status.applied", "§c{effect}§f 상태이상에 걸렸습니다!"));
        messageCache.put("status.removed", configUtil.getString("messages. status.removed", "§a{effect}§f 상태이상이 해제되었습니다!"));
    }
    
    public String getMessage(String key) {
        return messageCache.getOrDefault(key, "");
    }
    
    public String getMessage(String key, String defaultValue) {
        return messageCache. getOrDefault(key, defaultValue);
    }
    
    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public String getPrefixedMessage(String key) {
        String prefix = messageCache.getOrDefault("prefix", "§8[§c전투§8] ");
        String message = messageCache.getOrDefault(key, "");
        return prefix + message;
    }
    
    public String format(String message, Map<String, String> replacements) {
        String result = message;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return colorize(result);
    }
    
    public void sendMessage(Player player, String key) {
        player.sendMessage(getPrefixedMessage(key));
    }
    
    public void sendMessage(Player player, String key, Map<String, String> replacements) {
        String message = getMessage(key);
        player.sendMessage(colorize(getPrefixedMessage("prefix") + format(message, replacements)));
    }
    
    public void reloadMessages() {
        messageCache.clear();
        loadMessages();
    }
}