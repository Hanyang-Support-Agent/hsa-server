package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.channel.ChannelType;
import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateRequest;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateResponse;
import com.example.hsa_core.domain.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
// 문의 생성과 같은 기본 문의 도메인 유스케이스를 처리합니다.
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    // 문의를 RECEIVED 상태로 생성하고 저장 결과를 반환합니다.
    public InquiryCreateResponse createInquiry(InquiryCreateRequest request) {
        Inquiry inquiry = new Inquiry(request.customerId(), request.content(), request.channelType());
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        return InquiryCreateResponse.from(savedInquiry);
    }
}
