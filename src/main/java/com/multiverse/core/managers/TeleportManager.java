package com.multiverse.core.managers;

import com.multiverse.core.MultiverseCore;
import com.multiverse.core.data.YAMLDataManager;
import com.multiverse.core.models.Dimension;
import com.multiverse.core.models.WarmupData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TeleportManager {
    private final MultiverseCore plugin;
    private final YAMLDataManager dataManager;
    private final BalanceManager balanceManager;
    private final DimensionManager dimensionManager;

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, WarmupData> warmups = new HashMap<>();

    public TeleportManager(MultiverseCore plugin, YAMLDataManager dataManager, BalanceManager balanceManager, DimensionManager dimensionManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.balanceManager = balanceManager;
        this.dimensionManager = dimensionManager;
        cooldowns.putAll(dataManager.loadCooldowns());
    }

    // ===== 텔레포트 실행 =====
    public void teleportToDimension(Player player, String dimensionId) {
        teleportToDimension(player, dimensionId, false);
    }

    public void teleportToDimension(Player player, String dimensionId, boolean bypassCost) {
        if (!canTeleport(player, dimensionId)) {
            player.sendMessage(getTeleportDenyReason(player, dimensionId));
            return;
        }
        int cost = bypassCost ? 0 : getTeleportCost(player, getPlayerDimension(player), dimensionId);
        if (!bypassCost && !chargeTeleportCost(player, cost)) {
            player.sendMessage(plugin.getMessageUtil().get("restrictions.insufficient-money").replace("{cost}", String.valueOf(cost)));
            return;
        }
        setCooldown(player);
        // 웜업 적용
        int warmupSeconds = plugin.getConfig().getInt("teleport.warmup.duration", 5);
        startWarmup(player, dimensionId);
        player.sendMessage(plugin.getMessageUtil().get("teleport.warmup-start").replace("{time}", String.valueOf(warmupSeconds)));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isWarming(player)) {
                    Dimension dim = dimensionManager.getDimension(dimensionId);
                    if (dim != null) {
                        org.bukkit.World world = Bukkit.getWorld(dim.getWorldName());
                        if (world != null) {
                            player.teleport(world.getSpawnLocation());
                            player.sendMessage(plugin.getMessageUtil().get("teleport.success").replace("{dimension}", dim.getName()));
                        }
                    }
                    cancelWarmup(player);
                }
            }
        }.runTaskLater(plugin, warmupSeconds * 20);
    }

    // ===== 텔레포트 가능 여부 =====
    public boolean canTeleport(Player player, String dimensionId) {
        Dimension dim = dimensionManager.getDimension(dimensionId);
        if (dim == null) return false;
        if (!dim.isActive()) return false;
        if (isOnCooldown(player) && !player.hasPermission("multiverse.teleport.nocooldown")) return false;
        if (player.getLevel() < dim.getLevelRequirement()
            && plugin.getConfig().getBoolean("teleport.restrictions.check-level", true)) return false;
        // 퀘스트 조건 체크 등 추가
        return true;
    }

    public String getTeleportDenyReason(Player player, String dimensionId) {
        Dimension dim = dimensionManager.getDimension(dimensionId);
        if (dim == null) return plugin.getMessageUtil().get("dimension.not-found");
        if (!dim.isActive()) return plugin.getMessageUtil().get("dimension.inactive");
        if (isOnCooldown(player) && !player.hasPermission("multiverse.teleport.nocooldown")) {
            long sec = getCooldownRemaining(player);
            return plugin.getMessageUtil().get("teleport.cooldown").replace("{time}", String.valueOf(sec));
        }
        if (player.getLevel() < dim.getLevelRequirement()) {
            return plugin.getMessageUtil().get("restrictions.insufficient-level").replace("{level}", String.valueOf(dim.getLevelRequirement()));
        }
        // 퀘스트, 허가 등 추가 이유
        return "§c텔레포트 조건을 만족하지 않습니다.";
    }

    // ===== 비용 처리 =====
    public int getTeleportCost(Player player, String fromDim, String toDim) {
        if ("nether_realm".equalsIgnoreCase(fromDim) || "nether_realm".equalsIgnoreCase(toDim)) {
            return plugin.getConfig().getInt("teleport.cost.via-nether", 1000);
        }
        return plugin.getConfig().getInt("teleport.cost.direct", 10000);
    }

    public boolean chargeTeleportCost(Player player, int cost) {
        if (cost <= 0) return true;
        // Vault 연동 필요: EconomyUtil.has(player, cost), withdrawPlayer(player, cost)
        // 여기서는 더미 처리
        return true;
    }

    // ===== 쿨다운 관리 =====
    public boolean isOnCooldown(Player player) {
        if (!plugin.getConfig().getBoolean("teleport.cooldown.enabled", true)) return false;
        Long cooldownEnd = cooldowns.get(player.getUniqueId());
        return cooldownEnd != null && cooldownEnd > System.currentTimeMillis();
    }

    public long getCooldownRemaining(Player player) {
        Long cooldownEnd = cooldowns.get(player.getUniqueId());
        if (cooldownEnd == null) return 0;
        long remain = (cooldownEnd - System.currentTimeMillis()) / 1000;
        return Math.max(0, remain);
    }

    public void setCooldown(Player player) {
        int cooldownSec = plugin.getConfig().getInt("teleport.cooldown.duration", 300);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldownSec * 1000L);
        dataManager.saveCooldowns(cooldowns);
    }

    // ===== 웜업 관리 =====
    public void startWarmup(Player player, String dimensionId) {
        warmups.put(player.getUniqueId(), new WarmupData(true, dimensionId, System.currentTimeMillis()));
    }

    public void cancelWarmup(Player player) {
        warmups.remove(player.getUniqueId());
        player.sendMessage(plugin.getMessageUtil().get("teleport.warmup-cancelled"));
    }

    public boolean isWarming(Player player) {
        WarmupData wd = warmups.get(player.getUniqueId());
        if (wd == null) return false;
        return wd.isActive();
    }

    // ===== 귀환석 아이템 =====
    public ItemStack createReturnStone(String lastDimension) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b귀환석");
            List<String> lore = new ArrayList<>();
            lore.add("§7우클릭하여 마지막 방문 차원으로 귀환");
            lore.add("§8" + lastDimension);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void useReturnStone(Player player, ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR || item.getItemMeta() == null) return;
        List<String> lore = item.getItemMeta().getLore();
        if (lore == null || lore.size() < 2) return;
        String dim = lore.get(1).replace("§8", "");
        teleportToDimension(player, dim);
        player.getInventory().removeItem(item);
        player.sendMessage("§b귀환석을 사용하여 " + dim + "으로 돌아갑니다.");
    }

    // 현재 차원 id
    private String getPlayerDimension(Player player) {
        return dimensionManager.getPlayerCurrentDimension(player);
    }
}