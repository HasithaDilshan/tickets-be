package com.tikets.tickets.controller;

import org.springframework.web.bind.annotation.*;
import com.tikets.tickets.model.*;
import com.tikets.tickets.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:4200")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/configure")
    public void configure(@RequestBody Configuration config) {
        ticketService.configure(config);
    }

    @PostMapping("/start")
    public void startSimulation() {
        ticketService.startSimulation();
    }

    @PostMapping("/stop")
    public void stopSimulation() {
        ticketService.stopSimulation();
    }

    // @GetMapping("/status")
    // public String getStatus() {
    // return ticketService.getStatus();
    // }

    @PostMapping("/vendor/add")
    public void addVendor() {
        ticketService.addVendor();
    }

    @PostMapping("/vendor/remove")
    public void removeVendor() {
        ticketService.removeVendor();
    }

    @PostMapping("/customer/add")
    public void addCustomer(@RequestParam boolean isVip) {
        ticketService.addCustomer(isVip);
    }

    @PostMapping("/customer/remove")
    public void removeCustomer() {
        ticketService.removeCustomer();
    }
}