package com.example.hsa_core.domain.inquiry.dto;

import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.InquiryStatus;

import java.time.OffsetDateTime;

// 문의 저장 후 생성된 문의 정보와 초기 처리 상태를 반환하는 응답 DTO입니다.
public record InquiryCreateResponse(
        Long inquiryId,
        Long customerId,
        String content,
        InquiryStatus status,
        OffsetDateTime createdTime
) {

    // 저장된 Inquiry 엔티티를 생성 응답 DTO로 변환합니다.
    public static InquiryCreateResponse from(Inquiry inquiry) {
        return new InquiryCreateResponse(
                inquiry.getId(),
                inquiry.getCustomerId(),
                inquiry.getContent(),
                inquiry.getStatus(),
                inquiry.getCreatedTime()
        );
    }
}
