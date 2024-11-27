package com.tikets.tickets.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Ticket {
    @Id
    private int ticketId;

    public Ticket(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getTicketId() {
        return ticketId;
    }
}