package com.example.hsa_core.domain.response.service;

import com.example.hsa_core.domain.response.ActorType;
import com.example.hsa_core.domain.response.ProcessingEventType;
import com.example.hsa_core.domain.response.ProcessingLog;
import com.example.hsa_core.domain.response.Response;
import com.example.hsa_core.domain.response.ResponseStatus;
import com.example.hsa_core.domain.response.ResponseType;
import com.example.hsa_core.domain.response.repository.ProcessingLogRepository;
import com.example.hsa_core.domain.response.repository.ResponseRepository;
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

    @AfterEach
    void tearDown() {
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
}
