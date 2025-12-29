package com. multiverse.pet. command;

import com. multiverse.pet. PetCore;
import com.multiverse.pet.command.subcommand.*;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit. command.CommandExecutor;
import org. bukkit.command. CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * 메인 펫 명령어 클래스
 * /pet 명령어 처리
 */
public class PetCommand implements CommandExecutor, TabCompleter {

    private final PetCore plugin;
    
    // 서브 명령어 맵
    private final Map<String, SubCommand> subCommands;
    
    // 서브 명령어 별칭
    private final Map<String, String> aliases;

    /**
     * 생성자
     */
    public PetCommand(PetCore plugin) {
        this.plugin = plugin;
        this.subCommands = new LinkedHashMap<>();
        this.aliases = new HashMap<>();
        
        registerSubCommands();
    }

    /**
     * 서브 명령어 등록
     */
    private void registerSubCommands() {
        // 기본 명령어
        registerSubCommand(new SummonSubCommand(plugin));
        registerSubCommand(new UnsummonSubCommand(plugin));
        registerSubCommand(new ListSubCommand(plugin));
        registerSubCommand(new InfoSubCommand(plugin));
        registerSubCommand(new RenameSubCommand(plugin));
        registerSubCommand(new SkillsSubCommand(plugin));
        registerSubCommand(new EvolveSubCommand(plugin));
        registerSubCommand(new FeedSubCommand(plugin));
        registerSubCommand(new BreedSubCommand(plugin));
        registerSubCommand(new BattleSubCommand(plugin));
        
        // 별칭 등록
        registerAlias("s", "summon");
        registerAlias("u", "unsummon");
        registerAlias("us", "unsummon");
        registerAlias("l", "list");
        registerAlias("i", "info");
        registerAlias("r", "rename");
        registerAlias("sk", "skills");
        registerAlias("e", "evolve");
        registerAlias("f", "feed");
        registerAlias("br", "breed");
        registerAlias("b", "battle");
    }

    /**
     * 서브 명령어 등록
     */
    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    /**
     * 별칭 등록
     */
    public void registerAlias(String alias, String command) {
        aliases.put(alias.toLowerCase(), command.toLowerCase());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 플레이어 확인
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        // 인자 없으면 도움말 또는 메인 GUI
        if (args. length == 0) {
            openMainMenu(player);
            return true;
        }

        // 서브 명령어 찾기
        String subCommandName = args[0].toLowerCase();
        
        // 별칭 확인
        if (aliases.containsKey(subCommandName)) {
            subCommandName = aliases. get(subCommandName);
        }

        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            // 도움말 표시
            if (subCommandName.equals("help") || subCommandName. equals("? ")) {
                showHelp(player, args. length > 1 ? args[1] : null);
            } else {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("command.unknown-subcommand")
                        .replace("{command}", subCommandName));
                showHelp(player, null);
            }
            return true;
        }

        // 권한 확인
        if (!subCommand.hasPermission(player)) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("command.no-permission"));
            return true;
        }

        // 서브 명령어 실행
        String[] subArgs = Arrays.copyOfRange(args, 1, args. length);
        
        try {
            subCommand.execute(player, subArgs);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("command.error")
                    .replace("{error}", e.getMessage()));
            plugin.getLogger().warning("명령어 실행 중 오류:  " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 첫 번째 인자 - 서브 명령어
            String input = args[0]. toLowerCase();
            
            for (SubCommand subCommand : subCommands.values()) {
                if (subCommand.hasPermission(player) && 
                    subCommand.getName().toLowerCase().startsWith(input)) {
                    completions.add(subCommand.getName());
                }
            }
            
            // 별칭도 추가
            for (Map.Entry<String, String> entry :  aliases.entrySet()) {
                if (entry.getKey().startsWith(input)) {
                    completions.add(entry.getKey());
                }
            }
            
            // help 추가
            if ("help".startsWith(input)) {
                completions. add("help");
            }
            
        } else if (args.length > 1) {
            // 서브 명령어의 탭 완성
            String subCommandName = args[0].toLowerCase();
            
            if (aliases.containsKey(subCommandName)) {
                subCommandName = aliases.get(subCommandName);
            }
            
            SubCommand subCommand = subCommands.get(subCommandName);
            
            if (subCommand != null && subCommand.hasPermission(player)) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                completions.addAll(subCommand.tabComplete(player, subArgs));
            }
        }

        return completions;
    }

    /**
     * 메인 메뉴 열기
     */
    private void openMainMenu(Player player) {
        plugin.getGUIManager().openMainMenu(player);
    }

    /**
     * 도움말 표시
     */
    private void showHelp(Player player, String specificCommand) {
        if (specificCommand != null) {
            // 특정 명령어 도움말
            String cmdName = specificCommand. toLowerCase();
            if (aliases.containsKey(cmdName)) {
                cmdName = aliases.get(cmdName);
            }
            
            SubCommand subCommand = subCommands.get(cmdName);
            if (subCommand != null && subCommand. hasPermission(player)) {
                showCommandHelp(player, subCommand);
                return;
            }
        }

        // 전체 도움말
        StringBuilder help = new StringBuilder();
        help.append("\n§6§l===== 펫 명령어 도움말 =====\n\n");
        
        for (SubCommand subCommand : subCommands.values()) {
            if (subCommand.hasPermission(player)) {
                help.append("§e/pet ").append(subCommand.getName());
                help.append(" §7- ").append(subCommand.getDescription());
                help.append("\n");
            }
        }
        
        help.append("\n§7/pet help <명령어> §8- 상세 도움말");
        help.append("\n§7/pet §8- 펫 메인 메뉴 열기");
        
        MessageUtil. sendMessage(player, help.toString());
    }

    /**
     * 특정 명령어 도움말 표시
     */
    private void showCommandHelp(Player player, SubCommand subCommand) {
        StringBuilder help = new StringBuilder();
        help.append("\n§6§l===== /pet ").append(subCommand.getName()).append(" =====\n\n");
        help.append("§7").append(subCommand. getDescription()).append("\n\n");
        help.append("§e사용법:  §f").append(subCommand. getUsage()).append("\n");
        
        // 별칭 표시
        List<String> cmdAliases = new ArrayList<>();
        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            if (entry.getValue().equals(subCommand.getName().toLowerCase())) {
                cmdAliases.add(entry. getKey());
            }
        }
        if (!cmdAliases.isEmpty()) {
            help.append("§e별칭: §f").append(String.join(", ", cmdAliases)).append("\n");
        }
        
        // 권한 표시
        help.append("§e권한: §f").append(subCommand. getPermission()).append("\n");
        
        // 예시 표시
        String[] examples = subCommand. getExamples();
        if (examples != null && examples.length > 0) {
            help.append("\n§e예시:\n");
            for (String example : examples) {
                help.append("§7  ").append(example).append("\n");
            }
        }
        
        MessageUtil.sendMessage(player, help.toString());
    }

    /**
     * 서브 명령어 가져오기
     */
    public SubCommand getSubCommand(String name) {
        return subCommands. get(name. toLowerCase());
    }

    /**
     * 모든 서브 명령어 가져오기
     */
    public Collection<SubCommand> getSubCommands() {
        return Collections.unmodifiableCollection(subCommands.values());
    }

    /**
     * 서브 명령어 인터페이스
     */
    public interface SubCommand {
        
        /**
         * 명령어 이름
         */
        String getName();
        
        /**
         * 명령어 설명
         */
        String getDescription();
        
        /**
         * 사용법
         */
        String getUsage();
        
        /**
         * 권한
         */
        String getPermission();
        
        /**
         * 예시
         */
        default String[] getExamples() {
            return new String[0];
        }
        
        /**
         * 권한 확인
         */
        default boolean hasPermission(Player player) {
            String permission = getPermission();
            return permission == null || permission.isEmpty() || player.hasPermission(permission);
        }
        
        /**
         * 명령어 실행
         */
        void execute(Player player, String[] args);
        
        /**
         * 탭 완성
         */
        default List<String> tabComplete(Player player, String[] args) {
            return Collections.emptyList();
        }
    }
}