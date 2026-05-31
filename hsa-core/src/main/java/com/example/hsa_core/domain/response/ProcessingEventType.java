package com.example.hsa_core.domain.response;

// 문의 접수부터 답변 발송까지의 처리 단계 이벤트를 나타냅니다.
public enum ProcessingEventType {
    RECEIVED,
    CLASSIFIED,
    DRAFT_CREATED,
    ADMIN_MODIFIED,
    CONFIRMED,
    SEND_REQUESTED,
    SENT,
    SEND_FAILED
}
