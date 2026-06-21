package com.example.hsa_core.domain.response.repository;

import com.example.hsa_core.domain.response.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findByInquiryIdOrderByCreatedTimeAsc(Long inquiryId);

    Optional<Response> findTopByInquiryIdOrderByCreatedTimeDesc(Long inquiryId);
}
