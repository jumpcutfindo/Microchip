package com.jumpcutfindo.microchip.helper;

public class StringUtils {
    public static String truncatedName(String name, int maxLength) {
        if (name.length() <= maxLength) return name;
        else return name.substring(0, maxLength) + "...";
    }
}
