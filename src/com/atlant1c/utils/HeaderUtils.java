package com.atlant1c.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HeaderUtils {

    // 默认文件路径
    private static final Logger LOGGER = Logger.getLogger(HeaderUtils.class.getName());
    private static final String DEFAULT_HEADER_FILE = "headers.bin";

    public static Map<String, String> readHeadersFromFile() {
        return readHeadersFromFile(DEFAULT_HEADER_FILE);
    }

    public static Map<String, String> readHeadersFromFile(String filePath) {
        Map<String, String> headers = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                int colonIndex = line.indexOf(':');
                if (colonIndex != -1) {
                    String key = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    headers.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headers;
    }
}
