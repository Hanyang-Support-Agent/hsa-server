package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.InquiryResult;
import com.example.hsa_core.domain.inquiry.client.AiInquiryClient;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryError;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryErrorCode;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryRequest;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
// 저장된 문의를 AI 서버에 전달하고 처리 결과를 기존 결과 저장 흐름에 연결합니다.
public class AiInquiryProcessingService {

    private final AiInquiryClient aiInquiryClient;
    private final AiInquiryResultService aiInquiryResultService;
    private final AiInquiryRetryPolicy aiInquiryRetryPolicy;

    @Transactional
    // 이미 조립된 AI 처리 요청을 보내고, 최종 응답을 InquiryResult에 반영합니다.
    public InquiryResult requestProcessing(Long inquiryId, AiInquiryRequest request) {
        AiInquiryResponse response = requestWithSingleRetry(request);

        return aiInquiryResultService.applyAiResult(inquiryId, response);
    }

    // AI 응답이 재시도 가능한 오류인 경우 한 번 더 호출합니다.
    private AiInquiryResponse requestWithSingleRetry(AiInquiryRequest request) {
        AiInquiryResponse response = requestSafely(request);

        if (aiInquiryRetryPolicy.isRetryable(response)) {
            return requestSafely(request);
        }

        return response;
    }

    // HTTP 호출 실패나 빈 응답을 AI 에러 응답 형태로 변환해 후속 저장 로직이 처리하게 합니다.
    private AiInquiryResponse requestSafely(AiInquiryRequest request) {
        try {
            return aiInquiryClient.requestInquiryProcessing(request);
        } catch (RestClientException | IllegalStateException e) {
            return new AiInquiryResponse(
                    "error",
                    null,
                    new AiInquiryError(
                            AiInquiryErrorCode.EXTERNAL_SYSTEM_ERROR.name(),
                            e.getMessage()
                    )
            );
        }
    }
}
