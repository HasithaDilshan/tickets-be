package com.tikets.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tikets.tickets.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
