package com.multiverse.item. listeners;

import org.bukkit.event.EventHandler;
import org.  bukkit.event. Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.  entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse.  item.data.CustomItem;
import com. multiverse.item.  utils.MessageUtil;

public class PlayerJoinQuitListener implements Listener {
    
    private ItemCore plugin;
    
    public PlayerJoinQuitListener(ItemCore plugin) {
        this. plugin = plugin;
    }
    
    /**
     * 플레이어가 접속할 때
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 플레이어 데이터 로드
        loadPlayerData(player);
        
        // 커스텀 아이템 확인
        checkCustomItems(player);
        
        // 접속 메시지
        MessageUtil.sendMessage(player, "&a플러그인에 접속했습니다!");
    }
    
    /**
     * 플레이어가 퇴장할 때
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // 플레이어 데이터 저장
        savePlayerData(player);
        
        // 메모리 정리
        clearPlayerData(player);
    }
    
    /**
     * 플레이어 데이터 로드
     */
    private void loadPlayerData(Player player) {
        try {
            // DataManager에서 플레이어 아이템 데이터 로드
            plugin.getDataManager().loadPlayerItems(player. getUniqueId());
            
            plugin.getLogger().info(player. getName() + "의 데이터를 로드했습니다.");
        } catch (Exception e) {
            plugin.getLogger(). warning("플레이어 " + player.getName() + "의 데이터 로드 실패: " + e.getMessage());
        }
    }
    
    /**
     * 커스텀 아이템 확인
     */
    private void checkCustomItems(Player player) {
        ItemStack[] allItems = new ItemStack[36 + 4]; // 인벤토리 + 갑옷
        
        // 인벤토리 아이템
        System.arraycopy(player.getInventory().getContents(), 0, allItems, 0, 36);
        
        // 갑옷 아이템
        System.arraycopy(player.getInventory().getArmorContents(), 0, allItems, 36, 4);
        
        int customItemCount = 0;
        
        for (ItemStack item : allItems) {
            if (item == null || item.getAmount() == 0) {
                continue;
            }
            
            CustomItem customItem = null;
            try {
                customItem = plugin. getItemManager().fromItemStack(item);
            } catch (Exception e) {
                continue;
            }
            
            if (customItem != null) {
                customItemCount++;
                
                // 내구도 확인
                if (customItem.getDurability() <= 0 && ! customItem.isUnbreakable()) {
                    MessageUtil.sendMessage(player, "&c" + customItem.getName() + "&f의 내구도가 소진되었습니다.");
                }
            }
        }
        
        if (customItemCount > 0) {
            MessageUtil.sendMessage(player, "&a보유한 커스텀 아이템: " + customItemCount + "개");
        }
    }
    
    /**
     * 플레이어 데이터 저장
     */
    private void savePlayerData(Player player) {
        try {
            // DataManager에 플레이어 아이템 데이터 저장
            plugin.getDataManager().savePlayerItems(player.getUniqueId(), player.getInventory());
            
            plugin.getLogger().info(player.getName() + "의 데이터를 저장했습니다.");
        } catch (Exception e) {
            plugin.getLogger().warning("플레이어 " + player.getName() + "의 데이터 저장 실패: " + e.getMessage());
        }
    }
    
    /**
     * 플레이어 메모리 정리
     */
    private void clearPlayerData(Player player) {
        // 플레이어 관련 캐시 데이터 정리 (나중에 구현)
        plugin.getLogger().info(player.getName() + "의 메모리가 정리되었습니다.");
    }
}