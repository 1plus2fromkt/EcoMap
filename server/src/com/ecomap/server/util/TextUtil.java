package com.ecomap.server.util;

public class TextUtil {

    public static String removeIllegalSymbols(String s) {
        if (s.isEmpty()) {
            return s;
        }
        return s.replaceAll("[\']", "");
    }

}
