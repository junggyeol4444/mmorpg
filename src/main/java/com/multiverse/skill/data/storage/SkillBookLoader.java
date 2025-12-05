package com.multiverse.skill.data.storage;

import com.multiverse.skill.SkillCore;
import com.multiverse. skill.data.models.*;
import com.multiverse.skill.data.enums.SkillBookType;
import org. bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * 스킬 북 데이터 로더
 */
public class SkillBookLoader {

    private final SkillCore plugin;
    private final DataStorage storage;
    private final Map<String, SkillBook> bookCache;

    public SkillBookLoader(SkillCore plugin, DataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.bookCache = new HashMap<>();
    }

    /**
     * 모든 스킬 북 로드
     */
    public List<SkillBook> loadAllSkillBooks() {
        List<SkillBook> books = new ArrayList<>();
        File booksFolder = new File(plugin.getDataFolder(), "skill-books");

        if (!booksFolder.exists()) {
            plugin.getLogger().warning("⚠️ 스킬 북 폴더가 없습니다: " + booksFolder.getPath());
            return books;
        }

        File[] bookFiles = booksFolder. listFiles((d, name) -> name.endsWith(".yml"));
        if (bookFiles == null) {
            return books;
        }

        for (File bookFile : bookFiles) {
            try {
                SkillBook book = loadBookFromFile(bookFile);
                if (book != null) {
                    books.add(book);
                    bookCache.put(book.getBookId(), book);
                }
            } catch (Exception e) {
                plugin. getLogger().warning("스킬 북 로드 실패: " + bookFile.getName());
                e.printStackTrace();
            }
        }

        return books;
    }

    /**
     * 파일에서 스킬 북 로드
     */
    private SkillBook loadBookFromFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        SkillBook book = new SkillBook();
        book.setBookId(config.getString("id", file.getName().replace(".yml", "")));
        book.setSkillId(config.getString("skill-id", ""));
        book.setDescription(config.getString("description", ""));

        // 타입
        String typeString = config.getString("type", "LEARN");
        try {
            book.setType(SkillBookType.valueOf(typeString));
        } catch (IllegalArgumentException e) {
            book.setType(SkillBookType. LEARN);
        }

        // 스킬 레벨
        book.setSkillLevel(config.getInt("skill-level", 1));

        // 요구사항
        book.setRequiredLevel(config.getInt("required-level", 1));
        book.setRequiredClass(config.getString("required-class", ""));

        // 특성
        book.setPermanent(config.getBoolean("permanent", false));
        book.setSoulbound(config.getBoolean("soulbound", false));

        return book;
    }

    /**
     * 스킬 북 조회
     */
    public SkillBook getBook(String bookId) {
        return bookCache.getOrDefault(bookId, null);
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        bookCache.clear();
    }
}