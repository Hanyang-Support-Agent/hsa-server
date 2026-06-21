package com.example.hsa_core.domain.admin.dto;

import com.example.hsa_core.domain.channel.ChannelType;
import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.InquiryCategory;
import com.example.hsa_core.domain.inquiry.InquiryResult;
import com.example.hsa_core.domain.inquiry.InquiryStatus;
import com.example.hsa_core.domain.response.Response;
import com.example.hsa_core.domain.response.ResponseStatus;

import java.time.OffsetDateTime;

public record AdminInquiryDetailResponse(
        Long inquiryId,
        Long customerId,
        String content,
        ChannelType channelType,
        InquiryStatus status,
        OffsetDateTime createdTime,
        ResultDto result,
        ResponseDto response
) {
    public record ResultDto(
            InquiryCategory category,
            boolean autoReply,
            boolean adminReview,
            String reason,
            String riskTags,
            String usedSources
    ) {
        public static ResultDto from(InquiryResult r) {
            return new ResultDto(
                    r.getCategory(),
                    r.isAutoReply(),
                    r.isAdminReview(),
                    r.getReason(),
                    r.getRiskTags(),
                    r.getUsedSources()
            );
        }
    }

    public record ResponseDto(
            Long responseId,
            String draftContent,
            String finalContent,
            ResponseStatus status
    ) {
        public static ResponseDto from(Response r) {
            return new ResponseDto(
                    r.getId(),
                    r.getDraftContent(),
                    r.getFinalContent(),
                    r.getStatus()
            );
        }
    }

    public static AdminInquiryDetailResponse from(
            Inquiry inquiry,
            InquiryResult inquiryResult,
            Response response
    ) {
        return new AdminInquiryDetailResponse(
                inquiry.getId(),
                inquiry.getCustomerId(),
                inquiry.getContent(),
                inquiry.getChannelType(),
                inquiry.getStatus(),
                inquiry.getCreatedTime(),
                inquiryResult != null ? ResultDto.from(inquiryResult) : null,
                response != null ? ResponseDto.from(response) : null
        );
    }
}
