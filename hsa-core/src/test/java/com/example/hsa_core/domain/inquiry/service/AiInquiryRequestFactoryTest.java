package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.channel.ChannelType;
import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.dto.InquiryContextResponse;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiInquiryRequestFactoryTest {

    private final InquiryContextService inquiryContextService = mock(InquiryContextService.class);
    private final AiInquiryRequestFactory requestFactory = new AiInquiryRequestFactory(inquiryContextService);

    @Test
    // Inquiry 정보와 주문/배송 context가 AI 요청 payload에 함께 담기는지 확인합니다.
    void fromCreatesAiInquiryRequestWithContext() {
        Inquiry inquiry = new Inquiry(1L, "배송이 언제 도착하나요?", ChannelType.KAKAO);
        InquiryContextResponse context = new InquiryContextResponse(
                "SHIPPED",
                "1234567890",
                "CJ대한통운",
                "한양 후드티",
                "IN_TRANSIT",
                "대전 허브"
        );
        when(inquiryContextService.buildContext(1L)).thenReturn(context);

        AiInquiryRequest request = requestFactory.from(10L, inquiry);

        assertThat(request.inquiryId()).isEqualTo("10");
        assertThat(request.message()).isEqualTo("배송이 언제 도착하나요?");
        assertThat(request.channel()).isEqualTo("KAKAO");
        assertThat(request.context())
                .containsEntry("customerId", 1L)
                .containsEntry("orderStatus", "SHIPPED")
                .containsEntry("trackingNumber", "1234567890")
                .containsEntry("carrier", "CJ대한통운")
                .containsEntry("productName", "한양 후드티")
                .containsEntry("deliveryStatus", "IN_TRANSIT")
                .containsEntry("currentLocation", "대전 허브");
    }

    @Test
    // 주문/배송 데이터가 없는 경우 empty context 값이 AI 요청에 담기는지 확인합니다.
    void fromCreatesAiInquiryRequestWithEmptyContext() {
        Inquiry inquiry = new Inquiry(1L, "배송이 언제 도착하나요?", ChannelType.KAKAO);
        when(inquiryContextService.buildContext(1L)).thenReturn(InquiryContextResponse.empty());

        AiInquiryRequest request = requestFactory.from(10L, inquiry);

        assertThat(request.context())
                .containsEntry("customerId", 1L)
                .containsEntry("orderStatus", "NONE")
                .containsEntry("trackingNumber", "NONE")
                .containsEntry("carrier", "NONE")
                .containsEntry("productName", "NONE")
                .containsEntry("deliveryStatus", "NONE")
                .containsEntry("currentLocation", "NONE");
    }
}
