package com.example.hsa_core.domain.response.repository;

import com.example.hsa_core.domain.response.ProcessingLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessingLogRepository extends JpaRepository<ProcessingLog, Long> {

    List<ProcessingLog> findByInquiryIdOrderByCreatedTimeAsc(Long inquiryId);

    List<ProcessingLog> findByResponseIdOrderByCreatedTimeAsc(Long responseId);
}
