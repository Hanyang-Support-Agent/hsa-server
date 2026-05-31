package com.example.hsa_core.domain.inquiry.dto.ai;

import java.util.List;

// AI 처리 성공 또는 관리자 검토 필요 응답의 본문 데이터입니다.
public record AiInquiryData(
        String inquiryId,
        boolean autoReplyAvailable,
        String draftAnswer,
        boolean needsAdminReview,
        String reason,
        List<String> riskTags,
        List<String> usedSources
) {
}
