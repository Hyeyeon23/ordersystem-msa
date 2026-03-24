package com.example.ordering.ordering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@NoArgsConstructor@AllArgsConstructor@Builder
public class ProductDot {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
}
