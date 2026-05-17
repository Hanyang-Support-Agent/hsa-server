package com.example.hsa_core.domain.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class InfraTestController {

    private final RestTemplate restTemplate;

    @GetMapping("/api/test/proxy-ping")
    public ResponseEntity<String> proxyPing() {
        try {
            String aiResponse = restTemplate.getForObject("http://hsa-ai.hsa.local:8000/api/test/ping", String.class);
            return ResponseEntity.ok("AI 서버 연결 성공! 응답: " + aiResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("AI 서버 연결 실패: " + e.getMessage());
        }
    }
}