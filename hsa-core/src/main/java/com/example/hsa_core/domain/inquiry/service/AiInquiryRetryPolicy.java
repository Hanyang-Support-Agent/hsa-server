package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryError;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryErrorCode;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import org.springframework.stereotype.Component;

@Component
public class AiInquiryRetryPolicy {

    public boolean isRetryable(AiInquiryResponse response) {
        if (response == null) {
            return false;
        }

        return isRetryable(response.error());
    }

    public boolean isRetryable(AiInquiryError error) {
        if (error == null || error.code() == null) {
            return false;
        }

        return AiInquiryErrorCode.from(error.code())
                .map(this::isRetryable)
                .orElse(false);
    }

    private boolean isRetryable(AiInquiryErrorCode errorCode) {
        return switch (errorCode) {
            case LLM_TIMEOUT, EXTERNAL_SYSTEM_ERROR -> true;
            case LLM_PARSE_FAILED -> false;
        };
    }
}
