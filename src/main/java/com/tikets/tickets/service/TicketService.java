package com.tikets.tickets.service;

import com.tikets.tickets.Exceptions.ConfigurationNotSetException;
import com.tikets.tickets.Exceptions.NoCustomersToRemoveException;
import com.tikets.tickets.Exceptions.NoVendorsToRemoveException;
import com.tikets.tickets.Exceptions.SimulationAlreadyRunningException;
import com.tikets.tickets.Exceptions.SimulationNotRunningException;
import com.tikets.tickets.model.*;

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
    private boolean isSimulationRunning = false;

    private Configuration currentConfig;
    private List<Thread> activeThreads = new CopyOnWriteArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private final LogService logService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public TicketService(TicketPool ticketPool, VendorService vendorService, CustomerService customerService,
            LogService logService) {
        this.ticketPool = ticketPool;
        this.vendorService = vendorService;
        this.customerService = customerService;
        this.ticketPool.addListener(this);
        this.logService = logService;
    }

    public void configure(Configuration config) {
        this.currentConfig = config;
        this.currentConfig.setTicketReleaseRate(1000 / config.getTicketReleaseRate());
        this.currentConfig.setCustomerRetrievalRate(1000 / config.getCustomerRetrievalRate());
        this.currentConfig = config;
        ticketPool.setMaxCapacity(config.getMaxTicketCapacity());
        String configMessage = String.format(
                "Configuration updated:\n" +
                        "Max Ticket Capacity: %d\n" +
                        "Ticket Release Rate: %d\n" +
                        "Customer Retrieval Rate: %d",
                config.getMaxTicketCapacity(),
                config.getTicketReleaseRate(),
                config.getCustomerRetrievalRate());
        logService.logInfo(configMessage);
    }

    public void startSimulation() {
        lock.lock();
        try {
            if (currentConfig == null) {
                throw new ConfigurationNotSetException();
            }
            if (isSimulationRunning) {
                throw new SimulationAlreadyRunningException();
            }
            vendorService.seedVendors(5, currentConfig.getTicketReleaseRate(), currentConfig.getTotalTickets(),
                    ticketPool);
            customerService.seedCustomers(8, currentConfig.getCustomerRetrievalRate(), ticketPool);
            activeThreads.addAll(vendorService.getVendorThreads());
            activeThreads.addAll(customerService.getCustomerThreads());
            isSimulationRunning = true;
            logService.logInfo("Simulation Started");
            logService.logInfo("Total vendors: " + vendorService.getVendorThreadsSize());
            logService.logInfo("Total customers: " + customerService.getCustomerThreadsSize());
        } catch (ConfigurationNotSetException | SimulationAlreadyRunningException e) {
            logService.logError(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void stopSimulation() {
        lock.lock();
        try {
            if (!isSimulationRunning) {
                throw new SimulationNotRunningException();
            }
            for (Thread thread : activeThreads) {
                thread.interrupt();
            }
            activeThreads.clear();
            vendorService.getVendorThreads().clear();
            customerService.getCustomerThreads().clear();
            isSimulationRunning = false;
            logService.logInfo("Simulation Stopped");
        } catch (SimulationNotRunningException e) {
            logService.logError(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void addVendor() {
        lock.lock();
        try {
            if (!isSimulationRunning) {
                throw new SimulationNotRunningException();
            }
            Thread vendorThread = vendorService.addVendor(currentConfig.getTicketReleaseRate(),
                    currentConfig.getTotalTickets(), ticketPool);
            activeThreads.add(vendorThread);
            logService.logInfo("Vendor added. Total vendors: " + vendorService.getVendorThreadsSize());
        } catch (SimulationNotRunningException e) {
            logService.logError(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void removeVendor() {
        lock.lock();
        try {
            if (!isSimulationRunning) {
                throw new SimulationNotRunningException();
            }
            if (vendorService.getVendorThreadsSize() == 0) {
                throw new NoVendorsToRemoveException();
            }
            vendorService.removeVendor();
            if (!activeThreads.isEmpty()) {
                activeThreads.remove(activeThreads.size() - 1);
                logService.logInfo("Vendor removed. Remaining vendors: " + vendorService.getVendorThreadsSize());
            }
        } catch (SimulationNotRunningException | NoVendorsToRemoveException e) {
            logService.logError(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void addCustomer(boolean isVip) {
        lock.lock();
        try {
            if (!isSimulationRunning) {
                throw new SimulationNotRunningException();
            }
            Thread customerThread = customerService.addCustomer(currentConfig.getCustomerRetrievalRate(), ticketPool);
            activeThreads.add(customerThread);
            logService.logInfo("Customer added (VIP: " + isVip + "). Total customers: "
                    + customerService.getCustomerThreadsSize());
        } catch (SimulationNotRunningException e) {
            logService.logError(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void removeCustomer() {
        lock.lock();
        try {
            if (!isSimulationRunning) {
                throw new SimulationNotRunningException();
            }
            if (customerService.getCustomerThreadsSize() == 0) {
                throw new NoCustomersToRemoveException();
            }
            customerService.removeCustomer();
            if (!activeThreads.isEmpty()) {
                activeThreads.remove(activeThreads.size() - 1);
            }
            logService.logInfo("Customer removed. Remaining customers: " + customerService.getCustomerThreadsSize());
        } catch (SimulationNotRunningException | NoCustomersToRemoveException e) {
            logService.logError(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onTicketPoolChange(String logMessage, int ticketCount) {
        logService.logInfo(logMessage);
        sendTicketCountUpdate(ticketCount);
    }

    private void sendTicketCountUpdate(int count) {
        messagingTemplate.convertAndSend("/tickets/ticketCount", count);
    }

    public int getTicketCount() {
        return ticketPool.getAvailableTickets();
    }
}