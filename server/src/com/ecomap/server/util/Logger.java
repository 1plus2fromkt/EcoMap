package com.ecomap.server.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Logger {
    public static void log(Object msg) {
        System.out.println(msg + "\t\t" + now());
    }

    private static String now() {
        LocalDateTime time = LocalDateTime.now();
        return time.plus(8, ChronoUnit.HOURS).toString();
    }

    public static void err(Object msg) {
        System.err.println(msg + "\t\t" + now());
    }
}
