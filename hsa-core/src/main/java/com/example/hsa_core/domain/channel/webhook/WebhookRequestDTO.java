package com.example.hsa_core.domain.channel.webhook;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "웹훅 문의 접수 요청")
public class WebhookRequestDTO {
    @Schema(description = "고객 식별자 (카톡 유저키, 이메일 주소 등)", example = "kakao_user_001")
    private String identifier;

    @Schema(description = "고객 문의 내용", example = "제 주문 언제 오나요?")
    private String content;
}
