package com.example.billingSystem.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Item {


    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "CHAR(36)", nullable = false, updatable = false)
    private String id;

    @NotBlank(message = "Item code is required")
    @Size(max = 50, message = "Item code must be at most 50 characters")
    private String itemCode;

    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name must be at most 100 characters")
    private String itemName;

    @NotNull(message = "Item quantity is required")
    private Integer itemQuantity;

    @NotBlank(message = "Item unit is required")
    private String itemUnit;

    @NotNull(message = "Item GST percent is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "GST percent cannot be negative")
    @DecimalMax(value = "100.0", message = "GST percent cannot exceed 100")
    private Double itemGstPercent;

    @NotBlank(message = "GST number is required")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$",
            message = "Invalid GST number format"
    )
    private String itemGstNumber;

    @NotNull(message = "Item price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double itemPrice;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);  // Compare based on the 'id' field (or another unique field).
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);  // Ensure that hashCode is consistent with equals.
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", itemCode='" + itemCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemQuantity=" + itemQuantity +
                ", itemUnit='" + itemUnit + '\'' +
                ", itemGstPercent=" + itemGstPercent +
                ", itemGstNumber='" + itemGstNumber + '\'' +
                ", itemPrice=" + itemPrice +
                '}';
    }
}
