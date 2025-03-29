package com.hussain.data_manipulation.util;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HexFormat;

public class FileUtil {
    public static String computeHash(InputStream inputStream,String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        return HexFormat.of().formatHex(digest.digest()); // Convert byte[] to hex string
    }
}
