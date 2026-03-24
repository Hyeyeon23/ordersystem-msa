package com.example.product.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductResDto {

    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
}
