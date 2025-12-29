package com.multiverse.pet.command.subcommand;

import com.multiverse.pet.PetCore;
import com.multiverse. pet.command.PetCommand.SubCommand;
import com. multiverse.pet. model.Pet;
import com.multiverse. pet.model.PetRarity;
import com.multiverse.pet.model.PetStatus;
import com.multiverse.pet.model.PetType;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.*;
import java.util. stream.Collectors;

/**
 * 펫 목록 서브 명령어
 * /pet list [페이지] [필터]
 */
public class ListSubCommand implements SubCommand {

    private final PetCore plugin;

    private static final int PETS_PER_PAGE = 10;

    public ListSubCommand(PetCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "보유한 펫 목록을 표시합니다.";
    }

    @Override
    public String getUsage() {
        return "/pet list [페이지] [필터]";
    }

    @Override
    public String getPermission() {
        return "multiverse. pet.list";
    }

    @Override
    public String[] getExamples() {
        return new String[] {
            "/pet list",
            "/pet list 2",
            "/pet list 1 rare",
            "/pet list 1 combat",
            "/pet l"
        };
    }

    @Override
    public void execute(Player player, String[] args) {
        UUID playerId = player.getUniqueId();
        List<Pet> allPets = plugin. getPetManager().getAllPets(playerId);

        if (allPets.isEmpty()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.no-pets"));
            return;
        }

        // 페이지 파싱
        int page = 1;
        String filter = null;

        if (args. length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // 첫 번째 인자가 필터일 수 있음
                filter = args[0];
            }
        }

        if (args.length >= 2) {
            filter = args[1];
        }

        // 필터 적용
        List<Pet> filteredPets = applyFilter(allPets, filter);

        if (filteredPets.isEmpty()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.no-pets-filtered")
                    . replace("{filter}", filter != null ? filter : ""));
            return;
        }

        // 페이지 계산
        int totalPages = (int) Math.ceil((double) filteredPets.size() / PETS_PER_PAGE);
        page = Math.max(1, Math.min(page, totalPages));

        int startIndex = (page - 1) * PETS_PER_PAGE;
        int endIndex = Math.min(startIndex + PETS_PER_PAGE, filteredPets.size());

        List<Pet> pagePets = filteredPets.subList(startIndex, endIndex);

        // 목록 표시
        showPetList(player, pagePets, page, totalPages, filter, filteredPets.size(), allPets.size());
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            
            // 페이지 번호
            for (int i = 1; i <= 10; i++) {
                if (String.valueOf(i).startsWith(input)) {
                    completions.add(String.valueOf(i));
                }
            }
            
            // 필터
            addFilterCompletions(completions, input);
        } else if (args. length == 2) {
            addFilterCompletions(completions, args[1]. toLowerCase());
        }

        return completions;
    }

    /**
     * 필터 자동완성 추가
     */
    private void addFilterCompletions(List<String> completions, String input) {
        // 희귀도 필터
        for (PetRarity rarity : PetRarity.values()) {
            if (rarity.name().toLowerCase().startsWith(input)) {
                completions. add(rarity. name().toLowerCase());
            }
        }

        // 타입 필터
        for (PetType type : PetType.values()) {
            if (type.name().toLowerCase().startsWith(input)) {
                completions.add(type. name().toLowerCase());
            }
        }

        // 상태 필터
        for (String statusFilter : Arrays.asList("active", "stored", "favorite")) {
            if (statusFilter.startsWith(input)) {
                completions. add(statusFilter);
            }
        }
    }

    /**
     * 필터 적용
     */
    private List<Pet> applyFilter(List<Pet> pets, String filter) {
        if (filter == null || filter.isEmpty()) {
            return pets;
        }

        String filterLower = filter. toLowerCase();

        // 희귀도 필터
        try {
            PetRarity rarity = PetRarity.valueOf(filter. toUpperCase());
            return pets.stream()
                    . filter(pet -> pet.getRarity() == rarity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ignored) {
        }

        // 타입 필터
        try {
            PetType type = PetType.valueOf(filter.toUpperCase());
            return pets.stream()
                    .filter(pet -> pet.getType() == type)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ignored) {
        }

        // 상태 필터
        switch (filterLower) {
            case "active": 
                return pets. stream()
                        .filter(Pet::isActive)
                        .collect(Collectors.toList());
            case "stored": 
                return pets. stream()
                        .filter(pet -> pet.getStatus() == PetStatus. STORED)
                        .collect(Collectors.toList());
            case "favorite": 
                return pets. stream()
                        .filter(Pet:: isFavorite)
                        .collect(Collectors.toList());
        }

        // 이름 검색
        return pets.stream()
                .filter(pet -> pet.getPetName().toLowerCase().contains(filterLower) ||
                              pet.getSpeciesId().toLowerCase().contains(filterLower))
                .collect(Collectors.toList());
    }

    /**
     * 펫 목록 표시
     */
    private void showPetList(Player player, List<Pet> pets, int page, int totalPages, 
                             String filter, int filteredCount, int totalCount) {
        StringBuilder sb = new StringBuilder();

        // 헤더
        sb.append("\n§6§l===== 펫 목록 =====");
        if (filter != null) {
            sb. append(" §7[필터: ").append(filter).append("]");
        }
        sb.append("\n");
        sb.append("§7총 ").append(filteredCount);
        if (filteredCount != totalCount) {
            sb.append("/").append(totalCount);
        }
        sb.append("마리\n\n");

        // 펫 목록
        int index = (page - 1) * PETS_PER_PAGE + 1;
        for (Pet pet :  pets) {
            sb.append(formatPetEntry(pet, index));
            sb.append("\n");
            index++;
        }

        // 페이지 네비게이션
        sb.append("\n§7페이지 §f").append(page).append("§7/§f").append(totalPages);
        
        if (page > 1) {
            sb.append("  §e[이전:  /pet list ").append(page - 1);
            if (filter != null) sb.append(" ").append(filter);
            sb.append("]");
        }
        
        if (page < totalPages) {
            sb.append("  §e[다음:  /pet list ").append(page + 1);
            if (filter != null) sb.append(" ").append(filter);
            sb.append("]");
        }

        MessageUtil.sendMessage(player, sb.toString());
    }

    /**
     * 펫 항목 포맷팅
     */
    private String formatPetEntry(Pet pet, int index) {
        StringBuilder sb = new StringBuilder();

        // 번호
        sb. append("§7").append(String.format("%2d", index)).append(". ");

        // 상태 아이콘
        if (pet.isActive()) {
            sb.append("§a✦ ");
        } else if (pet.isFavorite()) {
            sb.append("§e★ ");
        } else {
            sb.append("§8○ ");
        }

        // 희귀도 색상 + 이름
        sb.append(pet. getRarity().getColorCode().replace("&", "§"));
        sb.append(pet. getPetName());

        // 레벨
        sb.append(" §7Lv. ").append(pet.getLevel());

        // 타입
        if (pet.getType() != null) {
            sb.append(" §8[").append(pet.getType().getDisplayName()).append("]");
        }

        // 상태 (활성 외)
        if (pet.getStatus() != PetStatus.STORED && pet.getStatus() != PetStatus. ACTIVE) {
            sb.append(" §c(").append(pet.getStatus().getDisplayName()).append(")");
        }

        // 체력 경고
        if (pet.getHealth() < pet.getMaxHealth() * 0.3) {
            sb.append(" §c❤");
        }

        // 배고픔 경고
        if (pet.getHunger() < 30) {
            sb.append(" §e🍖");
        }

        return sb.toString();
    }
}