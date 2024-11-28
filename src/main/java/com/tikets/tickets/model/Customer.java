package com.tikets.tickets.model;

import com.tikets.tickets.service.LogService;

import jakarta.persistence.*;

@Entity
public class Customer implements Runnable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerId;
    private int retrievalInterval;

    @Transient
    private TicketPool ticketPool;
    @Transient
    private final LogService logService;

    public Customer(int customerId, int retrievalInterval, TicketPool ticketPool, LogService logService) {
        this.customerId = customerId;
        this.retrievalInterval = retrievalInterval;
        this.ticketPool = ticketPool;
        this.logService = logService;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ticketPool.removeTicket(this); // Returned ticket from pool can be used in further logic if needed
                Thread.sleep(retrievalInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // logService.logError(e.getMessage(), e);
            }
        }
    }

    public int getId() {
        return customerId;
    }
}