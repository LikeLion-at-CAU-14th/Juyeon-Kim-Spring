package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
}