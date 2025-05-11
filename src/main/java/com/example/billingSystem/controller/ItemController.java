package com.example.billingSystem.controller;

import java.util.ArrayList;
import java.util.List;
import com.example.billingSystem.entity.Item;
import com.example.billingSystem.repository.ItemRepository;
import com.example.billingSystem.service.ItemService;
import com.example.billingSystem.service.imp.ItemServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ItemController {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemServiceImp itemServiceImp;

    @Autowired
    ItemRepository itemRepository;

    @GetMapping("/addItem")
    public String addItemForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("unitOptions", List.of(
                "Piece", "Box", "Packet", "Set", "Meter", "Foot",
                "Roll", "Litre", "Kg", "Gram", "Dozen", "Pair", "Bag"
        ));
        return "add_Item";
    }

    @PostMapping("/saveItem")
    public String saveItem(@Valid @ModelAttribute("item") Item item,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        // Check for duplicate item code
        if (itemService.existsByItemCode(item.getItemCode())) {
            result.rejectValue("itemCode", "error.item", "Item code already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("unitOptions", List.of(
                    "Piece", "Box", "Packet", "Set", "Meter", "Foot",
                    "Roll", "Litre", "Kg", "Gram", "Dozen", "Pair", "Bag"
            ));
            model.addAttribute("item", item);
            return "add_Item";
        }

        itemService.addItem(item);
        redirectAttributes.addFlashAttribute("successMessage", "Item is added successfully!");
        return "redirect:/addItem";
    }


    @GetMapping("/allItems")
    public String allItem(Model model) {
        List<Item> items = itemService.allItems();
        if (items == null) {
            items = new ArrayList<>(); // Avoid null in view
        }
        model.addAttribute("itemList", items);
        return "items";
    }


    @GetMapping("deleteItem/{id}")
    public String deleteItem(@PathVariable String id, RedirectAttributes redirectAttributes ) {
        itemService.deleteItem(id);
        redirectAttributes.addFlashAttribute("successMessage", "Item deleted successfully!");
        return "redirect:/allItems";
    }

    @GetMapping("/loadEditItem/{id}")
    public String loadEditItem(@PathVariable String id, Model model) {
        model.addAttribute("item", itemService.getItemById(id));
        model.addAttribute("unitOptions", List.of(
                "Piece", "Box", "Packet", "Set", "Meter", "Foot",
                "Roll", "Litre", "Kg", "Gram", "Dozen", "Pair", "Bag"
        ));
        return "modify_Item";
    }

    @PostMapping("/updateItem")
    public String updateItem(@Valid @ModelAttribute Item item, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        Item oldItem = itemService.getItemById(item.getId());

        if (result.hasErrors()) {
            model.addAttribute("unitOptions", List.of(
                    "Piece", "Box", "Packet", "Set", "Meter", "Foot",
                    "Roll", "Litre", "Kg", "Gram", "Dozen", "Pair", "Bag"
            ));
            model.addAttribute("item", item);
            return "modify_Item";
        }

        if (oldItem != null) {
            oldItem.setId(oldItem.getId());
            oldItem.setItemCode(item.getItemCode());
            oldItem.setItemName(item.getItemName());
            oldItem.setItemQuantity(item.getItemQuantity());
            oldItem.setItemUnit(item.getItemUnit());
            oldItem.setItemGstNumber(item.getItemGstNumber());
            oldItem.setItemGstPercent(item.getItemGstPercent());
            oldItem.setItemPrice(item.getItemPrice());

            itemService.addItem(oldItem);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Item modified successfully!");
        return "redirect:/allItems";
    }

    @GetMapping("/all-items")
    public String viewAllItems(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Item> itemsPage = itemRepository.findAll(PageRequest.of(page, 10));
        System.out.println("=".repeat(200));
        System.out.println("Item Page = "+itemsPage);
        System.out.println("=".repeat(200));
        model.addAttribute("itemsPage", itemsPage);
        return "items";
    }


}
