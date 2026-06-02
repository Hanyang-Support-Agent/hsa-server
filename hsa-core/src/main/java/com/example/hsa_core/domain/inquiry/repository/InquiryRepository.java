package com.example.hsa_core.domain.inquiry.repository;

import com.example.hsa_core.domain.inquiry.Inquiry;
import com.example.hsa_core.domain.inquiry.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Inquiry 엔티티의 저장과 상태/고객 기준 조회를 담당합니다.
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByCustomerIdOrderByCreatedTimeDesc(Long customerId);

    List<Inquiry> findByStatusOrderByCreatedTimeAsc(InquiryStatus status);
}
