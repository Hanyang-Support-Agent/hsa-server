package com.example.hsa_core.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminUpdateRequest(
        @NotBlank String name
) {
}
