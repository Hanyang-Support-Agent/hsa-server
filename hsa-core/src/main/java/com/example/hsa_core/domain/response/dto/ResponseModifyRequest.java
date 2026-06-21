package com.example.hsa_core.domain.response.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResponseModifyRequest(
        @NotNull Long adminId,
        @NotBlank String finalContent
) {
}
