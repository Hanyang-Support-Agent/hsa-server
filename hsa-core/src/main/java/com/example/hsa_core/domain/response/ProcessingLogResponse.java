package com.example.hsa_core.domain.response;

import java.time.OffsetDateTime;

public record ProcessingLogResponse(
        String eventType,
        String actorType,
        String detail,
        OffsetDateTime createdTime
) {
    public static ProcessingLogResponse from(ProcessingLog log){
        return new ProcessingLogResponse(
                log.getEventType().name(),
                log.getActorType().name(),
                log.getCurrentState(),
                log.getCreatedTime()
        );
    }
}
