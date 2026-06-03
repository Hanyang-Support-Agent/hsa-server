package com.example.hsa_core.domain.inquiry.dto;

import com.example.hsa_core.domain.channel.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// 고객 문의를 저장할 때 클라이언트 또는 웹훅에서 전달하는 요청 DTO입니다.
public record InquiryCreateRequest(
        @NotNull Long customerId,
        @NotBlank String content,
        ChannelType channelType
) {
}
