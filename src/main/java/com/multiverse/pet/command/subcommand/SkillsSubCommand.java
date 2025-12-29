package com.multiverse.pet.command.subcommand;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. command.PetCommand. SubCommand;
import com.multiverse.pet.entity.PetEntity;
import com.multiverse. pet.model.Pet;
import com.multiverse. pet.model.skill.PetSkill;
import com.multiverse.pet.model. skill.SkillType;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util. ArrayList;
import java. util.List;
import java.util. UUID;

/**
 * 펫 스킬 서브 명령어
 * /pet skills [펫이름] [use/upgrade <스킬명>]
 */
public class SkillsSubCommand implements SubCommand {

    private final PetCore plugin;

    public SkillsSubCommand(PetCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "skills";
    }

    @Override
    public String getDescription() {
        return "펫의 스킬을 확인하거나 사용합니다.";
    }

    @Override
    public String getUsage() {
        return "/pet skills [펫이름] [use/upgrade <스킬명>]";
    }

    @Override
    public String getPermission() {
        return "multiverse.pet.skills";
    }

    @Override
    public String[] getExamples() {
        return new String[]{
            "/pet skills",
            "/pet skills 늑대",
            "/pet skills use 파이어볼",
            "/pet skills upgrade 돌진",
            "/pet sk"
        };
    }

    @Override
    public void execute(Player player, String[] args) {
        UUID playerId = player.getUniqueId();

        // 인자 파싱
        Pet pet = null;
        String action = null;
        String skillName = null;

        if (args.length == 0) {
            // 활성 펫 스킬 목록
            PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
            if (activePet == null) {
                MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.no-active-pet"));
                return;
            }
            pet = activePet.getPet();
        } else if (args.length == 1) {
            // 펫 이름 또는 액션
            if (args[0]. equalsIgnoreCase("use") || args[0]. equalsIgnoreCase("upgrade")) {
                action = args[0].toLowerCase();
                PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
                if (activePet == null) {
                    MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.no-active-pet"));
                    return;
                }
                pet = activePet.getPet();
            } else {
                pet = findPet(playerId, args[0]);
                if (pet == null) {
                    MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.not-found")
                            .replace("{name}", args[0]));
                    return;
                }
            }
        } else if (args.length == 2) {
            if (args[0]. equalsIgnoreCase("use") || args[0]. equalsIgnoreCase("upgrade")) {
                action = args[0].toLowerCase();
                skillName = args[1];
                PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
                if (activePet == null) {
                    MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.no-active-pet"));
                    return;
                }
                pet = activePet. getPet();
            } else {
                pet = findPet(playerId, args[0]);
                if (pet == null) {
                    MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("pet.not-found")
                            .replace("{name}", args[0]));
                    return;
                }
                action = args[1]. toLowerCase();
            }
        } else if (args.length >= 3) {
            if (args[0]. equalsIgnoreCase("use") || args[0].equalsIgnoreCase("upgrade")) {
                action = args[0]. toLowerCase();
                skillName = args[1];
                pet = findPet(playerId, args[2]);
            } else {
                pet = findPet(playerId, args[0]);
                action = args[1]. toLowerCase();
                skillName = args[2];
            }

            if (pet == null) {
                PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
                if (activePet == null) {
                    MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("pet.no-active-pet"));
                    return;
                }
                pet = activePet.getPet();
                skillName = args[1];
            }
        }

        // 액션 처리
        if (action == null) {
            showSkillList(player, pet);
        } else if (action.equals("use")) {
            if (skillName == null) {
                MessageUtil.sendMessage(player, "§c사용법: /pet skills use <스킬명>");
                return;
            }
            useSkill(player, pet, skillName);
        } else if (action.equals("upgrade")) {
            if (skillName == null) {
                MessageUtil.sendMessage(player, "§c사용법:  /pet skills upgrade <스킬명>");
                return;
            }
            upgradeSkill(player, pet, skillName);
        } else {
            showSkillList(player, pet);
        }
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        UUID playerId = player. getUniqueId();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            // 액션
            for (String action : new String[]{"use", "upgrade"}) {
                if (action.startsWith(input)) {
                    completions.add(action);
                }
            }

            // 펫 이름
            for (Pet pet : plugin.getPetManager().getAllPets(playerId)) {
                if (pet.getPetName().toLowerCase().startsWith(input)) {
                    completions.add(pet.getPetName());
                }
            }
        } else if (args. length == 2) {
            String input = args[1].toLowerCase();

            if (args[0].equalsIgnoreCase("use") || args[0].equalsIgnoreCase("upgrade")) {
                // 활성 펫의 스킬 목록
                PetEntity activePet = plugin.getPetManager().getActivePet(playerId);
                if (activePet != null) {
                    for (PetSkill skill : activePet.getPet().getSkills()) {
                        if (skill.getName().toLowerCase().startsWith(input)) {
                            completions. add(skill.getName());
                        }
                    }
                }
            } else {
                // 액션
                for (String action : new String[]{"use", "upgrade"}) {
                    if (action.startsWith(input)) {
                        completions.add(action);
                    }
                }
            }
        } else if (args. length == 3) {
            String input = args[2].toLowerCase();
            Pet pet = findPet(playerId, args[0]);

            if (pet != null) {
                for (PetSkill skill : pet.getSkills()) {
                    if (skill.getName().toLowerCase().startsWith(input)) {
                        completions.add(skill.getName());
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

        for (Pet pet :  pets) {
            if (pet.getPetName().equalsIgnoreCase(identifier)) {
                return pet;
            }
        }

        return null;
    }

    /**
     * 스킬 목록 표시
     */
    private void showSkillList(Player player, Pet pet) {
        List<PetSkill> skills = pet.getSkills();

        StringBuilder sb = new StringBuilder();
        sb.append("\n§6§l===== ").append(pet.getPetName()).append("의 스킬 =====\n\n");

        if (skills.isEmpty()) {
            sb. append("§7보유한 스킬이 없습니다.\n");
        } else {
            // 패시브 스킬
            List<PetSkill> passiveSkills = new ArrayList<>();
            List<PetSkill> activeSkills = new ArrayList<>();

            for (PetSkill skill : skills) {
                if (skill.isPassive()) {
                    passiveSkills. add(skill);
                } else {
                    activeSkills.add(skill);
                }
            }

            // 액티브 스킬
            if (!activeSkills.isEmpty()) {
                sb.append("§e§l[ 액티브 스킬 ]\n");
                for (PetSkill skill : activeSkills) {
                    sb.append(formatSkillEntry(skill));
                    sb.append("\n");
                }
                sb.append("\n");
            }

            // 패시브 스킬
            if (!passiveSkills.isEmpty()) {
                sb.append("§b§l[ 패시브 스킬 ]\n");
                for (PetSkill skill : passiveSkills) {
                    sb.append(formatSkillEntry(skill));
                    sb.append("\n");
                }
                sb.append("\n");
            }
        }

        // 스킬 포인트
        int skillPoints = pet.getSkillPoints();
        sb.append("§e스킬 포인트: §f").append(skillPoints).append("\n");

        // 슬롯 정보
        int maxSlots = pet.getRarity().getSkillSlots();
        sb.append("§e스킬 슬롯: §f").append(skills.size()).append("/").append(maxSlots).append("\n");

        sb.append("\n§7/pet skills use <스킬명> §8- 스킬 사용");
        sb.append("\n§7/pet skills upgrade <스킬명> §8- 스킬 강화");

        MessageUtil.sendMessage(player, sb.toString());
    }

    /**
     * 스킬 항목 포맷팅
     */
    private String formatSkillEntry(PetSkill skill) {
        StringBuilder sb = new StringBuilder();

        // 타입 색상
        SkillType type = skill.getType();
        String typeColor = getTypeColor(type);

        sb.append(typeColor).append("▸ ").append(skill.getName());
        sb.append(" §7Lv. ").append(skill.getCurrentLevel());

        if (! skill.isMaxLevel()) {
            sb.append("/").append(skill.getMaxLevel());
        } else {
            sb.append(" §6(MAX)");
        }

        // 쿨다운
        if (skill. isOnCooldown()) {
            sb.append(" §c[").append(skill.getRemainingCooldownSeconds()).append("초]");
        }

        // 설명
        sb. append("\n  §7").append(skill.getDescription());

        // 효과
        if (skill.getEffectValue("damage") > 0) {
            sb. append("\n  §c⚔ 피해: ").append(String.format("%.0f", skill.getEffectValue("damage")));
        }
        if (skill. getEffectValue("healing") > 0) {
            sb. append("\n  §a❤ 회복: ").append(String.format("%. 0f", skill. getEffectValue("healing")));
        }

        return sb.toString();
    }

    /**
     * 타입별 색상
     */
    private String getTypeColor(SkillType type) {
        if (type == null) return "§f";

        switch (type) {
            case ATTACK: 
                return "§c";
            case DEFENSE:
                return "§b";
            case BUFF:
                return "§a";
            case DEBUFF: 
                return "§5";
            case HEAL:
                return "§d";
            case GATHERING:
                return "§e";
            case SUPPORT:
                return "§9";
            case SPECIAL:
            case ULTIMATE:
                return "§6";
            default: 
                return "§f";
        }
    }

    /**
     * 스킬 사용
     */
    private void useSkill(Player player, Pet pet, String skillName) {
        // 펫이 활성 상태인지 확인
        if (!pet. isActive()) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("skill.pet-not-active"));
            return;
        }

        // 스킬 찾기
        PetSkill skill = findSkill(pet, skillName);
        if (skill == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("skill.not-found")
                    .replace("{name}", skillName));
            return;
        }

        // 패시브 스킬은 사용 불가
        if (skill.isPassive()) {
            MessageUtil.sendMessage(player, plugin. getConfigManager().getMessage("skill.passive-cannot-use"));
            return;
        }

        // 스킬 사용
        if (plugin.getPetSkillManager().useSkill(pet, skill. getSkillId(), null)) {
            // 성공 메시지는 SkillManager에서 처리
        }
    }

    /**
     * 스킬 강화
     */
    private void upgradeSkill(Player player, Pet pet, String skillName) {
        // 스킬 찾기
        PetSkill skill = findSkill(pet, skillName);
        if (skill == null) {
            MessageUtil.sendMessage(player, plugin.getConfigManager().getMessage("skill.not-found")
                    . replace("{name}", skillName));
            return;
        }

        // 스킬 강화
        if (plugin. getPetSkillManager().upgradeSkill(player, pet, skill. getSkillId())) {
            // 성공 메시지는 SkillManager에서 처리
        }
    }

    /**
     * 스킬 찾기
     */
    private PetSkill findSkill(Pet pet, String skillName) {
        for (PetSkill skill : pet.getSkills()) {
            if (skill. getName().equalsIgnoreCase(skillName) ||
                skill.getSkillId().equalsIgnoreCase(skillName)) {
                return skill;
            }
        }

        // 부분 일치
        for (PetSkill skill : pet.getSkills()) {
            if (skill.getName().toLowerCase().contains(skillName.toLowerCase())) {
                return skill;
            }
        }

        return null;
    }
}