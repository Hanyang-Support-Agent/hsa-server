package com.example.hsa_core.domain.response.controller;

import com.example.hsa_core.domain.inquiry.repository.InquiryRepository;
import com.example.hsa_core.domain.response.ProcessingLogResponse;
import com.example.hsa_core.domain.response.repository.ProcessingLogRepository;
import com.example.hsa_core.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Processing Log", description = "문의 처리 로그 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/inquiries")
public class ProcessingLogController {

    private final InquiryRepository inquiryRepository;
    private final ProcessingLogRepository processingLogRepository;

    @Operation(summary = "처리 로그 조회", description = "특정 문의의 처리 이력을 타임라인 형태로 조회합니다.")
    @GetMapping("/{id}/logs")
    public ResponseEntity<ApiResponse<List<ProcessingLogResponse>>> getLogs(@PathVariable Long id) {
        if (!inquiryRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 문의입니다.");
        }

        List<ProcessingLogResponse> logs = processingLogRepository
                .findByInquiryIdOrderByCreatedTimeAsc(id)
                .stream()
                .map(ProcessingLogResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(logs));
    }
}

