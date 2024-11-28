package com.tikets.tickets.model;

import com.tikets.tickets.interfaces.ThreadLifecycleListener;
import com.tikets.tickets.service.LogService;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Vendor implements Runnable {
    @Id
    private int vendorId;
    private int releaseInterval;
    private int nextTicketId = 1;
    private int totalTickets;

    @Transient
    private TicketPool ticketPool;
    @Transient
    private final LogService logService;
    @Transient
    private ThreadLifecycleListener<Vendor> lifecycleListener;

    public void setLifecycleListener(ThreadLifecycleListener<Vendor> listener) {
        this.lifecycleListener = listener;
    }

    public Vendor(int vendorId, int totalTickets, int releaseInterval, TicketPool ticketPool, LogService logService) {
        this.vendorId = vendorId;
        this.releaseInterval = releaseInterval;
        this.ticketPool = ticketPool;
        this.totalTickets = totalTickets;
        this.logService = logService;
    }

    @Override
    public void run() {
        if (lifecycleListener != null) {
            lifecycleListener.onThreadStart(this);
        }
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (nextTicketId > totalTickets) {
                    logService.logInfo("Vendor " + vendorId + " has run out of tickets. Terminating...");
                    Thread.currentThread().interrupt();
                    break;
                }
                Ticket ticket = new Ticket(
                        Integer.parseInt(Integer.toString(vendorId) + Integer.toString(nextTicketId++)));
                ticketPool.addTicket(ticket, this);
                Thread.sleep(releaseInterval);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // logService.logError(e.getMessage(), e);
        } finally {
            if (lifecycleListener != null) {
                lifecycleListener.onThreadExit(this);
            }
        }
    }

    public int getVendorId() {
        return vendorId;
    }
}
