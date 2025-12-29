package com.multiverse.pet.command. subcommand;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. command.PetCommand. SubCommand;
import com.multiverse.pet.entity.PetEntity;
import com. multiverse.pet. model.Pet;
import com.multiverse. pet.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java. util.List;
import java.util. UUID;

/**
 * 펫 소환 해제 서브 명령어
 * /pet unsummon [펫이름 또는 all]
 */
public class UnsummonSubCommand implements SubCommand {

    private final PetCore plugin;

    public UnsummonSubCommand(PetCore plugin) {
        this. plugin = plugin;
    }

    @Override
    public String getName() {
        return "unsummon";
    }

    @Override
    public String getDescription() {
        return "소환된 펫을 해제하여 보관함으로 돌려보냅니다. ";
    }

    @Override
    public String getUsage() {
        return "/pet unsummon [펫이름 또는 all]";
    }

    @Override
    public String getPermission() {
        return "multiverse.pet.unsummon";
    }

    @Override
    public String[] getExamples() {
        return new String[] {
            "/pet unsummon",
            "/pet unsummon 늑대",
            "/pet unsummon all",
            "/pet u"
        };
    }

    @Override
    public void execute(Player player, String[] args) {
        UUID playerId = player. getUniqueId();

        // 활성 펫 확인
        if (!plugin.getPetManager().hasActivePet(playerId)) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.no-active-pet"));
            return;
        }

        // 인자가 없으면 첫 번째 활성 펫 해제
        if (args.length == 0) {
            PetEntity activePet = plugin. getPetManager().getActivePet(playerId);
            if (activePet != null) {
                plugin.getPetManager().unsummonPet(player, activePet. getPet().getPetId());
            }
            return;
        }

        String identifier = args[0]. toLowerCase();

        // 모든 펫 해제
        if (identifier.equals("all")) {
            int count = plugin.getPetManager().getActivePetCount(playerId);
            plugin.getPetManager().unsummonAllPets(player);
            MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("pet.all-unsummoned")
                    .replace("{count}", String.valueOf(count)));
            return;
        }

        // 특정 펫 해제
        Pet pet = findActivePet(playerId, identifier);

        if (pet == null) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.not-active-named")
                    . replace("{name}", identifier));
            return;
        }

        plugin.getPetManager().unsummonPet(player, pet.getPetId());
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0]. toLowerCase();
            List<PetEntity> activePets = plugin. getPetManager().getActivePets(player.getUniqueId());

            for (PetEntity petEntity : activePets) {
                String petName = petEntity.getPet().getPetName();
                if (petName.toLowerCase().startsWith(input)) {
                    completions.add(petName);
                }
            }

            if ("all".startsWith(input) && activePets.size() > 1) {
                completions.add("all");
            }
        }

        return completions;
    }

    /**
     * 활성 펫 찾기
     */
    private Pet findActivePet(UUID playerId, String identifier) {
        List<PetEntity> activePets = plugin.getPetManager().getActivePets(playerId);

        for (PetEntity petEntity : activePets) {
            Pet pet = petEntity. getPet();
            if (pet.getPetName().equalsIgnoreCase(identifier)) {
                return pet;
            }
        }

        // 부분 일치
        for (PetEntity petEntity : activePets) {
            Pet pet = petEntity.getPet();
            if (pet.getPetName().toLowerCase().contains(identifier.toLowerCase())) {
                return pet;
            }
        }

        return null;
    }
}