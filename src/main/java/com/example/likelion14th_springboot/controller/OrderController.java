package com.example.likelion14th_springboot.controller;

import com.example.likelion14th_springboot.dto.OrderCreateRequestDto;
import com.example.likelion14th_springboot.dto.OrderResponseDto;
import com.example.likelion14th_springboot.dto.ShippingAddressUpdateRequestDto;
import com.example.likelion14th_springboot.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> createOrder(
            @RequestBody OrderCreateRequestDto request
    ) {
        Long orderId = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("orderId", orderId));
    }

    // 구매자별 주문 목록 조회
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByBuyer(
            @PathVariable("buyerId") Long buyerId
    ) {
        return ResponseEntity.ok(
                orderService.getOrdersByBuyer(buyerId)
        );
    }

    // 단건 주문 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable("orderId") Long orderId
    ) {
        return ResponseEntity.ok(
                orderService.getOrderById(orderId)
        );
    }

    @PutMapping("/{orderId}/shipping-address")
    public ResponseEntity<Void> updateShippingAddress(
            @PathVariable("orderId") Long orderId,
            @RequestBody ShippingAddressUpdateRequestDto request
    ) {
        orderService.updateShippingAddress(orderId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable("orderId") Long orderId
    ) {
        orderService.deleteOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}