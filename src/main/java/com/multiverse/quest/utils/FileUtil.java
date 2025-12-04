package com.multiverse.quest.utils;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 파일 유틸리티
 * 파일 및 디렉토리 관리 기능을 제공합니다.
 */
public class FileUtil {
    private final JavaPlugin plugin;
    private final File dataFolder;

    /**
     * 생성자
     * @param plugin 플러그인 인스턴스
     */
    public FileUtil(JavaPlugin plugin) {
        this.  plugin = plugin;
        this.  dataFolder = plugin.getDataFolder();
    }

    // ============ Directory Management ============

    /**
     * 디렉토리 생성
     */
    public boolean createDirectory(String path) {
        try {
            File directory = new File(dataFolder, path);
            if (!directory.exists()) {
                return directory.mkdirs();
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("디렉토리 생성 실패 (" + path + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * 디렉토리 존재 여부 확인
     */
    public boolean directoryExists(String path) {
        File directory = new File(dataFolder, path);
        return directory.exists() && directory.isDirectory();
    }

    /**
     * 디렉토리 생성 (절대 경로)
     */
    public boolean createDirectoryAbsolute(String path) {
        try {
            File directory = new File(path);
            if (!directory.exists()) {
                return directory.mkdirs();
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("디렉토리 생성 실패 (" + path + "): " + e.getMessage());
            return false;
        }
    }

    // ============ File Management ============

    /**
     * 파일 생성
     */
    public boolean createFile(String path) {
        try {
            File file = new File(dataFolder, path);
            
            // 부모 디렉토리 생성
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (! file.exists()) {
                return file.createNewFile();
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("파일 생성 실패 (" + path + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(String path) {
        File file = new File(dataFolder, path);
        return file.exists() && file.isFile();
    }

    /**
     * 파일 삭제
     */
    public boolean deleteFile(String path) {
        try {
            File file = new File(dataFolder, path);
            return file.delete();
        } catch (Exception e) {
            plugin.getLogger().warning("파일 삭제 실패 (" + path + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * 파일 크기 반환 (바이트)
     */
    public long getFileSize(String path) {
        File file = new File(dataFolder, path);
        if (file.exists() && file.isFile()) {
            return file.length();
        }
        return 0;
    }

    /**
     * 파일 크기 포맷 (KB, MB 등)
     */
    public String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        
        return String.format("%.1f %s", bytes / Math. pow(1024, digitGroups), units[digitGroups]);
    }

    // ============ File I/O ============

    /**
     * 파일 읽기
     */
    public String readFile(String path) {
        try {
            File file = new File(dataFolder, path);
            if (!file.exists()) {
                return null;
            }

            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            plugin.getLogger().warning("파일 읽기 실패 (" + path + "): " + e.getMessage());
            return null;
        }
    }

    /**
     * 파일 쓰기
     */
    public boolean writeFile(String path, String content) {
        try {
            File file = new File(dataFolder, path);
            
            // 부모 디렉토리 생성
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            Files.write(file.toPath(), content.getBytes());
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("파일 쓰기 실패 (" + path + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * 파일에 내용 추가
     */
    public boolean appendToFile(String path, String content) {
        try {
            File file = new File(dataFolder, path);
            
            if (!file.exists()) {
                return writeFile(path, content);
            }

            String existingContent = readFile(path);
            if (existingContent != null) {
                return writeFile(path, existingContent + "\n" + content);
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("파일 추가 실패 (" + path + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * 파일을 라인 리스트로 읽기
     */
    public List<String> readFileAsLines(String path) {
        try {
            File file = new File(dataFolder, path);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            return Files.readAllLines(file. toPath());
        } catch (Exception e) {
            plugin. getLogger().  warning("파일 읽기 실패 (" + path + "): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 파일 복사
     */
    public boolean copyFile(String sourcePath, String destPath) {
        try {
            File sourceFile = new File(dataFolder, sourcePath);
            File destFile = new File(dataFolder, destPath);

            if (! sourceFile.exists()) {
                return false;
            }

            // 대상 부모 디렉토리 생성
            File destParent = destFile.getParentFile();
            if (destParent != null && !destParent.exists()) {
                destParent.mkdirs();
            }

            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            plugin.getLogger(). warning("파일 복사 실패 (" + sourcePath + " -> " + destPath + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * 파일 이동
     */
    public boolean moveFile(String sourcePath, String destPath) {
        try {
            File sourceFile = new File(dataFolder, sourcePath);
            File destFile = new File(dataFolder, destPath);

            if (!sourceFile.exists()) {
                return false;
            }

            // 대상 부모 디렉토리 생성
            File destParent = destFile.getParentFile();
            if (destParent != null && !destParent. exists()) {
                destParent.mkdirs();
            }

            Files.move(sourceFile.toPath(), destFile.toPath(), StandardCopyOption. REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("파일 이동 실패 (" + sourcePath + " -> " + destPath + "): " + e.getMessage());
            return false;
        }
    }

    // ============ Directory Operations ============

    /**
     * 디렉토리의 모든 파일 목록
     */
    public List<File> listFiles(String path) {
        List<File> files = new ArrayList<>();

        try {
            File directory = new File(dataFolder, path);
            
            if (! directory.exists() || !directory.isDirectory()) {
                return files;
            }

            File[] fileArray = directory.listFiles();
            if (fileArray != null) {
                files.addAll(Arrays.asList(fileArray));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("파일 목록 조회 실패 (" + path + "): " + e. getMessage());
        }

        return files;
    }

    /**
     * 디렉토리의 모든 파일을 필터링하여 반환
     */
    public List<File> listFilesByExtension(String path, String extension) {
        List<File> filteredFiles = new ArrayList<>();

        try {
            File directory = new File(dataFolder, path);
            
            if (!directory. exists() || !directory.isDirectory()) {
                return filteredFiles;
            }

            File[] fileArray = directory.listFiles((dir, name) -> name.endsWith("." + extension));
            if (fileArray != null) {
                filteredFiles.addAll(Arrays.asList(fileArray));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("파일 목록 조회 실패 (" + path + "): " + e. getMessage());
        }

        return filteredFiles;
    }

    /**
     * 디렉토리 삭제 (내용 포함)
     */
    public boolean deleteDirectory(String path) {
        try {
            File directory = new File(dataFolder, path);
            return deleteDirectoryRecursively(directory);
        } catch (Exception e) {
            plugin.getLogger().warning("디렉토리 삭제 실패 (" + path + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * 재귀적으로 디렉토리 삭제
     */
    private boolean deleteDirectoryRecursively(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectoryRecursively(file);
                }
            }
        }
        return directory.delete();
    }

    // ============ Backup Operations ============

    /**
     * 파일 백업
     */
    public boolean backupFile(String filePath) {
        try {
            File originalFile = new File(dataFolder, filePath);
            if (!originalFile.exists()) {
                return false;
            }

            String backupPath = filePath + ".backup";
            return copyFile(filePath, backupPath);
        } catch (Exception e) {
            plugin.getLogger().warning("파일 백업 실패 (" + filePath + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * 백업에서 복원
     */
    public boolean restoreFromBackup(String filePath) {
        try {
            String backupPath = filePath + ". backup";
            return copyFile(backupPath, filePath);
        } catch (Exception e) {
            plugin.getLogger().warning("백업 복원 실패 (" + filePath + "): " + e.getMessage());
            return false;
        }
    }

    // ============ Statistics ============

    /**
     * 데이터 폴더 크기 반환
     */
    public long getDirectorySize(File directory) {
        long size = 0;

        if (directory.isFile()) {
            return directory.length();
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                size += getDirectorySize(file);
            }
        }

        return size;
    }

    /**
     * 데이터 폴더 크기 반환 (포맷됨)
     */
    public String getFormattedDirectorySize() {
        long size = getDirectorySize(dataFolder);
        return formatFileSize(size);
    }

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 파일 유틸리티 상태 ===§r\n");
        sb.append("§7데이터 폴더: §f"). append(dataFolder.getPath()).append("\n");
        sb.append("§7폴더 크기: §f").append(getFormattedDirectorySize()).append("\n");

        return sb.toString();
    }

    // ============ Getters ============

    /**
     * 데이터 폴더 반환
     */
    public File getDataFolder() {
        return dataFolder;
    }

    /**
     * 플러그인 인스턴스 반환
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }
}