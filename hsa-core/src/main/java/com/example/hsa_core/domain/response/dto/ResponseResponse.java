package com.example.hsa_core.domain.response.dto;

import com.example.hsa_core.domain.response.Response;
import com.example.hsa_core.domain.response.ResponseStatus;
import com.example.hsa_core.domain.response.ResponseType;

import java.time.OffsetDateTime;

public record ResponseResponse(
        Long id,
        Long inquiryId,
        Long inquiryResultId,
        Long adminId,
        ResponseType responseType,
        String draftContent,
        String finalContent,
        ResponseStatus status,
        OffsetDateTime createdTime,
        OffsetDateTime updatedTime
) {

    public static ResponseResponse from(Response response) {
        return new ResponseResponse(
                response.getId(),
                response.getInquiryId(),
                response.getInquiryResultId(),
                response.getAdminId(),
                response.getResponseType(),
                response.getDraftContent(),
                response.getFinalContent(),
                response.getStatus(),
                response.getCreatedTime(),
                response.getUpdatedTime()
        );
    }
}
