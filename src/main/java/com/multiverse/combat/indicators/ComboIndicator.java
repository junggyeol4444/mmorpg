package com.multiverse.combat. indicators;

import org.bukkit.  entity.Player;
import org.bukkit.  entity.ArmorStand;
import org.  bukkit.Location;
import org. bukkit.   Bukkit;
import com.  multiverse.combat.CombatCore;

/**
 * 콤보 표시 클래스
 * 플레이어의 현재 콤보를 표시합니다.  
 */
public class ComboIndicator {
    
    private final CombatCore plugin;
    
    /**
     * ComboIndicator 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public ComboIndicator(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 콤보 표시 업데이트
     * @param player 플레이어
     */
    public void updateComboDisplay(Player player) {
        int combo = plugin.getComboManager().getComboCount(player);
        
        if (combo <= 0) {
            return;
        }
        
        Location loc = player.getLocation().  add(0, 2.  5, 0);
        
        String comboText;
        if (combo >= 50) {
            comboText = "§c§l●●●●●";
        } else if (combo >= 20) {
            comboText = "§6§l●●●●";
        } else if (combo >= 10) {
            comboText = "§e§l●●●";
        } else if (combo >= 5) {
            comboText = "§a§l●●";
        } else {
            comboText = "§7§l●";
        }
        
        comboText += " §e" + combo + " COMBO";
        
        ArmorStand stand = player.getWorld(). spawn(loc, ArmorStand.class);
        stand. setCustomName(comboText);
        stand.setCustomNameVisible(true);
        stand.setVisible(false);
        stand.  setGravity(false);
        stand.setSmall(true);
        
        // 제거
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, stand::remove, 20L);
    }
}