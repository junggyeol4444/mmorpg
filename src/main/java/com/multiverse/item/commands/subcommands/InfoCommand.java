package com. multiverse.item. commands. subcommands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import com.multiverse.item.data.ItemOption;
import com.multiverse. item.data. Gem;
import com.multiverse. item.utils.MessageUtil;
import com. multiverse.item.utils.ColorUtil;
import java.util.Map;
import java.util.List;

public class InfoCommand implements SubCommand {
    
    private ItemCore plugin;
    
    public InfoCommand(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void execute(Player player, String[] args) {
        
        // 손에 들고 있는 아이템 확인
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        
        if (itemStack == null || itemStack.getAmount() == 0) {
            MessageUtil.sendMessage(player, "&c손에 들고 있는 아이템이 없습니다!");
            return;
        }
        
        // CustomItem으로 변환
        CustomItem customItem = null;
        try {
            customItem = plugin.getItemManager(). fromItemStack(itemStack);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c이 아이템은 커스텀 아이템이 아닙니다!");
            return;
        }
        
        if (customItem == null) {
            MessageUtil.sendMessage(player, "&c이 아이템은 커스텀 아이템이 아닙니다!");
            return;
        }
        
        // 정보 출력
        showItemInfo(player, customItem);
    }
    
    private void showItemInfo(Player player, CustomItem item) {
        MessageUtil.sendMessage(player, "");
        MessageUtil.sendMessage(player, "&8╔════════════════════════════════════════╗");
        
        // 아이템 이름
        MessageUtil.sendMessage(player, "&8║ " + item.getRarity().getColor() + item.getName() + " &8║");
        MessageUtil.sendMessage(player, "&8╠════════════════════════════════════════╣");
        
        // 기본 정보
        MessageUtil.sendMessage(player, "&8║ &7기본 정보");
        MessageUtil.sendMessage(player, "&8║  &f- ID: &7" + item.getItemId());
        MessageUtil.sendMessage(player, "&8║  &f- 등급: &7" + ColorUtil.colorize(item.getRarity().getColor() + item.getRarity().name()));
        MessageUtil.sendMessage(player, "&8║  &f- 타입: &7" + item. getType(). name());
        MessageUtil.sendMessage(player, "&8║  &f- 강화: &7+" + item.getEnhanceLevel() + "/" + item.getMaxEnhance());
        
        // 설명
        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            MessageUtil.sendMessage(player, "&8║");
            MessageUtil.sendMessage(player, "&8║ &7설명");
            for (String line : item.getDescription().split("\n")) {
                MessageUtil.sendMessage(player, "&8║  &f" + line);
            }
        }
        
        // 요구사항
        MessageUtil.sendMessage(player, "&8║");
        MessageUtil.sendMessage(player, "&8║ &7요구사항");
        MessageUtil.sendMessage(player, "&8║  &f- 레벨: &7" + item.getRequiredLevel());
        if (item.getRequiredClass() != null && !item.getRequiredClass(). isEmpty()) {
            MessageUtil. sendMessage(player, "&8║  &f- 클래스: &7" + item.getRequiredClass());
        }
        if (item.getRequiredRace() != null && !item.getRequiredRace().isEmpty()) {
            MessageUtil.sendMessage(player, "&8║  &f- 종족: &7" + item. getRequiredRace());
        }
        
        // 기본 스탯
        if (item.getBaseStats() != null && !item.getBaseStats().isEmpty()) {
            MessageUtil.sendMessage(player, "&8║");
            MessageUtil.sendMessage(player, "&8║ &7기본 스탯");
            for (Map.Entry<String, Double> stat : item.getBaseStats().entrySet()) {
                double value = stat.getValue() * item.getRarity().getStatMultiplier();
                MessageUtil.sendMessage(player, "&8║  &f" + stat.getKey() + ": &7" + String.format("%.2f", value));
            }
        }
        
        // 옵션
        List<ItemOption> options = item. getOptions();
        if (options != null && !options.isEmpty()) {
            MessageUtil.sendMessage(player, "&8║");
            MessageUtil.sendMessage(player, "&8║ &7옵션 (" + options.size() + ")");
            for (int i = 0; i < options.size(); i++) {
                ItemOption option = options.get(i);
                String value = option.isPercentage() ? String.format("%. 1f%%", option.getValue()) : String.format("%.0f", option.getValue());
                MessageUtil.sendMessage(player, "&8║  &f[" + (i + 1) + "] &7" + option.getName() + ": &6" + value);
            }
        }
        
        // 소켓 및 보석
        if (item.getSockets() > 0) {
            MessageUtil.sendMessage(player, "&8║");
            MessageUtil.sendMessage(player, "&8║ &7소켓 (" + item.getGems().size() + "/" + item.getSockets() + ")");
            
            List<Gem> gems = item. getGems();
            for (int i = 0; i < item.getSockets(); i++) {
                if (i < gems.size() && gems.get(i) != null) {
                    Gem gem = gems.get(i);
                    MessageUtil.sendMessage(player, "&8║  &f[" + (i + 1) + "] &7" + gem.getName() + " (" + gem.getRarity().name() + ")");
                } else {
                    MessageUtil. sendMessage(player, "&8║  &f[" + (i + 1) + "] &7<비어있음>");
                }
            }
        }
        
        // 세트
        if (item.getSetId() != null && !item.getSetId().isEmpty()) {
            MessageUtil.sendMessage(player, "&8║");
            MessageUtil.sendMessage(player, "&8║ &7세트");
            MessageUtil.sendMessage(player, "&8║  &f세트 ID: &7" + item.getSetId());
        }
        
        // 내구도
        if (item.getMaxDurability() > 0) {
            MessageUtil.sendMessage(player, "&8║");
            MessageUtil.sendMessage(player, "&8║ &7내구도");
            int durability = item.getDurability();
            int maxDurability = item.getMaxDurability();
            double percentage = (double) durability / maxDurability * 100;
            
            String durabilityColor;
            if (percentage >= 75) {
                durabilityColor = "&a";
            } else if (percentage >= 50) {
                durabilityColor = "&e";
            } else if (percentage >= 25) {
                durabilityColor = "&6";
            } else {
                durabilityColor = "&c";
            }
            
            MessageUtil.sendMessage(player, "&8║  &f" + durability + "/" + maxDurability + " " + durabilityColor + String.format("%.1f%%", percentage));
        }
        
        // 거래 정보
        MessageUtil.sendMessage(player, "&8║");
        MessageUtil. sendMessage(player, "&8║ &7거래 정보");
        MessageUtil.sendMessage(player, "&8║  &f귀속: &7" + (item.isSoulbound() ? "&c예" : "&a아니오"));
        MessageUtil.sendMessage(player, "&8║  &f거래 횟수: &7" + item.getTradeCount() + "/" + item.getMaxTrades());
        
        MessageUtil.sendMessage(player, "&8╚════════════════════════════════════════╝");
        MessageUtil.sendMessage(player, "");
    }
}