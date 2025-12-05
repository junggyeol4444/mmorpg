package com.multiverse.item. listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event. Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org. bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item. ItemCore;
import com.multiverse.item.data.CustomItem;
import com.multiverse. item.data.ItemOption;
import com.multiverse.item. data.OptionTrigger;

public class ItemKillListener implements Listener {
    
    private ItemCore plugin;
    
    public ItemKillListener(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어가 다른 플레이어를 처치할 때
     */
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        if (killer == null) {
            return;
        }
        
        // 손에 들고 있는 아이템 확인
        ItemStack itemStack = killer.getInventory().getItemInMainHand();
        
        if (itemStack == null || itemStack.getAmount() == 0) {
            return;
        }
        
        // CustomItem으로 변환 시도
        CustomItem customItem = null;
        try {
            customItem = plugin.getItemManager().fromItemStack(itemStack);
        } catch (Exception e) {
            return;
        }
        
        if (customItem == null) {
            return;
        }
        
        // 처치 시 옵션 발동
        triggerKillOptions(killer, customItem, victim, OptionTrigger.ON_KILL);
        
        // 경험치 보상
        grantExperience(killer, customItem);
        
        // 처치 횟수 증가 (킬스트릭 시스템)
        incrementKillStreak(killer);
    }
    
    /**
     * 처치 시 옵션 발동
     */
    private void triggerKillOptions(Player killer, CustomItem item, Player victim, OptionTrigger trigger) {
        if (item.getOptions() == null) {
            return;
        }
        
        for (ItemOption option : item.getOptions()) {
            if (option.getTrigger() != trigger) {
                continue;
            }
            
            if (Math.random() * 100 > option.getValue()) {
                continue;
            }
            
            applyKillEffect(killer, victim, option);
        }
    }
    
    /**
     * 처치 효과 적용
     */
    private void applyKillEffect(Player killer, Player victim, ItemOption option) {
        switch (option.getType()) {
            case LIFESTEAL:
                // 처치 시 생명력 회복
                double healAmount = option.getValue();
                double newHealth = Math.min(killer.getMaxHealth(), killer.getHealth() + healAmount);
                killer.setHealth(newHealth);
                break;
            case EXPERIENCE:
                // 추가 경험치는 grantExperience에서 처리
                break;
            case DAMAGE:
                // 처치 시 임시 공격력 증가
                break;
        }
    }
    
    /**
     * 경험치 보상
     */
    private void grantExperience(Player killer, CustomItem item) {
        int baseExp = 100; // 기본 경험치
        int bonusExp = 0;
        
        // 옵션에서 경험치 보너스 확인
        if (item.getOptions() != null) {
            for (ItemOption option : item. getOptions()) {
                if (option.getType(). name().equals("EXPERIENCE")) {
                    bonusExp += (int) option.getValue();
                }
            }
        }
        
        int totalExp = baseExp + bonusExp;
        killer.giveExp(totalExp);
    }
    
    /**
     * 킬스트릭 증가
     */
    private void incrementKillStreak(Player killer) {
        // 플레이어별 킬스트릭 카운트 (나중에 플레이어 데이터에 저장)
        // 현재는 스코어보드로 임시 구현
        if (killer.getScoreboard(). getObjective("killstreak") == null) {
            killer.getScoreboard(). registerNewObjective("killstreak", "dummy");
        }
        
        int currentKills = killer.getScoreboard(). getObjective("killstreak"). getScore(killer. getName()). getScore();
        killer.getScoreboard().getObjective("killstreak").getScore(killer.getName()).setScore(currentKills + 1);
    }
}