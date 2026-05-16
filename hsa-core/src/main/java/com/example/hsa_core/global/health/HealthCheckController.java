package com.example.hsa_core.global.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "서버 상태 확인 및 배포 테스트 API")
@RestController
public class HealthCheckController {

    @Operation(summary = "서버 구동 여부 확인", description = "배포 직후 서버가 정상적으로 통신 가능한 상태인지 확인합니다.")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Server is running perfectly!");
    }
}
