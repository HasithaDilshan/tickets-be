package com.tikets.tickets.Listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.tikets.tickets.interfaces.LogListener;

@Component
public class WebsocketLogListener implements LogListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void logInfo(String message) {
        messagingTemplate.convertAndSend("/tickets/logs", message);
    }

    @Override
    public void logError(String message, Throwable throwable) {
        messagingTemplate.convertAndSend("/tickets/logs", "Error " + message);
    }

}