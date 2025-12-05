package com. multiverse.item. commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;
import com.multiverse.item. commands.subcommands.*;
import com.multiverse.item. utils.MessageUtil;
import java.util.HashMap;
import java.util.Map;

public class ItemCommand implements CommandExecutor {
    
    private ItemCore plugin;
    private Map<String, SubCommand> subCommands;
    
    public ItemCommand(ItemCore plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();
        
        // 서브커맨드 등록
        registerSubCommands();
    }
    
    private void registerSubCommands() {
        subCommands.put("give", new GiveCommand(plugin));
        subCommands.put("info", new InfoCommand(plugin));
        subCommands.put("enhance", new EnhanceCommand(plugin));
        subCommands.put("socket", new SocketCommand(plugin));
        subCommands.put("disassemble", new DisassembleCommand(plugin));
        subCommands.put("identify", new IdentifyCommand(plugin));
        subCommands.put("reroll", new RerollCommand(plugin));
        subCommands.put("trade", new TradeCommand(plugin));
        subCommands.put("admin", new AdminCommand(plugin));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        // 플레이어 확인
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "&c이 명령어는 플레이어만 사용 가능합니다!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // 인자 없음
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        // 서브커맨드 없음
        if (! subCommands.containsKey(subCommand)) {
            MessageUtil.sendMessage(player, "&c알 수 없는 커맨드입니다.  &7/item help&c를 입력하세요.");
            return true;
        }
        
        // 권한 확인
        if (! hasPermission(player, subCommand)) {
            MessageUtil.sendMessage(player, "&c이 명령어를 사용할 권한이 없습니다!");
            return true;
        }
        
        try {
            // 서브커맨드 실행
            SubCommand sub = subCommands.get(subCommand);
            String[] subArgs = new String[args.length - 1];
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);
            
            sub.execute(player, subArgs);
            
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "&c명령어 실행 중 오류가 발생했습니다!");
            plugin.getLogger().severe("명령어 실행 오류: " + subCommand);
            e.printStackTrace();
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        MessageUtil.sendMessage(player, "");
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
        MessageUtil.sendMessage(player, "&eItemCore 커맨드 도움말");
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
        MessageUtil.sendMessage(player, "&7/item info &f- 아이템 정보 확인");
        MessageUtil.sendMessage(player, "&7/item enhance &f- 아이템 강화");
        MessageUtil.sendMessage(player, "&7/item socket &f- 보석 장착");
        MessageUtil.sendMessage(player, "&7/item disassemble &f- 아이템 분해");
        MessageUtil.sendMessage(player, "&7/item identify &f- 아이템 식별");
        MessageUtil.sendMessage(player, "&7/item reroll &f- 옵션 리롤");
        MessageUtil.sendMessage(player, "&7/item trade &f- 거래");
        
        if (player.hasPermission("item.admin")) {
            MessageUtil.sendMessage(player, "&8");
            MessageUtil.sendMessage(player, "&c[관리자 커맨드]");
            MessageUtil.sendMessage(player, "&7/item admin give <플레이어> <아이템ID> [등급] [강화]");
            MessageUtil.sendMessage(player, "&7/item admin create <아이템ID>");
            MessageUtil.sendMessage(player, "&7/item admin reload");
        }
        
        MessageUtil.sendMessage(player, "&8═══════════════════════════════════");
        MessageUtil.sendMessage(player, "");
    }
    
    private boolean hasPermission(Player player, String subCommand) {
        if (subCommand.equals("admin")) {
            return player.hasPermission("item.admin");
        }
        return player.hasPermission("item. player");
    }
}