package com.example.hsa_core.domain.inquiry.dto.ai;

import java.util.Arrays;
import java.util.Optional;

public enum AiInquiryErrorCode {
    LLM_TIMEOUT,
    LLM_PARSE_FAILED,
    EXTERNAL_SYSTEM_ERROR;

    public static Optional<AiInquiryErrorCode> from(String code) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.name().equals(code))
                .findFirst();
    }
}
