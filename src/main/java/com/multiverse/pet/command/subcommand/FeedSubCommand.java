package com.multiverse.pet.command. subcommand;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.command.PetCommand.SubCommand;
import com.multiverse.pet.entity.PetEntity;
import com.multiverse.pet.manager.PetCareManager;
import com.multiverse.pet.model.Pet;
import com.multiverse. pet.util.MessageUtil;
import org.bukkit.Material;
import org. bukkit.entity. Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java. util.List;
import java.util. Set;
import java.util. UUID;

/**
 * 펫 먹이주기 서브 명령어
 * /pet feed [펫이름] [음식]
 */
public class FeedSubCommand implements SubCommand {

    private final PetCore plugin;

    public FeedSubCommand(PetCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "feed";
    }

    @Override
    public String getDescription() {
        return "펫에게 먹이를 줍니다.";
    }

    @Override
    public String getUsage() {
        return "/pet feed [펫이름] [음식]";
    }

    @Override
    public String getPermission() {
        return "multiverse.pet. feed";
    }

    @Override
    public String[] getExamples() {
        return new String[]{
            "/pet feed",
            "/pet feed 늑대",
            "/pet feed 늑대 COOKED_BEEF",
            "/pet f"
        };
    }

    @Override
    public void execute(Player player, String[] args) {
        UUID playerId = player.getUniqueId();

        Pet pet;
        String foodId = null;

        if (args.length == 0) {
            // 활성 펫에게 손에 든 아이템 또는 자동 먹이
            PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
            if (activePet == null) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.no-active-pet"));
                return;
            }
            pet = activePet.getPet();
        } else if (args.length == 1) {
            // 펫 이름 또는 음식
            pet = findPet(playerId, args[0]);
            if (pet == null) {
                // 음식으로 해석
                PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
                if (activePet == null) {
                    MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found")
                            .replace("{name}", args[0]));
                    return;
                }
                pet = activePet.getPet();
                foodId = args[0]. toUpperCase();
            }
        } else {
            // 펫 이름 + 음식
            pet = findPet(playerId, args[0]);
            if (pet == null) {
                MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found")
                        .replace("{name}", args[0]));
                return;
            }
            foodId = args[1].toUpperCase();
        }

        // 배고픔 확인
        if (pet.getHunger() >= 100) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.already-full")
                    .replace("{name}", pet. getPetName()));
            return;
        }

        // 먹이 처리
        if (foodId != null) {
            // 특정 음식
            if (plugin.getPetCareManager().feedPet(player, pet, foodId)) {
                // 성공 메시지는 CareManager에서 처리
            }
        } else {
            // 손에 든 아이템 사용
            ItemStack handItem = player.getInventory().getItemInMainHand();

            if (handItem != null && handItem. getType() != Material.AIR) {
                String itemId = handItem.getType().name();
                PetCareManager. FoodEffect effect = plugin.getPetCareManager().getFoodEffect(itemId);

                if (effect != null) {
                    if (plugin.getPetCareManager().feedPet(player, pet, itemId)) {
                        // 성공
                    }
                } else {
                    // 자동 먹이
                    if (plugin.getPetCareManager().feedPetAuto(player, pet)) {
                        // 성공
                    } else {
                        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("care.invalid-food"));
                    }
                }
            } else {
                // 자동으로 최적의 음식 선택
                if (plugin. getPetCareManager().feedPetAuto(player, pet)) {
                    // 성공
                } else {
                    MessageUtil. sendMessage(player, plugin.getConfigManager().getMessage("care.no-food"));
                }
            }
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
                if (pet.getPetName().toLowerCase().startsWith(input)) {
                    completions.add(pet.getPetName());
                }
            }

            // 음식 ID
            Set<String> foods = plugin.getPetCareManager().getAllFoods();
            for (String food : foods) {
                if (food.toLowerCase().startsWith(input)) {
                    completions.add(food);
                }
            }
        } else if (args.length == 2) {
            String input = args[1].toLowerCase();

            // 음식 ID
            Set<String> foods = plugin.getPetCareManager().getAllFoods();
            for (String food : foods) {
                if (food. toLowerCase().startsWith(input)) {
                    completions.add(food);
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

        for (Pet pet :  pets) {
            if (pet.getPetName().equalsIgnoreCase(identifier)) {
                return pet;
            }
        }

        return null;
    }
}