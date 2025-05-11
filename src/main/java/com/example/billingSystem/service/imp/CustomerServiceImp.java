package com.example.billingSystem.service.imp;


import com.example.billingSystem.entity.Customer;
import com.example.billingSystem.repository.CustomerRepository;
import com.example.billingSystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImp implements CustomerService {


    @Autowired
    CustomerRepository customerRepository;

    @Override
    public void addCustomer(Customer customer) {

        customerRepository.save(customer);

    }

    @Override
    public List<Customer> allCustomer() {
        return customerRepository.findAll();
    }

    @Override
    public boolean deleteById(String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent())
        {
            customerRepository.delete(customer.get());
            return true;
        }
        return false;
    }

    @Override
    public Customer getCustomerById(String id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        return customer;
    }

    @Override
    public boolean existsByCustomerId(String customerId) {
        return customerRepository.existsByCustomerId(customerId);
    }

}
