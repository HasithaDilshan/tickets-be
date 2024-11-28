package com.tikets.tickets.model;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

@Component
public class TicketPool {
    // private final PriorityBlockingQueue<Customer> customerQueue;
    private final BlockingQueue<Ticket> tickets;
    private int maxCapacity;
    private final ReentrantLock lock = new ReentrantLock();
    // private final ExecutorService customerExecutor;

    private final List<TicketPoolListener> listeners = new CopyOnWriteArrayList<>();

    public TicketPool() {
        // this.customerQueue = new PriorityBlockingQueue<>(100,
        // Comparator.comparing(Customer::isVip).reversed()
        // .thenComparing(c -> c.hashCode()));
        this.tickets = new LinkedBlockingQueue<>();
        // this.customerExecutor = Executors.newSingleThreadExecutor();
        // startCustomerProcessor();
    }

    // private void startCustomerProcessor() {
    // customerExecutor.submit(() -> {
    // while (true) {
    // try {
    // Customer customer = customerQueue.take();
    // CompletableFuture.runAsync(() -> removeTicket(customer));
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // break;
    // }
    // }
    // });
    // }

    // public void queueCustomer(Customer customer) {
    // customerQueue.offer(customer);
    // notifyListeners("Queued customer" + (customer.isVip() ? "(VIP) " : " ") + ":
    // " + customer.getId());
    // }

    // public void dequeueCustomer(Customer customer) {
    // if (customerQueue.remove(customer)) {
    // notifyListeners("Dequeued customer: " + customer.getId());
    // }
    // }

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
                        "Customer" + (customer.isVip() ? "(VIP): " : ": ") + customer.getId() + " purchased ticket: "
                                + ticket.getTicketId());
                return ticket;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    // private void notifyNextCustomer() {
    // Customer customer = customerQueue.poll();
    // if (customer != null) {
    // CompletableFuture.runAsync(customer::run);
    // }
    // }

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