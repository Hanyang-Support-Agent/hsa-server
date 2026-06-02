package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.InquiryResult;
import com.example.hsa_core.domain.inquiry.InquiryStatus;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import com.example.hsa_core.domain.inquiry.repository.InquiryRepository;
import com.example.hsa_core.domain.inquiry.repository.InquiryResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
// AI 처리 결과를 InquiryResult에 저장하고 Inquiry 상태를 함께 갱신합니다.
public class AiInquiryResultService {

    private final InquiryRepository inquiryRepository;
    private final InquiryResultRepository inquiryResultRepository;
    private final AiInquiryStatusMapper aiInquiryStatusMapper;

    @Transactional
    // AI 응답을 반영할 문의를 조회하고, 중복 결과 저장을 방지한 뒤 상태와 결과를 저장합니다.
    public InquiryResult applyAiResult(Long inquiryId, AiInquiryResponse response) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의입니다."));

        if (inquiryResultRepository.existsByInquiryId(inquiryId)) {
            throw new IllegalArgumentException("이미 AI 처리 결과가 저장된 문의입니다.");
        }

        InquiryStatus nextStatus = aiInquiryStatusMapper.mapToInquiryStatus(response);
        inquiry.changeStatus(nextStatus);

        InquiryResult inquiryResult = InquiryResult.fromAiResponse(inquiryId, response);

        return inquiryResultRepository.save(inquiryResult);
    }
}
