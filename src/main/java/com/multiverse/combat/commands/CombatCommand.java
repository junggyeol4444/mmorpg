package com.multiverse.combat. commands;

import org.bukkit. Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models. Skill;
import com.multiverse.combat.utils.MessageUtil;
import java.util.List;

/**
 * 플레이어 명령어 처리 클래스
 * /combat 하위 명령어들을 처리합니다. 
 */
public class CombatCommand implements CommandExecutor {
    
    private final CombatCore plugin;
    private final MessageUtil messageUtil;
    
    /**
     * CombatCommand 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public CombatCommand(CombatCore plugin) {
        this.plugin = plugin;
        this.messageUtil = new MessageUtil(plugin);
    }
    
    /**
     * 명령어 실행
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showMainMenu(player);
            return true;
        }
        
        String subCommand = args[0]. toLowerCase();
        
        switch (subCommand) {
            case "skills":
                showSkillsMenu(player);
                break;
            case "skill":
                handleSkillCommand(player, args);
                break;
            case "stats":
                showStats(player);
                break;
            case "pvp":
                handlePvPCommand(player, args);
                break;
            case "ranking":
                showRanking(player);
                break;
            default:
                showMainMenu(player);
        }
        
        return true;
    }
    
    /**
     * 메인 메뉴 표시
     */
    private void showMainMenu(Player player) {
        player.sendMessage("§8§l========== CombatCore ==========");
        player.sendMessage("§6/combat skills§f - 스킬 목록 보기");
        player.sendMessage("§6/combat skill info <스킬ID>§f - 스킬 정보 조회");
        player.sendMessage("§6/combat skill bind <슬롯> <스킬ID>§f - 스킬 핫바 설정");
        player.sendMessage("§6/combat stats§f - 전투 통계 보기");
        player.sendMessage("§6/combat pvp [on|off]§f - PvP 설정");
        player.sendMessage("§6/combat ranking§f - PvP 랭킹 보기");
        player.sendMessage("§8§l================================");
    }
    
    /**
     * 스킬 메뉴 표시
     */
    private void showSkillsMenu(Player player) {
        if (! player.hasPermission("combat.player.skills")) {
            player. sendMessage(messageUtil.getMessage("messages.prefix") + "§c권한이 없습니다.");
            return;
        }
        
        List<Skill> playerSkills = plugin.getSkillManager().getPlayerSkills(player);
        
        player. sendMessage("§8§l========== 보유 스킬 ==========");
        if (playerSkills.isEmpty()) {
            player.sendMessage("§c보유한 스킬이 없습니다.");
        } else {
            for (Skill skill : playerSkills) {
                int level = plugin.getSkillManager().getSkillLevel(player, skill.getSkillId());
                player. sendMessage("§6" + skill.getName() + " §f(Lv.  " + level + "/" + skill.getMaxLevel() + ")");
                player.sendMessage("  §7" + skill.getDescription());
            }
        }
        player.sendMessage("§8§l============================");
    }
    
    /**
     * 스킬 관련 명령어 처리
     */
    private void handleSkillCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(messageUtil.getMessage("messages.prefix") + "§c사용법: /combat skill [info|bind] [스킬ID|슬롯] [스킬ID]");
            return;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "info":
                showSkillInfo(player, args);
                break;
            case "bind":
                bindSkillToHotbar(player, args);
                break;
            default:
                player.sendMessage(messageUtil.getMessage("messages.prefix") + "§c알 수 없는 액션: " + action);
        }
    }
    
    /**
     * 스킬 정보 표시
     */
    private void showSkillInfo(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(messageUtil.getMessage("messages.prefix") + "§c사용법: /combat skill info <스킬ID>");
            return;
        }
        
        String skillId = args[2];
        Skill skill = plugin.getSkillManager(). getSkill(skillId);
        
        if (skill == null) {
            player.sendMessage(messageUtil.getMessage("messages. prefix") + "§c스킬을 찾을 수 없습니다.");
            return;
        }
        
        if (!plugin.getSkillManager().hasSkill(player, skillId)) {
            player.sendMessage(messageUtil.getMessage("messages.prefix") + "§c아직 배우지 않은 스킬입니다.");
            return;
        }
        
        int level = plugin.getSkillManager().getSkillLevel(player, skillId);
        
        player.sendMessage("§8§l========== " + skill.getName() + " ==========");
        player.sendMessage("§f타입: §6" + skill.getType(). getDisplayName());
        player.sendMessage("§f카테고리: §6" + skill.getCategory().getDisplayName());
        player.sendMessage("§f현재 레벨: §6" + level + "§f / " + skill.getMaxLevel());
        player.sendMessage("§f설명: §7" + skill.getDescription());
        
        if (skill.getSkillEffect() != null) {
            player.sendMessage("§f기본 데미지: §6" + skill.getSkillEffect().getBaseDamage());
            player. sendMessage("§f사거리: §6" + skill. getSkillEffect().getRange());
            player.sendMessage("§f쿨다운: §6" + (skill.getBaseCooldown() / 1000. 0) + "초");
        }
        
        long cooldown = plugin.getSkillManager().getRemainingCooldown(player, skillId);
        if (cooldown > 0) {
            player.sendMessage("§c쿨다운: " + (cooldown / 1000.0) + "초");
        } else {
            player.sendMessage("§a사용 가능!");
        }
        
        player.sendMessage("§8§l=====================================");
    }
    
    /**
     * 스킬을 핫바에 바인드
     */
    private void bindSkillToHotbar(Player player, String[] args) {
        if (args.length < 4) {
            player.sendMessage(messageUtil.getMessage("messages. prefix") + "§c사용법: /combat skill bind <슬롯(1-5)> <스킬ID>");
            return;
        }
        
        try {
            int slot = Integer. parseInt(args[2]);
            String skillId = args[3];
            
            if (slot < 1 || slot > 5) {
                player.sendMessage(messageUtil.getMessage("messages.prefix") + "§c슬롯은 1~5 사이여야 합니다.");
                return;
            }
            
            if (!plugin.getSkillManager().hasSkill(player, skillId)) {
                player.sendMessage(messageUtil.getMessage("messages.prefix") + "§c배우지 않은 스킬입니다.");
                return;
            }
            
            plugin.getSkillManager().bindSkillToHotbar(player, slot - 1, skillId);
            Skill skill = plugin.getSkillManager().getSkill(skillId);
            player.sendMessage(messageUtil.getMessage("messages.prefix") + "§a" + skill.getName() + "을(를) 슬롯 " + slot + "에 바인드했습니다.");
            
        } catch (NumberFormatException e) {
            player. sendMessage(messageUtil.getMessage("messages.prefix") + "§c슬롯은 숫자여야 합니다.");
        }
    }
    
    /**
     * 전투 통계 표시
     */
    private void showStats(Player player) {
        if (!player.hasPermission("combat.player.stats")) {
            player.sendMessage(messageUtil. getMessage("messages.prefix") + "§c권한이 없습니다.");
            return;
        }
        
        // 통계 조회
        double totalDamageDealt = plugin.getCombatDataManager().getTotalDamageDealt(player);
        double totalDamageTaken = plugin.getCombatDataManager().getTotalDamageTaken(player);
        int totalKills = plugin.getCombatDataManager().getTotalKills(player);
        int totalDeaths = plugin.getCombatDataManager().getTotalDeaths(player);
        int maxCombo = plugin.getCombatDataManager().getMaxCombo(player);
        int totalCrits = plugin.getCombatDataManager().getTotalCrits(player);
        
        double kda = totalDeaths == 0 ? totalKills : (double) totalKills / totalDeaths;
        
        player.sendMessage("§8§l========== 전투 통계 ==========");
        player.sendMessage("§f누적 피해량: §6" + String.format("%. 0f", totalDamageDealt));
        player.sendMessage("§f누적 피해입은량: §6" + String.format("%.0f", totalDamageTaken));
        player. sendMessage("§f처치: §6" + totalKills + " §f| §6죽음: §f" + totalDeaths + " §f| §6KDA: §6" + String.format("%.2f", kda));
        player.sendMessage("§f최대 콤보: §6" + maxCombo);
        player.sendMessage("§f총 크리티컬: §6" + totalCrits);
        player.sendMessage("§8§l============================");
    }
    
    /**
     * PvP 설정 명령어
     */
    private void handlePvPCommand(Player player, String[] args) {
        if (! player.hasPermission("combat. player.pvp")) {
            player.sendMessage(messageUtil. getMessage("messages.prefix") + "§c권한이 없습니다.");
            return;
        }
        
        boolean currentState = plugin.getPvPManager().isPvPEnabled(player);
        boolean newState = currentState;
        
        if (args. length > 1) {
            newState = args[1].equalsIgnoreCase("on");
        } else {
            newState = !currentState;
        }
        
        plugin.getPvPManager().setPvPEnabled(player, newState);
        
        if (newState) {
            player.sendMessage(messageUtil.getMessage("messages.prefix") + "§aPvP가 활성화되었습니다.");
        } else {
            player.sendMessage(messageUtil.getMessage("messages. prefix") + "§cPvP가 비활성화되었습니다.");
        }
    }
    
    /**
     * PvP 랭킹 표시
     */
    private void showRanking(Player player) {
        if (!player.hasPermission("combat.player.ranking")) {
            player.sendMessage(messageUtil.getMessage("messages.prefix") + "§c권한이 없습니다.");
            return;
        }
        
        List<Player> topPlayers = plugin.getPvPManager().getTopPlayers(10);
        
        player.sendMessage("§8§l========== PvP 랭킹 ==========");
        int rank = 1;
        for (Player topPlayer : topPlayers) {
            int kills = plugin.getCombatDataManager().getTotalKills(topPlayer);
            int deaths = plugin. getCombatDataManager().getTotalDeaths(topPlayer);
            double kda = deaths == 0 ? kills : (double) kills / deaths;
            
            player.sendMessage("§6#" + rank + " §f" + topPlayer.getName() + " - §6처치: " + kills + " §f| §6KDA: " + String.format("%.2f", kda));
            rank++;
        }
        player.sendMessage("§8§l============================");
    }
}