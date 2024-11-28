package com.tikets.tickets.service;

import org.springframework.stereotype.Service;

import com.tikets.tickets.model.Customer;
import com.tikets.tickets.model.TicketPool;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    private List<Thread> customerThreads = new ArrayList<>();

    public void startCustomers(int numCustomers, int retrievalInterval, TicketPool ticketPool) {
        for (int i = 0; i < numCustomers; i++) {
            Customer customer = new Customer(i, retrievalInterval, false, ticketPool);
            Thread thread = new Thread(customer);
            customerThreads.add(thread);
            thread.start();
            // ticketPool.queueCustomer(customer);
        }
    }

    public Thread addCustomer(int retrievalInterval, TicketPool ticketPool, boolean isVip) {
        Customer customer = new Customer(customerThreads.size(), retrievalInterval, isVip, ticketPool);
        Thread thread = new Thread(customer);
        customerThreads.add(thread);
        thread.start();
        // ticketPool.queueCustomer(customer);
        return thread;
    }

    public void removeCustomer() {
        if (!customerThreads.isEmpty()) {
            Thread thread = customerThreads.remove(customerThreads.size() - 1);
            thread.interrupt();
        }
    }

    public List<Thread> getCustomerThreads() {
        return customerThreads;
    }

    public int getCustomerThreadsSize() {
        return customerThreads.size();
    }
}