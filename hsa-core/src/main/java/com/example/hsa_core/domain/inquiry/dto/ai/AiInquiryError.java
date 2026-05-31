package com.example.hsa_core.domain.inquiry.dto.ai;

// AI 처리 실패 시 반환되는 오류 정보입니다.
public record AiInquiryError(
        String code,
        String message
) {
}
