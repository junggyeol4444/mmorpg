package com. multiverse.party. listeners;

import com.multiverse.party.PartyCore;
import com. multiverse. party. models.Party;
import com.multiverse. party.models.LootSession;
import com. multiverse.party. models.enums.LootDistribution;
import org.  bukkit. Bukkit;
import org.bukkit.Location;
import org. bukkit.entity. Entity;
import org.  bukkit. entity.Item;
import org. bukkit.  entity.LivingEntity;
import org.bukkit. entity.Monster;
import org.  bukkit. entity.  Player;
import org.bukkit.  event.EventHandler;
import org.bukkit. event.EventPriority;
import org.bukkit.  event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit. event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class LootListener implements Listener {

    private final PartyCore plugin;
    private final Map<UUID, UUID> roundRobinIndex;
    private final Set<UUID> processedItems;

    public LootListener(PartyCore plugin) {
        this.plugin = plugin;
        this.roundRobinIndex = new HashMap<>();
        this.processedItems = new HashSet<>();
    }

    // ==================== 몬스터 드롭 처리 ====================
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!plugin. getConfig().getBoolean("loot. enabled", true)) return;

        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        
        if (killer == null) return;
        if (!(entity instanceof Monster)) return;

        Party party = plugin.getPartyManager().getPlayerParty(killer);
        if (party == null) return;

        LootDistribution distribution = party. getLootDistribution();
        
        // FREE_FOR_ALL은 기본 동작 사용
        if (distribution == LootDistribution.FREE_FOR_ALL) return;

        List<ItemStack> drops = event.getDrops();
        if (drops.isEmpty()) return;

        // 희귀 아이템 필터링
        List<ItemStack> rareItems = filterRareItems(drops);
        
        if (rareItems.isEmpty()) return;

        // 분배 방식에 따라 처리
        switch (distribution) {
            case ROUND_ROBIN: 
                handleRoundRobinDrop(party, killer, rareItems, entity. getLocation());
                break;
            case NEED_BEFORE_GREED:
                handleNeedBeforeGreedDrop(party, killer, rareItems, entity.getLocation());
                break;
            case MASTER_LOOT:
                handleMasterLootDrop(party, killer, rareItems, entity.getLocation());
                break;
        }

        // 처리된 아이템 드롭에서 제거
        drops.removeAll(rareItems);
    }

    // ==================== 순차 분배 (Round Robin) ====================
    private void handleRoundRobinDrop(Party party, Player killer, List<ItemStack> items, Location location) {
        List<UUID> members = new ArrayList<>(party. getMembers());
        
        // 범위 내 멤버만 필터링
        double lootRange = plugin. getConfig().getDouble("loot.range", 50.0);
        members.removeIf(uuid -> {
            Player player = Bukkit. getPlayer(uuid);
            if (player == null || !player.isOnline()) return true;
            if (!player.getWorld().equals(location.getWorld())) return true;
            return player.getLocation().distanceSquared(location) > lootRange * lootRange;
        });

        if (members.isEmpty()) return;

        // 현재 인덱스 가져오기
        UUID partyId = party. getPartyId();
        int currentIndex = 0;
        
        if (roundRobinIndex. containsKey(partyId)) {
            UUID lastReceiver = roundRobinIndex.get(partyId);
            int lastIndex = members. indexOf(lastReceiver);
            if (lastIndex >= 0) {
                currentIndex = (lastIndex + 1) % members.size();
            }
        }

        // 아이템 분배
        for (ItemStack item : items) {
            UUID receiverId = members.get(currentIndex);
            Player receiver = Bukkit.getPlayer(receiverId);
            
            if (receiver != null && receiver.isOnline()) {
                giveItemToPlayer(receiver, item);
                
                // 파티 알림
                notifyLootReceived(party, receiver, item);
            }
            
            currentIndex = (currentIndex + 1) % members.size();
        }

        // 마지막 수령자 저장
        if (!members.isEmpty()) {
            int lastReceiverIndex = (currentIndex - 1 + members.size()) % members.size();
            roundRobinIndex.put(partyId, members.get(lastReceiverIndex));
        }
    }

    // ==================== 필요 우선 분배 (Need Before Greed) ====================
    private void handleNeedBeforeGreedDrop(Party party, Player killer, List<ItemStack> items, Location location) {
        for (ItemStack item :  items) {
            // 롤 세션 생성
            UUID sessionId = UUID.randomUUID();
            int rollDuration = plugin.getConfig().getInt("loot.roll-duration", 30);
            
            // 범위 내 멤버 확인
            List<UUID> eligiblePlayers = getEligiblePlayers(party, location);
            if (eligiblePlayers.isEmpty()) continue;

            // 세션 생성
            LootSession session = new LootSession(sessionId, party.getPartyId(), item, 
                    eligiblePlayers, rollDuration);
            plugin.getLootManager().registerSession(session);

            // GUI 열기
            for (UUID playerUUID : eligiblePlayers) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    plugin.getGuiManager().openLootRollGUI(player, party, sessionId);
                }
            }

            // 타임아웃 스케줄링
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (! session.isFinished()) {
                    plugin.getLootManager().finishSession(sessionId);
                }
            }, rollDuration * 20L);
        }
    }

    // ==================== 리더 분배 (Master Loot) ====================
    private void handleMasterLootDrop(Party party, Player killer, List<ItemStack> items, Location location) {
        Player leader = Bukkit.getPlayer(party.getLeaderId());
        
        if (leader == null || !leader.isOnline()) {
            // 리더가 없으면 부리더에게
            for (UUID memberUUID : party. getMembers()) {
                if (plugin.getPartyRoleManager().isOfficer(party, memberUUID)) {
                    leader = Bukkit. getPlayer(memberUUID);
                    if (leader != null && leader.isOnline()) break;
                }
            }
        }

        if (leader == null) {
            // 아무도 없으면 킬러에게
            leader = killer;
        }

        // 리더/대리인에게 모든 아이템 전달
        for (ItemStack item : items) {
            giveItemToPlayer(leader, item);
        }

        // 파티 알림
        if (! items.isEmpty()) {
            plugin.getPartyChatManager().sendNotification(party,
                    plugin.getMessageUtil().getMessage("loot. master-received",
                            "%player%", leader.getName(),
                            "%count%", String.valueOf(items. size())));
        }
    }

    // ==================== 아이템 픽업 제한 ====================
    @EventHandler(priority = EventPriority. HIGH, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        Item itemEntity = event.getItem();
        
        // 파티 전용 아이템 확인
        if (itemEntity.hasMetadata("partyLoot")) {
            String ownerId = itemEntity.getMetadata("partyLoot").get(0).asString();
            
            if (!player.getUniqueId().toString().equals(ownerId)) {
                event.setCancelled(true);
                return;
            }
        }

        // 파티 멤버만 픽업 가능 설정
        if (itemEntity.hasMetadata("partyOnly")) {
            String partyId = itemEntity.getMetadata("partyOnly").get(0).asString();
            Party party = plugin.getPartyManager().getParty(UUID.fromString(partyId));
            
            if (party == null || ! party.getMembers().contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    // ==================== 유틸리티 ====================
    private List<ItemStack> filterRareItems(List<ItemStack> drops) {
        List<ItemStack> rareItems = new ArrayList<>();
        int minRarity = plugin.getConfig().getInt("loot.min-rarity-for-distribution", 1);

        for (ItemStack item : drops) {
            if (item == null) continue;
            
            int rarity = getItemRarity(item);
            if (rarity >= minRarity) {
                rareItems.add(item);
            }
        }

        return rareItems;
    }

    private int getItemRarity(ItemStack item) {
        // 기본 희귀도 판정
        // 0:  일반, 1: 희귀, 2: 에픽, 3: 전설
        
        if (item. hasItemMeta() && item.getItemMeta().hasEnchants()) {
            return 1;
        }

        switch (item.getType()) {
            case DIAMOND:
            case EMERALD:
            case DIAMOND_SWORD: 
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return 1;
            case NETHERITE_SWORD:
            case NETHERITE_PICKAXE:
            case NETHERITE_AXE:
            case NETHERITE_HELMET: 
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS: 
            case NETHERITE_BOOTS:
            case NETHERITE_INGOT:
                return 2;
            case ELYTRA:
            case NETHER_STAR:
            case DRAGON_EGG:
            case ENCHANTED_GOLDEN_APPLE: 
            case TOTEM_OF_UNDYING:
                return 3;
            default:
                return 0;
        }
    }

    private List<UUID> getEligiblePlayers(Party party, Location location) {
        List<UUID> eligible = new ArrayList<>();
        double lootRange = plugin.getConfig().getDouble("loot. range", 50.0);
        double rangeSquared = lootRange * lootRange;

        for (UUID memberUUID : party. getMembers()) {
            Player player = Bukkit.getPlayer(memberUUID);
            if (player == null || ! player.isOnline()) continue;
            if (!player.getWorld().equals(location.getWorld())) continue;
            
            if (player. getLocation().distanceSquared(location) <= rangeSquared) {
                eligible. add(memberUUID);
            }
        }

        return eligible;
    }

    private void giveItemToPlayer(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(item);
        
        // 인벤토리가 가득 찼으면 드롭
        for (ItemStack leftover : overflow.values()) {
            Item droppedItem = player.getWorld().dropItemNaturally(player. getLocation(), leftover);
            // 해당 플레이어만 픽업 가능하도록 설정
            droppedItem.setMetadata("partyLoot", 
                    new FixedMetadataValue(plugin, player.getUniqueId().toString()));
            droppedItem.setPickupDelay(0);
        }
    }

    private void notifyLootReceived(Party party, Player receiver, ItemStack item) {
        String itemName = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ?
                item.getItemMeta().getDisplayName() : item.getType().name();

        plugin.getPartyChatManager().sendNotification(party,
                plugin.getMessageUtil().getMessage("loot.item-received",
                        "%player%", receiver.getName(),
                        "%item%", itemName));
    }

    public void resetRoundRobin(UUID partyId) {
        roundRobinIndex. remove(partyId);
    }
}