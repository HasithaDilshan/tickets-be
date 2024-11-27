package com.tikets.tickets.service;

import org.springframework.stereotype.Service;

import com.tikets.tickets.model.TicketPool;
import com.tikets.tickets.model.Vendor;

import java.util.ArrayList;
import java.util.List;

@Service
public class VendorService {
    private List<Thread> vendorThreads = new ArrayList<>();

    public void startVendors(int numVendors, int ticketsPerRelease, int releaseInterval, TicketPool ticketPool) {
        for (int i = 0; i < numVendors; i++) {
            Vendor vendor = new Vendor(i, ticketsPerRelease, releaseInterval, ticketPool);
            Thread thread = new Thread(vendor);
            vendorThreads.add(thread);
            thread.start();
        }
    }

    public Thread addVendor(int ticketsPerRelease, int releaseInterval, TicketPool ticketPool) {
        Vendor vendor = new Vendor(vendorThreads.size(), ticketsPerRelease, releaseInterval, ticketPool);
        Thread thread = new Thread(vendor);
        vendorThreads.add(thread);
        thread.start();
        return thread;
    }

    public void removeVendor() {
        if (!vendorThreads.isEmpty()) {
            Thread thread = vendorThreads.remove(vendorThreads.size() - 1);
            thread.interrupt();
        }
    }

    public List<Thread> getVendorThreads() {
        return vendorThreads;
    }

    public int getVendorThreadsSize() {
        return vendorThreads.size();
    }
}