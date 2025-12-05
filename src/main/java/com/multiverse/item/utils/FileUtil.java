package com. multiverse.item.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file. Paths;
import java.util. ArrayList;
import java.util. List;

public class FileUtil {
    
    /**
     * 폴더 생성
     */
    public static boolean createDirectory(String path) {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                return dir.mkdirs();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 파일 생성
     */
    public static boolean createFile(String path) {
        try {
            File file = new File(path);
            if (! file.exists()) {
                file.getParentFile().mkdirs();
                return file.createNewFile();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 파일 존재 여부 확인
     */
    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }
    
    /**
     * 폴더 존재 여부 확인
     */
    public static boolean directoryExists(String path) {
        File dir = new File(path);
        return dir.exists() && dir. isDirectory();
    }
    
    /**
     * 파일 읽기
     */
    public static String readFile(String path) {
        try {
            Path filePath = Paths.get(path);
            return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
    
    /**
     * 파일을 라인 단위로 읽기
     */
    public static List<String> readFileLines(String path) {
        try {
            Path filePath = Paths.get(path);
            return Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * 파일 쓰기
     */
    public static boolean writeFile(String path, String content) {
        try {
            createDirectory(new File(path).getParent());
            Path filePath = Paths.get(path);
            Files.write(filePath, content. getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 파일에 내용 추가
     */
    public static boolean appendFile(String path, String content) {
        try {
            Path filePath = Paths.get(path);
            String currentContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            Files.write(filePath, (currentContent + content).getBytes(StandardCharsets. UTF_8));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 파일 삭제
     */
    public static boolean deleteFile(String path) {
        try {
            File file = new File(path);
            return file.delete();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 폴더 삭제 (재귀)
     */
    public static boolean deleteDirectory(String path) {
        try {
            File dir = new File(path);
            if (! dir.exists()) {
                return false;
            }
            
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file.getAbsolutePath());
                    } else {
                        file.delete();
                    }
                }
            }
            
            return dir.delete();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 파일 크기 (바이트)
     */
    public static long getFileSize(String path) {
        try {
            File file = new File(path);
            return file. length();
        } catch (Exception e) {
            return -1;
        }
    }
    
    /**
     * 파일 목록 가져오기
     */
    public static List<String> listFiles(String directoryPath) {
        List<String> files = new ArrayList<>();
        try {
            File directory = new File(directoryPath);
            File[] fileList = directory.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (file.isFile()) {
                        files.add(file.getName());
                    }
                }
            }
        } catch (Exception e) {
            // 오류 처리
        }
        return files;
    }
    
    /**
     * 파일 확장자 가져오기
     */
    public static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }
    
    /**
     * 파일명 (확장자 제외) 가져오기
     */
    public static String getFileNameWithoutExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(0, lastDot);
        }
        return filename;
    }
}