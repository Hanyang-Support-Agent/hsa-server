package com.example.hsa_core.domain.inquiry.dto.ai;

// AI 서버의 status, data, error 래퍼 응답 DTO입니다.
public record AiInquiryResponse(
        String status,
        AiInquiryData data,
        AiInquiryError error
) {
}
