package com.multiverse.combat. commands;

import org.bukkit. Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models. Skill;
import com.multiverse. combat.models.StatusEffect;
import com.multiverse.combat.models.enums. StatusEffectType;
import com.multiverse.combat. utils.MessageUtil;

/**
 * 관리자 명령어 처리 클래스
 * /combat admin 하위 명령어들을 처리합니다.
 */
public class AdminCommand implements CommandExecutor {
    
    private final CombatCore plugin;
    private final MessageUtil messageUtil;
    
    /**
     * AdminCommand 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public AdminCommand(CombatCore plugin) {
        this.plugin = plugin;
        this.messageUtil = new MessageUtil(plugin);
    }
    
    /**
     * 명령어 실행
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 권한 확인
        if (! sender.hasPermission("combat. admin")) {
            sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§c권한이 없습니다.");
            return true;
        }
        
        if (args.length < 2) {
            sendAdminHelp(sender);
            return true;
        }
        
        String subCommand = args[1]. toLowerCase();
        
        switch (subCommand) {
            case "skill":
                handleSkillCommand(sender, args);
                break;
            case "resetcooldowns":
                handleResetCooldowns(sender, args);
                break;
            case "effect":
                handleEffectCommand(sender, args);
                break;
            case "cleareffects":
                handleClearEffects(sender, args);
                break;
            case "pvp":
                handlePvPCommand(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            default:
                sendAdminHelp(sender);
        }
        
        return true;
    }
    
    /**
     * 스킬 관련 명령어 처리
     * /combat admin skill [give|setlevel] <플레이어> <스킬ID> [레벨]
     */
    private void handleSkillCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§c사용법: /combat admin skill [give|setlevel] <플레이어> <스킬ID> [레벨]");
            return;
        }
        
        String action = args[2].toLowerCase();
        Player target = Bukkit.getPlayer(args[3]);
        
        if (target == null) {
            sender. sendMessage(messageUtil.getMessage("messages.prefix") + "§c플레이어를 찾을 수 없습니다.");
            return;
        }
        
        String skillId = args[4];
        Skill skill = plugin.getSkillManager().getSkill(skillId);
        
        if (skill == null) {
            sender. sendMessage(messageUtil.getMessage("messages.prefix") + "§c스킬을 찾을 수 없습니다: " + skillId);
            return;
        }
        
        switch (action) {
            case "give":
                int giveLevel = args. length > 5 ? parseLevel(args[5], 1) : 1;
                plugin.getSkillManager().learnSkill(target, skillId);
                if (giveLevel > 1) {
                    plugin.getSkillManager().setSkillLevel(target, skillId, giveLevel);
                }
                sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§a" + target.getName() + "에게 " + skill.getName() + " §a(Lv." + giveLevel + ")를 지급했습니다.");
                target.sendMessage(messageUtil.getMessage("messages.prefix") + "§a" + skill.getName() + " §a(Lv." + giveLevel + ")를 획득했습니다!");
                break;
                
            case "setlevel":
                if (args.length < 6) {
                    sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§c사용법: /combat admin skill setlevel <플레이어> <스킬ID> <레벨>");
                    return;
                }
                int level = parseLevel(args[5], skill.getMaxLevel());
                plugin.getSkillManager().setSkillLevel(target, skillId, level);
                sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§a" + target.getName() + "의 " + skill.getName() + " 레벨을 §e" + level + "§a(으)로 설정했습니다.");
                break;
                
            default:
                sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§c알 수 없는 액션: " + action);
        }
    }
    
    /**
     * 쿨다운 초기화 명령어
     * /combat admin resetcooldowns <플레이어>
     */
    private void handleResetCooldowns(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(messageUtil.getMessage("messages. prefix") + "§c사용법: /combat admin resetcooldowns <플레이어>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[2]);
        
        if (target == null) {
            sender. sendMessage(messageUtil.getMessage("messages.prefix") + "§c플레이어를 찾을 수 없습니다.");
            return;
        }
        
        plugin.getSkillManager().resetAllCooldowns(target);
        sender.sendMessage(messageUtil. getMessage("messages.prefix") + "§a" + target.getName() + "의 모든 스킬 쿨다운을 초기화했습니다.");
        target.sendMessage(messageUtil. getMessage("messages.prefix") + "§a모든 스킬 쿨다운이 초기화되었습니다!");
    }
    
    /**
     * 상태이상 적용 명령어
     * /combat admin effect <플레이어> <효과> <레벨> <시간(초)>
     */
    private void handleEffectCommand(CommandSender sender, String[] args) {
        if (args.length < 6) {
            sender.sendMessage(messageUtil.getMessage("messages. prefix") + "§c사용법: /combat admin effect <플레이어> <효과> <레벨> <시간(초)>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[2]);
        
        if (target == null) {
            sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§c플레이어를 찾을 수 없습니다.");
            return;
        }
        
        try {
            StatusEffectType effectType = StatusEffectType.fromString(args[3]);
            if (effectType == null) {
                sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§c알 수 없는 상태이상: " + args[3]);
                return;
            }
            
            int level = Integer.parseInt(args[4]);
            int seconds = Integer.parseInt(args[5]);
            
            StatusEffect effect = new StatusEffect(effectType, level, seconds * 1000L);
            plugin.getStatusEffectManager().applyEffect(target, effect);
            
            sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§a" + target.getName() + "에게 " + effectType.getDisplayName() + " 상태이상을 적용했습니다.");
            target.sendMessage(messageUtil. getMessage("messages.prefix") + "§c" + effectType.getDisplayName() + " 상태이상에 걸렸습니다!");
            
        } catch (NumberFormatException e) {
            sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§c레벨과 시간은 숫자여야 합니다.");
        }
    }
    
    /**
     * 상태이상 제거 명령어
     * /combat admin cleareffects <플레이어>
     */
    private void handleClearEffects(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(messageUtil.getMessage("messages. prefix") + "§c사용법: /combat admin cleareffects <플레이어>");
            return;
        }
        
        Player target = Bukkit. getPlayer(args[2]);
        
        if (target == null) {
            sender.sendMessage(messageUtil.getMessage("messages. prefix") + "§c플레이어를 찾을 수 없습니다.");
            return;
        }
        
        plugin.getStatusEffectManager().clearAllEffects(target);
        sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§a" + target.getName() + "의 모든 상태이상을 제거했습니다.");
        target.sendMessage(messageUtil.getMessage("messages.prefix") + "§a모든 상태이상이 제거되었습니다!");
    }
    
    /**
     * PvP 설정 명령어
     * /combat admin pvp <플레이어> <on|off>
     */
    private void handlePvPCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(messageUtil.getMessage("messages. prefix") + "§c사용법: /combat admin pvp <플레이어> <on|off>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[2]);
        
        if (target == null) {
            sender.sendMessage(messageUtil. getMessage("messages.prefix") + "§c플레이어를 찾을 수 없습니다.");
            return;
        }
        
        boolean enabled = args[3]. equalsIgnoreCase("on");
        plugin.getPvPManager().setPvPEnabled(target, enabled);
        
        String status = enabled ? "§a활성화" : "§c비활성화";
        sender. sendMessage(messageUtil.getMessage("messages.prefix") + "§a" + target.getName() + "의 PvP가 " + status + "§a되었습니다.");
        target.sendMessage(messageUtil.getMessage("messages.prefix") + "PvP가 " + status + "§a되었습니다!");
    }
    
    /**
     * 설정 리로드 명령어
     * /combat admin reload
     */
    private void handleReload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(messageUtil.getMessage("messages.prefix") + "§a설정을 다시 로드했습니다.");
    }
    
    /**
     * 관리자 명령어 도움말 표시
     */
    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage("§8§l========== CombatCore 관리자 명령어 ==========");
        sender.sendMessage("§6/combat admin skill give <플레이어> <스킬ID> [레벨]");
        sender.sendMessage("§6/combat admin skill setlevel <플레이어> <스킬ID> <레벨>");
        sender.sendMessage("§6/combat admin resetcooldowns <플레이어>");
        sender. sendMessage("§6/combat admin effect <플레이어> <효과> <레벨> <시간(초)>");
        sender.sendMessage("§6/combat admin cleareffects <플레이어>");
        sender. sendMessage("§6/combat admin pvp <플레이어> <on|off>");
        sender.sendMessage("§6/combat admin reload");
        sender.sendMessage("§8§l==========================================");
    }
    
    /**
     * 문자열을 정수로 변환 (범위 제한)
     */
    private int parseLevel(String input, int max) {
        try {
            int level = Integer.parseInt(input);
            return Math.min(level, max);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}