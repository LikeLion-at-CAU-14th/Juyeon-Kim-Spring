package com.example.likelion14th_springboot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShippingAddressUpdateRequestDto {

    private String recipientName;
    private String phoneNumber;
    private String roadAddress;
    private String detailAddress;
    private String postalCode;
}