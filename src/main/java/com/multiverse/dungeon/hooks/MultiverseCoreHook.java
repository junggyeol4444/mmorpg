package com.multiverse.dungeon.hooks;

import com.multiverse.dungeon.DungeonCore;
import org.bukkit.entity.Player;
import org.bukkit.World;

/**
 * MultiverseCore 플러그인 연동
 */
public class MultiverseCoreHook {

    private final DungeonCore plugin;
    private boolean enabled = false;

    /**
     * 생성자
     */
    public MultiverseCoreHook(DungeonCore plugin) {
        this.plugin = plugin;
        this.enabled = initialize();
    }

    /**
     * 초기화
     */
    private boolean initialize() {
        try {
            if (org.bukkit. Bukkit.getPluginManager().  getPlugin("Multiverse-Core") != null) {
                plugin.getLogger().info("✅ MultiverseCore 플러그인이 감지되었습니다.");
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ MultiverseCore 연동 실패: " + e.getMessage());
        }

        return false;
    }

    /**
     * 연동 활성화 여부
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 월드가 존재하는지 확인
     *
     * @param worldName 월드 이름
     * @return 존재하면 true
     */
    public boolean hasWorld(String worldName) {
        if (!enabled || worldName == null) {
            return false;
        }

        try {
            return org.bukkit.  Bukkit.getWorld(worldName) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 월드 가져오기
     *
     * @param worldName 월드 이름
     * @return World 객체
     */
    public World getWorld(String worldName) {
        if (!enabled || worldName == null) {
            return null;
        }

        try {
            return org.bukkit.Bukkit.getWorld(worldName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 월드 목록 조회
     *
     * @return 월드 목록
     */
    public java.util.List<World> getWorlds() {
        java.util.List<World> worlds = new java.util.ArrayList<>();

        if (!enabled) {
            return worlds;
        }

        try {
            worlds. addAll(org.bukkit. Bukkit.  getWorlds());
        } catch (Exception e) {
            plugin.getLogger().warning("⚠️ 월드 목록 조회 실패: " + e.getMessage());
        }

        return worlds;
    }

    /**
     * 플레이어를 월드로 텔레포트
     *
     * @param player 플레이어
     * @param worldName 월드 이름
     * @return 성공하면 true
     */
    public boolean teleportToWorld(Player player, String worldName) {
        if (!enabled || player == null || worldName == null) {
            return false;
        }

        try {
            World world = org.bukkit.  Bukkit.getWorld(worldName);
            if (world == null) {
                return false;
            }

            org.bukkit.Location spawnLoc = world.getSpawnLocation();
            return player.teleport(spawnLoc);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 월드의 스폰 지점 가져오기
     *
     * @param worldName 월드 이름
     * @return 스폰 지점
     */
    public org.bukkit.Location getWorldSpawn(String worldName) {
        if (!enabled || worldName == null) {
            return null;
        }

        try {
            World world = org.bukkit. Bukkit.getWorld(worldName);
            if (world == null) {
                return null;
            }

            return world.getSpawnLocation();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 월드의 난이도 설정
     *
     * @param worldName 월드 이름
     * @param difficulty 난이도
     * @return 성공하면 true
     */
    public boolean setDifficulty(String worldName, org.bukkit.  Difficulty difficulty) {
        if (!enabled || worldName == null || difficulty == null) {
            return false;
        }

        try {
            World world = org. bukkit.Bukkit.getWorld(worldName);
            if (world == null) {
                return false;
            }

            world.setDifficulty(difficulty);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 월드의 난이도 가져오기
     *
     * @param worldName 월드 이름
     * @return 난이도
     */
    public org.bukkit.Difficulty getDifficulty(String worldName) {
        if (!enabled || worldName == null) {
            return org.bukkit.  Difficulty.NORMAL;
        }

        try {
            World world = org.  bukkit.Bukkit.getWorld(worldName);
            if (world == null) {
                return org.bukkit. Difficulty.NORMAL;
            }

            return world.getDifficulty();
        } catch (Exception e) {
            return org.  bukkit. Difficulty.NORMAL;
        }
    }

    /**
     * 월드가 로드되어 있는지 확인
     *
     * @param worldName 월드 이름
     * @return 로드되어 있으면 true
     */
    public boolean isWorldLoaded(String worldName) {
        if (!enabled || worldName == null) {
            return false;
        }

        try {
            return org.bukkit.Bukkit.getWorld(worldName) != null;
        } catch (Exception e) {
            return false;
        }
    }
}