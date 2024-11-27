package com.tikets.tickets.model;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

@Component
public class TicketPool {
    private PriorityBlockingQueue<Customer> customerQueue;
    private BlockingQueue<Ticket> tickets;
    private int maxCapacity;
    private ReentrantLock lock = new ReentrantLock();

    private final List<TicketPoolListener> listeners = new CopyOnWriteArrayList<>();

    public TicketPool() {
        this.customerQueue = new PriorityBlockingQueue<>(11,
                Comparator.comparing(Customer::isVip).reversed()
                        .thenComparing(c -> c.hashCode()));
        this.tickets = new LinkedBlockingQueue<>();
    }

    public void addCustomer(Customer customer) {
        customerQueue.offer(customer);
    }

    public void removeCustomer(Customer customer) {
        customerQueue.remove(customer);
    }

    public void addTicket(Ticket ticket) throws InterruptedException {
        lock.lock();
        try {
            if (tickets.size() < maxCapacity) {
                tickets.put(ticket);
                notifyNextCustomer();
                notifyListeners("Ticket added: " + ticket.getTicketId());
            }
        } finally {
            lock.unlock();
        }
    }

    public Ticket removeTicket(boolean isVip) throws InterruptedException {
        lock.lock();
        try {
            Ticket ticket = tickets.poll();
            if (ticket != null) {
                notifyListeners("Ticket removed: " + ticket.getTicketId());
                return ticket;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    private void notifyNextCustomer() {
        Customer customer = customerQueue.poll();
        if (customer != null) {
            new Thread(() -> customer.run()).start();
        }
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
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