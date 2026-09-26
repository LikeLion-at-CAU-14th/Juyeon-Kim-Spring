package com.example.likelion14th_springboot.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {

    private String recipientName;
    private String phoneNumber;
    private String roadAddress;
    private String detailAddress;
    private String postalCode;
}