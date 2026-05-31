package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.InquiryResult;
import com.example.hsa_core.domain.inquiry.InquiryStatus;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateRequest;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateResponse;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryData;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryError;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import com.example.hsa_core.domain.inquiry.repository.InquiryRepository;
import com.example.hsa_core.domain.inquiry.repository.InquiryResultRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AiInquiryResultServiceTest {

    @Autowired
    private AiInquiryResultService aiInquiryResultService;

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryResultRepository inquiryResultRepository;

    @AfterEach
    void tearDown() {
        inquiryResultRepository.deleteAll();
        inquiryRepository.deleteAll();
    }

    @Test
    void applyAiResultSavesAutoReplyResultAndChangesInquiryStatus() {
        InquiryCreateResponse inquiry = createInquiry();
        AiInquiryResponse response = new AiInquiryResponse(
                "success",
                new AiInquiryData(
                        String.valueOf(inquiry.inquiryId()),
                        true,
                        "자동응답 초안은 response 도메인에서 저장합니다.",
                        false,
                        "자동응답 가능",
                        List.of(),
                        List.of("context.orderStatus", "policy.shipping")
                ),
                null
        );

        InquiryResult result = aiInquiryResultService.applyAiResult(inquiry.inquiryId(), response);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getInquiryId()).isEqualTo(inquiry.inquiryId());
        assertThat(result.isAutoReply()).isTrue();
        assertThat(result.isAdminReview()).isFalse();
        assertThat(result.getReason()).isEqualTo("자동응답 가능");
        assertThat(result.getRiskTags()).isNull();
        assertThat(result.getUsedSources()).isEqualTo("context.orderStatus,policy.shipping");
        assertThat(result.getErrorCode()).isNull();
        assertThat(inquiryRepository.findById(inquiry.inquiryId()))
                .isPresent()
                .get()
                .extracting("status")
                .isEqualTo(InquiryStatus.AUTO_REPLIED);
    }

    @Test
    void applyAiResultChangesInquiryStatusToFirstCreatedWhenDraftExists() {
        InquiryCreateResponse inquiry = createInquiry();
        AiInquiryResponse response = new AiInquiryResponse(
                "success",
                new AiInquiryData(
                        String.valueOf(inquiry.inquiryId()),
                        false,
                        "검토용 초안입니다.",
                        false,
                        "초안 생성",
                        List.of("claim"),
                        List.of("policy.exchange-refund")
                ),
                null
        );

        InquiryResult result = aiInquiryResultService.applyAiResult(inquiry.inquiryId(), response);

        assertThat(result.isAutoReply()).isFalse();
        assertThat(result.isAdminReview()).isFalse();
        assertThat(result.getRiskTags()).isEqualTo("claim");
        assertThat(result.getUsedSources()).isEqualTo("policy.exchange-refund");
        assertThat(inquiryRepository.findById(inquiry.inquiryId()))
                .isPresent()
                .get()
                .extracting("status")
                .isEqualTo(InquiryStatus.FIRST_CREATED);
    }

    @Test
    void applyAiResultSavesNeedsReviewResultAndChangesInquiryStatus() {
        InquiryCreateResponse inquiry = createInquiry();
        AiInquiryResponse response = new AiInquiryResponse(
                "needs_review",
                new AiInquiryData(
                        String.valueOf(inquiry.inquiryId()),
                        false,
                        null,
                        true,
                        "정책 근거 부족",
                        List.of("policy_conflict"),
                        List.of()
                ),
                null
        );

        InquiryResult result = aiInquiryResultService.applyAiResult(inquiry.inquiryId(), response);

        assertThat(result.isAutoReply()).isFalse();
        assertThat(result.isAdminReview()).isTrue();
        assertThat(result.getReason()).isEqualTo("정책 근거 부족");
        assertThat(result.getRiskTags()).isEqualTo("policy_conflict");
        assertThat(result.getUsedSources()).isNull();
        assertThat(inquiryRepository.findById(inquiry.inquiryId()))
                .isPresent()
                .get()
                .extracting("status")
                .isEqualTo(InquiryStatus.ADMIN_REVIEW_REQUIRED);
    }

    @Test
    void applyAiResultSavesErrorResultAndChangesInquiryStatusToFailed() {
        InquiryCreateResponse inquiry = createInquiry();
        AiInquiryResponse response = new AiInquiryResponse(
                "error",
                null,
                new AiInquiryError("LLM_TIMEOUT", "LLM 호출 시간 초과")
        );

        InquiryResult result = aiInquiryResultService.applyAiResult(inquiry.inquiryId(), response);

        assertThat(result.isAutoReply()).isFalse();
        assertThat(result.isAdminReview()).isTrue();
        assertThat(result.getReason()).isNull();
        assertThat(result.getErrorCode()).isEqualTo("LLM_TIMEOUT");
        assertThat(result.getErrorMessage()).isEqualTo("LLM 호출 시간 초과");
        assertThat(inquiryRepository.findById(inquiry.inquiryId()))
                .isPresent()
                .get()
                .extracting("status")
                .isEqualTo(InquiryStatus.FAILED);
    }

    @Test
    void applyAiResultThrowsExceptionWhenResultAlreadyExists() {
        InquiryCreateResponse inquiry = createInquiry();
        AiInquiryResponse response = new AiInquiryResponse(
                "error",
                null,
                new AiInquiryError("LLM_TIMEOUT", "LLM 호출 시간 초과")
        );
        aiInquiryResultService.applyAiResult(inquiry.inquiryId(), response);

        assertThatThrownBy(() -> aiInquiryResultService.applyAiResult(inquiry.inquiryId(), response))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private InquiryCreateResponse createInquiry() {
        return inquiryService.createInquiry(new InquiryCreateRequest(1L, "배송이 언제 도착하나요?"));
    }
}
