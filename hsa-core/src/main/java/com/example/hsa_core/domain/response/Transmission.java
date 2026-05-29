package com.example.hsa_core.domain.response;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "transmissions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
// 최종 답변을 외부 채널로 전송한 요청과 결과를 관리합니다.
public class Transmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    // 전송 요청 고유 식별자입니다.
    private Long id;

    @Column(name = "response_id", nullable = false)
    // 전송 대상 답변 식별자입니다.
    private Long responseId;

    @Column(name = "channel_id", nullable = false)
    // 답변을 전송할 채널 식별자입니다.
    private Long channelId;

    @Column(name = "recipient_identifier", nullable = false)
    // 수신자를 식별하는 채널별 주소 또는 계정 값입니다.
    private String recipientIdentifier;

    @Column(name = "attempt_no", nullable = false)
    @Builder.Default
    // 동일 답변 전송의 시도 횟수입니다.
    private Integer attemptNo = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "send_status", nullable = false, length = 50)
    // 외부 채널 전송 처리 결과 상태입니다.
    private SendStatus sendStatus;

    @Column(name = "external_message_id")
    // 외부 전송 시스템에서 반환한 메시지 식별자입니다.
    private String externalMessageId;

    @Column(name = "error_message")
    // 전송 실패 시 기록할 오류 메시지입니다.
    private String errorMessage;

    @Column(name = "requested_time", nullable = false, updatable = false)
    // 전송 요청이 생성된 시각입니다.
    private OffsetDateTime requestedTime;

    @Column(name = "sent_time")
    // 전송이 성공 처리된 시각입니다.
    private OffsetDateTime sentTime;

    // 전송 성공 결과와 외부 메시지 식별자를 기록합니다.
    public void markSuccess(String externalMessageId) {
        this.sendStatus = SendStatus.SUCCESS;
        this.externalMessageId = externalMessageId;
        this.sentTime = OffsetDateTime.now();
    }

    // 전송 실패 결과와 실패 사유를 기록합니다.
    public void markFail(String errorMessage) {
        this.sendStatus = SendStatus.FAIL;
        this.errorMessage = errorMessage;
    }

    // 전송 재시도 중인 상태로 변경합니다.
    public void markRetrying() {
        this.sendStatus = SendStatus.RETRYING;
    }

    // 최초 저장 시 요청 시각과 기본 시도 횟수를 채웁니다.
    @PrePersist
    protected void onCreate() {
        this.requestedTime = OffsetDateTime.now();

        if (this.attemptNo == null) {
            this.attemptNo = 1;
        }
    }
}
