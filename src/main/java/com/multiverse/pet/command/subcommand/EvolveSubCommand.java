package com.multiverse.pet.command.subcommand;

import com.multiverse.pet.PetCore;
import com.multiverse. pet.command.PetCommand.SubCommand;
import com. multiverse.pet. entity.PetEntity;
import com. multiverse.pet. model.Pet;
import com.multiverse.pet.model. evolution.PetEvolution;
import com. multiverse.pet. util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java. util.UUID;

/**
 * 펫 진화 서브 명령어
 * /pet evolve [펫이름] [진화ID]
 */
public class EvolveSubCommand implements SubCommand {

    private final PetCore plugin;

    public EvolveSubCommand(PetCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "evolve";
    }

    @Override
    public String getDescription() {
        return "펫을 진화시킵니다.";
    }

    @Override
    public String getUsage() {
        return "/pet evolve [펫이름] [진화ID]";
    }

    @Override
    public String getPermission() {
        return "multiverse.pet.evolve";
    }

    @Override
    public String[] getExamples() {
        return new String[]{
            "/pet evolve",
            "/pet evolve 늑대",
            "/pet evolve 늑대 dire_wolf",
            "/pet e"
        };
    }

    @Override
    public void execute(Player player, String[] args) {
        UUID playerId = player.getUniqueId();

        Pet pet;
        String evolutionId = null;

        if (args.length == 0) {
            // 활성 펫 진화
            PetEntity activePet = plugin. getPetManager().getActivePet(playerId);
            if (activePet == null) {
                MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.no-active-pet"));
                return;
            }
            pet = activePet.getPet();
        } else if (args.length == 1) {
            // 펫 이름 또는 진화 ID
            pet = findPet(playerId, args[0]);
            if (pet == null) {
                // 활성 펫의 진화 ID로 해석
                PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
                if (activePet == null) {
                    MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found")
                            .replace("{name}", args[0]));
                    return;
                }
                pet = activePet.getPet();
                evolutionId = args[0];
            }
        } else {
            // 펫 이름 + 진화 ID
            pet = findPet(playerId, args[0]);
            if (pet == null) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found")
                        .replace("{name}", args[0]));
                return;
            }
            evolutionId = args[1];
        }

        // 진화 처리
        if (evolutionId == null) {
            showAvailableEvolutions(player, pet);
        } else {
            performEvolution(player, pet, evolutionId);
        }
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        UUID playerId = player.getUniqueId();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            // 펫 이름
            for (Pet pet : plugin.getPetManager().getAllPets(playerId)) {
                if (plugin.getEvolutionManager().hasAvailableEvolution(pet)) {
                    if (pet.getPetName().toLowerCase().startsWith(input)) {
                        completions.add(pet.getPetName());
                    }
                }
            }

            // 활성 펫의 진화 ID
            PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
            if (activePet != null) {
                List<PetEvolution> evolutions = plugin.getEvolutionManager()
                        .getAvailableEvolutions(activePet. getPet(), player);
                for (PetEvolution evolution :  evolutions) {
                    if (evolution.getEvolutionId().toLowerCase().startsWith(input)) {
                        completions.add(evolution. getEvolutionId());
                    }
                }
            }
        } else if (args.length == 2) {
            String input = args[1].toLowerCase();
            Pet pet = findPet(playerId, args[0]);

            if (pet != null) {
                List<PetEvolution> evolutions = plugin.getEvolutionManager()
                        .getAvailableEvolutions(pet, player);
                for (PetEvolution evolution : evolutions) {
                    if (evolution.getEvolutionId().toLowerCase().startsWith(input)) {
                        completions.add(evolution.getEvolutionId());
                    }
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

        for (Pet pet : pets) {
            if (pet.getPetName().equalsIgnoreCase(identifier)) {
                return pet;
            }
        }

        return null;
    }

    /**
     * 가능한 진화 목록 표시
     */
    private void showAvailableEvolutions(Player player, Pet pet) {
        List<PetEvolution> evolutions = plugin.getEvolutionManager().getAvailableEvolutions(pet, player);

        StringBuilder sb = new StringBuilder();
        sb.append("\n§6§l===== ").append(pet.getPetName()).append(" 진화 =====\n\n");

        sb.append("§e현재 종족: §f").append(pet.getSpeciesId()).append("\n");
        sb.append("§e진화 단계: §f").append(pet.getEvolutionStage()).append("단계\n\n");

        if (evolutions.isEmpty()) {
            sb.append("§7현재 가능한 진화가 없습니다.\n\n");

            // 잠재적 진화 표시
            List<PetEvolution> allEvolutions = plugin. getEvolutionManager()
                    .getEvolutionsForSpecies(pet.getSpeciesId());

            if (! allEvolutions.isEmpty()) {
                sb.append("§e§l[ 잠재적 진화 ]\n");
                for (PetEvolution evolution : allEvolutions) {
                    sb.append("§8▸ ").append(evolution.getToSpeciesId());
                    sb. append(" §7- 요구 레벨: ").append(evolution.getRequiredLevel());

                    List<String> unmet = evolution.getUnmetConditions(pet, null, 0, null, null, true, null);
                    if (!unmet.isEmpty()) {
                        sb.append("\n  §c").append(String.join(", ", unmet));
                    }
                    sb.append("\n");
                }
            }
        } else {
            sb.append("§a§l[ 가능한 진화 ]\n");
            for (PetEvolution evolution : evolutions) {
                sb.append(formatEvolutionEntry(evolution));
                sb.append("\n");
            }

            sb.append("\n§7/pet evolve ").append(pet.getPetName()).append(" <진화ID> §8- 진화 실행");
        }

        MessageUtil.sendMessage(player, sb. toString());
    }

    /**
     * 진화 항목 포맷팅
     */
    private String formatEvolutionEntry(PetEvolution evolution) {
        StringBuilder sb = new StringBuilder();

        sb.append("§a▸ ").append(evolution.getEvolutionId());
        sb.append("\n  §7→ §f").append(evolution.getToSpeciesId());
        sb.append(" §7(").append(evolution.getToStage()).append("단계)");

        // 성공 확률
        sb.append("\n  §e성공률: §f").append(String.format("%.1f", evolution.getSuccessChance())).append("%");

        // 비용
        if (evolution.getGoldCost() > 0) {
            sb.append("\n  §6비용: §f").append(String.format("%.0f", evolution.getGoldCost())).append(" 골드");
        }

        // 필요 아이템
        if (! evolution.getRequiredItems().isEmpty()) {
            sb.append("\n  §d필요 아이템:");
            for (PetEvolution.ItemRequirement req : evolution.getRequiredItems()) {
                sb.append(" ").append(req.getItemId()).append("x").append(req.getAmount());
            }
        }

        // 스탯 보너스
        if (! evolution.getStatBonuses().isEmpty()) {
            sb. append("\n  §b스탯 보너스:");
            evolution.getStatBonuses().forEach((stat, value) -> {
                sb.append(" ").append(stat).append("+").append(String.format("%.0f", value));
            });
        }

        // 새 스킬
        if (! evolution.getNewSkills().isEmpty()) {
            sb.append("\n  §a새 스킬:  ").append(String.join(", ", evolution.getNewSkills()));
        }

        return sb. toString();
    }

    /**
     * 진화 실행
     */
    private void performEvolution(Player player, Pet pet, String evolutionId) {
        // 진화 확인
        PetEvolution evolution = plugin.getEvolutionManager().getEvolution(evolutionId);
        if (evolution == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("evolution.not-found")
                    .replace("{id}", evolutionId));
            return;
        }

        // 이 펫이 해당 진화가 가능한지 확인
        List<PetEvolution> available = plugin.getEvolutionManager().getAvailableEvolutions(pet, player);
        boolean canEvolve = available. stream()
                .anyMatch(e -> e. getEvolutionId().equals(evolutionId));

        if (!canEvolve) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("evolution.not-available")
                    . replace("{name}", pet.getPetName())
                    .replace("{evolution}", evolutionId));
            return;
        }

        // 확인 메시지
        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("evolution.confirm")
                .replace("{name}", pet.getPetName())
                .replace("{evolution}", evolution.getToSpeciesId())
                .replace("{chance}", String.format("%.1f", evolution.getSuccessChance())));

        // 진화 실행
        if (plugin.getEvolutionManager().evolve(player, pet, evolutionId)) {
            // 성공 메시지는 EvolutionManager에서 처리
        }
    }
}