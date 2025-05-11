package com.example.billingSystem.service;


import com.example.billingSystem.entity.Customer;

import java.util.List;


public interface CustomerService {

    public void addCustomer(Customer customer);

    public List<Customer> allCustomer();

    public boolean deleteById(String id);

    public Customer getCustomerById(String id);

    boolean existsByCustomerId(String customerId);

}
