package com.tikets.tickets.service;

import org.springframework.stereotype.Service;

import com.tikets.tickets.interfaces.LogListener;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {
    private final List<LogListener> listeners = new ArrayList<>();

    public void addListener(LogListener listener) {
        listeners.add(listener);
    }

    public void removeListener(LogListener listener) {
        listeners.remove(listener);
    }

    public void logInfo(String message) {
        for (LogListener listener : listeners) {
            listener.logInfo(message);
        }
    }

    public void logError(String message, Throwable throwable) {
        for (LogListener listener : listeners) {
            listener.logError(message, throwable);
        }
    }
}
