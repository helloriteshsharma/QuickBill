package com.example.billingSystem.service.imp;




import com.example.billingSystem.entity.Item;
import com.example.billingSystem.repository.ItemRepository;
import com.example.billingSystem.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


@Service
public class ItemServiceImp implements ItemService {


    @Autowired
    ItemRepository itemRepository;



    public void addItem(Item item)
    {
        itemRepository.save(item);
    }


    public List<Item> allItems()
    {
        return itemRepository.findAll();
    }


    public boolean deleteItem(String id)
    {
        Item item = itemRepository.findById(id).orElse(null);

        if (!ObjectUtils.isEmpty(item)) {
            itemRepository.delete(item);
            return true;
        }
        return false;
    }

    public Item getItemById(String id)
    {
        Item item =  itemRepository.findById(id).orElse(null);
        return item;
    }

    @Override
    public boolean existsByItemCode(String itemCode) {
        return itemRepository.existsByItemCode(itemCode);
    }

    public Page<Item> getPaginatedItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.findAll(pageable);
    }


}
