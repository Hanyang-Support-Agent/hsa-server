package com.example.hsa_core.domain.inquiry.dto;

import com.example.hsa_core.domain.inquiry.InquiryResult;

// AI 처리 요청 후 저장된 분석 결과 식별자를 반환합니다.
public record InquiryAiProcessingResponse(
        Long inquiryResultId,
        Long inquiryId
) {

    // 저장된 InquiryResult 엔티티를 API 응답 DTO로 변환합니다.
    public static InquiryAiProcessingResponse from(InquiryResult inquiryResult) {
        return new InquiryAiProcessingResponse(
                inquiryResult.getId(),
                inquiryResult.getInquiryId()
        );
    }
}
