package com.multiverse.playerdata.utils;

import java.io.*;

public class FileUtil {

    /**
     * 디렉토리 전체 복사
     */
    public static void copyDirectory(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) dest.mkdirs();
            String[] children = src.list();
            if (children == null) return;
            for (String fileName : children) {
                File srcFile = new File(src, fileName);
                File destFile = new File(dest, fileName);
                copyDirectory(srcFile, destFile);
            }
        } else {
            copyFile(src, dest);
        }
    }

    /**
     * 파일 복사
     */
    public static void copyFile(File src, File dest) throws IOException {
        try (
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest)
        ) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }
}