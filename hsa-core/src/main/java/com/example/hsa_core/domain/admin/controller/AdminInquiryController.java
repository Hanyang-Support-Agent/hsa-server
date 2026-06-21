package com.example.hsa_core.domain.admin.controller;

import com.example.hsa_core.domain.admin.dto.AdminInquiryDetailResponse;
import com.example.hsa_core.domain.admin.dto.AdminInquiryListResponse;
import com.example.hsa_core.domain.admin.service.AdminInquiryService;
import com.example.hsa_core.domain.channel.ChannelType;
import com.example.hsa_core.domain.inquiry.InquiryStatus;
import com.example.hsa_core.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Inquiry", description = "관리자 문의 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/inquiries")
public class AdminInquiryController {

    private final AdminInquiryService adminInquiryService;

    @Operation(summary = "문의 목록 조회", description = "상태별/채널별 필터와 페이징을 지원하는 문의 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminInquiryListResponse>>> getInquiries(
            @RequestParam(required = false) InquiryStatus status,
            @RequestParam(required = false) ChannelType channelType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));
        Page<AdminInquiryListResponse> response = adminInquiryService.getInquiries(status, channelType, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "문의 상세 조회", description = "문의 원문 + AI 분류 결과 + 답변 초안 통합 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminInquiryDetailResponse>> getInquiryDetail(
            @PathVariable Long id
    ) {
        AdminInquiryDetailResponse response = adminInquiryService.getInquiryDetail(id);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
