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
@Table(name = "processing_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
// 문의 처리 과정에서 발생한 주요 이벤트와 상태 변화를 기록합니다.
public class ProcessingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    // 처리 로그 고유 식별자입니다.
    private Long id;

    @Column(name = "inquiry_id", nullable = false)
    // 로그가 연결된 문의 식별자입니다.
    private Long inquiryId;

    @Column(name = "inquiry_result_id")
    // 로그가 연결된 문의 분석 결과 식별자입니다.
    private Long inquiryResultId;

    @Column(name = "response_id")
    // 로그가 연결된 답변 식별자입니다.
    private Long responseId;

    @Column(name = "admin_id")
    // 로그를 발생시킨 관리자 식별자입니다.
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false, length = 50)
    // 로그 이벤트를 발생시킨 주체 유형입니다.
    private ActorType actorType;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 100)
    // 문의 처리 과정에서 발생한 이벤트 유형입니다.
    private ProcessingEventType eventType;

    @Column(name = "previous_state", columnDefinition = "TEXT")
    // 이벤트 발생 전 상태 또는 데이터입니다.
    private String previousState;

    @Column(name = "current_state", columnDefinition = "TEXT")
    // 이벤트 발생 후 상태 또는 데이터입니다.
    private String currentState;

    @Column(name = "created_time", nullable = false, updatable = false)
    // 로그가 생성된 시각입니다.
    private OffsetDateTime createdTime;

    // 최초 저장 시 로그 생성 시각을 기록합니다.
    @PrePersist
    protected void onCreate() {
        this.createdTime = OffsetDateTime.now();
    }
}
