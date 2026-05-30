package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.InquiryStatus;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryData;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import org.springframework.stereotype.Component;

@Component
public class AiInquiryStatusMapper {

    private static final String SUCCESS = "success";
    private static final String NEEDS_REVIEW = "needs_review";
    private static final String ERROR = "error";

    public InquiryStatus mapToInquiryStatus(AiInquiryResponse response) {
        if (response == null || response.status() == null) {
            throw new IllegalArgumentException("AI 응답 상태가 비어 있습니다.");
        }

        return switch (response.status()) {
            case SUCCESS -> mapSuccessStatus(response.data());
            case NEEDS_REVIEW -> InquiryStatus.ADMIN_REVIEW_REQUIRED;
            case ERROR -> InquiryStatus.FAILED;
            default -> throw new IllegalArgumentException("지원하지 않는 AI 응답 상태입니다.");
        };
    }

    private InquiryStatus mapSuccessStatus(AiInquiryData data) {
        if (data == null) {
            throw new IllegalArgumentException("AI 성공 응답 데이터가 비어 있습니다.");
        }

        if (data.autoReplyAvailable()) {
            return InquiryStatus.AUTO_REPLIED;
        }

        if (data.draftAnswer() != null) {
            return InquiryStatus.FIRST_CREATED;
        }

        throw new IllegalArgumentException("AI 성공 응답을 문의 상태로 변환할 수 없습니다.");
    }
}
