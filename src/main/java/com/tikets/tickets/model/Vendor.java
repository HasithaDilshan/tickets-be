package com.tikets.tickets.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Vendor implements Runnable {
    @Id
    private int vendorId;
    private int releaseInterval;
    private int nextTicketId = 1;

    @Transient
    private TicketPool ticketPool;

    public Vendor(int vendorId, int releaseInterval, TicketPool ticketPool) {
        this.vendorId = vendorId;
        this.releaseInterval = releaseInterval;
        this.ticketPool = ticketPool;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Ticket ticket = new Ticket(
                        Integer.parseInt(Integer.toString(vendorId) + Integer.toString(nextTicketId++)));
                ticketPool.addTicket(ticket, this);
                System.out.println("Vendor " + vendorId + " added ticket " + ticket.getTicketId());
                Thread.sleep(releaseInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getVendorId() {
        return vendorId;
    }
}
