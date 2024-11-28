package com.tikets.tickets.Exceptions;

public class ConfigurationNotSetException extends RuntimeException {
    public ConfigurationNotSetException() {
        super("Configuration not set.");
    }
    
}
