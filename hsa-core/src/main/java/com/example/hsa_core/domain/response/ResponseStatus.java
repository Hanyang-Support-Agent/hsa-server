package com.example.hsa_core.domain.response;

// 답변 초안 생성 이후 최종 발송까지의 답변 상태를 나타냅니다.
public enum ResponseStatus {
    DRAFTED,
    MODIFIED,
    READY_TO_SEND,
    SENDING,
    SENT,
    SEND_FAILED,
    CANCELLED
}
