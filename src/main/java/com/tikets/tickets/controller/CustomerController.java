// package com.tikets.tickets.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import com.tikets.tickets.service.CustomerService;

// @RestController
// @RequestMapping("/api/customers")
// public class CustomerController {

//     @Autowired
//     private CustomerService customerService;

//     @PostMapping("/add")
//     public void addCustomer(@RequestParam int retrievalInterval, @RequestParam boolean isVip) {
//         customerService.addCustomer(retrievalInterval, isVip);
//     }

//     @PostMapping("/remove/{customerId}")
//     public void removeCustomer(@PathVariable int customerId) {
//         customerService.removeCustomer(customerId);
//     }
// }