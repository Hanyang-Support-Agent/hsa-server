package com.example.hsa_core.domain.response;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "responses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
// 문의 분석 결과를 바탕으로 생성된 답변 초안과 최종 답변을 관리합니다.
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    // 답변 고유 식별자입니다.
    private Long id;

    @Column(name = "inquiry_id", nullable = false)
    // 답변이 연결된 문의 식별자입니다.
    private Long inquiryId;

    @Column(name = "inquiry_result_id", nullable = false)
    // 답변 생성 기준이 된 문의 분석 결과 식별자입니다.
    private Long inquiryResultId;

    @Column(name = "admin_id")
    // 답변을 검토하거나 수정한 관리자 식별자입니다.
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_type", nullable = false, length = 50)
    // 답변 생성 방식을 나타냅니다.
    private ResponseType responseType;

    @Column(name = "draft_content", columnDefinition = "TEXT")
    // 자동 또는 AI로 생성된 답변 초안 내용입니다.
    private String draftContent;

    @Column(name = "final_content", columnDefinition = "TEXT")
    // 최종 확정되어 전송될 답변 내용입니다.
    private String finalContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    // 답변 작성과 전송 흐름의 현재 상태입니다.
    private ResponseStatus status = ResponseStatus.DRAFTED;

    @Column(name = "created_time", nullable = false, updatable = false)
    // 답변 레코드가 생성된 시각입니다.
    private OffsetDateTime createdTime;

    @Column(name = "updated_time")
    // 답변 레코드가 마지막으로 수정된 시각입니다.
    private OffsetDateTime updatedTime;

    // 관리자 검토 후 확정된 최종 답변 내용을 변경합니다.
    public void updateFinalContent(String finalContent) {
        this.finalContent = finalContent;
    }

    // 답변 작성, 발송 준비, 발송 완료 등 처리 상태를 변경합니다.
    public void changeStatus(ResponseStatus status) {
        this.status = status;
    }

    // 최초 저장 시 생성 시각과 기본 상태를 채웁니다.
    @PrePersist
    protected void onCreate() {
        this.createdTime = OffsetDateTime.now();

        if (this.status == null) {
            this.status = ResponseStatus.DRAFTED;
        }
    }

    // 엔티티 수정 시 수정 시각을 갱신합니다.
    @PreUpdate
    protected void onUpdate() {
        this.updatedTime = OffsetDateTime.now();
    }
}
