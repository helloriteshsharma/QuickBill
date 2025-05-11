package com.example.billingSystem.repository;
import com.example.billingSystem.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, String> {

    boolean existsByItemCode(String itemCode);

    Page<Item> findAll(Pageable pageable);
}
