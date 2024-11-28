package com.tikets.tickets.service;

import org.springframework.stereotype.Service;

import com.tikets.tickets.interfaces.ThreadLifecycleListener;
import com.tikets.tickets.model.TicketPool;
import com.tikets.tickets.model.Vendor;

import java.util.ArrayList;
import java.util.List;

@Service
public class VendorService implements ThreadLifecycleListener<Vendor> {
    private List<Thread> vendorThreads = new ArrayList<>();
    private final LogService logService;

    public VendorService(LogService logService) {
        this.logService = logService;
    }

    @Override
    public void onThreadStart(Vendor vendor) {
        logService.logInfo("Vendor " + vendor.getVendorId() + " thread started.");
    }

    @Override
    public void onThreadExit(Vendor vendor) {
        vendorThreads.removeIf(thread -> !thread.isAlive());
        logService.logInfo("Vendor " + vendor.getVendorId() + " thread exited.");
    }

    public void seedVendors(int numVendors, int releaseInterval, int totalTickets, TicketPool ticketPool) {
        for (int i = 0; i < numVendors; i++) {
            addVendor(releaseInterval, totalTickets, ticketPool);
        }
    }

    public Thread addVendor(int releaseInterval, int totalTickets, TicketPool ticketPool) {
        Vendor vendor = new Vendor(vendorThreads.size(), totalTickets, releaseInterval, ticketPool, logService);
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