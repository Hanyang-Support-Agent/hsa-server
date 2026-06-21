package com.example.hsa_core.domain.admin.dto;

import com.example.hsa_core.domain.channel.ChannelType;
import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.InquiryStatus;

import java.time.OffsetDateTime;

public record AdminInquiryListResponse(
        Long inquiryId,
        Long customerId,
        String content,
        ChannelType channelType,
        InquiryStatus status,
        OffsetDateTime createdTime
) {
    public static AdminInquiryListResponse from(Inquiry inquiry) {
        return new AdminInquiryListResponse(
                inquiry.getId(),
                inquiry.getCustomerId(),
                inquiry.getContent(),
                inquiry.getChannelType(),
                inquiry.getStatus(),
                inquiry.getCreatedTime()
        );
    }
}
