package com.example.likelion14th_springboot.controller;

import com.example.likelion14th_springboot.config.PrincipalHandler;
import com.example.likelion14th_springboot.domain.Member;
import com.example.likelion14th_springboot.dto.request.JoinRequestDto;
import com.example.likelion14th_springboot.dto.response.TokenResponseDto;
import com.example.likelion14th_springboot.jwt.JwtTokenProvider;
import com.example.likelion14th_springboot.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/join")
    public void join(@RequestBody JoinRequestDto joinRequestDto) {
        memberService.join(joinRequestDto);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleException(
            ResponseStatusException e) {

        return ResponseEntity.status(e.getStatusCode())
                .body(Map.of(
                        "code", "M-001",
                        "message", e.getReason() == null
                                ? "요청 처리에 실패했습니다."
                                : e.getReason(),
                        "status", e.getStatusCode().value(),
                        "timestamp", LocalDateTime.now().toString()
                ));
    }

    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody JoinRequestDto joinRequestDto) {
        Member member = memberService.login(joinRequestDto);
        return TokenResponseDto.of(jwtTokenProvider.generateAccessToken(member.getName()), jwtTokenProvider.generateRefreshToken(member.getName()));
    }

    @GetMapping("/my")
    public ResponseEntity<String> getMyName() {
        String name = PrincipalHandler.getUsernameFromPrincipal();
        return ResponseEntity.ok(name);
    }

}