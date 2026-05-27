package com.example.hsa_core.domain.inquiry.dto;

import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.InquiryStatus;

import java.time.OffsetDateTime;

public record InquiryCreateResponse(
        Long inquiryId,
        Long customerId,
        String content,
        InquiryStatus status,
        OffsetDateTime createdTime
) {

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
