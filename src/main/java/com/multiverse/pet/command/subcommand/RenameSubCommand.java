package com.multiverse.pet.command. subcommand;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.command.PetCommand.SubCommand;
import com.multiverse.pet.entity.PetEntity;
import com.multiverse.pet.model.Pet;
import com.multiverse. pet.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java. util.List;
import java.util. UUID;

/**
 * 펫 이름 변경 서브 명령어
 * /pet rename [펫이름] <새이름>
 */
public class RenameSubCommand implements SubCommand {

    private final PetCore plugin;

    public RenameSubCommand(PetCore plugin) {
        this. plugin = plugin;
    }

    @Override
    public String getName() {
        return "rename";
    }

    @Override
    public String getDescription() {
        return "펫의 이름을 변경합니다.";
    }

    @Override
    public String getUsage() {
        return "/pet rename [펫이름] <새이름>";
    }

    @Override
    public String getPermission() {
        return "multiverse.pet.rename";
    }

    @Override
    public String[] getExamples() {
        return new String[] {
            "/pet rename 멋진늑대",
            "/pet rename 늑대 멋진늑대",
            "/pet r 나의드래곤"
        };
    }

    @Override
    public void execute(Player player, String[] args) {
        UUID playerId = player.getUniqueId();

        if (args. length == 0) {
            MessageUtil.sendMessage(player, "§c사용법: " + getUsage());
            return;
        }

        Pet pet;
        String newName;

        if (args.length == 1) {
            // 활성 펫 이름 변경
            PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
            if (activePet == null) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.no-active-pet"));
                return;
            }
            pet = activePet. getPet();
            newName = args[0];
        } else {
            // 특정 펫 이름 변경
            pet = findPet(playerId, args[0]);
            if (pet == null) {
                // 첫 번째 인자가 펫 이름이 아니면, 활성 펫 + 전체 인자를 새 이름으로
                PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
                if (activePet == null) {
                    MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found")
                            .replace("{name}", args[0]));
                    return;
                }
                pet = activePet.getPet();
                newName = String.join(" ", args);
            } else {
                // 나머지 인자를 새 이름으로
                StringBuilder nameBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    if (i > 1) nameBuilder.append(" ");
                    nameBuilder. append(args[i]);
                }
                newName = nameBuilder.toString();
            }
        }

        // 이름 잠금 확인
        if (pet.isNameLocked()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.name-locked")
                    .replace("{name}", pet.getPetName()));
            return;
        }

        // 이름 변경
        if (plugin.getPetManager().renamePet(player, pet. getPetId(), newName)) {
            // 성공 메시지는 PetManager에서 처리
        }
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<Pet> pets = plugin.getPetManager().getAllPets(player.getUniqueId());

            for (Pet pet : pets) {
                if (! pet.isNameLocked() && pet.getPetName().toLowerCase().startsWith(input)) {
                    completions. add(pet.getPetName());
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
}