package com.multiverse.pet.command. subcommand;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. command.PetCommand. SubCommand;
import com. multiverse.pet. model.Pet;
import com.multiverse. pet.model.PetStatus;
import com. multiverse.pet. util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java. util.List;
import java.util. UUID;

/**
 * 펫 소환 서브 명령어
 * /pet summon <펫이름 또는 번호>
 */
public class SummonSubCommand implements SubCommand {

    private final PetCore plugin;

    public SummonSubCommand(PetCore plugin) {
        this. plugin = plugin;
    }

    @Override
    public String getName() {
        return "summon";
    }

    @Override
    public String getDescription() {
        return "보관함에서 펫을 소환합니다.";
    }

    @Override
    public String getUsage() {
        return "/pet summon <펫이름 또는 번호>";
    }

    @Override
    public String getPermission() {
        return "multiverse.pet.summon";
    }

    @Override
    public String[] getExamples() {
        return new String[] {
            "/pet summon 늑대",
            "/pet summon 1",
            "/pet s 드래곤"
        };
    }

    @Override
    public void execute(Player player, String[] args) {
        UUID playerId = player. getUniqueId();

        // 인자가 없으면 펫 목록 또는 GUI 표시
        if (args.length == 0) {
            showPetList(player);
            return;
        }

        String identifier = args[0];
        Pet pet = findPet(playerId, identifier);

        if (pet == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found")
                    .replace("{name}", identifier));
            return;
        }

        // 이미 소환 중인지 확인
        if (pet.isActive()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.already-summoned")
                    .replace("{name}", pet.getPetName()));
            return;
        }

        // 소환 가능한 상태인지 확인
        if (!pet.getStatus().canBeSummoned()) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.cannot-summon")
                    .replace("{name}", pet.getPetName())
                    .replace("{status}", pet. getStatus().getDisplayName()));
            return;
        }

        // 소환
        if (plugin.getPetManager().summonPet(player, pet. getPetId())) {
            // 성공 메시지는 PetManager에서 처리
        }
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0]. toLowerCase();
            List<Pet> pets = plugin.getPetManager().getAllPets(player.getUniqueId());

            int index = 1;
            for (Pet pet : pets) {
                // 소환 가능한 펫만
                if (pet. getStatus().canBeSummoned()) {
                    if (pet.getPetName().toLowerCase().startsWith(input)) {
                        completions.add(pet. getPetName());
                    }
                    if (String.valueOf(index).startsWith(input)) {
                        completions.add(String.valueOf(index));
                    }
                }
                index++;
            }
        }

        return completions;
    }

    /**
     * 펫 찾기 (이름 또는 번호)
     */
    private Pet findPet(UUID playerId, String identifier) {
        List<Pet> pets = plugin.getPetManager().getAllPets(playerId);

        // 번호로 검색
        try {
            int index = Integer.parseInt(identifier) - 1;
            if (index >= 0 && index < pets.size()) {
                return pets.get(index);
            }
        } catch (NumberFormatException ignored) {
        }

        // 이름으로 검색
        for (Pet pet : pets) {
            if (pet.getPetName().equalsIgnoreCase(identifier)) {
                return pet;
            }
        }

        // 부분 일치 검색
        for (Pet pet : pets) {
            if (pet.getPetName().toLowerCase().contains(identifier.toLowerCase())) {
                return pet;
            }
        }

        return null;
    }

    /**
     * 펫 목록 표시
     */
    private void showPetList(Player player) {
        List<Pet> pets = plugin.getPetManager().getAllPets(player.getUniqueId());

        if (pets.isEmpty()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.no-pets"));
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n§6§l===== 소환 가능한 펫 =====\n\n");

        int index = 1;
        int availableCount = 0;

        for (Pet pet : pets) {
            if (pet. getStatus().canBeSummoned()) {
                sb.append("§e").append(index).append(".  ");
                sb.append(pet.getRarity().getColorCode().replace("&", "§"));
                sb. append(pet.getPetName());
                sb.append(" §7Lv. ").append(pet.getLevel());
                sb.append(" §8(").append(pet.getSpeciesId()).append(")\n");
                availableCount++;
            } else {
                sb.append("§8").append(index).append(". ");
                sb.append(pet.getPetName());
                sb.append(" §7[").append(pet.getStatus().getDisplayName()).append("]\n");
            }
            index++;
        }

        if (availableCount == 0) {
            sb.append("§7소환 가능한 펫이 없습니다.\n");
        }

        sb.append("\n§7/pet summon <이름 또는 번호> 로 소환하세요.");

        MessageUtil.sendMessage(player, sb.toString());
    }
}