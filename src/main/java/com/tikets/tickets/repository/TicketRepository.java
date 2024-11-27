package com.tikets.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tikets.tickets.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
}
