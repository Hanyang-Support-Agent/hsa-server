package com.example.hsa_core.domain.response.dto;

import com.example.hsa_core.domain.response.SendStatus;
import com.example.hsa_core.domain.response.Transmission;

import java.time.OffsetDateTime;

public record TransmissionResponse(
        Long id,
        Long responseId,
        Long channelId,
        String recipientIdentifier,
        Integer attemptNo,
        SendStatus sendStatus,
        String externalMessageId,
        String errorMessage,
        OffsetDateTime requestedTime,
        OffsetDateTime sentTime
) {

    public static TransmissionResponse from(Transmission transmission) {
        return new TransmissionResponse(
                transmission.getId(),
                transmission.getResponseId(),
                transmission.getChannelId(),
                transmission.getRecipientIdentifier(),
                transmission.getAttemptNo(),
                transmission.getSendStatus(),
                transmission.getExternalMessageId(),
                transmission.getErrorMessage(),
                transmission.getRequestedTime(),
                transmission.getSentTime()
        );
    }
}
