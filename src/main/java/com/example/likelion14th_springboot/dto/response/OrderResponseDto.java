package com.example.likelion14th_springboot.dto;

import com.example.likelion14th_springboot.domain.Orders;
import com.example.likelion14th_springboot.domain.ShippingAddress;
import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderResponseDto {

    private Long orderId;
    private Long buyerId;
    private DeliverStatus deliverStatus;

    private String recipientName;
    private String phoneNumber;
    private String roadAddress;
    private String detailAddress;
    private String postalCode;

    private List<OrderItemResponse> items;

    public static OrderResponseDto from(Orders order) {
        ShippingAddress address = order.getShippingAddress();

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .buyerId(order.getBuyer().getId())
                .deliverStatus(order.getDeliverStatus())
                .recipientName(address == null ? null : address.getRecipientName())
                .phoneNumber(address == null ? null : address.getPhoneNumber())
                .roadAddress(address == null ? null : address.getRoadAddress())
                .detailAddress(address == null ? null : address.getDetailAddress())
                .postalCode(address == null ? null : address.getPostalCode())
                .items(order.getProductOrders().stream()
                        .map(OrderItemResponse::from)
                        .toList())
                .build();
    }

    @Getter
    @Builder
    public static class OrderItemResponse {

        private Long productId;
        private String productName;
        private Integer quantity;

        public static OrderItemResponse from(ProductOrders item) {
            return OrderItemResponse.builder()
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .build();
        }
    }
}