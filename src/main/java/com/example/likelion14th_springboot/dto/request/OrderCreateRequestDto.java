package com.example.likelion14th_springboot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateRequestDto {

    private Long buyerId;
    private List<OrderItemRequest> items;

    private String recipientName;
    private String phoneNumber;
    private String roadAddress;
    private String detailAddress;
    private String postalCode;

    @Getter
    @NoArgsConstructor
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }
}