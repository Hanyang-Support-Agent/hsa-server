package com.example.hsa_core.domain.response.dto;

import jakarta.validation.constraints.NotNull;

public record ResponseConfirmRequest(
        @NotNull Long adminId
) {
}
