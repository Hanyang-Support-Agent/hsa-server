package com.example.hsa_core.domain.inquiry;

// 문의 접수 이후 AI 처리, 관리자 검토, 발송까지의 상태를 나타냅니다.
public enum InquiryStatus {
    RECEIVED, //문의 접수
    FIRST_CREATED, //초안 생성
    ADMIN_REVIEW_REQUIRED, //관리자 검토 필요
    AUTO_REPLIED, //자동응답 완료
    SENT, //최종 발송 완료
    FAILED //처리 실패

}
