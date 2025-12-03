package com.multiverse.npcai.utils;

import java.io.File;
import java.io.IOException;

/**
 * 파일 관련 유틸리티 기능을 제공하는 클래스
 */
public class FileUtil {

    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static boolean createFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return file.createNewFile();
        }
        return false;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}