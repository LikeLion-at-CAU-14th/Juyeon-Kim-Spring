package com.example.likelion14th_springboot.controller;

import com.example.likelion14th_springboot.dto.request.JoinRequestDto;
import com.example.likelion14th_springboot.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final MemberService memberService;

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

}