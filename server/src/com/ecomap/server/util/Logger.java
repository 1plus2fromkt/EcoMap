package com.ecomap.server.util;

public class Logger {
    public static void log(Object msg) {
        System.out.println(msg);
    }

    public static void err(Object msg) {
        System.err.println(msg);
    }
}
