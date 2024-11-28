package com.tikets.tickets.Listeners;

import org.springframework.stereotype.Component;

import com.tikets.tickets.interfaces.LogListener;
import java.util.logging.Logger;

@Component
public class ConsoleLogListener implements LogListener {

    private static final Logger logger = Logger.getLogger(ConsoleLogListener.class.getName());

    @Override
    public void logInfo(String message) {
        logger.info(message);
    }

    @Override
    public void logError(String message, Throwable throwable) {
        logger.severe(message);
        throwable.printStackTrace();
    }

}
