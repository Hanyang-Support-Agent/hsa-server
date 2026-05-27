package com.example.hsa_core.domain.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InquiryCreateRequest(
        @NotNull Long customerId,
        @NotBlank String content
) {
}
