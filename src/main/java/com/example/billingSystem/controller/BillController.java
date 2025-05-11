package com.example.billingSystem.controller;


import com.example.billingSystem.entity.Customer;
import com.example.billingSystem.entity.Item;
import com.example.billingSystem.helper.Helper;
import com.example.billingSystem.service.CustomerService;
import com.example.billingSystem.service.ItemService;
import com.itextpdf.kernel.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class BillController {

    @Autowired
    CustomerService customerService;

    @Autowired
    ItemService itemService;
    


    @GetMapping("/allItemAndCustomer")
    public String allCustomer(Model model, HttpSession session) {
        // Clear session attributes to avoid stale data
        session.removeAttribute("itemlist");
        session.removeAttribute("selectedCustomer");
        session.removeAttribute("purchaseQuantities");

        List<Customer> customers = customerService.allCustomer();
        model.addAttribute("customerList", customers);

        // Get all items and remove those with quantity 0
        List<Item> items = itemService.allItems();
        items.removeIf(item -> item.getItemQuantity() == 0); // Remove items where quantity is 0

        model.addAttribute("itemList", items);
        return "ItemsAndCustomer";
    }


    @GetMapping("/bill/addItem/{id}")
    @ResponseBody
    public void addItem(@PathVariable("id") String itemId, HttpSession session, RedirectAttributes redirectAttributes) {
        Item addItem = itemService.getItemById(itemId);

        List<Item> itemlist = (List<Item>) session.getAttribute("itemlist");
        if (itemlist == null) {
            itemlist = new ArrayList<>();
        }

        if (!Helper.itemPresentOrNot(itemlist, addItem)) {
            itemlist.add(addItem);
        }

        session.setAttribute("itemlist", itemlist);
    }


    @GetMapping("/bill/removeItem/{id}")
    @ResponseBody
    public String removeItem(@PathVariable("id") String itemId, HttpSession session) {
        Item removeItem = itemService.getItemById(itemId);
        List<Item> itemlist = (List<Item>) session.getAttribute("itemlist");
        if (itemlist != null && removeItem != null) {
            Helper.itemRemoved(itemlist, removeItem);
            session.setAttribute("itemlist", itemlist);
        }

        // Also remove quantity for removed item
        Map<String, Integer> purchaseQuantities = (Map<String, Integer>) session.getAttribute("purchaseQuantities");
        if (purchaseQuantities != null) {
            purchaseQuantities.remove(itemId);
            session.setAttribute("purchaseQuantities", purchaseQuantities);
        }

        return "Item removed successfully";
    }

    @GetMapping("/bill/addCustomer/{id}")
    @ResponseBody
    public void addCustomer(@PathVariable("id") String customerId, HttpSession session) {
        Customer selectedCustomer = customerService.getCustomerById(customerId);
        session.setAttribute("selectedCustomer", selectedCustomer);
    }

    @GetMapping("/bill/removeCustomer")
    @ResponseBody
    public void removeCustomer(HttpSession session) {
        session.removeAttribute("selectedCustomer");
    }

    @GetMapping("bill/clearAllItems")
    public String clearAllItems(HttpSession session) {
        session.removeAttribute("itemlist");
        session.removeAttribute("purchaseQuantities");
        return "redirect:/allItemAndCustomer";
    }

    @GetMapping("/bill/ItemsAndCustomer")
    public String listOfSelectItemsAndCustomer(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        List<Item> itemlist = (List<Item>) session.getAttribute("itemlist");
        Customer selectedCustomer = (Customer) session.getAttribute("selectedCustomer");
        Map<String, Integer> purchaseQuantities = (Map<String, Integer>) session.getAttribute("purchaseQuantities");

        // Check if itemlist, selectedCustomer, or purchaseQuantities are null or empty
        if (((itemlist == null) || (itemlist.isEmpty())) && (selectedCustomer == null)) {
            redirectAttributes.addFlashAttribute("successMessage1", "Please select a customer and an item!");
            return "redirect:/allItemAndCustomer";
        }
       else if (itemlist == null || itemlist.isEmpty())
        {
            redirectAttributes.addFlashAttribute("successMessage3", "Please select at least 1 item!");
            return "redirect:/allItemAndCustomer";
        }
        else if(selectedCustomer == null)
        {
            redirectAttributes.addFlashAttribute("successMessage2", "Please select a customer!");
            return "redirect:/allItemAndCustomer";
        }

        model.addAttribute("itemsList", itemlist);
        model.addAttribute("customer", selectedCustomer);
        model.addAttribute("purchaseQuantities", purchaseQuantities);
        return "selectedItemsAndCustomer";
    }



    @PostMapping("/updateQuantity")
    public String updateSingleQuantity(@RequestParam String itemId,
                                       @RequestParam Integer purchaseQuantity,
                                       Model model,
                                       HttpSession session) {

        Item item = itemService.getItemById(itemId);
        if (item == null) {
            model.addAttribute("error", "Invalid item ID.");
        } else if (purchaseQuantity > item.getItemQuantity()) {
            model.addAttribute("error", "Purchase quantity exceeds available stock");
        } else {
            Map<String, Integer> purchaseQuantities = (Map<String, Integer>) session.getAttribute("purchaseQuantities");
            if (purchaseQuantities == null) {
                purchaseQuantities = new HashMap<>();
            }
            purchaseQuantities.put(itemId, purchaseQuantity);
            session.setAttribute("purchaseQuantities", purchaseQuantities);
        }

        List<Item> itemlist = (List<Item>) session.getAttribute("itemlist");
        Customer selectedCustomer = (Customer) session.getAttribute("selectedCustomer");
        Map<String, Integer> purchaseQuantities = (Map<String, Integer>) session.getAttribute("purchaseQuantities");

        model.addAttribute("itemsList", itemlist);
        model.addAttribute("customer", selectedCustomer);
        model.addAttribute("purchaseQuantities", purchaseQuantities);

        return "selectedItemsAndCustomer";
    }



    @PostMapping("/bill/generate")
    public String generateBill(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        List<Item> items = (List<Item>) session.getAttribute("itemlist");
        Map<String, Integer> purchaseQuantities = (Map<String, Integer>) session.getAttribute("purchaseQuantities");

        if (items == null )
        {
            return "redirect:/bill/ItemsAndCustomer";
        }
        else if(purchaseQuantities == null)
        {
            redirectAttributes.addFlashAttribute("successMessage", "Purchase quantities are not provided for all items. Please update the quantities.");

            return "redirect:/bill/ItemsAndCustomer";
        }


        // Check if the size of purchaseQuantities matches the size of items list
        if (items.size() != purchaseQuantities.size()) {

            redirectAttributes.addFlashAttribute("successMessage", "Purchase quantities are not provided for all items. Please update the quantities.");

            return "redirect:/bill/ItemsAndCustomer";
        }

        for (Map.Entry<String, Integer> entry : purchaseQuantities.entrySet()) {
            System.out.println("=".repeat(100));
            System.out.println(entry.getKey() + "   " + entry.getValue());
            System.out.println("=".repeat(100));

            if (entry.getValue() == null || entry.getValue() == 0) {

                return "redirect:/bill/ItemsAndCustomer";
            }
        }

        for (Item item : items) {
            int purchasedQty = purchaseQuantities.getOrDefault(item.getId(), 0);
            if (purchasedQty > 0 && item.getItemQuantity() >= purchasedQty) {
                // Update the item quantity after purchase
                item.setItemQuantity(item.getItemQuantity() - purchasedQty);

                // Set the item details (assuming you're saving/updating the item)
                item.setId(item.getId());
                item.setItemCode(item.getItemCode());
                item.setItemName(item.getItemName());
                item.setItemUnit(item.getItemUnit());
                item.setItemGstNumber(item.getItemGstNumber());
                item.setItemGstPercent(item.getItemGstPercent());
                item.setItemPrice(item.getItemPrice());

                System.out.println("Before updating item: " + item);
                itemService.addItem(item);
            } else {
                model.addAttribute("error", "Purchased quantity for item " + item.getItemName() + " exceeds available stock.");
                return "redirect:/bill/ItemsAndCustomer";
            }
        }

        return "redirect:/bill/view";
    }



    @GetMapping("/bill/view")
    public String viewBill(Model model, HttpSession session) {
        List<Item> items = (List<Item>) session.getAttribute("itemlist");
        Customer customer = (Customer) session.getAttribute("selectedCustomer");
        Map<String, Integer> purchaseQuantities = (Map<String, Integer>) session.getAttribute("purchaseQuantities");

        System.out.println("=".repeat(200));
        System.out.println("PurchaseQuantity :"+purchaseQuantities);
        System.out.println("=".repeat(200));

        if (items == null || customer == null || purchaseQuantities == null) {
            return "redirect:/allItemAndCustomer";
        }

        double subtotal = 0.0;
        double gst = 0.0;
        for (Item item : items) {
            int quantity = purchaseQuantities.getOrDefault(item.getId(), 0);
            double itemTotal = item.getItemPrice() * quantity;
            double itemGst = itemTotal * (item.getItemGstPercent() / 100);
            subtotal += itemTotal;
            gst += itemGst;
        }
        double totalAmount = subtotal + gst;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        model.addAttribute("customer", customer);
        model.addAttribute("items", items);
        model.addAttribute("purchaseQuantities", purchaseQuantities);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("gst", gst);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("printDate", formattedDate); // Pass formatted date

        return "bill";
    }

    @PostMapping("/bill/pdf")
    public void generatePdf(HttpServletResponse response, HttpSession session) throws IOException {
        List<Item> items = (List<Item>) session.getAttribute("itemlist");
        Customer customer = (Customer) session.getAttribute("selectedCustomer");
        Map<String, Integer> purchaseQuantities = (Map<String, Integer>) session.getAttribute("purchaseQuantities");

        double subtotal = 0.0;
        double gst = 0.0;
        for (Item item : items) {
            int quantity = purchaseQuantities.getOrDefault(item.getId(), 0);
            double itemTotal = item.getItemPrice() * quantity;
            double itemGst = itemTotal * (item.getItemGstPercent() / 100);
            subtotal += itemTotal;
            gst += itemGst;
        }
        double totalAmount = subtotal + gst;

        // Set up the PDF document
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=bill.pdf");

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(response.getOutputStream()));
        Document document = new Document(pdfDoc);

        // Add title
        document.add(new Paragraph("Invoice").setFontSize(18).setBold());

        // Customer details
        document.add(new Paragraph("Customer Name: " + customer.getCustomerName()));
        document.add(new Paragraph("GST Number: " + customer.getCustomerGstNumber()));
        document.add(new Paragraph("Mobile: " + customer.getCustomerMobile()));
        document.add(new Paragraph("Address: " + customer.getCustomerAddress()));
        document.add(new Paragraph("Print Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))));

        // Table for items
        Table table = new Table(6);
        table.addHeaderCell("Item");
        table.addHeaderCell("Qty");
        table.addHeaderCell("Unit");
        table.addHeaderCell("Price");
        table.addHeaderCell("GST %");
        table.addHeaderCell("Total");

        for (Item item : items) {
            int quantity = purchaseQuantities.getOrDefault(item.getId(), 0);
            double itemTotal = item.getItemPrice() * quantity;

            table.addCell(item.getItemName());
            table.addCell(String.valueOf(quantity));
            table.addCell(item.getItemUnit());
            table.addCell(String.valueOf(item.getItemPrice()));
            table.addCell(String.valueOf(item.getItemGstPercent()));
            table.addCell(String.valueOf(itemTotal));
        }

        document.add(table);

        // Subtotal, GST, Total
        document.add(new Paragraph("Subtotal: ₹" + subtotal));
        document.add(new Paragraph("GST: ₹" + gst));
        document.add(new Paragraph("Total Amount: ₹" + totalAmount));

        document.close();
    }

}
