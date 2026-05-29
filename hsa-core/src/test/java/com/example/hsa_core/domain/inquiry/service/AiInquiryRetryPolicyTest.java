package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryError;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiInquiryRetryPolicyTest {

    private final AiInquiryRetryPolicy retryPolicy = new AiInquiryRetryPolicy();

    @Test
    void isRetryableReturnsTrueWhenLlmTimeout() {
        AiInquiryError error = new AiInquiryError("LLM_TIMEOUT", "LLM 호출 시간 초과");

        boolean retryable = retryPolicy.isRetryable(error);

        assertThat(retryable).isTrue();
    }

    @Test
    void isRetryableReturnsTrueWhenExternalSystemError() {
        AiInquiryResponse response = new AiInquiryResponse(
                "error",
                null,
                new AiInquiryError("EXTERNAL_SYSTEM_ERROR", "외부 시스템 호출 실패")
        );

        boolean retryable = retryPolicy.isRetryable(response);

        assertThat(retryable).isTrue();
    }

    @Test
    void isRetryableReturnsFalseWhenLlmParseFailed() {
        AiInquiryError error = new AiInquiryError("LLM_PARSE_FAILED", "LLM 출력 파싱 실패");

        boolean retryable = retryPolicy.isRetryable(error);

        assertThat(retryable).isFalse();
    }

    @Test
    void isRetryableReturnsFalseWhenErrorCodeIsUnknown() {
        AiInquiryError error = new AiInquiryError("UNKNOWN_ERROR", "알 수 없는 오류");

        boolean retryable = retryPolicy.isRetryable(error);

        assertThat(retryable).isFalse();
    }
}
