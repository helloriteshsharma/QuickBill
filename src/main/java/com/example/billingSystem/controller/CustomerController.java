package com.example.billingSystem.controller;


import com.example.billingSystem.entity.Customer;
import com.example.billingSystem.entity.Item;
import com.example.billingSystem.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CustomerController
{
    @Autowired
    CustomerService customerService;

    @GetMapping("/addCustomer")
    public String addCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "add_Customer";
    }



    @PostMapping("/saveCustomer")
    public String saveCustomer(@Valid @ModelAttribute("customer") Customer customer,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (customerService.existsByCustomerId(customer.getCustomerId())) {
            result.rejectValue("customerId", "error.customer", "Customer ID already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("customer", customer);
            return "add_Customer";
        }

        customerService.addCustomer(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Customer is added successfully!");
        return "redirect:/addCustomer?success";
    }



    @GetMapping("/allCustomer")
    public String allCustomer(Model model) {
        List<Customer> customers = customerService.allCustomer();
        if(customers == null)
        {
            customers = new ArrayList<>();
        }
        model.addAttribute("customerList", customers);
        return "customers";
    }

    @GetMapping("/deleteCustomer/{id}")
    public String deleteCustomerById(@PathVariable String id, RedirectAttributes redirectAttributes)
    {
        customerService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Item deleted successfully!");
        return "redirect:/allCustomer";
    }

    @GetMapping("loadEditCustomer/{id}")
    public String loadEditCustomer(@PathVariable String id, Model model)
    {
        model.addAttribute("customer",customerService.getCustomerById(id));
        return "modify_Customer";
    }

    @PostMapping("/updateCustomer")
    public String updateCustomer(@Valid @ModelAttribute Customer customer, BindingResult result, Model model, RedirectAttributes redirectAttributes)
    {

        Customer oldCustomer = customerService.getCustomerById(customer.getId());

        if(result.hasErrors())
        {
            model.addAttribute("customer",customer);
            return "modify_Customer";
        }


        if(oldCustomer != null) {
            oldCustomer.setId(oldCustomer.getId());
            oldCustomer.setCustomerId(customer.getCustomerId());
            oldCustomer.setCustomerName(customer.getCustomerName());
            oldCustomer.setCustomerMobile(customer.getCustomerMobile());
            oldCustomer.setCustomerAddress(customer.getCustomerAddress());
            oldCustomer.setCustomerGstNumber(customer.getCustomerGstNumber());

            customerService.addCustomer(oldCustomer);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Customer modified successfully!");


        return "redirect:/allCustomer";
    }

}
