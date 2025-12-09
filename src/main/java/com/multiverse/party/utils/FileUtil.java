package com.multiverse.party.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 파일 처리 유틸리티
 */
public class FileUtil {

    /** 파일 내용 읽기 */
    public static String readFile(Path path) throws IOException {
        return Files.readString(path);
    }

    /** 파일 내용 쓰기 */
    public static void writeFile(Path path, String data) throws IOException {
        Files.writeString(path, data);
    }

    /** 디렉토리 생성 */
    public static void createDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /** 파일 삭제 */
    public static boolean deleteFile(Path path) throws IOException {
        return Files.deleteIfExists(path);
    }
}