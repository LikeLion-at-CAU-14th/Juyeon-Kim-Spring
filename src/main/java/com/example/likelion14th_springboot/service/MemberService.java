package com.example.likelion14th_springboot.service;

import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Page<Member> getMembersByPage(int page, int size) {
        return memberRepository.findAll(PageRequest.of(page, size));
    }
}
