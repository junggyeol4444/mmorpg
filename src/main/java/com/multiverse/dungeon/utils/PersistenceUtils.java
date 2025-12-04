package com.multiverse. dungeon.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io. File;
import java.io. IOException;
import java.util.UUID;

/**
 * 데이터 저장/로드 유틸리티
 */
public class PersistenceUtils {

    /**
     * 객체를 파일에 저장
     *
     * @param object 저장할 객체
     * @param file 저장 대상 파일
     * @return 성공하면 true
     */
    public static boolean saveObject(Object object, File file) {
        if (object == null || file == null) {
            return false;
        }

        try {
            YamlConfiguration config = new YamlConfiguration();
            
            // 객체의 정보를 YAML에 저장
            if (object instanceof java.util.Map) {
                java.util.Map<?, ?> map = (java.util.Map<?, ?>) object;
                for (java.util.Map.Entry<?, ?> entry : map.entrySet()) {
                    config. set(entry.getKey().toString(), entry.getValue());
                }
            }
            
            config.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 파일에서 객체 로드
     *
     * @param file 로드 대상 파일
     * @return 로드된 설정
     */
    public static YamlConfiguration loadObject(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        try {
            return YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * UUID를 문자열로 저장
     *
     * @param uuid UUID
     * @return UUID 문자열
     */
    public static String serializeUUID(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        return uuid.toString();
    }

    /**
     * 문자열에서 UUID 복원
     *
     * @param uuidString UUID 문자열
     * @return UUID 객체
     */
    public static UUID deserializeUUID(String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            return null;
        }

        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Location을 문자열로 저장
     *
     * @param location 위치
     * @return Location 문자열
     */
    public static String serializeLocation(org.bukkit.Location location) {
        if (location == null) {
            return null;
        }

        return location.getWorld().getName() + "," + 
               location.getX() + "," + 
               location.getY() + "," + 
               location. getZ() + "," + 
               location.getYaw() + "," + 
               location.getPitch();
    }

    /**
     * 문자열에서 Location 복원
     *
     * @param locationString Location 문자열
     * @return Location 객체
     */
    public static org.bukkit.Location deserializeLocation(String locationString) {
        if (locationString == null || locationString.isEmpty()) {
            return null;
        }

        try {
            String[] parts = locationString.split(",");
            if (parts.length < 6) {
                return null;
            }

            org.bukkit.World world = org.bukkit. Bukkit.getWorld(parts[0]);
            if (world == null) {
                return null;
            }

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double. parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);

            return new org. bukkit.Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 리스트를 쉼표로 구분된 문자열로 저장
     *
     * @param list 리스트
     * @return 쉼표로 구분된 문자열
     */
    public static String serializeList(java.util.List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        return String.join(",", list);
    }

    /**
     * 쉼표로 구분된 문자열을 리스트로 복원
     *
     * @param listString 쉼표로 구분된 문자열
     * @return 리스트
     */
    public static java.util. List<String> deserializeList(String listString) {
        java.util.List<String> list = new java.util.ArrayList<>();

        if (listString == null || listString.isEmpty()) {
            return list;
        }

        String[] parts = listString.split(",");
        for (String part : parts) {
            if (! part.trim().isEmpty()) {
                list.add(part.trim());
            }
        }

        return list;
    }

    /**
     * 맵을 YAML로 저장
     *
     * @param map 맵
     * @param file 파일
     * @return 성공하면 true
     */
    public static boolean saveMap(java.util. Map<String, ? > map, File file) {
        if (map == null || file == null) {
            return false;
        }

        try {
            YamlConfiguration config = new YamlConfiguration();
            for (java.util.Map.Entry<String, ?> entry : map.entrySet()) {
                config. set(entry.getKey(), entry.getValue());
            }
            config.save(file);
            return true;
        } catch (IOException e) {
            e. printStackTrace();
            return false;
        }
    }

    /**
     * YAML에서 맵으로 로드
     *
     * @param file 파일
     * @return 맵
     */
    public static java.util.Map<String, Object> loadMap(File file) {
        java.util.Map<String, Object> map = new java.util. HashMap<>();

        if (file == null || !file.exists()) {
            return map;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config. getKeys(false)) {
                map.put(key, config.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}