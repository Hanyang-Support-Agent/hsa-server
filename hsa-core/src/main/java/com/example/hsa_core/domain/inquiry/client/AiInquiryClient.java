package com.example.hsa_core.domain.inquiry.client;

import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryRequest;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
// AI 서버의 문의 처리 API 호출을 담당합니다.
public class AiInquiryClient {

    private final RestTemplate restTemplate;
    private final String inquiryProcessUrl;

    public AiInquiryClient(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${hsa.ai.inquiry-process-url:http://hsa-ai.hsa.local:8000/api/inquiries/process}") String inquiryProcessUrl,
            @Value("${hsa.ai.connect-timeout:5s}") String connectTimeout,
            @Value("${hsa.ai.read-timeout:60s}") String readTimeout
    ) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(parseDuration(connectTimeout))
                .readTimeout(parseDuration(readTimeout))
                .build();
        this.inquiryProcessUrl = inquiryProcessUrl;
    }

    // AI 서버에 문의 처리 요청을 전송하고 응답 DTO로 변환합니다.
    public AiInquiryResponse requestInquiryProcessing(AiInquiryRequest request) {
        AiInquiryResponse response = restTemplate.postForObject(
                inquiryProcessUrl,
                request,
                AiInquiryResponse.class
        );

        if (response == null) {
            throw new IllegalStateException("AI 서버 응답이 비어 있습니다.");
        }

        return response;
    }

    private Duration parseDuration(String value) {
        return DurationStyle.detectAndParse(value);
    }
}
