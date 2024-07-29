package com.atlant1c.utils;

import java.util.Base64;

public class Base64Utils {
    public static String encodeToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
