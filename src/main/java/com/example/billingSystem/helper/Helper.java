package com.example.billingSystem.helper;

import com.example.billingSystem.entity.Item;

import java.util.List;

public class Helper {


    public static boolean itemPresentOrNot(List<Item> list, Item item)
    {
        if(list.isEmpty())
        {
            return false;
        }

        for(Item it : list)
        {
            if(it == item)
            {
                return true;
            }
        }
        return false;
    }

    public static void itemRemoved(List<Item> list, Item item) {
        list.removeIf(it -> it.equals(item));
    }

}
