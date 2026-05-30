package com.example.hsa_core.domain.inquiry.dto.ai;

import java.util.List;

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
