package com.example.billingSystem.repository;

import com.example.billingSystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,String> {

    boolean existsByCustomerId(String customerId);

}
