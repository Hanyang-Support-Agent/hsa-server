package com.example.hsa_core.domain.response.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResponseSendRequest(
        @NotNull Long channelId,
        @NotBlank String recipientIdentifier
) {
}
