package com.example.hsa_core.domain.inquiry.repository;

import com.example.hsa_core.domain.inquiry.InquiryResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// InquiryResult 엔티티의 저장과 문의별 분석 결과 조회를 담당합니다.
public interface InquiryResultRepository extends JpaRepository<InquiryResult, Long> {

    // 특정 문의에 저장된 AI 분석 결과를 조회합니다.
    Optional<InquiryResult> findByInquiryId(Long inquiryId);

    // 같은 문의에 AI 분석 결과가 중복 저장되는 것을 방지할 때 사용합니다.
    boolean existsByInquiryId(Long inquiryId);
}
