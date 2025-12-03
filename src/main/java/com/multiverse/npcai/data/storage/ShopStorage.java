package com.multiverse.npcai.data.storage;

import com.multiverse.npcai.models.Shop;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 상점 데이터 YAML 파일 기반 저장/로딩
 */
public class ShopStorage {

    private final File baseDir;

    public ShopStorage(File baseDir) {
        this.baseDir = new File(baseDir, "shops");
        if (!this.baseDir.exists()) this.baseDir.mkdirs();
    }

    public Shop load(String shopId) {
        File file = new File(baseDir, shopId + ".yml");
        if (!file.exists()) return null;
        return Shop.fromYAML(YamlConfiguration.loadConfiguration(file));
    }

    public void save(Shop shop) {
        File file = new File(baseDir, shop.getShopId() + ".yml");
        YamlConfiguration yml = shop.toYAML();
        try { yml.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    public void delete(String shopId) {
        File file = new File(baseDir, shopId + ".yml");
        if (file.exists()) file.delete();
    }

    public List<Shop> loadAll() {
        List<Shop> result = new ArrayList<>();
        for (File f : Objects.requireNonNull(baseDir.listFiles())) {
            result.add(Shop.fromYAML(YamlConfiguration.loadConfiguration(f)));
        }
        return result;
    }
}