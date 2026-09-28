package com.example.likelion14th_springboot.repository;

import com.example.likelion14th_springboot.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
    Page<Member> findByAgeGreaterThanEqual(int age,Pageable pageable);
    List<Member> findByNameStartingWith(String name);

}