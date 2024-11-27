package com.tikets.tickets.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Vendor implements Runnable {
    @Id
    private int vendorId;
    private int ticketsPerRelease;
    private int releaseInterval;
    private int nextTicketId = 1;

    @Transient
    private TicketPool ticketPool;
    
    public Vendor(int vendorId, int ticketsPerRelease, int releaseInterval, TicketPool ticketPool) {
        this.vendorId = vendorId;
        this.ticketsPerRelease = ticketsPerRelease;
        this.releaseInterval = releaseInterval;
        this.ticketPool = ticketPool;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (int i = 0; i < ticketsPerRelease; i++) {
                    Ticket ticket = new Ticket(nextTicketId++);
                    ticketPool.addTicket(ticket);
                    System.out.println("Vendor " + vendorId + " added ticket " + ticket.getTicketId());
                }
                Thread.sleep(releaseInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
