package com.multiverse.death.data;

import com.multiverse.death.models.*;
import com.multiverse.death.models.enums.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.*;

public interface DataManager {

    // 사망 기록
    void saveDeathRecord(Player player, DeathRecord deathRecord);
    DeathRecord getLastDeathRecord(Player player);
    List<DeathRecord> getDeathHistory(Player player, int limit);
    int getDeathCount(Player player);
    void clearDeathLocation(Player player);
    boolean isPlayerSpiritRace(Player player);

    // 명계 위치
    NetherRealmLocation getNetherRealmLocation(LocationType type);
    void setNetherRealmLocation(LocationType type, Location loc);
    List<NetherRealmLocation> getAllNetherRealmLocations();

    // 플레이어 보험
    Insurance getPlayerInsurance(Player player);
    void savePlayerInsurance(Player player, Insurance insurance);

    // 부활 퀘스트
    RevivalQuest getRevivalQuest(Player player);
    void saveRevivalQuest(Player player, RevivalQuest quest);

    // 소울 코인
    double getSoulCoinBalance(Player player);
    void setSoulCoinBalance(Player player, double balance);

    double getTotalSoulCoinBurned();
    void setTotalSoulCoinBurned(double burned);

    double getTotalSoulCoinCirculation();
    double getTotalSoulCoinEarned();
    double getTotalSoulCoinSpent();

    void recordGlobalBurn(String reason, double amount);

    void addSoulCoinTransaction(Player player, SoulCoinTransaction transaction);
    List<SoulCoinTransaction> getSoulCoinTransactions(Player player, int limit);

    // NPC
    void spawnNPC(NetherRealmNPC npc);
    void removeNPC(String id);
    NetherRealmNPC getNPC(String id);
    List<NetherRealmNPC> getAllNPCs();

    // 기타
    List<Player> getAllOnlinePlayers();

    // 저장 전/후 처리
    void saveAll();
}