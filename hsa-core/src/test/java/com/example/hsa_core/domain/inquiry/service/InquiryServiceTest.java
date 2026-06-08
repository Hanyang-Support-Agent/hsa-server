package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.channel.ChannelType;
import com.example.hsa_core.domain.inquiry.InquiryStatus;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateRequest;
import com.example.hsa_core.domain.inquiry.dto.InquiryCreateResponse;
import com.example.hsa_core.domain.inquiry.repository.InquiryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InquiryServiceTest {

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private InquiryRepository inquiryRepository;

    @AfterEach
    void tearDown() {
        inquiryRepository.deleteAll();
    }

    @Test
    void createInquirySavesInquiryWithReceivedStatus() {
        InquiryCreateRequest request = new InquiryCreateRequest(1L, "배송이 언제 도착하나요?", ChannelType.KAKAO);

        InquiryCreateResponse response = inquiryService.createInquiry(request);

        assertThat(response.inquiryId()).isNotNull();
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.content()).isEqualTo("배송이 언제 도착하나요?");
        assertThat(response.status()).isEqualTo(InquiryStatus.RECEIVED);
        assertThat(response.createdTime()).isNotNull();
        assertThat(inquiryRepository.findById(response.inquiryId()))
                .isPresent()
                .get()
                .extracting("status")
                .isEqualTo(InquiryStatus.RECEIVED);
        assertThat(inquiryRepository.findById(response.inquiryId()))
                .isPresent()
                .get()
                .extracting("channelType")
                .isEqualTo(ChannelType.KAKAO);
    }
}
