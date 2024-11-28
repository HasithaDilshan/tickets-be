package com.tikets.tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tikets.tickets.model.Customer;
import com.tikets.tickets.model.TicketPool;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    private List<Thread> customerThreads = new ArrayList<>();

    private final LogService logService;

    @Autowired
    public CustomerService(LogService logService) {
        this.logService = logService;
    }

    public void seedCustomers(int numCustomers, int retrievalInterval, TicketPool ticketPool) {
        for (int i = 0; i < numCustomers; i++) {
            addCustomer(retrievalInterval, ticketPool);
        }
    }

    public Thread addCustomer(int retrievalInterval, TicketPool ticketPool) {
        Customer customer = new Customer(customerThreads.size(), retrievalInterval, ticketPool, logService);
        Thread thread = new Thread(customer);
        customerThreads.add(thread);
        thread.start();
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