package com.multiverse.combat.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file. Paths;
import com.multiverse.combat.CombatCore;

public class FileUtil {
    
    private final CombatCore plugin;
    
    public FileUtil(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    public boolean createFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                return folder.mkdirs();
            }
            return true;
        } catch (SecurityException e) {
            plugin. getLogger().warning("폴더 생성 실패: " + folderPath + " - " + e.getMessage());
            return false;
        }
    }
    
    public boolean createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile(). mkdirs()) {
                    plugin.getLogger().warning("부모 폴더 생성 실패: " + filePath);
                    return false;
                }
            }
            if (!file.exists()) {
                return file.createNewFile();
            }
            return true;
        } catch (IOException e) {
            plugin.getLogger().warning("파일 생성 실패: " + filePath + " - " + e. getMessage());
            return false;
        } catch (SecurityException e) {
            plugin.getLogger().warning("파일 생성 권한 없음: " + filePath);
            return false;
        }
    }
    
    public boolean fileExists(String filePath) {
        try {
            return new File(filePath).exists();
        } catch (SecurityException e) {
            plugin.getLogger(). warning("파일 접근 권한 없음: " + filePath);
            return false;
        }
    }
    
    public boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
            return false;
        } catch (SecurityException e) {
            plugin.getLogger().warning("파일 삭제 권한 없음: " + filePath);
            return false;
        }
    }
    
    public boolean deleteFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (! folder.exists()) {
                return false;
            }
            
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file.getAbsolutePath());
                    } else {
                        file.delete();
                    }
                }
            }
            return folder.delete();
        } catch (SecurityException e) {
            plugin.getLogger().warning("폴더 삭제 권한 없음: " + folderPath);
            return false;
        }
    }
    
    public long getFileSize(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.length();
            }
            return 0;
        } catch (SecurityException e) {
            plugin.getLogger().warning("파일 크기 조회 권한 없음: " + filePath);
            return 0;
        }
    }
    
    public boolean copyFile(String sourcePath, String destinationPath) {
        try {
            Files.copy(Paths.get(sourcePath), Paths.get(destinationPath));
            return true;
        } catch (IOException e) {
            plugin.getLogger().warning("파일 복사 실패: " + sourcePath + " -> " + destinationPath + " - " + e.getMessage());
            return false;
        }
    }
    
    public boolean renameFile(String oldPath, String newPath) {
        try {
            File oldFile = new File(oldPath);
            File newFile = new File(newPath);
            if (oldFile.exists()) {
                return oldFile.renameTo(newFile);
            }
            return false;
        } catch (SecurityException e) {
            plugin. getLogger().warning("파일 이름 변경 권한 없음: " + oldPath);
            return false;
        }
    }
    
    public File[] listFiles(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                return folder. listFiles();
            }
            return new File[0];
        } catch (SecurityException e) {
            plugin.getLogger().warning("폴더 목록 조회 권한 없음: " + folderPath);
            return new File[0];
        }
    }
    
    public boolean clearFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                return false;
            }
            
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file.getAbsolutePath());
                    } else {
                        file.delete();
                    }
                }
            }
            return true;
        } catch (SecurityException e) {
            plugin.getLogger().warning("폴더 정리 권한 없음: " + folderPath);
            return false;
        }
    }
}