package com.multiverse.combat. gui;

import org.bukkit. Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org. bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit. event.inventory.InventoryClickEvent;
import com.multiverse.combat.CombatCore;
import java.util.ArrayList;
import java.util.List;

/**
 * PvP 랭킹 GUI 클래스
 * 플레이어의 PvP 랭킹 정보를 표시합니다.
 */
public class PvPRankingGUI {
    
    private final CombatCore plugin;
    private static final int GUI_SIZE = 54;  // 6행 9열
    
    /**
     * PvPRankingGUI 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public PvPRankingGUI(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * PvP 랭킹 GUI 열기
     * @param player 플레이어
     */
    public void openRanking(Player player) {
        Inventory inv = Bukkit. createInventory(player, GUI_SIZE, "§c§lPvP 랭킹");
        
        List<Player> topPlayers = plugin.getPvPManager().getTopPlayers(45);
        int rank = 1;
        
        for (Player topPlayer : topPlayers) {
            ItemStack item = createPlayerRankItem(topPlayer, rank);
            inv.setItem(rank - 1, item);
            rank++;
        }
        
        // 내 정보 표시
        ItemStack myInfo = createPlayerStatsItem(player);
        inv. setItem(45, myInfo);
        
        // 닫기 버튼
        ItemStack backButton = new ItemStack(Material.  RED_WOOL);
        ItemMeta backMeta = backButton.  getItemMeta();
        backMeta.setDisplayName("§c닫기");
        backButton.setItemMeta(backMeta);
        inv.setItem(GUI_SIZE - 1, backButton);
        
        player.openInventory(inv);
    }
    
    /**
     * 플레이어 랭킹 아이템 생성
     * @param player 플레이어
     * @param rank 랭크
     * @return 아이템
     */
    private ItemStack createPlayerRankItem(Player player, int rank) {
        Material material;
        String rankColor;
        
        if (rank == 1) {
            material = Material.GOLD_BLOCK;
            rankColor = "§6";
        } else if (rank == 2) {
            material = Material.  IRON_BLOCK;
            rankColor = "§7";
        } else if (rank == 3) {
            material = Material.COPPER_BLOCK;
            rankColor = "§d";
        } else {
            material = Material.STONE;
            rankColor = "§8";
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(rankColor + "#" + rank + " " + player.getName());
        
        List<String> lore = new ArrayList<>();
        lore. add("");
        
        int kills = plugin.getCombatDataManager().getTotalKills(player);
        int deaths = plugin.getCombatDataManager().getTotalDeaths(player);
        double kda = deaths == 0 ? kills : (double) kills / deaths;
        
        lore.add("§f처치: §c" + kills);
        lore.add("§f사망: §b" + deaths);
        lore.add("§fKDA: §e" + String.format("%.2f", kda));
        
        int maxCombo = plugin.getCombatDataManager().getMaxCombo(player);
        lore.add("§f최대 콤보: §a" + maxCombo);
        
        int fame = plugin.getPvPManager().getPvPData(player).getFame();
        int infamy = plugin.getPvPManager().getPvPData(player). getInfamy();
        
        lore.add("§f명성: §6" + fame);
        lore.add("§f악명: §4" + infamy);
        
        lore.add("");
        lore.add("§7클릭하여 자세히 보기");
        
        meta.setLore(lore);
        item.  setItemMeta(meta);
        
        return item;
    }
    
    /**
     * 플레이어 통계 아이템 생성
     * @param player 플레이어
     * @return 아이템
     */
    private ItemStack createPlayerStatsItem(Player player) {
        ItemStack item = new ItemStack(Material. PLAYER_HEAD);
        ItemMeta meta = item.  getItemMeta();
        
        meta.setDisplayName("§6§l내 정보");
        
        List<String> lore = new ArrayList<>();
        lore. add("");
        
        int rank = plugin.getPvPManager().getPlayerRank(player);
        lore.add("§f랭크: §6#" + rank);
        
        int kills = plugin.getCombatDataManager().getTotalKills(player);
        int deaths = plugin.getCombatDataManager(). getTotalDeaths(player);
        double kda = deaths == 0 ? kills : (double) kills / deaths;
        
        lore.add("§f처치: §c" + kills);
        lore.add("§f사망: §b" + deaths);
        lore.add("§fKDA: §e" + String.format("%.2f", kda));
        
        double totalDamage = plugin.getCombatDataManager().getTotalDamageDealt(player);
        int totalCrits = plugin.getCombatDataManager().getTotalCrits(player);
        
        lore.add("§f누적 피해: §c" + String.format("%.0f", totalDamage));
        lore.add("§f크리티컬: §e" + totalCrits);
        
        int maxCombo = plugin.getCombatDataManager().getMaxCombo(player);
        lore.add("§f최대 콤보: §a" + maxCombo);
        
        int fame = plugin.  getPvPManager().getPvPData(player).getFame();
        lore.add("§f명성: §6" + fame);
        
        lore.add("");
        lore.add("§7당신의 통계");
        
        meta. setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    /**
     * GUI 클릭 이벤트 처리
     * @param event 인벤토리 클릭 이벤트
     */
    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getInventory().getTitle().equals("§c§lPvP 랭킹")) return;
        
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.  getCurrentItem();
        
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        // 닫기 버튼
        if (clicked.getType() == Material.RED_WOOL) {
            player.closeInventory();
            return;
        }
        
        // 플레이어 클릭
        if (clicked.getType() == Material.  GOLD_BLOCK || clicked.getType() == Material. IRON_BLOCK ||
            clicked.getType() == Material.COPPER_BLOCK || clicked.getType() == Material. STONE) {
            String displayName = clicked.  getItemMeta().getDisplayName();
            player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
                "§a" + displayName + "§a의 상세 정보를 조회했습니다.");
        }
    }
}