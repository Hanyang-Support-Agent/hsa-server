package com.example.hsa_core.domain.admin.service;

import com.example.hsa_core.domain.admin.dto.AdminInquiryDetailResponse;
import com.example.hsa_core.domain.admin.dto.AdminInquiryListResponse;
import com.example.hsa_core.domain.channel.ChannelType;
import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.InquiryResult;
import com.example.hsa_core.domain.inquiry.InquiryStatus;
import com.example.hsa_core.domain.inquiry.repository.InquiryRepository;
import com.example.hsa_core.domain.inquiry.repository.InquiryResultRepository;
import com.example.hsa_core.domain.response.Response;
import com.example.hsa_core.domain.response.repository.ResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminInquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryResultRepository inquiryResultRepository;
    private final ResponseRepository responseRepository;

    // 목록 조회
    public Page<AdminInquiryListResponse> getInquiries(
            InquiryStatus status,
            ChannelType channelType,
            Pageable pageable
    ) {
        Page<Inquiry> page;

        if (status != null && channelType != null) {
            page = inquiryRepository.findByStatusAndChannelType(status, channelType, pageable);
        } else if (status != null) {
            page = inquiryRepository.findByStatus(status, pageable);
        } else if (channelType != null) {
            page = inquiryRepository.findByChannelType(channelType, pageable);
        } else {
            page = inquiryRepository.findAll(pageable);
        }

        return page.map(AdminInquiryListResponse::from);
    }

    // 상세 조회
    public AdminInquiryDetailResponse getInquiryDetail(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의입니다."));

        InquiryResult result = inquiryResultRepository.findByInquiryId(inquiryId)
                .orElse(null);

        Response response = responseRepository.findTopByInquiryIdOrderByCreatedTimeDesc(inquiryId)
                .orElse(null);

        return AdminInquiryDetailResponse.from(inquiry, result, response);
    }
}
