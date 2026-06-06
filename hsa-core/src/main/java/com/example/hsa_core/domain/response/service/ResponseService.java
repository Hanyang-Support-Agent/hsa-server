package com.example.hsa_core.domain.response.service;

import com.example.hsa_core.domain.response.ActorType;
import com.example.hsa_core.domain.response.ProcessingEventType;
import com.example.hsa_core.domain.response.ProcessingLog;
import com.example.hsa_core.domain.response.Response;
import com.example.hsa_core.domain.response.ResponseStatus;
import com.example.hsa_core.domain.response.ResponseType;
import com.example.hsa_core.domain.response.repository.ProcessingLogRepository;
import com.example.hsa_core.domain.response.repository.ResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final ProcessingLogRepository processingLogRepository;

    @Transactional
    public Response createDraftResponse(Long inquiryId, Long inquiryResultId, String draftContent) {
        validateContent(draftContent, "draftContent");

        Response response = Response.builder()
                .inquiryId(inquiryId)
                .inquiryResultId(inquiryResultId)
                .responseType(ResponseType.AI_DRAFT)
                .draftContent(draftContent)
                .finalContent(null)
                .status(ResponseStatus.DRAFTED)
                .build();

        Response savedResponse = responseRepository.save(response);

        saveProcessingLog(
                inquiryId,
                inquiryResultId,
                savedResponse.getId(),
                ProcessingEventType.DRAFT_CREATED,
                null,
                draftContent
        );

        return savedResponse;
    }

    @Transactional
    public Response createAutoResponse(Long inquiryId, Long inquiryResultId, String answerContent) {
        validateContent(answerContent, "answerContent");

        Response response = Response.builder()
                .inquiryId(inquiryId)
                .inquiryResultId(inquiryResultId)
                .responseType(ResponseType.AUTO)
                .draftContent(null)
                .finalContent(answerContent)
                .status(ResponseStatus.READY_TO_SEND)
                .build();

        Response savedResponse = responseRepository.save(response);

        saveProcessingLog(
                inquiryId,
                inquiryResultId,
                savedResponse.getId(),
                ProcessingEventType.CONFIRMED,
                null,
                answerContent
        );

        return savedResponse;
    }

    private void validateContent(String content, String fieldName) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    private void saveProcessingLog(
            Long inquiryId,
            Long inquiryResultId,
            Long responseId,
            ProcessingEventType eventType,
            String previousState,
            String currentState
    ) {
        ProcessingLog processingLog = ProcessingLog.builder()
                .inquiryId(inquiryId)
                .inquiryResultId(inquiryResultId)
                .responseId(responseId)
                .adminId(null)
                .actorType(ActorType.SYSTEM)
                .eventType(eventType)
                .previousState(previousState)
                .currentState(currentState)
                .build();

        processingLogRepository.save(processingLog);
    }
}
