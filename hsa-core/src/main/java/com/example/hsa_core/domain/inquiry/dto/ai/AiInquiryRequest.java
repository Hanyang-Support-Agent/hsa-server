package com.example.hsa_core.domain.inquiry.dto.ai;

import java.util.Map;

// 백엔드가 AI 서버에 저장된 문의 처리를 요청할 때 사용하는 요청 DTO입니다.
public record AiInquiryRequest(
        String inquiryId,
        String message,
        String channel,
        Map<String, Object> context
) {
}
