package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.channel.ChannelType;
import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateRequest;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateResponse;
import com.example.hsa_core.domain.inquiry.repository.InquiryRepository;
import com.example.hsa_core.domain.response.ActorType;
import com.example.hsa_core.domain.response.ProcessingEventType;
import com.example.hsa_core.domain.response.service.ProcessingLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
// 문의 생성과 같은 기본 문의 도메인 유스케이스를 처리합니다.
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final ProcessingLogService processingLogService;

    @Transactional
    // 문의를 RECEIVED 상태로 생성하고 저장 결과를 반환합니다.
    public InquiryCreateResponse createInquiry(InquiryCreateRequest request) {
        Inquiry inquiry = new Inquiry(request.customerId(), request.content(), request.channelType());
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        processingLogService.log(
                savedInquiry.getId(),
                ProcessingEventType.RECEIVED,
                ActorType.SYSTEM,
                request.channelType() + " 채널로 문의 접수"
        );

        return InquiryCreateResponse.from(savedInquiry);
    }
}
