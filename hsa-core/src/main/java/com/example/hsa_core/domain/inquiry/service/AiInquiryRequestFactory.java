package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.dto.InquiryContextResponse;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
// 문의 정보와 주문/배송 context를 AI 서버 요청 DTO로 조립합니다.
public class AiInquiryRequestFactory {

    private final InquiryContextService inquiryContextService;

    public AiInquiryRequestFactory(InquiryContextService inquiryContextService) {
        this.inquiryContextService = inquiryContextService;
    }

    // 문의 원문, 채널, 고객의 주문/배송 context를 AI 요청 payload로 변환합니다.
    public AiInquiryRequest from(Long inquiryId, Inquiry inquiry) {
        InquiryContextResponse context = inquiryContextService.buildContext(inquiry.getCustomerId());

        return new AiInquiryRequest(
                String.valueOf(inquiryId),
                inquiry.getContent(),
                inquiry.getChannelType().name(),
                Map.of(
                        "customerId", inquiry.getCustomerId(),
                        "orderStatus", context.orderStatus(),
                        "trackingNumber", context.trackingNumber(),
                        "carrier", context.carrier(),
                        "productName", context.productName(),
                        "deliveryStatus", context.deliveryStatus(),
                        "currentLocation", context.currnetLocation()
                )
        );
    }
}
