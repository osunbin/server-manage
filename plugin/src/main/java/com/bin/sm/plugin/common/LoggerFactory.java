package com.bin.sm.plugin.common;

import java.util.logging.Logger;

public class LoggerFactory {

    private static volatile Logger defaultLogger;


    public static Logger getLogger() {
        // Avoid obtaining logs repeatedly
        if (defaultLogger == null) {
            synchronized (LoggerFactory.class) {
                if (defaultLogger == null) {
                    defaultLogger = Logger.getLogger("plugin");
                }
            }
        }
        return defaultLogger;
    }
}
