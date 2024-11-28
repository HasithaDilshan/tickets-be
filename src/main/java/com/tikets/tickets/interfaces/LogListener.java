package com.tikets.tickets.interfaces;

public interface LogListener {
    void logInfo(String message);

    void logError(String message, Throwable throwable);
}
