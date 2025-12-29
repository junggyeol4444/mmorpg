package com.multiverse.pet.command.subcommand;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. command.PetCommand. SubCommand;
import com.multiverse.pet.manager. BreedingManager;
import com.multiverse.pet.model.Pet;
import com.multiverse. pet.model.breeding.PetBreeding;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util. ArrayList;
import java. util.List;
import java.util. UUID;

/**
 * 펫 교배 서브 명령어
 * /pet breed <펫1> <펫2> | list | cancel <교배ID> | collect <교배ID>
 */
public class BreedSubCommand implements SubCommand {

    private final PetCore plugin;

    public BreedSubCommand(PetCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "breed";
    }

    @Override
    public String getDescription() {
        return "두 펫을 교배합니다.";
    }

    @Override
    public String getUsage() {
        return "/pet breed <펫1> <펫2> | list | cancel <교배ID> | collect <교배ID>";
    }

    @Override
    public String getPermission() {
        return "multiverse.pet.breed";
    }

    @Override
    public String[] getExamples() {
        return new String[]{
            "/pet breed 늑대1 늑대2",
            "/pet breed list",
            "/pet breed cancel 1",
            "/pet breed collect 1",
            "/pet br 드래곤 피닉스"
        };
    }

    @Override
    public void execute(Player player, String[] args) {
        UUID playerId = player. getUniqueId();

        if (args. length == 0) {
            showBreedingHelp(player);
            return;
        }

        String action = args[0]. toLowerCase();

        switch (action) {
            case "list": 
                showBreedingList(player);
                break;
            case "cancel":
                if (args.length < 2) {
                    MessageUtil. sendMessage(player, "§c사용법: /pet breed cancel <교배번호>");
                    return;
                }
                cancelBreeding(player, args[1]);
                break;
            case "collect": 
                if (args.length < 2) {
                    MessageUtil.sendMessage(player, "§c사용법:  /pet breed collect <교배번호>");
                    return;
                }
                collectBreeding(player, args[1]);
                break;
            default:
                // 교배 시작
                if (args.length < 2) {
                    MessageUtil.sendMessage(player, "§c사용법:  /pet breed <펫1> <펫2>");
                    return;
                }
                startBreeding(player, args[0], args[1]);
                break;
        }
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        UUID playerId = player.getUniqueId();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            // 액션
            for (String action : new String[]{"list", "cancel", "collect"}) {
                if (action.startsWith(input)) {
                    completions.add(action);
                }
            }

            // 교배 가능한 펫 이름
            List<Pet> pets = plugin.getPetManager().getAllPets(playerId);
            for (Pet pet :  pets) {
                if (plugin.getBreedingManager().canBreed(pet)) {
                    if (pet.getPetName().toLowerCase().startsWith(input)) {
                        completions.add(pet. getPetName());
                    }
                }
            }
        } else if (args.length == 2) {
            String input = args[1].toLowerCase();

            if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("collect")) {
                // 교배 번호
                List<PetBreeding> breedings = plugin.getBreedingManager().getPlayerBreedings(playerId);
                for (int i = 0; i < breedings. size(); i++) {
                    String num = String.valueOf(i + 1);
                    if (num.startsWith(input)) {
                        completions.add(num);
                    }
                }
            } else {
                // 두 번째 펫 (첫 번째와 다른 펫)
                List<Pet> pets = plugin.getPetManager().getAllPets(playerId);
                for (Pet pet :  pets) {
                    if (plugin.getBreedingManager().canBreed(pet)) {
                        if (! pet.getPetName().equalsIgnoreCase(args[0])) {
                            if (pet.getPetName().toLowerCase().startsWith(input)) {
                                completions.add(pet.getPetName());
                            }
                        }
                    }
                }
            }
        }

        return completions;
    }

    /**
     * 교배 도움말 표시
     */
    private void showBreedingHelp(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n§6§l===== 펫 교배 =====\n\n");
        sb.append("§e/pet breed <펫1> <펫2> §7- 두 펫 교배\n");
        sb.append("§e/pet breed list §7- 진행 중인 교배 목록\n");
        sb.append("§e/pet breed cancel <번호> §7- 교배 취소\n");
        sb.append("§e/pet breed collect <번호> §7- 교배 결과 수령\n\n");

        // 교배 조건 안내
        sb. append("§7§l[ 교배 조건 ]\n");
        sb.append("§7- 최소 레벨: §f").append(plugin.getBreedingManager().getMinBreedingLevel()).append("\n");
        sb.append("§7- 최소 행복도: §f").append(plugin.getBreedingManager().getMinBreedingHappiness()).append("\n");
        sb.append("§7- 보관 상태여야 함\n");
        sb.append("§7- 쿨다운 없어야 함\n");

        MessageUtil.sendMessage(player, sb.toString());
    }

    /**
     * 진행 중인 교배 목록 표시
     */
    private void showBreedingList(Player player) {
        UUID playerId = player. getUniqueId();
        List<PetBreeding> breedings = plugin. getBreedingManager().getPlayerBreedings(playerId);

        StringBuilder sb = new StringBuilder();
        sb.append("\n§6§l===== 진행 중인 교배 =====\n\n");

        if (breedings.isEmpty()) {
            sb.append("§7진행 중인 교배가 없습니다.\n");
        } else {
            int index = 1;
            for (PetBreeding breeding : breedings) {
                sb.append("§e").append(index).append(". ");
                sb.append("§f").append(breeding.getParent1SpeciesId());
                sb.append(" §7+ §f").append(breeding.getParent2SpeciesId());
                sb.append("\n");

                sb.append("   §7상태: ");
                switch (breeding.getStatus()) {
                    case IN_PROGRESS: 
                        sb.append("§e진행 중 §7(").append(breeding.getRemainingTimeFormatted()).append(" 남음)");
                        break;
                    case AWAITING_COLLECTION:
                        sb.append("§a수령 대기 중");
                        if (breeding.isMutation()) {
                            sb.append(" §d[변이! ]");
                        }
                        break;
                    case CANCELLED:
                        sb.append("§c취소됨");
                        break;
                    case FAILED:
                        sb.append("§c실패");
                        break;
                    default:
                        sb.append(breeding.getStatus().getDisplayName());
                        break;
                }
                sb.append("\n");

                index++;
            }
        }

        // 동시 교배 한도
        int current = breedings.size();
        int max = plugin.getBreedingManager().getMaxConcurrentBreedings();
        sb.append("\n§7교배 슬롯:  §f").append(current).append("/").append(max);

        MessageUtil.sendMessage(player, sb.toString());
    }

    /**
     * 교배 시작
     */
    private void startBreeding(Player player, String pet1Name, String pet2Name) {
        UUID playerId = player. getUniqueId();

        // 펫 찾기
        Pet pet1 = findPet(playerId, pet1Name);
        if (pet1 == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found")
                    .replace("{name}", pet1Name));
            return;
        }

        Pet pet2 = findPet(playerId, pet2Name);
        if (pet2 == null) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.not-found")
                    .replace("{name}", pet2Name));
            return;
        }

        // 교배 비용 표시
        double cost = plugin.getBreedingManager().calculateBreedingCost(pet1, pet2);
        long duration = plugin.getBreedingManager().calculateBreedingDuration(pet1, pet2);

        MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("breeding.cost-info")
                .replace("{cost}", String.format("%.0f", cost))
                .replace("{time}", formatTime(duration)));

        // 교배 시작
        if (plugin.getBreedingManager().startBreeding(player, pet1, pet2)) {
            // 성공 메시지는 BreedingManager에서 처리
        }
    }

    /**
     * 교배 취소
     */
    private void cancelBreeding(Player player, String indexStr) {
        UUID playerId = player.getUniqueId();
        List<PetBreeding> breedings = plugin.getBreedingManager().getPlayerBreedings(playerId);

        int index;
        try {
            index = Integer.parseInt(indexStr) - 1;
        } catch (NumberFormatException e) {
            MessageUtil. sendMessage(player, "§c잘못된 번호입니다:  " + indexStr);
            return;
        }

        if (index < 0 || index >= breedings.size()) {
            MessageUtil. sendMessage(player, "§c해당 번호의 교배를 찾을 수 없습니다.");
            return;
        }

        PetBreeding breeding = breedings.get(index);

        if (plugin. getBreedingManager().cancelBreeding(player, breeding. getBreedingId())) {
            // 성공 메시지는 BreedingManager에서 처리
        }
    }

    /**
     * 교배 결과 수령
     */
    private void collectBreeding(Player player, String indexStr) {
        UUID playerId = player.getUniqueId();
        List<PetBreeding> breedings = plugin.getBreedingManager().getPlayerBreedings(playerId);

        int index;
        try {
            index = Integer.parseInt(indexStr) - 1;
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(player, "§c잘못된 번호입니다: " + indexStr);
            return;
        }

        if (index < 0 || index >= breedings. size()) {
            MessageUtil.sendMessage(player, "§c해당 번호의 교배를 찾을 수 없습니다.");
            return;
        }

        PetBreeding breeding = breedings.get(index);

        Pet offspring = plugin.getBreedingManager().collectOffspring(player, breeding. getBreedingId());
        if (offspring != null) {
            // 성공 메시지는 BreedingManager에서 처리
        }
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

        // 부분 일치
        for (Pet pet : pets) {
            if (pet. getPetName().toLowerCase().contains(identifier.toLowerCase())) {
                return pet;
            }
        }

        return null;
    }

    /**
     * 시간 포맷팅
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String. format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String.format("%d초", seconds);
        }
    }
}