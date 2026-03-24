package com.example.product.product.service;

import com.example.product.product.domain.Product;
import com.example.product.product.dto.ProductRegisterDto;
import com.example.product.product.dto.ProductResDto;
import com.example.product.product.dto.ProductUpdateStock;
import com.example.product.product.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product productCreate(ProductRegisterDto dto, String userId) {
        Product save = productRepository.save(dto.toEntity(Long.parseLong(userId)));
        return save;
    }

    public ProductResDto productDetail(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("없는 샘플입니다"));

        ProductResDto productResDto = ProductResDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
        return productResDto;
    }

    public Product updateStockQunatity(ProductUpdateStock productUpdateStock) {
        Product product = productRepository.findById(productUpdateStock.getProductId()).orElseThrow(() -> new EntityNotFoundException("없는 상품"));
        product.updateStockQuantity(productUpdateStock.getProductQuantity());
        return product;
    }

    @KafkaListener(topics= "update-stock-topic", containerFactory="kafkaListener")
    public void stockConsumer(String message){
        ObjectMapper objectMapper = new ObjectMapper();
        ProductUpdateStock dto = null;
        try {
            dto = objectMapper.readValue(message, ProductUpdateStock.class);
            this.updateStockQunatity(dto);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
