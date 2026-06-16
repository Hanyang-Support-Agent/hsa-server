package com.example.hsa_core.domain.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminCreateRequest(
        @NotBlank String name
) {
}
