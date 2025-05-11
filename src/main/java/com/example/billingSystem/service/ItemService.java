package com.example.billingSystem.service;

import com.example.billingSystem.entity.Item;
import org.springframework.data.domain.Page;


import java.util.List;

public interface ItemService {

    public void addItem(Item item);

    public List<Item> allItems();


    public boolean deleteItem(String id);


    public Item getItemById(String id);

    boolean existsByItemCode(String itemCode);


}
