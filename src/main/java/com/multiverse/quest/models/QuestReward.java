package com.multiverse.quest.models;

import org.bukkit.inventory.ItemStack;
import java.util.*;

/**
 * 퀘스트 보상 데이터 모델
 * 퀘스트 완료 시 지급되는 보상을 정의합니다.
 */
public class QuestReward {
    private int experience;                          // 경험치
    private Map<String, Double> money;               // 화폐별 보상 (화폐명 -> 금액)
    private List<ItemStack> items;                   // 아이템 보상
    private int statPoints;                          // 스탯 포인트
    private Map<Integer, Integer> reputation;        // NPC 호감도 (NPC ID -> 호감도)
    private int questPoints;                         // 퀨스트 포인트
    private String title;                            // 칭호
    private List<String> permissions;                // 권한
    private List<String> commands;                   // 실행할 명령어
    private List<RewardChoice> choices;              // 선택 보상
    private boolean randomChoice;                    // 선택 보상 랜덤 선택 여부

    /**
     * 기본 생성자
     */
    public QuestReward() {
        this.experience = 0;
        this.money = new HashMap<>();
        this.items = new ArrayList<>();
        this. statPoints = 0;
        this.reputation = new HashMap<>();
        this.questPoints = 0;
        this.title = null;
        this.permissions = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.choices = new ArrayList<>();
        this. randomChoice = false;
    }

    // ============ Getters ============

    public int getExperience() {
        return experience;
    }

    public Map<String, Double> getMoney() {
        return new HashMap<>(money);
    }

    public double getMoney(String currency) {
        return money.getOrDefault(currency, 0.0);
    }

    public List<ItemStack> getItems() {
        return new ArrayList<>(items);
    }

    public int getStatPoints() {
        return statPoints;
    }

    public Map<Integer, Integer> getReputation() {
        return new HashMap<>(reputation);
    }

    public int getReputation(int npcId) {
        return reputation.getOrDefault(npcId, 0);
    }

    public int getQuestPoints() {
        return questPoints;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getPermissions() {
        return new ArrayList<>(permissions);
    }

    public List<String> getCommands() {
        return new ArrayList<>(commands);
    }

    public List<RewardChoice> getChoices() {
        return new ArrayList<>(choices);
    }

    public boolean isRandomChoice() {
        return randomChoice;
    }

    // ============ Setters ============

    public void setExperience(int experience) {
        this.experience = Math.max(experience, 0);
    }

    public void addExperience(int amount) {
        this.experience += Math.max(amount, 0);
    }

    public void addMoney(String currency, double amount) {
        if (amount > 0) {
            money.put(currency, money.getOrDefault(currency, 0.0) + amount);
        }
    }

    public void setMoney(String currency, double amount) {
        if (amount >= 0) {
            money. put(currency, amount);
        }
    }

    public void addItem(ItemStack item) {
        if (item != null && item.getAmount() > 0) {
            items.add(item. clone());
        }
    }

    public void addItems(List<ItemStack> itemList) {
        if (itemList != null) {
            itemList.forEach(this::addItem);
        }
    }

    public void setStatPoints(int statPoints) {
        this. statPoints = Math.max(statPoints, 0);
    }

    public void addStatPoints(int amount) {
        this.statPoints += Math.max(amount, 0);
    }

    public void addReputation(int npcId, int amount) {
        reputation.put(npcId, reputation.getOrDefault(npcId, 0) + amount);
    }

    public void setReputation(int npcId, int amount) {
        reputation. put(npcId, Math. max(amount, 0));
    }

    public void setQuestPoints(int questPoints) {
        this.questPoints = Math.max(questPoints, 0);
    }

    public void addQuestPoints(int amount) {
        this.questPoints += Math.max(amount, 0);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addPermission(String permission) {
        if (permission != null && ! permission.isEmpty() && !permissions.contains(permission)) {
            permissions.add(permission);
        }
    }

    public void addPermissions(List<String> permList) {
        if (permList != null) {
            permList.forEach(this::addPermission);
        }
    }

    public void addCommand(String command) {
        if (command != null && !command.isEmpty()) {
            commands.add(command);
        }
    }

    public void addCommands(List<String> cmdList) {
        if (cmdList != null) {
            cmdList.forEach(this::addCommand);
        }
    }

    public void addChoice(RewardChoice choice) {
        if (choice != null) {
            choices.add(choice);
        }
    }

    public void setRandomChoice(boolean randomChoice) {
        this.randomChoice = randomChoice;
    }

    // ============ Business Logic ============

    /**
     * 보상이 비어있는지 확인
     * @return 비어있는지 여부
     */
    public boolean isEmpty() {
        return experience == 0 && money.isEmpty() && items.isEmpty() && 
               statPoints == 0 && reputation.isEmpty() && questPoints == 0 && 
               title == null && permissions.isEmpty() && commands.isEmpty();
    }

    /**
     * 보상이 있는지 확인
     * @return 보상 여부
     */
    public boolean hasRewards() {
        return ! isEmpty();
    }

    /**
     * 보상 요약 반환
     * @return 보상 요약 문자열
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        
        if (experience > 0) {
            sb.append("§6경험치: §f").append(experience).append("\n");
        }
        
        for (Map.Entry<String, Double> entry : money.entrySet()) {
            sb.append("§6"). append(entry.getKey()).append(": §f").append(entry.getValue()).append("\n");
        }
        
        if (!items.isEmpty()) {
            sb.append("§6아이템: §f").append(items.size()).append("개\n");
        }
        
        if (statPoints > 0) {
            sb.append("§6스탯 포인트: §f").append(statPoints).append("\n");
        }
        
        if (!reputation.isEmpty()) {
            sb. append("§6호감도: §f").append(reputation.size()).append("명\n");
        }
        
        if (questPoints > 0) {
            sb.append("§6퀨스트 포인트: §f").append(questPoints).append("\n");
        }
        
        if (title != null) {
            sb.append("§6칭호: §f").append(title). append("\n");
        }
        
        if (!permissions.isEmpty()) {
            sb.append("§6권한: §f"). append(permissions.size()).append("개\n");
        }
        
        return sb.toString();
    }

    /**
     * 상세 보상 정보 반환
     * @return 상세 정보 문자열
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 보상 정보 ===§r\n");
        
        if (experience > 0) {
            sb.append("§7경험치: §a").append(experience).append("\n");
        }
        
        for (Map.Entry<String, Double> entry : money.entrySet()) {
            sb.append("§7").append(entry. getKey()).append(": §a").append(entry.getValue()).append("\n");
        }
        
        if (!items.isEmpty()) {
            sb.append("§7아이템 ("). append(items.size()).append("개):\n");
            for (int i = 0; i < items.size(); i++) {
                ItemStack item = items.get(i);
                sb.append("  §f- ").append(item.getType(). name()).append(" x").append(item.getAmount()). append("\n");
            }
        }
        
        if (statPoints > 0) {
            sb.append("§7스탯 포인트: §a"). append(statPoints).append("\n");
        }
        
        if (!reputation.isEmpty()) {
            sb.append("§7호감도:\n");
            reputation.forEach((npcId, amount) -> 
                sb.append("  §f- NPC #").append(npcId).append(": §a+").append(amount).append("\n")
            );
        }
        
        if (questPoints > 0) {
            sb. append("§7퀨스트 포인트: §a").append(questPoints).append("\n");
        }
        
        if (title != null) {
            sb.append("§7칭호: §a").append(title).append("\n");
        }
        
        if (! permissions.isEmpty()) {
            sb.append("§7권한 (").append(permissions.size()). append("개):\n");
            for (String perm : permissions) {
                sb.append("  §f- ").append(perm).append("\n");
            }
        }
        
        return sb.toString();
    }

    /**
     * 선택 보상 여부 확인
     * @return 선택 보상 여부
     */
    public boolean hasChoices() {
        return !choices.isEmpty();
    }

    /**
     * 선택 보상 개수 반환
     * @return 선택 보상 개수
     */
    public int getChoiceCount() {
        return choices.size();
    }

    /**
     * 선택 보상 가져오기
     * @param index 인덱스
     * @return RewardChoice (없으면 null)
     */
    public RewardChoice getChoice(int index) {
        if (index >= 0 && index < choices.size()) {
            return choices.get(index);
        }
        return null;
    }

    /**
     * 랜덤 선택 보상 가져오기
     * @return 랜덤 RewardChoice
     */
    public RewardChoice getRandomChoice() {
        if (choices.isEmpty()) return null;
        return choices.get(new Random().nextInt(choices.size()));
    }

    /**
     * 아이템 개수 반환
     * @return 아이템 개수
     */
    public int getItemCount() {
        return items.stream().mapToInt(ItemStack::getAmount).sum();
    }

    /**
     * 총 경험치 계산 (배수 적용)
     * @param multiplier 배수
     * @return 계산된 경험치
     */
    public int getExperienceWithMultiplier(double multiplier) {
        return (int) (experience * multiplier);
    }

    /**
     * 총 금액 계산 (특정 화폐, 배수 적용)
     * @param currency 화폐
     * @param multiplier 배수
     * @return 계산된 금액
     */
    public double getMoneyWithMultiplier(String currency, double multiplier) {
        return getMoney(currency) * multiplier;
    }

    /**
     * 복사본 생성
     * @return QuestReward 복사본
     */
    public QuestReward copy() {
        QuestReward copy = new QuestReward();
        copy.experience = this.experience;
        copy.money = new HashMap<>(this.money);
        copy.items = new ArrayList<>(this.items);
        copy.statPoints = this. statPoints;
        copy.reputation = new HashMap<>(this.reputation);
        copy.questPoints = this.questPoints;
        copy.title = this.title;
        copy.permissions = new ArrayList<>(this.permissions);
        copy.commands = new ArrayList<>(this.commands);
        copy.choices = new ArrayList<>(this.choices);
        copy.randomChoice = this.randomChoice;
        return copy;
    }

    /**
     * 문자열 표현
     * @return 문자열
     */
    @Override
    public String toString() {
        return String.format("QuestReward{exp=%d, money=%d, items=%d, statPoints=%d}",
                experience, money.size(), items.size(), statPoints);
    }
}