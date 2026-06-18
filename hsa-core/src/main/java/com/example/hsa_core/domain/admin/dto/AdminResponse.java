package com.example.hsa_core.domain.admin.dto;

import com.example.hsa_core.domain.admin.Admin;

import java.time.LocalDateTime;

public record AdminResponse(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static AdminResponse from(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getName(),
                admin.getCreatedAt(),
                admin.getUpdatedAt()
        );
    }
}
