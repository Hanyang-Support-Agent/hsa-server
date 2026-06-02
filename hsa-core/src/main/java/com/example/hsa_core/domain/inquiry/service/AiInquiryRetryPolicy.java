package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryError;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryErrorCode;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import org.springframework.stereotype.Component;

@Component
// AI 에러 응답이 후속 HTTP 연동에서 재시도 가능한지 판단합니다.
public class AiInquiryRetryPolicy {

    // AI 응답 래퍼에서 error 정보를 꺼내 재시도 가능 여부를 확인합니다.
    public boolean isRetryable(AiInquiryResponse response) {
        if (response == null) {
            return false;
        }

        return isRetryable(response.error());
    }

    // AI 에러 코드가 일시적 장애에 해당하는지 판단합니다.
    public boolean isRetryable(AiInquiryError error) {
        if (error == null || error.code() == null) {
            return false;
        }

        return AiInquiryErrorCode.from(error.code())
                .map(this::isRetryable)
                .orElse(false);
    }

    // 실제 재시도 실행은 AI HTTP client에서 담당하고, 여기서는 정책만 제공합니다.
    private boolean isRetryable(AiInquiryErrorCode errorCode) {
        return switch (errorCode) {
            case LLM_TIMEOUT, EXTERNAL_SYSTEM_ERROR -> true;
            case LLM_PARSE_FAILED -> false;
        };
    }
}
