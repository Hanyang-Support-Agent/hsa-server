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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ResponseServiceTest {

    @Autowired
    private ResponseService responseService;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private ProcessingLogRepository processingLogRepository;

    @Autowired
    private TransmissionRepository transmissionRepository;

    @AfterEach
    void tearDown() {
        transmissionRepository.deleteAll();
        processingLogRepository.deleteAll();
        responseRepository.deleteAll();
    }

    @Test
    void createDraftResponseSavesDraftAndProcessingLog() {
        Response response = responseService.createDraftResponse(1L, 10L, "AI draft answer");

        assertThat(response.getId()).isNotNull();
        assertThat(response.getInquiryId()).isEqualTo(1L);
        assertThat(response.getInquiryResultId()).isEqualTo(10L);
        assertThat(response.getResponseType()).isEqualTo(ResponseType.AI_DRAFT);
        assertThat(response.getDraftContent()).isEqualTo("AI draft answer");
        assertThat(response.getFinalContent()).isNull();
        assertThat(response.getStatus()).isEqualTo(ResponseStatus.DRAFTED);
        assertThat(response.getCreatedTime()).isNotNull();

        List<ProcessingLog> logs = processingLogRepository.findByResponseIdOrderByCreatedTimeAsc(response.getId());

        assertThat(logs).hasSize(1);
        assertThat(logs.getFirst().getInquiryId()).isEqualTo(1L);
        assertThat(logs.getFirst().getInquiryResultId()).isEqualTo(10L);
        assertThat(logs.getFirst().getActorType()).isEqualTo(ActorType.SYSTEM);
        assertThat(logs.getFirst().getEventType()).isEqualTo(ProcessingEventType.DRAFT_CREATED);
        assertThat(logs.getFirst().getPreviousState()).isNull();
        assertThat(logs.getFirst().getCurrentState()).isEqualTo("AI draft answer");
    }

    @Test
    void createAutoResponseSavesFinalContentAndProcessingLog() {
        Response response = responseService.createAutoResponse(1L, 10L, "Auto reply answer");

        assertThat(response.getId()).isNotNull();
        assertThat(response.getInquiryId()).isEqualTo(1L);
        assertThat(response.getInquiryResultId()).isEqualTo(10L);
        assertThat(response.getResponseType()).isEqualTo(ResponseType.AUTO);
        assertThat(response.getDraftContent()).isNull();
        assertThat(response.getFinalContent()).isEqualTo("Auto reply answer");
        assertThat(response.getStatus()).isEqualTo(ResponseStatus.READY_TO_SEND);

        List<ProcessingLog> logs = processingLogRepository.findByResponseIdOrderByCreatedTimeAsc(response.getId());

        assertThat(logs).hasSize(1);
        assertThat(logs.getFirst().getEventType()).isEqualTo(ProcessingEventType.CONFIRMED);
        assertThat(logs.getFirst().getCurrentState()).isEqualTo("Auto reply answer");
    }

    @Test
    void createDraftResponseThrowsExceptionWhenContentIsBlank() {
        assertThatThrownBy(() -> responseService.createDraftResponse(1L, 10L, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("draftContent must not be blank");

        assertThat(responseRepository.count()).isZero();
        assertThat(processingLogRepository.count()).isZero();
    }

    @Test
    void modifyResponseUpdatesFinalContentAndSavesProcessingLog() {
        Response response = responseService.createAutoResponse(1L, 10L, "before answer");

        Response modifiedResponse = responseService.modifyResponse(response.getId(), 100L, "after answer");

        assertThat(modifiedResponse.getFinalContent()).isEqualTo("after answer");
        assertThat(modifiedResponse.getAdminId()).isEqualTo(100L);
        assertThat(modifiedResponse.getStatus()).isEqualTo(ResponseStatus.MODIFIED);

        List<ProcessingLog> logs = processingLogRepository.findByResponseIdOrderByCreatedTimeAsc(response.getId());

        assertThat(logs).hasSize(2);
        assertThat(logs.get(1).getInquiryId()).isEqualTo(1L);
        assertThat(logs.get(1).getInquiryResultId()).isEqualTo(10L);
        assertThat(logs.get(1).getResponseId()).isEqualTo(response.getId());
        assertThat(logs.get(1).getAdminId()).isEqualTo(100L);
        assertThat(logs.get(1).getActorType()).isEqualTo(ActorType.ADMIN);
        assertThat(logs.get(1).getEventType()).isEqualTo(ProcessingEventType.ADMIN_MODIFIED);
        assertThat(logs.get(1).getPreviousState()).isEqualTo("before answer");
        assertThat(logs.get(1).getCurrentState()).isEqualTo("after answer");
    }

    @Test
    void modifyResponseThrowsExceptionWhenAdminIdIsNull() {
        Response response = responseService.createAutoResponse(1L, 10L, "before answer");

        assertThatThrownBy(() -> responseService.modifyResponse(response.getId(), null, "after answer"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("adminId must not be null");
    }

    @Test
    void modifyResponseThrowsExceptionWhenFinalContentIsBlank() {
        Response response = responseService.createAutoResponse(1L, 10L, "before answer");

        assertThatThrownBy(() -> responseService.modifyResponse(response.getId(), 100L, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("finalContent must not be blank");
    }

    @Test
    void confirmResponseChangesStatusToReadyToSendAndSavesProcessingLog() {
        Response response = responseService.createDraftResponse(1L, 10L, "draft answer");
        responseService.modifyResponse(response.getId(), 100L, "final answer");

        Response confirmedResponse = responseService.confirmResponse(response.getId(), 100L);

        assertThat(confirmedResponse.getStatus()).isEqualTo(ResponseStatus.READY_TO_SEND);

        List<ProcessingLog> logs = processingLogRepository.findByResponseIdOrderByCreatedTimeAsc(response.getId());

        assertThat(logs).hasSize(3);
        assertThat(logs.get(2).getInquiryId()).isEqualTo(1L);
        assertThat(logs.get(2).getInquiryResultId()).isEqualTo(10L);
        assertThat(logs.get(2).getResponseId()).isEqualTo(response.getId());
        assertThat(logs.get(2).getAdminId()).isEqualTo(100L);
        assertThat(logs.get(2).getActorType()).isEqualTo(ActorType.ADMIN);
        assertThat(logs.get(2).getEventType()).isEqualTo(ProcessingEventType.CONFIRMED);
        assertThat(logs.get(2).getPreviousState()).isEqualTo(ResponseStatus.MODIFIED.name());
        assertThat(logs.get(2).getCurrentState()).isEqualTo(ResponseStatus.READY_TO_SEND.name());
    }

    @Test
    void confirmResponseThrowsExceptionWhenAdminIdIsNull() {
        Response response = responseService.createAutoResponse(1L, 10L, "final answer");

        assertThatThrownBy(() -> responseService.confirmResponse(response.getId(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("adminId must not be null");
    }

    @Test
    void confirmResponseThrowsExceptionWhenFinalContentIsBlank() {
        Response response = responseService.createDraftResponse(1L, 10L, "draft answer");

        assertThatThrownBy(() -> responseService.confirmResponse(response.getId(), 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("finalContent must not be blank");
    }

    @Test
    void sendResponseMockSavesTransmissionAndChangesStatusToSent() {
        Response response = responseService.createAutoResponse(1L, 10L, "final answer");

        Transmission transmission = responseService.sendResponseMock(response.getId(), 20L, "customer-1");

        assertThat(transmission.getId()).isNotNull();
        assertThat(transmission.getResponseId()).isEqualTo(response.getId());
        assertThat(transmission.getChannelId()).isEqualTo(20L);
        assertThat(transmission.getRecipientIdentifier()).isEqualTo("customer-1");
        assertThat(transmission.getAttemptNo()).isEqualTo(1);
        assertThat(transmission.getSendStatus()).isEqualTo(SendStatus.SUCCESS);
        assertThat(transmission.getExternalMessageId()).isEqualTo("mock-" + response.getId() + "-" + transmission.getId());
        assertThat(transmission.getSentTime()).isNotNull();

        Response sentResponse = responseRepository.findById(response.getId()).orElseThrow();
        assertThat(sentResponse.getStatus()).isEqualTo(ResponseStatus.SENT);

        List<ProcessingLog> logs = processingLogRepository.findByResponseIdOrderByCreatedTimeAsc(response.getId());

        assertThat(logs).hasSize(2);
        assertThat(logs.get(1).getActorType()).isEqualTo(ActorType.SYSTEM);
        assertThat(logs.get(1).getEventType()).isEqualTo(ProcessingEventType.SENT);
        assertThat(logs.get(1).getPreviousState()).isEqualTo(ResponseStatus.READY_TO_SEND.name());
        assertThat(logs.get(1).getCurrentState()).isEqualTo(ResponseStatus.SENT.name());
    }

    @Test
    void sendResponseMockThrowsExceptionWhenChannelIdIsNull() {
        Response response = responseService.createAutoResponse(1L, 10L, "final answer");

        assertThatThrownBy(() -> responseService.sendResponseMock(response.getId(), null, "customer-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("channelId must not be null");
    }

    @Test
    void sendResponseMockThrowsExceptionWhenRecipientIdentifierIsBlank() {
        Response response = responseService.createAutoResponse(1L, 10L, "final answer");

        assertThatThrownBy(() -> responseService.sendResponseMock(response.getId(), 20L, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("recipientIdentifier must not be blank");
    }

    @Test
    void sendResponseMockThrowsExceptionWhenStatusIsNotReadyToSend() {
        Response response = responseService.createDraftResponse(1L, 10L, "draft answer");
        responseService.modifyResponse(response.getId(), 100L, "final answer");

        assertThatThrownBy(() -> responseService.sendResponseMock(response.getId(), 20L, "customer-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("response status must be READY_TO_SEND");
    }

    @Test
    void getResponsesByInquiryIdReturnsResponsesByCreatedTimeAsc() throws InterruptedException {
        Response firstResponse = responseService.createDraftResponse(1L, 10L, "first");
        Thread.sleep(5);
        Response secondResponse = responseService.createDraftResponse(1L, 11L, "second");
        responseService.createDraftResponse(2L, 12L, "other inquiry");

        List<Response> responses = responseService.getResponsesByInquiryId(1L);

        assertThat(responses).extracting(Response::getId)
                .containsExactly(firstResponse.getId(), secondResponse.getId());
    }

    @Test
    void getLatestResponseByInquiryIdReturnsLatestResponse() throws InterruptedException {
        responseService.createDraftResponse(1L, 10L, "first");
        Thread.sleep(5);
        Response latestResponse = responseService.createDraftResponse(1L, 11L, "latest");

        Response response = responseService.getLatestResponseByInquiryId(1L);

        assertThat(response.getId()).isEqualTo(latestResponse.getId());
    }

    @Test
    void getLatestResponseByInquiryIdThrowsExceptionWhenResponseDoesNotExist() {
        assertThatThrownBy(() -> responseService.getLatestResponseByInquiryId(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("response not found");
    }

    @Test
    void getTransmissionsByResponseIdReturnsTransmissionsByRequestedTimeAsc() throws InterruptedException {
        Response response = responseService.createAutoResponse(1L, 10L, "final answer");
        Transmission firstTransmission = transmissionRepository.saveAndFlush(
                Transmission.builder()
                        .responseId(response.getId())
                        .channelId(20L)
                        .recipientIdentifier("first")
                        .sendStatus(SendStatus.RETRYING)
                        .build()
        );
        Thread.sleep(5);
        Transmission secondTransmission = transmissionRepository.saveAndFlush(
                Transmission.builder()
                        .responseId(response.getId())
                        .channelId(20L)
                        .recipientIdentifier("second")
                        .sendStatus(SendStatus.RETRYING)
                        .build()
        );

        List<Transmission> transmissions = responseService.getTransmissionsByResponseId(response.getId());

        assertThat(transmissions).extracting(Transmission::getId)
                .containsExactly(firstTransmission.getId(), secondTransmission.getId());
    }

    @Test
    void getTransmissionsByResponseIdThrowsExceptionWhenResponseDoesNotExist() {
        assertThatThrownBy(() -> responseService.getTransmissionsByResponseId(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("response not found");
    }
}
