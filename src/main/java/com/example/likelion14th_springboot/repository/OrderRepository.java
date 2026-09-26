package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByBuyer_IdOrderByIdDesc(Long buyerId);
    List<Orders> findByBuyer_IdAndDeletedFalseOrderByIdDesc(Long buyerId);
    Optional<Orders> findByIdAndDeletedFalse(Long orderId);
}