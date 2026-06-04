package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.InquiryResult;
import com.example.hsa_core.domain.inquiry.client.AiInquiryClient;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryData;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryError;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryRequest;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiInquiryProcessingServiceTest {

    @Mock
    private AiInquiryClient aiInquiryClient;

    @Mock
    private AiInquiryResultService aiInquiryResultService;

    private final AiInquiryRetryPolicy retryPolicy = new AiInquiryRetryPolicy();

    @Test
    // AI 서버 호출과 결과 저장 서비스 연결이 수행되는지 확인합니다.
    void requestProcessingRequestsAiServerAndAppliesResult() {
        AiInquiryProcessingService service = createService();
        AiInquiryRequest request = createRequest();
        AiInquiryResponse aiResponse = successResponse();
        InquiryResult savedResult = InquiryResult.fromAiResponse(10L, aiResponse);

        when(aiInquiryClient.requestInquiryProcessing(any(AiInquiryRequest.class))).thenReturn(aiResponse);
        when(aiInquiryResultService.applyAiResult(10L, aiResponse)).thenReturn(savedResult);

        InquiryResult result = service.requestProcessing(10L, request);

        assertThat(result).isSameAs(savedResult);
        ArgumentCaptor<AiInquiryRequest> requestCaptor = ArgumentCaptor.forClass(AiInquiryRequest.class);
        verify(aiInquiryClient).requestInquiryProcessing(requestCaptor.capture());
        assertThat(requestCaptor.getValue().inquiryId()).isEqualTo("10");
        assertThat(requestCaptor.getValue().message()).isEqualTo("배송이 언제 도착하나요?");
        assertThat(requestCaptor.getValue().channel()).isEqualTo("KAKAO");
    }

    @Test
    // AI 응답이 재시도 가능한 오류이면 한 번 더 호출하는지 확인합니다.
    void requestProcessingRetriesOnceWhenAiResponseIsRetryable() {
        AiInquiryProcessingService service = createService();
        AiInquiryRequest request = createRequest();
        AiInquiryResponse retryableResponse = new AiInquiryResponse(
                "error",
                null,
                new AiInquiryError("LLM_TIMEOUT", "LLM 호출 시간 초과")
        );
        AiInquiryResponse successResponse = successResponse();
        InquiryResult savedResult = InquiryResult.fromAiResponse(10L, successResponse);

        when(aiInquiryClient.requestInquiryProcessing(any(AiInquiryRequest.class)))
                .thenReturn(retryableResponse)
                .thenReturn(successResponse);
        when(aiInquiryResultService.applyAiResult(10L, successResponse)).thenReturn(savedResult);

        InquiryResult result = service.requestProcessing(10L, request);

        assertThat(result).isSameAs(savedResult);
        verify(aiInquiryClient, org.mockito.Mockito.times(2))
                .requestInquiryProcessing(any(AiInquiryRequest.class));
    }

    @Test
    // AI 서버 HTTP 호출 실패가 기존 AI 에러 응답 저장 흐름으로 연결되는지 확인합니다.
    void requestProcessingConvertsAiHttpFailureToErrorResult() {
        AiInquiryProcessingService service = createService();
        AiInquiryRequest request = createRequest();
        InquiryResult savedResult = InquiryResult.fromAiResponse(
                10L,
                new AiInquiryResponse(
                        "error",
                        null,
                        new AiInquiryError("EXTERNAL_SYSTEM_ERROR", "AI 서버 연결 실패")
                )
        );

        when(aiInquiryClient.requestInquiryProcessing(any(AiInquiryRequest.class)))
                .thenThrow(new RestClientException("AI 서버 연결 실패"))
                .thenThrow(new RestClientException("AI 서버 연결 실패"));
        when(aiInquiryResultService.applyAiResult(any(), any(AiInquiryResponse.class))).thenReturn(savedResult);

        InquiryResult result = service.requestProcessing(10L, request);

        assertThat(result).isSameAs(savedResult);
        ArgumentCaptor<AiInquiryResponse> responseCaptor = ArgumentCaptor.forClass(AiInquiryResponse.class);
        verify(aiInquiryResultService).applyAiResult(org.mockito.Mockito.eq(10L), responseCaptor.capture());
        assertThat(responseCaptor.getValue().status()).isEqualTo("error");
        assertThat(responseCaptor.getValue().error().code()).isEqualTo("EXTERNAL_SYSTEM_ERROR");
    }

    private AiInquiryProcessingService createService() {
        return new AiInquiryProcessingService(
                aiInquiryClient,
                aiInquiryResultService,
                retryPolicy
        );
    }

    private AiInquiryRequest createRequest() {
        return new AiInquiryRequest(
                "10",
                "배송이 언제 도착하나요?",
                "KAKAO",
                Map.of("customerId", 1L)
        );
    }

    private AiInquiryResponse successResponse() {
        return new AiInquiryResponse(
                "success",
                new AiInquiryData(
                        "10",
                        true,
                        "자동응답 초안",
                        false,
                        "자동응답 가능",
                        List.of(),
                        List.of("policy.shipping")
                ),
                null
        );
    }
}
