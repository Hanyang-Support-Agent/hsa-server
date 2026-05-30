package com.example.hsa_core.domain.inquiry.dto.ai;

import java.util.Map;

public record AiInquiryRequest(
        String inquiryId,
        String message,
        String channel,
        Map<String, Object> context
) {
}
