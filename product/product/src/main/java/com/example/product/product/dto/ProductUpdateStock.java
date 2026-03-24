package com.example.product.product.dto;

import com.example.product.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductUpdateStock {

    private Long productId;
    private Integer productQuantity;

}
