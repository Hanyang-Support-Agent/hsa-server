package com.example.hsa_core.domain.response.controller;

import com.example.hsa_core.domain.response.Response;
import com.example.hsa_core.domain.response.ResponseStatus;
import com.example.hsa_core.domain.response.ResponseType;
import com.example.hsa_core.domain.response.SendStatus;
import com.example.hsa_core.domain.response.Transmission;
import com.example.hsa_core.domain.response.service.ResponseService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResponseControllerTest {

    private final ResponseService responseService = mock(ResponseService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ResponseController(responseService))
            .build();

    @Test
    void getResponsesByInquiryIdReturnsResponses() throws Exception {
        Response response = createResponse(1L, 10L, ResponseStatus.DRAFTED);
        when(responseService.getResponsesByInquiryId(10L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/admin/inquiries/10/responses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result[0].id").value(1L));

        verify(responseService).getResponsesByInquiryId(10L);
    }

    @Test
    void getLatestResponseByInquiryIdReturnsResponse() throws Exception {
        Response response = createResponse(2L, 10L, ResponseStatus.DRAFTED);
        when(responseService.getLatestResponseByInquiryId(10L)).thenReturn(response);

        mockMvc.perform(get("/api/admin/inquiries/10/responses/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(2L));

        verify(responseService).getLatestResponseByInquiryId(10L);
    }

    @Test
    void getTransmissionsByResponseIdReturnsTransmissions() throws Exception {
        Transmission transmission = Transmission.builder()
                .id(3L)
                .responseId(1L)
                .channelId(20L)
                .recipientIdentifier("customer-1")
                .attemptNo(1)
                .sendStatus(SendStatus.SUCCESS)
                .externalMessageId("mock-1-3")
                .build();
        when(responseService.getTransmissionsByResponseId(1L)).thenReturn(List.of(transmission));

        mockMvc.perform(get("/api/admin/responses/1/transmissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").value(3L))
                .andExpect(jsonPath("$.result[0].sendStatus").value("SUCCESS"));

        verify(responseService).getTransmissionsByResponseId(1L);
    }

    @Test
    void modifyResponseCallsService() throws Exception {
        Response response = createResponse(1L, 10L, ResponseStatus.MODIFIED);
        when(responseService.modifyResponse(1L, 100L, "modified answer")).thenReturn(response);

        mockMvc.perform(patch("/api/admin/responses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "adminId": 100,
                                  "finalContent": "modified answer"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.status").value("MODIFIED"));

        verify(responseService).modifyResponse(1L, 100L, "modified answer");
    }

    @Test
    void confirmResponseCallsService() throws Exception {
        Response response = createResponse(1L, 10L, ResponseStatus.READY_TO_SEND);
        when(responseService.confirmResponse(1L, 100L)).thenReturn(response);

        mockMvc.perform(patch("/api/admin/responses/1/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "adminId": 100
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.status").value("READY_TO_SEND"));

        verify(responseService).confirmResponse(1L, 100L);
    }

    @Test
    void sendResponseMockCallsService() throws Exception {
        Transmission transmission = Transmission.builder()
                .id(3L)
                .responseId(1L)
                .channelId(20L)
                .recipientIdentifier("customer-1")
                .attemptNo(1)
                .sendStatus(SendStatus.SUCCESS)
                .build();
        when(responseService.sendResponseMock(1L, 20L, "customer-1")).thenReturn(transmission);

        mockMvc.perform(post("/api/admin/responses/1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "channelId": 20,
                                  "recipientIdentifier": "customer-1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.sendStatus").value("SUCCESS"));

        verify(responseService).sendResponseMock(1L, 20L, "customer-1");
    }

    @Test
    void modifyResponseReturnsBadRequestWhenFinalContentIsBlank() throws Exception {
        mockMvc.perform(patch("/api/admin/responses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "adminId": 100,
                                  "finalContent": " "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmResponseReturnsBadRequestWhenAdminIdIsMissing() throws Exception {
        mockMvc.perform(patch("/api/admin/responses/1/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendResponseMockReturnsBadRequestWhenRecipientIdentifierIsBlank() throws Exception {
        mockMvc.perform(post("/api/admin/responses/1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "channelId": 20,
                                  "recipientIdentifier": " "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    private Response createResponse(Long id, Long inquiryId, ResponseStatus status) {
        return Response.builder()
                .id(id)
                .inquiryId(inquiryId)
                .inquiryResultId(100L)
                .responseType(ResponseType.AI_DRAFT)
                .draftContent("draft")
                .finalContent("final")
                .status(status)
                .build();
    }
}
