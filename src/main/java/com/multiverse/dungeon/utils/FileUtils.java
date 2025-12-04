package com.multiverse.  dungeon.utils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 파일 유틸리티
 */
public class FileUtils {

    /**
     * 디렉토리 생성
     *
     * @param path 경로
     * @return 생성 또는 이미 존재하면 true
     */
    public static boolean createDirectory(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        try {
            Files.createDirectories(Paths.get(path));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 파일 생성
     *
     * @param path 경로
     * @return 생성되면 true
     */
    public static boolean createFile(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        try {
            File file = new File(path);
            if (file.exists()) {
                return true;
            }
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 파일 삭제
     *
     * @param path 경로
     * @return 삭제되면 true
     */
    public static boolean deleteFile(String path) {
        if (path == null || path. isEmpty()) {
            return false;
        }

        try {
            return Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 디렉토리 삭제 (재귀)
     *
     * @param path 경로
     * @return 삭제되면 true
     */
    public static boolean deleteDirectory(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        try {
            Path directory = Paths.get(path);
            Files.walk(directory)
                .sorted(java.util. Comparator.reverseOrder())
                .forEach(filePath -> {
                    try {
                        Files.delete(filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 파일 존재 여부 확인
     *
     * @param path 경로
     * @return 존재하면 true
     */
    public static boolean exists(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        return Files.exists(Paths.get(path));
    }

    /**
     * 파일 읽기
     *
     * @param path 경로
     * @return 파일 내용
     */
    public static String readFile(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 파일 쓰기
     *
     * @param path 경로
     * @param content 내용
     * @return 성공하면 true
     */
    public static boolean writeFile(String path, String content) {
        if (path == null || path.isEmpty() || content == null) {
            return false;
        }

        try {
            Files.write(Paths. get(path), content.getBytes());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 파일에 내용 추가
     *
     * @param path 경로
     * @param content 추가할 내용
     * @return 성공하면 true
     */
    public static boolean appendFile(String path, String content) {
        if (path == null || path.isEmpty() || content == null) {
            return false;
        }

        try {
            Files.write(Paths.get(path), content. getBytes(), StandardOpenOption. APPEND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 파일 라인별로 읽기
     *
     * @param path 경로
     * @return 라인 리스트
     */
    public static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();

        if (path == null || path.isEmpty()) {
            return lines;
        }

        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * 파일 복사
     *
     * @param source 원본 경로
     * @param destination 대상 경로
     * @return 성공하면 true
     */
    public static boolean copyFile(String source, String destination) {
        if (source == null || destination == null) {
            return false;
        }

        try {
            Files.copy(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 디렉토리의 모든 파일 목록
     *
     * @param path 경로
     * @return 파일 리스트
     */
    public static List<File> listFiles(String path) {
        List<File> files = new ArrayList<>();

        if (path == null || path.isEmpty()) {
            return files;
        }

        try {
            File directory = new File(path);
            if (directory.isDirectory()) {
                File[] fileArray = directory.listFiles();
                if (fileArray != null) {
                    for (File file : fileArray) {
                        files.add(file);
                    }
                }
            }
        } catch (Exception e) {
            e. printStackTrace();
        }

        return files;
    }

    /**
     * 파일 크기 가져오기
     *
     * @param path 경로
     * @return 파일 크기 (바이트)
     */
    public static long getFileSize(String path) {
        if (path == null || path.isEmpty()) {
            return -1;
        }

        try {
            return Files.size(Paths.get(path));
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 파일 이름만 추출
     *
     * @param path 경로
     * @return 파일 이름
     */
    public static String getFileName(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        Path filePath = Paths.get(path);
        return filePath. getFileName().toString();
    }

    /**
     * 파일 확장자 추출
     *
     * @param path 경로
     * @return 확장자
     */
    public static String getFileExtension(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        String fileName = getFileName(path);
        if (fileName == null || !  fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf(". ") + 1);
    }

    /**
     * 상대 경로 생성
     *
     * @param parent 부모 경로
     * @param child 자식 경로
     * @return 합쳐진 경로
     */
    public static String joinPath(String parent, String child) {
        if (parent == null || child == null) {
            return null;
        }

        return Paths.get(parent, child).toString();
    }
}