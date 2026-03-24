package com.example.ordering.ordering.service;

import com.example.ordering.common.config.RestTemplateConfig;
import com.example.ordering.ordering.domain.Ordering;
import com.example.ordering.ordering.dto.OrderCreateDto;
import com.example.ordering.ordering.dto.ProductDot;
import com.example.ordering.ordering.dto.ProductUpdateStock;
import com.example.ordering.ordering.repository.OrderingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final RestTemplate restTemplate;
    private final ProductFeign productFeign;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Ordering orderCreate(OrderCreateDto orderCreateDto, String userId) {
        //product get 요청
        String productGetUrl = "http://product-service/product/" + orderCreateDto.getProductId();
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.set("X-User-Id", userId);
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<ProductDot> response = restTemplate.exchange(productGetUrl, HttpMethod.GET, httpEntity, ProductDot.class);
        ProductDot productDot = response.getBody();
        int quantity = orderCreateDto.getProductCount();
        if (productDot.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("재고 부족");
        } else {
            //product put 요청
            String productPutUrl = "http://product-service/product/updatestock";
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ProductUpdateStock> updateEntity = new HttpEntity<>(
                    ProductUpdateStock.builder()
                            .productId(orderCreateDto.getProductId())
                            .productQuantity(orderCreateDto.getProductCount())
                            .build()
                    , httpHeaders
            );
            restTemplate.exchange(productPutUrl, HttpMethod.POST, updateEntity, Void.class);
        }

        Ordering ordering = Ordering.builder()
                .memberId(Long.parseLong(userId))
                .productId(orderCreateDto.getProductId())
                .quantity(orderCreateDto.getProductCount())
                .build();

        orderingRepository.save(ordering);

        return ordering;
    }

    public Ordering orderFeignKafkaCreate(OrderCreateDto orderCreateDto, String userId){
        ProductDot productDot = productFeign.getProductById(orderCreateDto.getProductId(), userId);
        int quantity = orderCreateDto.getProductCount();

        if(productDot.getStockQuantity() < quantity){
            throw new IllegalArgumentException("재고부족");
        }else{
            ProductUpdateStock p = ProductUpdateStock.builder()
                    .productId(orderCreateDto.getProductId())
                    .productQuantity(orderCreateDto.getProductCount())
                    .build();
            kafkaTemplate.send("update-stock-topic", p);
        }

        Ordering ordering = Ordering.builder()
                .memberId(Long.parseLong(userId))
                .productId(orderCreateDto.getProductId())
                .quantity(orderCreateDto.getProductCount())
                .build();

        orderingRepository.save(ordering);
        return ordering;
    }

}
