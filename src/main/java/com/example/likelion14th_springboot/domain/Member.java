package com.example.likelion14th_springboot.domain;

import com.example.likelion14th_springboot.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String email;
    private String phoneNumber;
    private Integer age;
    private String password;

    @Builder
    public Member(String name, String address, String email, String phoneNumber, Integer age,String password, Role role, Boolean isAdmin, Integer deposit){
        this.name = name;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isAdmin = isAdmin;
        this.deposit = deposit;
        this.age = age;
        this.password= password;
    }

    @Enumerated(EnumType.STRING)
    private Role role; // 판매자면 SELLER, 구매자면 BUYER

    private Boolean isAdmin; // 관리자 계정 여부

    private Integer deposit; // 현재 계좌 잔액

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();

    public void chargeDeposit(int money){
        this.deposit += money;
    }
    public void useDeposit(int money) {
        if (money < 0) {
            throw new IllegalArgumentException("차감 금액은 음수일 수 없습니다.");
        }

        if (this.deposit == null || this.deposit < money) {
            throw new IllegalArgumentException("계좌 잔액이 부족합니다.");
        }

        this.deposit -= money;
    }

    //추가
    public boolean isSeller() {
        return Role.SELLER.equals(this.role);
    }

    public boolean isBuyer() {
        return Role.BUYER.equals(this.role);
    }
}
