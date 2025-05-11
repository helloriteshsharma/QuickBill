package com.example.billingSystem.controller;

import com.example.billingSystem.entity.Customer;
import com.example.billingSystem.entity.Item;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

public class HelperController {

    @ModelAttribute("itemlist")
    public List<Item> itemlist() {
        return new ArrayList<>();
    }

    @ModelAttribute("selectedCustomer")
    public Customer selectedCustomer() {
        return new Customer();
    }
}
