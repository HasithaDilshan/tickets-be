package com.tikets.tickets.Exceptions;

public class NoCustomersToRemoveException extends RuntimeException {
    public NoCustomersToRemoveException() {
        super("No customers to remove. Customer list is empty.");
    }
}
