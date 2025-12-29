package com. multiverse.pet. command.subcommand;

import com. multiverse.pet. PetCore;
import com.multiverse.pet.command. PetCommand.SubCommand;
import com.multiverse.pet.entity.PetEntity;
import com.multiverse.pet.model.Pet;
import com.multiverse.pet.model.skill.PetSkill;
import com.multiverse.pet.util. MessageUtil;
import org.bukkit. entity.Player;

import java.util. ArrayList;
import java.util.List;
import java.util.Map;
import java.util. UUID;

/**
 * 펫 정보 서브 명령어
 * /pet info [펫이름]
 */
public class InfoSubCommand implements SubCommand {

    private final PetCore plugin;

    public InfoSubCommand(PetCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "펫의 상세 정보를 표시합니다. ";
    }

    @Override
    public String getUsage() {
        return "/pet info [펫이름]";
    }

    @Override
    public String getPermission() {
        return "multiverse.pet. info";
    }

    @Override
    public String[] getExamples() {
        return new String[] {
            "/pet info",
            "/pet info 늑대",
            "/pet i 드래곤"
        };
    }

    @Override
    public void execute(Player player, String[] args) {
        UUID playerId = player.getUniqueId();
        Pet pet;

        if (args.length == 0) {
            // 활성 펫 정보
            PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
            if (activePet == null) {
                MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.no-active-pet-info"));
                return;
            }
            pet = activePet.getPet();
        } else {
            // 이름으로 검색
            pet = findPet(playerId, args[0]);
            if (pet == null) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found")
                        .replace("{name}", args[0]));
                return;
            }
        }

        showPetInfo(player, pet);
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<Pet> pets = plugin.getPetManager().getAllPets(player.getUniqueId());

            for (Pet pet : pets) {
                if (pet.getPetName().toLowerCase().startsWith(input)) {
                    completions.add(pet. getPetName());
                }
            }
        }

        return completions;
    }

    /**
     * 펫 찾기
     */
    private Pet findPet(UUID playerId, String identifier) {
        List<Pet> pets = plugin.getPetManager().getAllPets(playerId);

        // 정확히 일치
        for (Pet pet : pets) {
            if (pet. getPetName().equalsIgnoreCase(identifier)) {
                return pet;
            }
        }

        // 부분 일치
        for (Pet pet :  pets) {
            if (pet.getPetName().toLowerCase().contains(identifier.toLowerCase())) {
                return pet;
            }
        }

        return null;
    }

    /**
     * 펫 정보 표시
     */
    private void showPetInfo(Player player, Pet pet) {
        StringBuilder sb = new StringBuilder();

        // 헤더
        sb.append("\n");
        sb.append(pet.getRarity().getColorCode().replace("&", "§"));
        sb.append("§l===== ").append(pet.getPetName()).append(" =====\n\n");

        // 기본 정보
        sb.append("§e종족: §f").append(pet.getSpeciesId()).append("\n");
        sb.append("§e희귀도: ").append(pet.getRarity().getColoredName()).append("\n");
        sb.append("§e타입: §f").append(pet.getType() != null ? pet.getType().getDisplayName() : "없음").append("\n");
        sb.append("§e상태: §f").append(pet.getStatus().getDisplayName()).append("\n");
        sb.append("\n");

        // 레벨 & 경험치
        sb.append("§e레벨: §f").append(pet.getLevel());
        if (pet.getLevel() < plugin.getPetLevelManager().getMaxLevelForPet(pet)) {
            double expPercent = plugin.getPetLevelManager().getExpPercentage(pet);
            sb.append(" §7(").append(String.format("%.1f", expPercent)).append("%)");
        } else {
            sb.append(" §6(MAX)");
        }
        sb.append("\n");

        sb.append("§e경험치: §f").append(pet.getExperience()).append("§7/§f").append(pet.getExpToNext()).append("\n");
        sb.append("\n");

        // 상태
        sb.append("§c❤ 체력: §f").append(String.format("%.1f", pet.getHealth()));
        sb.append("§7/§f").append(String.format("%.1f", pet.getMaxHealth())).append("\n");
        
        sb.append("§6🍖 배고픔: ").append(getBarDisplay(pet.getHunger(), 100)).append("\n");
        sb.append("§d😊 행복도: ").append(getBarDisplay(pet.getHappiness(), 100)).append("\n");
        sb.append("\n");

        // 스탯
        sb. append("§e§l[ 스탯 ]\n");
        Map<String, Double> totalStats = pet.calculateTotalStats();
        
        sb.append("§c⚔ 공격력: §f").append(String.format("%.1f", totalStats. getOrDefault("attack", 0.0))).append("\n");
        sb.append("§b🛡 방어력: §f").append(String.format("%.1f", totalStats.getOrDefault("defense", 0.0))).append("\n");
        sb.append("§a💨 속도: §f").append(String.format("%.1f", totalStats.getOrDefault("speed", 0.0))).append("\n");
        
        if (totalStats.containsKey("critical_chance")) {
            sb.append("§e⚡ 치명타:  §f").append(String.format("%. 1f", totalStats.get("critical_chance"))).append("%\n");
        }
        sb.append("\n");

        // 스킬
        List<PetSkill> skills = pet. getSkills();
        if (!skills. isEmpty()) {
            sb.append("§e§l[ 스킬 ]\n");
            for (PetSkill skill : skills) {
                sb.append("§7- ");
                if (skill.isPassive()) {
                    sb.append("§b[패시브] ");
                }
                sb.append("§f").append(skill.getName());
                sb.append(" §7Lv.").append(skill.getCurrentLevel());
                
                if (skill. isOnCooldown()) {
                    sb.append(" §c(쿨타임:  ").append(skill.getRemainingCooldownSeconds()).append("초)");
                }
                sb.append("\n");
            }
            sb.append("\n");
        }

        // 진화 정보
        if (pet.getEvolutionStage() > 1) {
            sb.append("§e진화 단계: §f").append(pet.getEvolutionStage()).append("단계\n");
        }
        
        if (plugin.getEvolutionManager().hasAvailableEvolution(pet)) {
            sb.append("§a✦ 진화 가능!\n");
        }
        sb.append("\n");

        // 전투 통계
        if (pet.getBattleWins() > 0 || pet.getBattleLosses() > 0) {
            sb.append("§e§l[ 전투 기록 ]\n");
            sb.append("§a승리: §f").append(pet.getBattleWins());
            sb.append("  §c패배: §f").append(pet.getBattleLosses());
            int total = pet.getBattleWins() + pet.getBattleLosses();
            if (total > 0) {
                double winRate = (double) pet.getBattleWins() / total * 100;
                sb.append("  §7승률: ").append(String.format("%.1f", winRate)).append("%");
            }
            sb.append("\n");
            
            if (pet.getKillCount() > 0) {
                sb. append("§4처치 수: §f").append(pet.getKillCount()).append("\n");
            }
            sb.append("\n");
        }

        // 추가 정보
        sb.append("§8ID: ").append(pet.getPetId().toString().substring(0, 8)).append(".. .\n");
        
        if (pet. isMutation()) {
            sb.append("§d✦ 변이 개체\n");
        }
        
        if (pet.isFavorite()) {
            sb.append("§e★ 즐겨찾기\n");
        }

        MessageUtil.sendMessage(player, sb.toString());
    }

    /**
     * 바 형태 표시 생성
     */
    private String getBarDisplay(double current, double max) {
        double percent = current / max;
        int filled = (int) (percent * 10);

        StringBuilder bar = new StringBuilder();
        
        // 색상 결정
        String color;
        if (percent > 0.6) {
            color = "§a";
        } else if (percent > 0.3) {
            color = "§e";
        } else {
            color = "§c";
        }

        bar.append("§8[");
        for (int i = 0; i < 10; i++) {
            if (i < filled) {
                bar.append(color).append("█");
            } else {
                bar. append("§7░");
            }
        }
        bar.append("§8] ");
        bar.append(color).append(String.format("%.0f", current)).append("§7/§f").append(String.format("%.0f", max));

        return bar.toString();
    }
}