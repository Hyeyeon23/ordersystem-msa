package com.example.ordering.ordering.service;

import com.example.ordering.ordering.dto.ProductDot;
import com.example.ordering.ordering.dto.ProductUpdateStock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="product-service")
public interface ProductFeign {

    @GetMapping("/product/{productId}")
    ProductDot getProductById(@PathVariable Long productId, @RequestHeader("X-User-Id") String userId);

    @PutMapping("/product/updatestock")
    void updateProductStock(@RequestBody ProductUpdateStock productUpdateStock);

}
