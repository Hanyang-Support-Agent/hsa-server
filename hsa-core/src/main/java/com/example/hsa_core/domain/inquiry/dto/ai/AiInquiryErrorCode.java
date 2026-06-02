package com.example.hsa_core.domain.inquiry.dto.ai;

import java.util.Arrays;
import java.util.Optional;

// AI 계약에서 정의한 오류 코드를 백엔드에서 식별하기 위한 enum입니다.
public enum AiInquiryErrorCode {
    LLM_TIMEOUT,
    LLM_PARSE_FAILED,
    EXTERNAL_SYSTEM_ERROR;

    // AI 응답의 문자열 error code를 enum 값으로 변환합니다.
    public static Optional<AiInquiryErrorCode> from(String code) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.name().equals(code))
                .findFirst();
    }
}
