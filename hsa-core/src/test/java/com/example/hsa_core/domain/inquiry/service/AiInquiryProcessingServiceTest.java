package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.channel.ChannelType;
import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.InquiryResult;
import com.example.hsa_core.domain.inquiry.client.AiInquiryClient;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryData;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryError;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryRequest;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import com.example.hsa_core.domain.inquiry.repository.InquiryRepository;
import com.example.hsa_core.domain.response.service.ResponseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiInquiryProcessingServiceTest {

    @Mock
    private InquiryRepository inquiryRepository;

    @Mock
    private AiInquiryRequestFactory aiInquiryRequestFactory;

    @Mock
    private AiInquiryClient aiInquiryClient;

    @Mock
    private AiInquiryResultService aiInquiryResultService;

    @Mock
    private ResponseService responseService;

    private final AiInquiryRetryPolicy retryPolicy = new AiInquiryRetryPolicy();

    @Test
    // 문의 조회와 context 기반 요청 조립 후 AI 서버 호출까지 이어지는지 확인합니다.
    void requestProcessingCreatesRequestFromInquiryAndContext() {
        AiInquiryProcessingService service = createService();
        Inquiry inquiry = new Inquiry(1L, "배송이 언제 도착하나요?", ChannelType.KAKAO);
        AiInquiryRequest request = createRequest();
        AiInquiryResponse aiResponse = successResponse();
        InquiryResult savedResult = InquiryResult.fromAiResponse(10L, aiResponse);

        when(inquiryRepository.findById(10L)).thenReturn(Optional.of(inquiry));
        when(aiInquiryRequestFactory.from(10L, inquiry)).thenReturn(request);
        when(aiInquiryClient.requestInquiryProcessing(request)).thenReturn(aiResponse);
        when(aiInquiryResultService.applyAiResult(10L, aiResponse)).thenReturn(savedResult);

        InquiryResult result = service.requestProcessing(10L);

        assertThat(result).isSameAs(savedResult);
        verify(aiInquiryRequestFactory).from(10L, inquiry);
        verify(aiInquiryClient).requestInquiryProcessing(request);
    }

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

    @Test
    void requestProcessingCreatesAutoResponseWhenAutoReplyIsAvailable() {
        AiInquiryProcessingService service = createService();
        AiInquiryRequest request = createRequest();
        AiInquiryResponse aiResponse = responseWithDraft(true, "auto answer");
        InquiryResult savedResult = savedInquiryResult(100L);

        when(aiInquiryClient.requestInquiryProcessing(request)).thenReturn(aiResponse);
        when(aiInquiryResultService.applyAiResult(10L, aiResponse)).thenReturn(savedResult);

        InquiryResult result = service.requestProcessing(10L, request);

        assertThat(result).isSameAs(savedResult);
        verify(responseService).createAutoResponse(10L, 100L, "auto answer");
        verify(responseService, never()).createDraftResponse(any(), any(), any());
    }

    @Test
    void requestProcessingCreatesDraftResponseWhenAdminReviewIsRequired() {
        AiInquiryProcessingService service = createService();
        AiInquiryRequest request = createRequest();
        AiInquiryResponse aiResponse = responseWithDraft(false, "draft answer");
        InquiryResult savedResult = savedInquiryResult(100L);

        when(aiInquiryClient.requestInquiryProcessing(request)).thenReturn(aiResponse);
        when(aiInquiryResultService.applyAiResult(10L, aiResponse)).thenReturn(savedResult);

        InquiryResult result = service.requestProcessing(10L, request);

        assertThat(result).isSameAs(savedResult);
        verify(responseService).createDraftResponse(10L, 100L, "draft answer");
        verify(responseService, never()).createAutoResponse(any(), any(), any());
    }

    @Test
    void requestProcessingDoesNotCreateResponseWhenDraftAnswerIsNull() {
        AiInquiryProcessingService service = createService();
        AiInquiryRequest request = createRequest();
        AiInquiryResponse aiResponse = responseWithDraft(true, null);
        InquiryResult savedResult = savedInquiryResult(100L);

        when(aiInquiryClient.requestInquiryProcessing(request)).thenReturn(aiResponse);
        when(aiInquiryResultService.applyAiResult(10L, aiResponse)).thenReturn(savedResult);

        service.requestProcessing(10L, request);

        verifyNoInteractions(responseService);
    }

    @Test
    void requestProcessingDoesNotCreateResponseWhenDraftAnswerIsBlank() {
        AiInquiryProcessingService service = createService();
        AiInquiryRequest request = createRequest();
        AiInquiryResponse aiResponse = responseWithDraft(false, " ");
        InquiryResult savedResult = savedInquiryResult(100L);

        when(aiInquiryClient.requestInquiryProcessing(request)).thenReturn(aiResponse);
        when(aiInquiryResultService.applyAiResult(10L, aiResponse)).thenReturn(savedResult);

        service.requestProcessing(10L, request);

        verifyNoInteractions(responseService);
    }

    @Test
    void requestProcessingDoesNotCreateResponseWhenAiResponseDataIsNull() {
        AiInquiryProcessingService service = createService();
        AiInquiryRequest request = createRequest();
        AiInquiryResponse aiResponse = new AiInquiryResponse(
                "error",
                null,
                new AiInquiryError("INVALID_RESPONSE", "invalid response")
        );
        InquiryResult savedResult = mock(InquiryResult.class);

        when(aiInquiryClient.requestInquiryProcessing(request)).thenReturn(aiResponse);
        when(aiInquiryResultService.applyAiResult(10L, aiResponse)).thenReturn(savedResult);

        service.requestProcessing(10L, request);

        verifyNoInteractions(responseService);
    }

    @Test
    void requestProcessingDoesNotCreateResponseWhenInquiryResultSaveFails() {
        AiInquiryProcessingService service = createService();
        AiInquiryRequest request = createRequest();
        AiInquiryResponse aiResponse = responseWithDraft(true, "auto answer");

        when(aiInquiryClient.requestInquiryProcessing(request)).thenReturn(aiResponse);
        when(aiInquiryResultService.applyAiResult(10L, aiResponse))
                .thenThrow(new IllegalArgumentException("inquiry result already exists"));

        assertThatThrownBy(() -> service.requestProcessing(10L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("inquiry result already exists");

        verifyNoInteractions(responseService);
    }

    private AiInquiryProcessingService createService() {
        return new AiInquiryProcessingService(
                inquiryRepository,
                aiInquiryRequestFactory,
                aiInquiryClient,
                aiInquiryResultService,
                retryPolicy,
                responseService
        );
    }

    private InquiryResult savedInquiryResult(Long id) {
        InquiryResult inquiryResult = mock(InquiryResult.class);
        when(inquiryResult.getId()).thenReturn(id);
        return inquiryResult;
    }

    private AiInquiryResponse responseWithDraft(boolean autoReplyAvailable, String draftAnswer) {
        return new AiInquiryResponse(
                "success",
                new AiInquiryData(
                        "10",
                        autoReplyAvailable,
                        draftAnswer,
                        !autoReplyAvailable,
                        "reason",
                        List.of(),
                        List.of("policy.shipping")
                ),
                null
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
