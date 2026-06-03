package com.example.hsa_core.domain.response.repository;

import com.example.hsa_core.domain.response.Transmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransmissionRepository extends JpaRepository<Transmission, Long> {

    List<Transmission> findByResponseId(Long responseId);
}
