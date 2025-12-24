package com.multiverse.pvp.managers;

import com. multiverse.pvp.PvPCore;
import com. multiverse.pvp.data.Duel;
import com.multiverse.pvp. data.PvPArena;
import com.multiverse.pvp.enums.ArenaType;
import com.multiverse. pvp.enums.DuelEndReason;
import com.multiverse. pvp.enums.DuelStatus;
import com.multiverse.pvp. utils.MessageUtil;
import net.md_5.bungee.api.chat. ClickEvent;
import net.md_5.bungee. api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit. Bukkit;
import org.bukkit. Location;
import org. bukkit.entity. Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DuelManager {

    private final PvPCore plugin;
    private final Map<UUID, Duel> activeDuels;
    private final Map<UUID, UUID> playerDuelMap;
    private final Map<UUID, List<UUID>> pendingRequests;

    private boolean duelEnabled;
    private int requestExpireTime;
    private boolean bettingEnabled;
    private double maxBetMoney;
    private int duelDuration;

    public DuelManager(PvPCore plugin) {
        this.plugin = plugin;
        this. activeDuels = new ConcurrentHashMap<>();
        this.playerDuelMap = new ConcurrentHashMap<>();
        this.pendingRequests = new ConcurrentHashMap<>();
        loadConfig();
    }

    private void loadConfig() {
        this.duelEnabled = plugin.getConfig().getBoolean("duel.enabled", true);
        this.requestExpireTime = plugin.getConfig().getInt("duel.request.expire-time", 60);
        this.bettingEnabled = plugin.getConfig().getBoolean("duel.betting.enabled", true);
        this.maxBetMoney = plugin. getConfig().getDouble("duel.betting.max-money", 100000.0);
        this.duelDuration = plugin.getConfig().getInt("duel.duration", 300);
    }

    public void sendDuelRequest(Player challenger, Player opponent,
                                Map<String, Double> betMoney, List<ItemStack> betItems) {
        if (!duelEnabled) {
            MessageUtil. sendMessage(challenger, "&c듀얼 시스템이 비활성화되어 있습니다.");
            return;
        }

        if (challenger.equals(opponent)) {
            MessageUtil.sendMessage(challenger, "&c자기 자신에게 듀얼을 신청할 수 없습니다.");
            return;
        }

        if (isInDuel(challenger)) {
            MessageUtil.sendMessage(challenger, "&c이미 듀얼 중입니다.");
            return;
        }

        if (isInDuel(opponent)) {
            MessageUtil.sendMessage(challenger, "&c상대방이 이미 듀얼 중입니다.");
            return;
        }

        if (plugin.getArenaManager().isInArena(challenger)) {
            MessageUtil.sendMessage(challenger, "&c아레나에서는 듀얼을 신청할 수 없습니다.");
            return;
        }

        if (plugin.getArenaManager().isInArena(opponent)) {
            MessageUtil.sendMessage(challenger, "&c상대방이 아레나에 있습니다.");
            return;
        }

        if (bettingEnabled && betMoney != null && ! betMoney.isEmpty()) {
            for (Map.Entry<String, Double> entry : betMoney. entrySet()) {
                if (entry.getValue() > maxBetMoney) {
                    MessageUtil.sendMessage(challenger, "&c최대 베팅 금액은 " + maxBetMoney + "입니다.");
                    return;
                }

                if (! hasEnoughMoney(challenger, entry.getKey(), entry.getValue())) {
                    MessageUtil.sendMessage(challenger, "&c베팅 금액이 부족합니다.");
                    return;
                }
            }
        }

        Duel duel = new Duel(challenger. getUniqueId(), opponent.getUniqueId());
        duel.setDuration(duelDuration);

        if (betMoney != null) {
            duel.setBetMoney(betMoney);
        }
        if (betItems != null) {
            duel.setChallengerBetItems(betItems);
        }

        activeDuels.put(duel.getDuelId(), duel);

        pendingRequests.computeIfAbsent(opponent.getUniqueId(), k -> new ArrayList<>())
                .add(duel.getDuelId());

        MessageUtil.sendMessage(challenger, "&a" + opponent.getName() + "님에게 듀얼을 신청했습니다.");

        sendDuelRequestMessage(opponent, challenger, duel);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (duel.getStatus() == DuelStatus.REQUESTED) {
                expireDuelRequest(duel);
            }
        }, requestExpireTime * 20L);
    }

    private void sendDuelRequestMessage(Player opponent, Player challenger, Duel duel) {
        String prefix = plugin.getConfig().getString("messages. prefix", "&8[&cPvP&8]&r ");

        TextComponent message = new TextComponent(MessageUtil.colorize(prefix + "&e" +
                challenger.getName() + "님이 듀얼을 신청했습니다! "));

        if (duel.hasBetting()) {
            opponent.sendMessage(MessageUtil.colorize(prefix + "&6베팅:  &f" +
                    duel.getTotalBetMoney("default") + " 골드"));
        }

        TextComponent acceptButton = new TextComponent(MessageUtil.colorize(" &a[수락]"));
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pvp accept"));
        acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("클릭하여 듀얼 수락").create()));

        TextComponent declineButton = new TextComponent(MessageUtil.colorize(" &c[거절]"));
        declineButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pvp decline"));
        declineButton. setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("클릭하여 듀얼 거절").create()));

        message.addExtra(acceptButton);
        message.addExtra(declineButton);

        opponent.spigot().sendMessage(message);
    }

    public void acceptDuel(Player player, UUID duelId) {
        Duel duel = activeDuels.get(duelId);

        if (duel == null) {
            MessageUtil.sendMessage(player, "&c듀얼 요청을 찾을 수 없습니다.");
            return;
        }

        if (! duel.getOpponent().equals(player. getUniqueId())) {
            MessageUtil.sendMessage(player, "&c이 듀얼의 상대가 아닙니다.");
            return;
        }

        if (duel. getStatus() != DuelStatus.REQUESTED) {
            MessageUtil.sendMessage(player, "&c이미 처리된 듀얼 요청입니다.");
            return;
        }

        Player challenger = Bukkit.getPlayer(duel.getChallenger());
        if (challenger == null) {
            MessageUtil.sendMessage(player, "&c상대방이 오프라인입니다.");
            expireDuelRequest(duel);
            return;
        }

        // 베팅 금액 확인 및 차감
        if (duel.hasBetting()) {
            for (Map.Entry<String, Double> entry : duel.getBetMoney().entrySet()) {
                if (! hasEnoughMoney(player, entry.getKey(), entry.getValue())) {
                    MessageUtil.sendMessage(player, "&c베팅 금액이 부족합니다.");
                    return;
                }
                if (! hasEnoughMoney(challenger, entry. getKey(), entry.getValue())) {
                    MessageUtil. sendMessage(player, "&c상대방의 베팅 금액이 부족합니다.");
                    return;
                }

                // 금액 차감
                withdrawMoney(challenger, entry.getKey(), entry.getValue());
                withdrawMoney(player, entry.getKey(), entry.getValue());
            }
        }

        // 대기 요청에서 제거
        List<UUID> requests = pendingRequests.get(player. getUniqueId());
        if (requests != null) {
            requests.remove(duelId);
        }

        // 듀얼 수락
        duel.accept();

        // 플레이어 매핑
        playerDuelMap.put(challenger.getUniqueId(), duel. getDuelId());
        playerDuelMap.put(player. getUniqueId(), duel.getDuelId());

        // 메시지
        MessageUtil.sendMessage(challenger, "&a" + player.getName() + "님이 듀얼을 수락했습니다!");
        MessageUtil.sendMessage(player, "&a듀얼을 수락했습니다!");

        // 듀얼 시작
        startDuel(duel);
    }

    public void declineDuel(Player player, UUID duelId) {
        Duel duel = activeDuels. get(duelId);

        if (duel == null) {
            MessageUtil.sendMessage(player, "&c듀얼 요청을 찾을 수 없습니다.");
            return;
        }

        if (! duel.getOpponent().equals(player.getUniqueId())) {
            MessageUtil.sendMessage(player, "&c이 듀얼의 상대가 아닙니다.");
            return;
        }

        if (duel.getStatus() != DuelStatus. REQUESTED) {
            MessageUtil.sendMessage(player, "&c이미 처리된 듀얼 요청입니다.");
            return;
        }

        Player challenger = Bukkit. getPlayer(duel.getChallenger());

        // 대기 요청에서 제거
        List<UUID> requests = pendingRequests.get(player.getUniqueId());
        if (requests != null) {
            requests. remove(duelId);
        }

        // 듀얼 제거
        activeDuels.remove(duelId);

        MessageUtil.sendMessage(player, "&c듀얼을 거절했습니다.");
        if (challenger != null) {
            MessageUtil.sendMessage(challenger, "&c" + player.getName() + "님이 듀얼을 거절했습니다.");
        }
    }

    private void expireDuelRequest(Duel duel) {
        if (duel.getStatus() != DuelStatus. REQUESTED) {
            return;
        }

        Player challenger = Bukkit.getPlayer(duel.getChallenger());
        Player opponent = Bukkit.getPlayer(duel.getOpponent());

        // 대기 요청에서 제거
        List<UUID> requests = pendingRequests.get(duel.getOpponent());
        if (requests != null) {
            requests.remove(duel.getDuelId());
        }

        activeDuels.remove(duel.getDuelId());

        if (challenger != null) {
            MessageUtil.sendMessage(challenger, "&c듀얼 요청이 만료되었습니다.");
        }
        if (opponent != null) {
            MessageUtil.sendMessage(opponent, "&c듀얼 요청이 만료되었습니다.");
        }
    }

    public void startDuel(Duel duel) {
        Player challenger = Bukkit.getPlayer(duel.getChallenger());
        Player opponent = Bukkit.getPlayer(duel.getOpponent());

        if (challenger == null || opponent == null) {
            endDuel(duel, null, DuelEndReason. DISCONNECT);
            return;
        }

        // 이전 위치 저장
        duel.savePreviousLocation(challenger.getUniqueId(), challenger.getLocation().clone());
        duel.savePreviousLocation(opponent.getUniqueId(), opponent.getLocation().clone());

        // 체력 저장
        duel.saveStartHealth(challenger.getUniqueId(), challenger.getHealth());
        duel.saveStartHealth(opponent. getUniqueId(), opponent.getHealth());

        // 듀얼 아레나 찾기
        PvPArena arena = findDuelArena();
        if (arena != null) {
            duel.setArenaId(arena.getArenaId());

            // 스폰 위치로 텔레포트
            if (arena.getSpawnPoints().size() >= 2) {
                challenger.teleport(arena.getSpawnPoint(0));
                opponent.teleport(arena.getSpawnPoint(1));
            } else if (arena.getLobby() != null) {
                challenger.teleport(arena.getLobby());
                opponent.teleport(arena.getLobby());
            }
        }

        // 체력/허기 풀 충전
        challenger.setHealth(challenger.getMaxHealth());
        challenger.setFoodLevel(20);
        challenger. setSaturation(20f);

        opponent. setHealth(opponent. getMaxHealth());
        opponent.setFoodLevel(20);
        opponent.setSaturation(20f);

        // 준비 상태
        duel.setStatus(DuelStatus.PREPARING);

        // 카운트다운
        MessageUtil.sendMessage(challenger, "&e듀얼이 3초 후에 시작됩니다!");
        MessageUtil.sendMessage(opponent, "&e듀얼이 3초 후에 시작됩니다!");

        new org.bukkit.scheduler.BukkitRunnable() {
            int countdown = 3;

            @Override
            public void run() {
                if (duel.getStatus() != DuelStatus. PREPARING) {
                    cancel();
                    return;
                }

                Player c = Bukkit. getPlayer(duel.getChallenger());
                Player o = Bukkit. getPlayer(duel.getOpponent());

                if (c == null || o == null) {
                    endDuel(duel, c != null ? duel.getChallenger() : duel.getOpponent(),
                            DuelEndReason. DISCONNECT);
                    cancel();
                    return;
                }

                if (countdown <= 0) {
                    // 듀얼 시작
                    duel.start();
                    MessageUtil.sendMessage(c, "&c&l전투 시작!");
                    MessageUtil.sendMessage(o, "&c&l전투 시작!");

                    c.playSound(c.getLocation(), org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                    o.playSound(o.getLocation(), org.bukkit.Sound. ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);

                    // 시간 제한 타이머
                    startDuelTimer(duel);
                    cancel();
                    return;
                }

                MessageUtil.sendMessage(c, "&e" + countdown + ".. .");
                MessageUtil.sendMessage(o, "&e" + countdown + "...");

                c.playSound(c.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                o.playSound(o.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);

                countdown--;
            }
        }. runTaskTimer(plugin, 0L, 20L);
    }

    private void startDuelTimer(Duel duel) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (duel.getStatus() == DuelStatus.ACTIVE) {
                // 시간 초과 - 데미지 기준 승자 결정
                UUID winnerId = determineWinnerByDamage(duel);
                endDuel(duel, winnerId, DuelEndReason. TIMEOUT);
            }
        }, duel.getDuration() * 20L);
    }

    private UUID determineWinnerByDamage(Duel duel) {
        int challengerDamage = duel.getDamageDealt(duel.getChallenger());
        int opponentDamage = duel. getDamageDealt(duel.getOpponent());

        if (challengerDamage > opponentDamage) {
            return duel.getChallenger();
        } else if (opponentDamage > challengerDamage) {
            return duel.getOpponent();
        }

        // 동점이면 체력 비교
        Player challenger = Bukkit.getPlayer(duel.getChallenger());
        Player opponent = Bukkit.getPlayer(duel.getOpponent());

        if (challenger != null && opponent != null) {
            if (challenger.getHealth() > opponent.getHealth()) {
                return duel.getChallenger();
            } else if (opponent. getHealth() > challenger.getHealth()) {
                return duel.getOpponent();
            }
        }

        return null; // 무승부
    }

    public void endDuel(Duel duel, UUID winnerId, DuelEndReason reason) {
        if (duel. getStatus() == DuelStatus.ENDED) {
            return;
        }

        duel.end(winnerId, reason);

        Player challenger = Bukkit.getPlayer(duel.getChallenger());
        Player opponent = Bukkit.getPlayer(duel.getOpponent());

        // 승자/패자 메시지
        if (winnerId != null) {
            Player winner = Bukkit.getPlayer(winnerId);
            UUID loserId = winnerId. equals(duel. getChallenger()) ? duel.getOpponent() : duel.getChallenger();
            Player loser = Bukkit.getPlayer(loserId);

            if (winner != null) {
                MessageUtil.sendMessage(winner, reason. getWinnerMessage());
            }
            if (loser != null) {
                MessageUtil. sendMessage(loser, reason.getLoserMessage());
            }

            // 보상 지급
            if (reason. givesBetReward()) {
                giveDuelRewards(duel, winnerId);
            } else if (reason.refundsBet()) {
                refundBets(duel);
            }

            // 레이팅 업데이트
            if (reason.affectsRating() && winner != null && loser != null) {
                plugin.getRankingManager().updateRating(winner, loser);
            }

            // 통계 업데이트
            if (winner != null) {
                plugin.getStatisticsManager().recordDuelWin(winner);
            }
            if (loser != null) {
                if (reason == DuelEndReason.SURRENDER) {
                    plugin.getStatisticsManager().recordDuelSurrender(loser);
                } else {
                    plugin.getStatisticsManager().recordDuelLoss(loser);
                }
            }
        } else {
            // 무승부
            if (challenger != null) {
                MessageUtil.sendMessage(challenger, "&e무승부입니다!");
            }
            if (opponent != null) {
                MessageUtil. sendMessage(opponent, "&e무승부입니다!");
            }

            // 베팅 환불
            refundBets(duel);
        }

        // 플레이어 원위치
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            returnPlayerToLocation(duel.getChallenger(), duel);
            returnPlayerToLocation(duel. getOpponent(), duel);

            // 정리
            playerDuelMap.remove(duel.getChallenger());
            playerDuelMap.remove(duel.getOpponent());
            activeDuels.remove(duel.getDuelId());
        }, 60L); // 3초 후
    }

    private void returnPlayerToLocation(UUID playerId, Duel duel) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) {
            return;
        }

        Location previousLocation = duel.getPreviousLocation(playerId);
        if (previousLocation != null) {
            player.teleport(previousLocation);
        }

        // 체력 복구
        player. setHealth(player. getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);
    }

    private void giveDuelRewards(Duel duel, UUID winnerId) {
        Player winner = Bukkit. getPlayer(winnerId);
        if (winner == null) {
            return;
        }

        // 베팅 금액 지급 (2배)
        for (Map.Entry<String, Double> entry : duel.getBetMoney().entrySet()) {
            double totalBet = entry. getValue() * 2;
            depositMoney(winner, entry.getKey(), totalBet);
            MessageUtil.sendMessage(winner, "&a베팅 보상으로 " + totalBet + " " + entry.getKey() + "을(를) 획득했습니다!");
        }

        // 베팅 아이템 지급
        List<ItemStack> allItems = duel.getAllBetItems();
        for (ItemStack item : allItems) {
            HashMap<Integer, ItemStack> leftover = winner.getInventory().addItem(item);
            for (ItemStack left : leftover.values()) {
                winner.getWorld().dropItemNaturally(winner.getLocation(), left);
            }
        }

        // 기본 듀얼 보상
        plugin.getRewardManager().giveWinReward(winner, ArenaType.DUEL_1V1);
    }

    private void refundBets(Duel duel) {
        Player challenger = Bukkit. getPlayer(duel.getChallenger());
        Player opponent = Bukkit. getPlayer(duel.getOpponent());

        for (Map.Entry<String, Double> entry : duel.getBetMoney().entrySet()) {
            if (challenger != null) {
                depositMoney(challenger, entry.getKey(), entry.getValue());
                MessageUtil.sendMessage(challenger, "&e베팅 금액이 환불되었습니다.");
            }
            if (opponent != null) {
                depositMoney(opponent, entry.getKey(), entry.getValue());
                MessageUtil.sendMessage(opponent, "&e베팅 금액이 환불되었습니다.");
            }
        }

        // 아이템 환불
        if (challenger != null) {
            for (ItemStack item : duel.getChallengerBetItems()) {
                HashMap<Integer, ItemStack> leftover = challenger.getInventory().addItem(item);
                for (ItemStack left : leftover.values()) {
                    challenger.getWorld().dropItemNaturally(challenger. getLocation(), left);
                }
            }
        }
        if (opponent != null) {
            for (ItemStack item : duel.getOpponentBetItems()) {
                HashMap<Integer, ItemStack> leftover = opponent.getInventory().addItem(item);
                for (ItemStack left : leftover.values()) {
                    opponent.getWorld().dropItemNaturally(opponent. getLocation(), left);
                }
            }
        }
    }

    public void surrender(Player player) {
        Duel duel = getActiveDuel(player);

        if (duel == null) {
            MessageUtil.sendMessage(player, "&c현재 듀얼 중이 아닙니다.");
            return;
        }

        if (! duel.isActive()) {
            MessageUtil.sendMessage(player, "&c듀얼이 진행 중이 아닙니다.");
            return;
        }

        UUID winnerId = duel.getOpponentOf(player. getUniqueId());
        endDuel(duel, winnerId, DuelEndReason. SURRENDER);
    }

    public void handlePlayerDeath(Player player, Player killer) {
        Duel duel = getActiveDuel(player);

        if (duel == null || ! duel.isActive()) {
            return;
        }

        UUID winnerId = duel. getOpponentOf(player.getUniqueId());
        endDuel(duel, winnerId, DuelEndReason.DEATH);
    }

    public void handlePlayerDisconnect(Player player) {
        Duel duel = getActiveDuel(player);

        if (duel == null) {
            return;
        }

        if (duel.isPending()) {
            // 대기 중이면 취소
            activeDuels.remove(duel.getDuelId());
            playerDuelMap.remove(player.getUniqueId());

            UUID otherId = duel. getOpponentOf(player.getUniqueId());
            Player other = Bukkit. getPlayer(otherId);
            if (other != null) {
                MessageUtil. sendMessage(other, "&c상대방이 접속을 종료하여 듀얼이 취소되었습니다.");
            }
        } else if (duel.isActive()) {
            // 진행 중이면 패배 처리
            UUID winnerId = duel.getOpponentOf(player.getUniqueId());
            endDuel(duel, winnerId, DuelEndReason.DISCONNECT);
        }
    }

    public void recordDamage(Player attacker, Player victim, double damage) {
        Duel duel = getActiveDuel(attacker);

        if (duel == null || !duel.isActive()) {
            return;
        }

        if (! duel.isParticipant(victim. getUniqueId())) {
            return;
        }

        duel.recordDamage(attacker. getUniqueId(), victim.getUniqueId(), (int) damage);
    }

    public Duel getActiveDuel(Player player) {
        UUID duelId = playerDuelMap.get(player.getUniqueId());
        if (duelId == null) {
            return null;
        }
        return activeDuels.get(duelId);
    }

    public boolean isInDuel(Player player) {
        UUID duelId = playerDuelMap.get(player.getUniqueId());
        if (duelId == null) {
            return false;
        }
        Duel duel = activeDuels. get(duelId);
        return duel != null && (duel.isActive() || duel.isPending());
    }

    public boolean isDuelOpponent(Player player, Player target) {
        Duel duel = getActiveDuel(player);
        if (duel == null) {
            return false;
        }
        return duel. isParticipant(target.getUniqueId());
    }

    public Duel getPendingDuelRequest(Player player) {
        List<UUID> requests = pendingRequests. get(player.getUniqueId());
        if (requests == null || requests.isEmpty()) {
            return null;
        }

        // 가장 최근 요청 반환
        UUID latestDuelId = requests. get(requests.size() - 1);
        Duel duel = activeDuels.get(latestDuelId);

        if (duel != null && duel.getStatus() == DuelStatus.REQUESTED) {
            return duel;
        }

        return null;
    }

    private PvPArena findDuelArena() {
        List<PvPArena> duelArenas = plugin.getArenaManager().getArenasByType(ArenaType.DUEL_1V1);

        for (PvPArena arena :  duelArenas) {
            if (arena.getStatus().canJoin() && arena.getPlayers().isEmpty()) {
                return arena;
            }
        }

        return null;
    }

    public void endAllDuels() {
        for (Duel duel : new ArrayList<>(activeDuels.values())) {
            if (duel.isActive() || duel.isPending()) {
                endDuel(duel, null, DuelEndReason. ADMIN);
            }
        }
    }

    // EconomyCore 연동 메서드
    private boolean hasEnoughMoney(Player player, String currency, double amount) {
        if (! plugin.hasEconomyCore()) {
            return true;
        }

        try {
            // EconomyCore API 호출
            return getPlayerBalance(player, currency) >= amount;
        } catch (Exception e) {
            plugin.getLogger().warning("EconomyCore 연동 오류: " + e.getMessage());
            return false;
        }
    }

    private double getPlayerBalance(Player player, String currency) {
        // EconomyCore API 호출
        // 실제 구현은 EconomyCore의 API에 따라 달라짐
        return 0.0;
    }

    private void withdrawMoney(Player player, String currency, double amount) {
        if (!plugin.hasEconomyCore()) {
            return;
        }

        try {
            // EconomyCore API 호출
        } catch (Exception e) {
            plugin.getLogger().warning("EconomyCore 연동 오류: " + e.getMessage());
        }
    }

    private void depositMoney(Player player, String currency, double amount) {
        if (!plugin.hasEconomyCore()) {
            return;
        }

        try {
            // EconomyCore API 호출
        } catch (Exception e) {
            plugin. getLogger().warning("EconomyCore 연동 오류: " + e. getMessage());
        }
    }

    public void reload() {
        loadConfig();
    }
}