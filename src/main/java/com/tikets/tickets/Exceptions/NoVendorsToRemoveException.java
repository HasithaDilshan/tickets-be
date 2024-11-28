package com.tikets.tickets.Exceptions;

public class NoVendorsToRemoveException extends RuntimeException {
    public NoVendorsToRemoveException() {
        super("No vendors to remove. Vendor list is empty.");
    }
}
