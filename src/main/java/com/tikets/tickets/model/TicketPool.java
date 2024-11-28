package com.tikets.tickets.model;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

@Component
public class TicketPool {
    private int maxCapacity;
    private final BlockingQueue<Ticket> tickets = new LinkedBlockingQueue<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final List<TicketPoolListener> listeners = new CopyOnWriteArrayList<>();

    public void addTicket(Ticket ticket, Vendor vendor) {
        lock.lock();
        try {
            if (tickets.size() < maxCapacity) {
                tickets.offer(ticket);
                notifyListeners("Vendor: " + vendor.getVendorId() + " added ticket: " + ticket.getTicketId());
            }
        } finally {
            lock.unlock();
        }
    }

    public Ticket removeTicket(Customer customer) {
        lock.lock();
        try {
            Ticket ticket = tickets.poll();
            if (ticket != null) {
                notifyListeners(
                        "Customer " + customer.getId() + " purchased ticket: "
                                + ticket.getTicketId());
                return ticket;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getAvailableTickets() {
        return tickets.size();
    }

    public void addListener(TicketPoolListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(String message) {
        for (TicketPoolListener listener : listeners) {
            listener.onTicketPoolChange(message, tickets.size());
        }
    }

    public interface TicketPoolListener {
        void onTicketPoolChange(String logMessage, int ticketCount);
    }
}