package com.example.hsa_core.domain.channel.webhook;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WebhookRequestDTO {
    private Long customerId;
    private String content;
}
