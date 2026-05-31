package com.example.hsa_core.domain.inquiry.controller;

import com.example.hsa_core.domain.inquiry.dto.InquiryCreateRequest;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateResponse;
import com.example.hsa_core.domain.inquiry.service.InquiryService;
import com.example.hsa_core.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Inquiry", description = "문의 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
// 외부 요청을 받아 고객 문의 저장 흐름을 시작하는 컨트롤러입니다.
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "문의 저장", description = "고객 문의 내용을 저장합니다.")
    @PostMapping
    // 고객 문의 원문을 저장하고 생성된 문의 정보를 공통 응답 형식으로 반환합니다.
    public ResponseEntity<ApiResponse<InquiryCreateResponse>> createInquiry(
            @Valid @RequestBody InquiryCreateRequest request
    ) {
        InquiryCreateResponse response = inquiryService.createInquiry(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(response));
    }
}
