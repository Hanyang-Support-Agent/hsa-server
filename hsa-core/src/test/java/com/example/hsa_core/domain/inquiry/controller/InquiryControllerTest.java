package com.example.hsa_core.domain.inquiry.controller;

import com.example.hsa_core.domain.inquiry.InquiryResult;
import com.example.hsa_core.domain.inquiry.service.AiInquiryProcessingService;
import com.example.hsa_core.domain.inquiry.service.InquiryService;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InquiryControllerTest {

    private final InquiryService inquiryService = mock(InquiryService.class);
    private final AiInquiryProcessingService aiInquiryProcessingService = mock(AiInquiryProcessingService.class);
    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new InquiryController(inquiryService, aiInquiryProcessingService, jdbcTemplate))
            .build();

    @Test
    // AI 처리 요청 API가 service를 호출하고 저장된 분석 결과 정보를 반환하는지 확인합니다.
    void requestAiProcessingReturnsInquiryResultInfo() throws Exception {
        InquiryResult inquiryResult = mock(InquiryResult.class);
        when(inquiryResult.getId()).thenReturn(100L);
        when(inquiryResult.getInquiryId()).thenReturn(10L);
        when(aiInquiryProcessingService.requestProcessing(10L)).thenReturn(inquiryResult);

        mockMvc.perform(post("/api/inquiries/10/ai-processing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.inquiryResultId").value(100L))
                .andExpect(jsonPath("$.result.inquiryId").value(10L));

        verify(aiInquiryProcessingService).requestProcessing(10L);
    }
}
