package com.tikets.tickets.util;

import java.util.logging.Logger;

public class LoggingUtil {
    private static final Logger logger = Logger.getLogger(LoggingUtil.class.getName());

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logError(String message, Throwable throwable) {
        logger.severe(message);
        throwable.printStackTrace();
    }
}
