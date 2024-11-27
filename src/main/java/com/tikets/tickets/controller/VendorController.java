// package com.tikets.tickets.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// import com.tikets.tickets.service.VendorService;

// @RestController
// @RequestMapping("/api/vendors")
// public class VendorController {

//     @Autowired
//     private VendorService vendorService;

//     @PostMapping("/add")
//     public void addVendor(@RequestParam int ticketsPerRelease, @RequestParam int releaseInterval) {
//         vendorService.addVendor(ticketsPerRelease, releaseInterval);
//     }

//     @PostMapping("/remove/{vendorId}")
//     public void removeVendor(@PathVariable int vendorId) {
//         vendorService.removeVendor(vendorId);
//     }
// }
