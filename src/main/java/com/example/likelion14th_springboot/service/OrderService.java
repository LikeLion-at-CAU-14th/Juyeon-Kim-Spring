package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.*;
import com.example.likelion14th_springboot.domain.mapping.ProductOrders;
import com.example.likelion14th_springboot.dto.OrderCreateRequestDto;
import com.example.likelion14th_springboot.dto.OrderResponseDto;
import com.example.likelion14th_springboot.dto.ShippingAddressUpdateRequestDto;
import com.example.likelion14th_springboot.enums.DeliverStatus;
import com.example.likelion14th_springboot.enums.Role;
import com.example.likelion14th_springboot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long createOrder(OrderCreateRequestDto request) {

        // 1. 요청값 확인
        if (request.getBuyerId() == null) {
            throw badRequest("구매자 ID가 필요합니다.");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw badRequest("주문할 상품이 필요합니다.");
        }

        if (isBlank(request.getRecipientName())
                || isBlank(request.getPhoneNumber())
                || isBlank(request.getRoadAddress())
                || isBlank(request.getPostalCode())) {
            throw badRequest("수령인, 전화번호, 도로명주소, 우편번호를 입력해주세요.");
        }

        // 2. 구매자 조회 및 역할 확인
        Member buyer = memberRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "구매자가 존재하지 않습니다."
                ));

        if (buyer.getRole() != Role.BUYER) {
            throw badRequest("구매자만 주문할 수 있습니다.");
        }

        // 3. 배송정보 생성
        ShippingAddress shippingAddress = ShippingAddress.builder()
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .roadAddress(request.getRoadAddress())
                .detailAddress(request.getDetailAddress())
                .postalCode(request.getPostalCode())
                .build();

        // 4. 주문 객체 생성
        List<ProductOrders> orderItems = new ArrayList<>();

        Orders order = Orders.builder()
                .buyer(buyer)
                .deliverStatus(DeliverStatus.PREPARATION)
                .shippingAddress(shippingAddress)
                .productOrders(orderItems)
                .build();

        long totalPrice = 0L;
        Set<Long> productIds = new HashSet<>();

        // 5. 상품과 재고 확인, 총액 계산
        for (OrderCreateRequestDto.OrderItemRequest item : request.getItems()) {

            if (item == null || item.getProductId() == null
                    || item.getQuantity() == null
                    || item.getQuantity() <= 0) {
                throw badRequest("상품 ID와 1 이상의 주문 수량이 필요합니다.");
            }

            // 같은 상품은 한 항목에 수량을 합쳐 보내도록 제한
            if (!productIds.add(item.getProductId())) {
                throw badRequest("같은 상품은 수량을 합쳐서 보내주세요.");
            }

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "상품이 존재하지 않습니다."
                    ));

            if (product.getStock() == null
                    || product.getStock() < item.getQuantity()) {
                throw badRequest(product.getName() + "의 재고가 부족합니다.");
            }

            if (product.getPrice() == null || product.getPrice() < 0) {
                throw badRequest("상품 가격이 올바르지 않습니다.");
            }

            // 요청에서 받은 가격이 아닌 DB의 가격으로 계산
            totalPrice += (long) product.getPrice() * item.getQuantity();

            // 현재 잔액 타입이 Integer이므로 처리 가능한 금액 제한
            if (totalPrice > Integer.MAX_VALUE) {
                throw badRequest("처리 가능한 주문 금액을 초과했습니다.");
            }

            ProductOrders productOrders = ProductOrders.builder()
                    .product(product)
                    .orders(order)
                    .quantity(item.getQuantity())
                    .build();

            orderItems.add(productOrders);
        }

        // 6. 구매자 잔액 확인
        if (buyer.getDeposit() == null || buyer.getDeposit() < totalPrice) {
            throw badRequest("계좌 잔액이 부족합니다.");
        }

        // 7. 모든 검증을 통과한 후 재고와 잔액 차감
        for (ProductOrders item : orderItems) {
            item.getProduct().reduceStock(item.getQuantity());
        }

        buyer.useDeposit((int) totalPrice);

        // 8. 주문 저장
        // Orders의 cascade = ALL로 ProductOrders도 함께 저장
        Orders savedOrder = orderRepository.save(order);

        return savedOrder.getId();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    // 구매자별 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByBuyer(Long buyerId) {

        Member buyer = memberRepository.findById(buyerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "구매자가 존재하지 않습니다."
                ));

        if (buyer.getRole() != Role.BUYER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "구매자 계정이 아닙니다."
            );
        }

        return orderRepository.findByBuyer_IdAndDeletedFalseOrderByIdDesc(buyerId)
                .stream()
                .map(OrderResponseDto::from)
                .toList();
    }

    // 단건 주문 조회
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {

        Orders order = orderRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 주문이 존재하지 않습니다."
                ));

        return OrderResponseDto.from(order);
    }
    @Transactional
    public void updateShippingAddress(
            Long orderId,
            ShippingAddressUpdateRequestDto request
    ) {
        // 1. 주문 조회
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 주문이 존재하지 않습니다."
                ));

        // 2. 배송상태 확인
        if (order.getDeliverStatus() != DeliverStatus.PREPARATION) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "배송 준비 중인 주문만 배송정보를 수정할 수 있습니다."
            );
        }

        // 3. 필수 입력값 확인
        if (isBlank(request.getRecipientName())
                || isBlank(request.getPhoneNumber())
                || isBlank(request.getRoadAddress())
                || isBlank(request.getPostalCode())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "수령인, 전화번호, 도로명주소, 우편번호를 입력해주세요."
            );
        }

        // 4. 새 배송정보 생성
        ShippingAddress shippingAddress = ShippingAddress.builder()
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .roadAddress(request.getRoadAddress())
                .detailAddress(request.getDetailAddress())
                .postalCode(request.getPostalCode())
                .build();

        // 5. 배송정보 변경
        order.updateShippingAddress(shippingAddress);
    }
    @Transactional
    public void deleteOrder(Long orderId) {

        // 존재하지 않거나 이미 삭제된 주문은 404 처리
        Orders order = orderRepository.findByIdAndDeletedFalse(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 주문이 존재하지 않습니다."
                ));

        // 배송 완료 상태에서만 삭제 허용
        if (order.getDeliverStatus() != DeliverStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "배송 완료된 주문만 삭제할 수 있습니다."
            );
        }
        order.softDelete();
    }
}