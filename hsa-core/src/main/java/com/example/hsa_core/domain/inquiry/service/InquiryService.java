package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateRequest;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateResponse;
import com.example.hsa_core.domain.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    public InquiryCreateResponse createInquiry(InquiryCreateRequest request) {
        Inquiry inquiry = new Inquiry(request.customerId(), request.content());
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        return InquiryCreateResponse.from(savedInquiry);
    }
}
