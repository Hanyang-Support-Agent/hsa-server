package com.example.hsa_core.domain.response.service;

import com.example.hsa_core.domain.response.ActorType;
import com.example.hsa_core.domain.response.ProcessingEventType;
import com.example.hsa_core.domain.response.ProcessingLog;
import com.example.hsa_core.domain.response.Response;
import com.example.hsa_core.domain.response.ResponseStatus;
import com.example.hsa_core.domain.response.ResponseType;
import com.example.hsa_core.domain.response.SendStatus;
import com.example.hsa_core.domain.response.Transmission;
import com.example.hsa_core.domain.response.repository.ProcessingLogRepository;
import com.example.hsa_core.domain.response.repository.ResponseRepository;
import com.example.hsa_core.domain.response.repository.TransmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final ProcessingLogRepository processingLogRepository;
    private final TransmissionRepository transmissionRepository;

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
                null,
                ActorType.SYSTEM,
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
                null,
                ActorType.SYSTEM,
                ProcessingEventType.CONFIRMED,
                null,
                answerContent
        );

        return savedResponse;
    }

    @Transactional
    public Response modifyResponse(Long responseId, Long adminId, String finalContent) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalArgumentException("response not found"));

        validateContent(finalContent, "finalContent");
        validateAdminId(adminId);

        String previousState = response.getFinalContent();
        response.modifyFinalContent(finalContent, adminId);

        saveProcessingLog(
                response.getInquiryId(),
                response.getInquiryResultId(),
                response.getId(),
                adminId,
                ActorType.ADMIN,
                ProcessingEventType.ADMIN_MODIFIED,
                previousState,
                finalContent
        );

        return response;
    }

    @Transactional
    public Response confirmResponse(Long responseId, Long adminId) {
        // 확정할 답변을 조회하고, 존재하지 않으면 예외를 발생시킵니다.
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalArgumentException("response not found"));

        // 확정 작업을 수행한 관리자 식별자가 있는지 검증합니다.
        validateAdminId(adminId);

        // 최종 답변 내용이 비어 있으면 확정할 수 없도록 검증합니다.
        validateContent(response.getFinalContent(), "finalContent");

        // 처리 로그에 남기기 위해 확정 전 상태를 저장합니다.
        String previousState = response.getStatus().name();

        // 답변 상태를 발송 대기 상태로 변경합니다.
        response.changeStatus(ResponseStatus.READY_TO_SEND);

        // 관리자에 의해 최종 확정되었음을 처리 로그로 기록합니다.
        saveProcessingLog(
                response.getInquiryId(),
                response.getInquiryResultId(),
                response.getId(),
                adminId,
                ActorType.ADMIN,
                ProcessingEventType.CONFIRMED,
                previousState,
                ResponseStatus.READY_TO_SEND.name()
        );

        // 변경된 답변 엔티티를 반환합니다.
        return response;
    }

    @Transactional
    public Transmission sendResponseMock(Long responseId, Long channelId, String recipientIdentifier) {
        Response response = responseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalArgumentException("response not found"));

        // 발송에 필요한 채널과 수신자 정보를 검증합니다.
        validateChannelId(channelId);
        validateContent(recipientIdentifier, "recipientIdentifier");
        validateContent(response.getFinalContent(), "finalContent");
        validateReadyToSend(response);

        // 발송 전 상태를 로그에 남기고 발송 중 상태로 변경합니다.
        String previousState = response.getStatus().name();
        response.changeStatus(ResponseStatus.SENDING);

        // 외부 연동 대신 Mock 발송 내역을 먼저 생성합니다.
        Transmission transmission = Transmission.builder()
                .responseId(response.getId())
                .channelId(channelId)
                .recipientIdentifier(recipientIdentifier)
                .attemptNo(1)
                .sendStatus(SendStatus.RETRYING)
                .build();

        Transmission savedTransmission = transmissionRepository.save(transmission);

        // PoC 범위에서는 외부 API 호출 없이 항상 발송 성공으로 처리합니다.
        String externalMessageId = "mock-" + response.getId() + "-" + savedTransmission.getId();
        savedTransmission.markSuccess(externalMessageId);
        response.changeStatus(ResponseStatus.SENT);

        // 발송 완료 이벤트를 처리 로그로 기록합니다.
        saveProcessingLog(
                response.getInquiryId(),
                response.getInquiryResultId(),
                response.getId(),
                null,
                ActorType.SYSTEM,
                ProcessingEventType.SENT,
                previousState,
                ResponseStatus.SENT.name()
        );

        return savedTransmission;
    }

    private void validateContent(String content, String fieldName) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }

    private void validateAdminId(Long adminId) {
        if (adminId == null) {
            throw new IllegalArgumentException("adminId must not be null");
        }
    }

    private void validateChannelId(Long channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId must not be null");
        }
    }

    private void validateReadyToSend(Response response) {
        if (response.getStatus() != ResponseStatus.READY_TO_SEND) {
            throw new IllegalArgumentException("response status must be READY_TO_SEND");
        }
    }

    private void saveProcessingLog(
            Long inquiryId,
            Long inquiryResultId,
            Long responseId,
            Long adminId,
            ActorType actorType,
            ProcessingEventType eventType,
            String previousState,
            String currentState
    ) {
        ProcessingLog processingLog = ProcessingLog.builder()
                .inquiryId(inquiryId)
                .inquiryResultId(inquiryResultId)
                .responseId(responseId)
                .adminId(adminId)
                .actorType(actorType)
                .eventType(eventType)
                .previousState(previousState)
                .currentState(currentState)
                .build();

        processingLogRepository.save(processingLog);
    }
}
