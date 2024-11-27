package com.tikets.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tikets.tickets.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
}
