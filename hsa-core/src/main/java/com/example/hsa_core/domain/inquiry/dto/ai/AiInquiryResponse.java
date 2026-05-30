package com.example.hsa_core.domain.inquiry.dto.ai;

public record AiInquiryResponse(
        String status,
        AiInquiryData data,
        AiInquiryError error
) {
}
