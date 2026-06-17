package com.example.hsa_core.domain.response.service;

import com.example.hsa_core.domain.response.ActorType;
import com.example.hsa_core.domain.response.ProcessingEventType;
import com.example.hsa_core.domain.response.ProcessingLog;
import com.example.hsa_core.domain.response.repository.ProcessingLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessingLogService {

    private final ProcessingLogRepository processingLogRepository;

    @Transactional
    public void log(Long inquiryId, ProcessingEventType eventType, ActorType actorType, String detail) {
        ProcessingLog log = ProcessingLog.builder()
                .inquiryId(inquiryId)
                .actorType(actorType)
                .eventType(eventType)
                .currentState(detail)
                .build();

        processingLogRepository.save(log);
    }
}
