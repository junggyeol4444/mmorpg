package com.multiverse.pet.command;

import com. multiverse.pet. PetCore;
import com. multiverse.pet. model.Pet;
import com.multiverse. pet.model.PetRarity;
import com.multiverse.pet.model.PetSpecies;
import com.multiverse.pet.model.acquisition.PetEgg;
import com. multiverse.pet. model.acquisition.PetSummonScroll;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * 펫 관리자 명령어 클래스
 * /petadmin 명령어 처리
 */
public class PetAdminCommand implements CommandExecutor, TabCompleter {

    private final PetCore plugin;

    private static final String[] ADMIN_COMMANDS = {
        "give", "remove", "setlevel", "setexp", "setrarity",
        "addskill", "removeskill", "heal", "feed",
        "spawn", "despawn", "despawnall",
        "giveegg", "givescroll",
        "reload", "save", "debug", "stats"
    };

    public PetAdminCommand(PetCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! sender.hasPermission("multiverse.pet.admin")) {
            sender.sendMessage("§c권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            showAdminHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "give":  handleGive(sender, args); break;
            case "remove": handleRemove(sender, args); break;
            case "setlevel": handleSetLevel(sender, args); break;
            case "setexp": handleSetExp(sender, args); break;
            case "setrarity": handleSetRarity(sender, args); break;
            case "addskill":  handleAddSkill(sender, args); break;
            case "removeskill": handleRemoveSkill(sender, args); break;
            case "heal": handleHeal(sender, args); break;
            case "feed": handleFeed(sender, args); break;
            case "spawn": handleSpawn(sender, args); break;
            case "despawn": handleDespawn(sender, args); break;
            case "despawnall":  handleDespawnAll(sender, args); break;
            case "giveegg":  handleGiveEgg(sender, args); break;
            case "givescroll":  handleGiveScroll(sender, args); break;
            case "reload": handleReload(sender); break;
            case "save": handleSave(sender); break;
            case "debug": handleDebug(sender, args); break;
            case "stats":  handleStats(sender); break;
            default: 
                sender.sendMessage("§c알 수 없는 명령어입니다: " + subCommand);
                showAdminHelp(sender);
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("multiverse.pet. admin")) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0]. toLowerCase();
            for (String cmd : ADMIN_COMMANDS) {
                if (cmd.startsWith(input)) {
                    completions.add(cmd);
                }
            }
        } else if (args.length == 2) {
            String subCmd = args[0].toLowerCase();
            String input = args[1].toLowerCase();
            
            // 플레이어 이름 자동완성
            if (Arrays.asList("give", "remove", "setlevel", "setexp", "setrarity", 
                    "addskill", "removeskill", "heal", "feed", "spawn", "despawn",
                    "giveegg", "givescroll").contains(subCmd)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(input)) {
                        completions.add(player. getName());
                    }
                }
            } else if (subCmd.equals("debug")) {
                for (String opt : Arrays.asList("on", "off", "level")) {
                    if (opt.startsWith(input)) {
                        completions.add(opt);
                    }
                }
            }
        } else if (args.length == 3) {
            String subCmd = args[0].toLowerCase();
            String input = args[2].toLowerCase();
            
            switch (subCmd) {
                case "give":
                    for (String speciesId : plugin.getSpeciesCache().getAllSpeciesIds()) {
                        if (speciesId.toLowerCase().startsWith(input)) {
                            completions.add(speciesId);
                        }
                    }
                    break;
                case "setrarity":
                case "giveegg":
                case "givescroll": 
                    for (PetRarity rarity : PetRarity.values()) {
                        if (rarity. name().toLowerCase().startsWith(input)) {
                            completions. add(rarity. name());
                        }
                    }
                    break;
                case "addskill":
                    for (String skillId : plugin.getPetSkillManager().getSkillTemplates().keySet()) {
                        if (skillId.toLowerCase().startsWith(input)) {
                            completions.add(skillId);
                        }
                    }
                    break;
                case "remove":
                case "setlevel":
                case "setexp": 
                case "removeskill":
                case "heal":
                case "feed": 
                case "spawn": 
                case "despawn":
                    Player target = Bukkit. getPlayer(args[1]);
                    if (target != null) {
                        for (Pet pet : plugin.getPetManager().getAllPets(target. getUniqueId())) {
                            if (pet. getPetName().toLowerCase().startsWith(input)) {
                                completions. add(pet.getPetName());
                            }
                        }
                        if ("all".startsWith(input)) {
                            completions.add("all");
                        }
                    }
                    break;
            }
        }

        return completions;
    }

    // ===== 명령어 처리 메서드들 =====

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c사용법:  /petadmin give <플레이어> <종족ID> [레벨] [희귀도]");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        String speciesId = args[2];
        PetSpecies species = plugin. getSpeciesCache().getSpecies(speciesId);
        if (species == null) {
            sender.sendMessage("§c존재하지 않는 종족입니다: " + speciesId);
            return;
        }

        int level = args.length > 3 ? parseIntOrDefault(args[3], 1) : 1;
        PetRarity rarity = args.length > 4 ? parseRarityOrDefault(args[4], PetRarity. COMMON) : PetRarity. COMMON;

        Pet pet = createPet(target. getUniqueId(), species, level, rarity);

        if (plugin.getPetManager().addNewPet(target. getUniqueId(), pet)) {
            sender.sendMessage("§a" + target.getName() + "에게 " + species.getName() + 
                    " (Lv." + level + ", " + rarity.getDisplayName() + ") 펫을 지급했습니다.");
            MessageUtil.sendMessage(target, plugin.getConfigManager().getMessage("admin.pet-received")
                    .replace("{name}", species.getName()));
        } else {
            sender.sendMessage("§c펫 지급에 실패했습니다.");
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args. length < 3) {
            sender. sendMessage("§c사용법: /petadmin remove <플레이어> <펫이름 또는 all>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        String petName = args[2];

        if (petName.equalsIgnoreCase("all")) {
            List<Pet> pets = plugin.getPetManager().getAllPets(target.getUniqueId());
            int count = 0;
            for (Pet pet : new ArrayList<>(pets)) {
                if (plugin.getPetManager().removePet(target.getUniqueId(), pet.getPetId())) {
                    count++;
                }
            }
            sender. sendMessage("§a" + target.getName() + "의 펫 " + count + "마리를 제거했습니다.");
        } else {
            Pet pet = findPetByName(target.getUniqueId(), petName);
            if (pet == null) {
                sender.sendMessage("§c펫을 찾을 수 없습니다: " + petName);
                return;
            }

            if (plugin. getPetManager().removePet(target. getUniqueId(), pet.getPetId())) {
                sender.sendMessage("§a" + target.getName() + "의 펫 '" + petName + "'을(를) 제거했습니다.");
            } else {
                sender.sendMessage("§c펫 제거에 실패했습니다.");
            }
        }
    }

    private void handleSetLevel(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§c사용법:  /petadmin setlevel <플레이어> <펫이름> <레벨>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        Pet pet = findPetByName(target. getUniqueId(), args[2]);
        if (pet == null) {
            sender.sendMessage("§c펫을 찾을 수 없습니다: " + args[2]);
            return;
        }

        int level = parseIntOrDefault(args[3], -1);
        if (level < 1) {
            sender. sendMessage("§c잘못된 레벨입니다: " + args[3]);
            return;
        }

        plugin.getPetLevelManager().setLevel(pet, level);
        sender.sendMessage("§a" + pet.getPetName() + "의 레벨을 " + level + "(으)로 설정했습니다.");
    }

    private void handleSetExp(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§c사용법: /petadmin setexp <플레이어> <펫이름> <경험치>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        Pet pet = findPetByName(target.getUniqueId(), args[2]);
        if (pet == null) {
            sender. sendMessage("§c펫을 찾을 수 없습니다:  " + args[2]);
            return;
        }

        long exp;
        try {
            exp = Long.parseLong(args[3]);
        } catch (NumberFormatException e) {
            sender. sendMessage("§c잘못된 경험치입니다: " + args[3]);
            return;
        }

        pet.setExperience(exp);
        plugin.getPetManager().savePetData(target.getUniqueId(), pet);
        sender.sendMessage("§a" + pet.getPetName() + "의 경험치를 " + exp + "(으)로 설정했습니다.");
    }

    private void handleSetRarity(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§c사용법: /petadmin setrarity <플레이어> <펫이름> <희귀도>");
            return;
        }

        Player target = Bukkit. getPlayer(args[1]);
        if (target == null) {
            sender. sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        Pet pet = findPetByName(target.getUniqueId(), args[2]);
        if (pet == null) {
            sender.sendMessage("§c펫을 찾을 수 없습니다: " + args[2]);
            return;
        }

        PetRarity rarity = parseRarityOrDefault(args[3], null);
        if (rarity == null) {
            sender. sendMessage("§c잘못된 희귀도입니다: " + args[3]);
            return;
        }

        pet. setRarity(rarity);
        plugin.getPetManager().savePetData(target.getUniqueId(), pet);
        sender.sendMessage("§a" + pet.getPetName() + "의 희귀도를 " + rarity.getDisplayName() + "(으)로 설정했습니다.");
    }

    private void handleAddSkill(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§c사용법:  /petadmin addskill <플레이어> <펫이름> <스킬ID>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        Pet pet = findPetByName(target. getUniqueId(), args[2]);
        if (pet == null) {
            sender.sendMessage("§c펫을 찾을 수 없습니다: " + args[2]);
            return;
        }

        if (plugin.getPetSkillManager().unlockSkill(pet, args[3])) {
            sender.sendMessage("§a" + pet. getPetName() + "에게 스킬 '" + args[3] + "'을(를) 추가했습니다.");
        } else {
            sender. sendMessage("§c스킬 추가에 실패했습니다.");
        }
    }

    private void handleRemoveSkill(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§c사용법: /petadmin removeskill <플레이어> <펫이름> <스킬ID>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        Pet pet = findPetByName(target. getUniqueId(), args[2]);
        if (pet == null) {
            sender.sendMessage("§c펫을 찾을 수 없습니다: " + args[2]);
            return;
        }

        if (plugin.getPetSkillManager().removeSkill(pet, args[3])) {
            sender. sendMessage("§a" + pet.getPetName() + "에서 스킬 '" + args[3] + "'을(를) 제거했습니다.");
        } else {
            sender. sendMessage("§c스킬 제거에 실패했습니다.");
        }
    }

    private void handleHeal(CommandSender sender, String[] args) {
        if (args. length < 3) {
            sender. sendMessage("§c사용법: /petadmin heal <플레이어> <펫이름 또는 all>");
            return;
        }

        Player target = Bukkit. getPlayer(args[1]);
        if (target == null) {
            sender. sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        if (args[2]. equalsIgnoreCase("all")) {
            List<Pet> pets = plugin.getPetManager().getAllPets(target.getUniqueId());
            for (Pet pet : pets) {
                fullHealPet(pet);
                plugin.getPetManager().savePetData(target.getUniqueId(), pet);
            }
            sender.sendMessage("§a" + target.getName() + "의 모든 펫을 회복시켰습니다.");
        } else {
            Pet pet = findPetByName(target.getUniqueId(), args[2]);
            if (pet == null) {
                sender.sendMessage("§c펫을 찾을 수 없습니다: " + args[2]);
                return;
            }

            fullHealPet(pet);
            plugin.getPetManager().savePetData(target. getUniqueId(), pet);
            sender.sendMessage("§a" + pet.getPetName() + "을(를) 회복시켰습니다.");
        }
    }

    private void handleFeed(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c사용법: /petadmin feed <플레이어> <펫이름 또는 all>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        if (args[2].equalsIgnoreCase("all")) {
            List<Pet> pets = plugin.getPetManager().getAllPets(target.getUniqueId());
            for (Pet pet : pets) {
                pet.setHunger(100);
                plugin. getPetManager().savePetData(target.getUniqueId(), pet);
            }
            sender.sendMessage("§a" + target. getName() + "의 모든 펫에게 먹이를 주었습니다.");
        } else {
            Pet pet = findPetByName(target.getUniqueId(), args[2]);
            if (pet == null) {
                sender.sendMessage("§c펫을 찾을 수 없습니다: " + args[2]);
                return;
            }

            pet.setHunger(100);
            plugin.getPetManager().savePetData(target. getUniqueId(), pet);
            sender.sendMessage("§a" + pet.getPetName() + "에게 먹이를 주었습니다.");
        }
    }

    private void handleSpawn(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c사용법: /petadmin spawn <플레이어> <펫이름>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        Pet pet = findPetByName(target.getUniqueId(), args[2]);
        if (pet == null) {
            sender.sendMessage("§c펫을 찾을 수 없습니다: " + args[2]);
            return;
        }

        if (plugin.getPetManager().summonPet(target, pet. getPetId())) {
            sender.sendMessage("§a" + pet.getPetName() + "을(를) 소환했습니다.");
        } else {
            sender.sendMessage("§c펫 소환에 실패했습니다.");
        }
    }

    private void handleDespawn(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c사용법: /petadmin despawn <플레이어> <펫이름>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        Pet pet = findPetByName(target. getUniqueId(), args[2]);
        if (pet == null) {
            sender.sendMessage("§c펫을 찾을 수 없습니다: " + args[2]);
            return;
        }

        if (plugin.getPetManager().unsummonPet(target, pet.getPetId())) {
            sender.sendMessage("§a" + pet.getPetName() + "을(를) 해제했습니다.");
        } else {
            sender. sendMessage("§c펫 해제에 실패했습니다.");
        }
    }

    private void handleDespawnAll(CommandSender sender, String[] args) {
        if (args. length < 2) {
            plugin.getPetEntityManager().despawnAllPets();
            sender.sendMessage("§a서버의 모든 펫을 해제했습니다.");
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
                return;
            }

            plugin.getPetManager().unsummonAllPets(target);
            sender.sendMessage("§a" + target.getName() + "의 모든 펫을 해제했습니다.");
        }
    }

    private void handleGiveEgg(CommandSender sender, String[] args) {
        if (args. length < 3) {
            sender. sendMessage("§c사용법: /petadmin giveegg <플레이어> <알타입> [희귀도]");
            return;
        }

        Player target = Bukkit. getPlayer(args[1]);
        if (target == null) {
            sender. sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        String eggType = args[2];
        PetRarity minRarity = args.length > 3 ? parseRarityOrDefault(args[3], PetRarity.COMMON) : PetRarity.COMMON;

        PetEgg egg = plugin.getPetAcquisitionManager().createRandomEgg(eggType, minRarity);
        
        if (plugin. getPetAcquisitionManager().addEgg(target, egg)) {
            sender.sendMessage("§a" + target.getName() + "에게 " + eggType + " 알을 지급했습니다.");
        } else {
            sender. sendMessage("§c알 지급에 실패했습니다.");
        }
    }

    private void handleGiveScroll(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§c사용법: /petadmin givescroll <플레이어> <희귀도>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c플레이어를 찾을 수 없습니다: " + args[1]);
            return;
        }

        PetRarity rarity = parseRarityOrDefault(args[2], null);
        if (rarity == null) {
            sender. sendMessage("§c잘못된 희귀도입니다: " + args[2]);
            return;
        }

        PetSummonScroll scroll = plugin. getPetAcquisitionManager().createRandomScroll(rarity);
        
        // 아이템으로 지급
        target.getInventory().addItem(scroll. toItemStack());
        sender.sendMessage("§a" + target. getName() + "에게 " + rarity.getDisplayName() + " 소환서를 지급했습니다.");
    }

    private void handleReload(CommandSender sender) {
        long startTime = System.currentTimeMillis();
        
        plugin.reload();
        
        long endTime = System. currentTimeMillis();
        sender.sendMessage("§a플러그인을 리로드했습니다. (" + (endTime - startTime) + "ms)");
    }

    private void handleSave(CommandSender sender) {
        long startTime = System.currentTimeMillis();
        
        plugin. saveAllData();
        
        long endTime = System.currentTimeMillis();
        sender.sendMessage("§a모든 데이터를 저장했습니다. (" + (endTime - startTime) + "ms)");
    }

    private void handleDebug(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c사용법:  /petadmin debug <on/off/level>");
            return;
        }

        String option = args[1]. toLowerCase();

        switch (option) {
            case "on": 
                plugin.setDebugMode(true);
                sender.sendMessage("§a디버그 모드를 활성화했습니다.");
                break;
            case "off": 
                plugin.setDebugMode(false);
                sender.sendMessage("§a디버그 모드를 비활성화했습니다.");
                break;
            case "level":
                if (args.length < 3) {
                    sender.sendMessage("§c현재 디버그 레벨:  " + plugin.getDebugLevel());
                } else {
                    int level = parseIntOrDefault(args[2], 0);
                    plugin.setDebugLevel(level);
                    sender.sendMessage("§a디버그 레벨을 " + level + "(으)로 설정했습니다.");
                }
                break;
            default:
                sender.sendMessage("§c알 수 없는 옵션입니다: " + option);
                break;
        }
    }

    private void handleStats(CommandSender sender) {
        Map<String, Integer> entityStats = plugin.getPetEntityManager().getStatistics();
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n§6§l===== 펫 시스템 통계 =====\n\n");
        
        sb.append("§e활성 펫 엔티티: §f").append(entityStats. getOrDefault("total_entities", 0)).append("\n");
        sb.append("§e유효한 엔티티:  §f").append(entityStats.getOrDefault("valid_entities", 0)).append("\n");
        sb.append("§e무효한 엔티티: §f").append(entityStats.getOrDefault("invalid_entities", 0)).append("\n");
        sb.append("§e활성 플레이어: §f").append(entityStats.getOrDefault("total_players", 0)).append("\n");
        
        sb.append("\n§e캐시된 펫 데이터: §f").append(plugin.getPetCache().getCacheSize()).append("\n");
        sb.append("§e등록된 종족: §f").append(plugin.getSpeciesCache().getAllSpeciesIds().size()).append("\n");
        sb.append("§e등록된 스킬: §f").append(plugin.getPetSkillManager().getSkillTemplates().size()).append("\n");
        
        sender.sendMessage(sb.toString());
    }

    private void showAdminHelp(CommandSender sender) {
        StringBuilder help = new StringBuilder();
        help.append("\n§6§l===== 펫 관리자 명령어 =====\n\n");
        
        help.append("§e/petadmin give <플레이어> <종족ID> [레벨] [희귀도] §7- 펫 지급\n");
        help.append("§e/petadmin remove <플레이어> <펫이름/all> §7- 펫 제거\n");
        help.append("§e/petadmin setlevel <플레이어> <펫이름> <레벨> §7- 레벨 설정\n");
        help.append("§e/petadmin setexp <플레이어> <펫이름> <경험치> §7- 경험치 설정\n");
        help.append("§e/petadmin setrarity <플레이어> <펫이름> <희귀도> §7- 희귀도 설정\n");
        help.append("§e/petadmin addskill <플레이어> <펫이름> <스킬ID> §7- 스킬 추가\n");
        help.append("§e/petadmin removeskill <플레이어> <펫이름> <스킬ID> §7- 스킬 제거\n");
        help.append("§e/petadmin heal <플레이어> <펫이름/all> §7- 회복\n");
        help.append("§e/petadmin feed <플레이어> <펫이름/all> §7- 먹이주기\n");
        help.append("§e/petadmin spawn <플레이어> <펫이름> §7- 펫 소환\n");
        help.append("§e/petadmin despawn <플레이어> <펫이름> §7- 펫 해제\n");
        help.append("§e/petadmin despawnall [플레이어] §7- 모든 펫 해제\n");
        help.append("§e/petadmin giveegg <플레이어> <알타입> [희귀도] §7- 알 지급\n");
        help.append("§e/petadmin givescroll <플레이어> <희귀도> §7- 소환서 지급\n");
        help.append("§e/petadmin reload §7- 설정 리로드\n");
        help.append("§e/petadmin save §7- 데이터 저장\n");
        help.append("§e/petadmin debug <on/off/level> §7- 디버그 모드\n");
        help.append("§e/petadmin stats §7- 시스템 통계\n");
        
        sender.sendMessage(help.toString());
    }

    // ===== 유틸리티 메서드 =====

    private Pet findPetByName(UUID playerId, String name) {
        List<Pet> pets = plugin.getPetManager().getAllPets(playerId);
        for (Pet pet :  pets) {
            if (pet.getPetName().equalsIgnoreCase(name)) {
                return pet;
            }
        }
        return null;
    }

    private Pet createPet(UUID ownerId, PetSpecies species, int level, PetRarity rarity) {
        Pet pet = new Pet();
        pet.setOwnerId(ownerId);
        pet.setSpeciesId(species.getSpeciesId());
        pet.setPetName(species. getName());
        pet.setType(species.getType());
        pet.setRarity(rarity);
        pet.setLevel(level);
        pet.setEntityType(species.getEntityType());
        pet.setBaseStats(species.getAllStatsAtLevel(level));
        pet.setMaxHealth(pet.getTotalStat("health"));
        pet.setHealth(pet.getMaxHealth());
        pet.setHunger(100);
        pet.setHappiness(100);
        return pet;
    }

    private void fullHealPet(Pet pet) {
        pet.setHealth(pet.getMaxHealth());
        pet.setHunger(100);
        pet.setHappiness(100);
    }

    private int parseIntOrDefault(String str, int defaultValue) {
        try {
            return Integer. parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private PetRarity parseRarityOrDefault(String str, PetRarity defaultValue) {
        try {
            return PetRarity.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}