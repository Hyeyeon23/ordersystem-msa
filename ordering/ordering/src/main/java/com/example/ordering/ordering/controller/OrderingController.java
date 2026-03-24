package com.example.ordering.ordering.controller;

import com.example.ordering.ordering.domain.Ordering;
import com.example.ordering.ordering.dto.OrderCreateDto;
import com.example.ordering.ordering.service.OrderingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordering")
@RequiredArgsConstructor
public class OrderingController {

    private final OrderingService orderingService;

    @PostMapping("/create")
    public ResponseEntity<?> orderCreate(@RequestBody OrderCreateDto dtos, @RequestHeader("X-User-Id") String userId){
        Ordering ordering = orderingService.orderFeignKafkaCreate(dtos, userId);
        return new ResponseEntity<>(ordering.getId(), HttpStatus.CREATED);
    }
}
