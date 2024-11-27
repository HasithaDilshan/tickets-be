package com.tikets.tickets.model;

import java.util.concurrent.TimeUnit;

import jakarta.persistence.*;

@Entity
public class Customer implements Runnable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerId;
    private int retrievalInterval;
    private boolean isVip;

    @Transient
    private TicketPool ticketPool;

    public Customer(int customerId, int retrievalInterval, boolean isVip, TicketPool ticketPool) {
        this.customerId = customerId;
        this.retrievalInterval = retrievalInterval;
        this.isVip = isVip;
        this.ticketPool = ticketPool;
    }

    public boolean isVip() {
        return isVip;
    }

    public int getId() {
        return customerId;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Ticket ticket = ticketPool.removeTicket(isVip);
                if (ticket != null) {
                    System.out.println("Customer " + customerId + (isVip ? " (VIP)" : "") + " bought ticket "
                            + ticket.getTicketId());
                }
                Thread.sleep(retrievalInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}