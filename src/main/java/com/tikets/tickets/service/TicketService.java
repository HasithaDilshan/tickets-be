package com.tikets.tickets.service;

import com.tikets.tickets.model.*;
import com.tikets.tickets.repository.*;
import com.tikets.tickets.util.LoggingUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TicketService implements TicketPool.TicketPoolListener {

    private final TicketPool ticketPool;
    private final VendorService vendorService;
    private final CustomerService customerService;
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;
    private boolean isSimulationRunning = false;

    private Configuration currentConfig;
    private List<Thread> activeThreads = new CopyOnWriteArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public TicketService(TicketPool ticketPool, VendorService vendorService, CustomerService customerService,
            TicketRepository ticketRepository, CustomerRepository customerRepository,
            VendorRepository vendorRepository) {
        this.ticketPool = ticketPool;
        this.vendorService = vendorService;
        this.customerService = customerService;
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.vendorRepository = vendorRepository;

        this.ticketPool.addListener(this);
    }

    public void configure(Configuration config) {
        this.currentConfig = config;
        ticketPool.setMaxCapacity(config.getMaxTicketCapacity());
        ticketPool.setMaxCapacity(config.getMaxTicketCapacity());
        String configMessage = String.format(
                "Configuration updated:\n" +
                        "Max Ticket Capacity: %d\n" +
                        "Ticket Release Rate: %d\n" +
                        "Customer Retrieval Rate: %d",
                config.getMaxTicketCapacity(),
                config.getTicketReleaseRate(),
                config.getCustomerRetrievalRate());
        sendLogUpdate(configMessage);
    }

    public void startSimulation() {
        lock.lock();
        try {
            if (currentConfig == null) {
                sendLogUpdate("Please configure the simulation first.");
                return;
            }
            if (isSimulationRunning) {
                sendLogUpdate("Simulation is already running.");
                return;
            }
            vendorService.startVendors(5, currentConfig.getTicketReleaseRate(),
                    currentConfig.getTicketReleaseRate(),
                    ticketPool);
            customerService.startCustomers(10, currentConfig.getCustomerRetrievalRate(),
                    ticketPool);
            activeThreads.addAll(vendorService.getVendorThreads());
            activeThreads.addAll(customerService.getCustomerThreads());

            isSimulationRunning = true;
            sendLogUpdate("Simulation Started");
            sendLogUpdate("Total vendors: " + vendorService.getVendorThreadsSize());
            sendLogUpdate("Total customers: " + customerService.getCustomerThreadsSize());
        } finally {
            lock.unlock();
        }
    }

    public void stopSimulation() {
        lock.lock();
        try {
            if (!isSimulationRunning) {
                sendLogUpdate("Simulation is not running.");
                return;
            }
            for (Thread thread : activeThreads) {
                thread.interrupt();
            }
            activeThreads.clear();
            vendorService.getVendorThreads().clear();
            customerService.getCustomerThreads().clear();
            isSimulationRunning = false;
            sendLogUpdate("Simulation Stopped");
        } finally {
            lock.unlock();
        }
    }

    public void addVendor() {
        lock.lock();
        try {
            if (!isSimulationRunning) {
                sendLogUpdate("Cannot add vendor. Simulation is not running.");
                return;
            }
            Thread vendorThread = vendorService.addVendor(currentConfig.getTicketReleaseRate(),
                    currentConfig.getTicketReleaseRate(), ticketPool);
            activeThreads.add(vendorThread);
            sendLogUpdate("Vendor added. Total vendors: " + vendorService.getVendorThreadsSize());
        } finally {
            lock.unlock();
        }
    }

    public void removeVendor() {
        lock.lock();
        try {
            if (!isSimulationRunning) {
                sendLogUpdate("Cannot remove vendor. Simulation is not running.");
                return;
            }
            if (vendorService.getVendorThreadsSize() == 0) {
                sendLogUpdate("No vendors to remove. Vendor list is empty.");
                return;
            }
            vendorService.removeVendor();
            if (!activeThreads.isEmpty()) {
                activeThreads.remove(activeThreads.size() - 1);
                sendLogUpdate("Vendor removed. Remaining vendors: " + vendorService.getVendorThreadsSize());
            }
        } finally {
            lock.unlock();
        }
    }

    public void addCustomer(boolean isVip) {
        lock.lock();
        try {
            if (!isSimulationRunning) {
                sendLogUpdate("Cannot add customer. Simulation is not running.");
                return;
            }
            Thread customerThread = customerService.addCustomer(currentConfig.getCustomerRetrievalRate(), ticketPool,
                    isVip);
            activeThreads.add(customerThread);
            sendLogUpdate("Customer added (VIP: " + isVip + "). Total customers: "
                    + customerService.getCustomerThreadsSize());
        } finally {
            lock.unlock();
        }
    }

    public void removeCustomer() {
        lock.lock();
        try {
            if (!isSimulationRunning) {
                sendLogUpdate("Cannot remove customer. Simulation is not running.");
                return;
            }
            if (customerService.getCustomerThreadsSize() == 0) {
                sendLogUpdate("No customers to remove. Customer list is empty.");
                return;
            }
            customerService.removeCustomer();
            if (!activeThreads.isEmpty()) {
                activeThreads.remove(activeThreads.size() - 1);
            }
            sendLogUpdate("Customer removed. Remaining customers: " + customerService.getCustomerThreadsSize());
        } finally {
            lock.unlock();
        }
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public void onTicketPoolChange(String logMessage, int ticketCount) {
        sendLogUpdate(logMessage);
        sendTicketCountUpdate(ticketCount);
    }

    private void sendLogUpdate(String message) {
        messagingTemplate.convertAndSend("/tickets/logs", message);
        LoggingUtil.logInfo(message);
    }

    private void sendTicketCountUpdate(int count) {
        messagingTemplate.convertAndSend("/tickets/ticketCount", count);
    }

    public int getTicketCount() {
        return ticketPool.getAvailableTickets();
    }
}