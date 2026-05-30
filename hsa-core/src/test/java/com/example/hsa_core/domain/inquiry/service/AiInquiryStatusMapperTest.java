package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.InquiryStatus;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryData;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryError;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiInquiryStatusMapperTest {

    private final AiInquiryStatusMapper mapper = new AiInquiryStatusMapper();

    @Test
    void mapToInquiryStatusReturnsAutoRepliedWhenAutoReplyAvailable() {
        AiInquiryResponse response = new AiInquiryResponse(
                "success",
                new AiInquiryData("inq_001", true, "자동응답입니다.", false, "자동응답 가능", List.of(), List.of()),
                null
        );

        InquiryStatus status = mapper.mapToInquiryStatus(response);

        assertThat(status).isEqualTo(InquiryStatus.AUTO_REPLIED);
    }

    @Test
    void mapToInquiryStatusReturnsFirstCreatedWhenDraftAnswerExists() {
        AiInquiryResponse response = new AiInquiryResponse(
                "success",
                new AiInquiryData("inq_001", false, "검토용 초안입니다.", false, "초안 생성", List.of(), List.of()),
                null
        );

        InquiryStatus status = mapper.mapToInquiryStatus(response);

        assertThat(status).isEqualTo(InquiryStatus.FIRST_CREATED);
    }

    @Test
    void mapToInquiryStatusReturnsAdminReviewRequiredWhenNeedsReview() {
        AiInquiryResponse response = new AiInquiryResponse(
                "needs_review",
                new AiInquiryData("inq_001", false, null, true, "관리자 검토 필요", List.of(), List.of()),
                null
        );

        InquiryStatus status = mapper.mapToInquiryStatus(response);

        assertThat(status).isEqualTo(InquiryStatus.ADMIN_REVIEW_REQUIRED);
    }

    @Test
    void mapToInquiryStatusReturnsFailedWhenError() {
        AiInquiryResponse response = new AiInquiryResponse(
                "error",
                null,
                new AiInquiryError("LLM_TIMEOUT", "LLM 호출 시간 초과")
        );

        InquiryStatus status = mapper.mapToInquiryStatus(response);

        assertThat(status).isEqualTo(InquiryStatus.FAILED);
    }

    @Test
    void mapToInquiryStatusThrowsExceptionWhenStatusIsUnknown() {
        AiInquiryResponse response = new AiInquiryResponse("unknown", null, null);

        assertThatThrownBy(() -> mapper.mapToInquiryStatus(response))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
