package com.example.hsa_core.domain.inquiry;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Getter
@Entity
@Table(name = "inquiries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 고객 문의 원문과 현재 처리 상태를 관리합니다.
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    // 문의 고유 식별자입니다.
    private Long id;

    @Column(name = "customer_id", nullable = false)
    // 문의를 남긴 고객 식별자입니다.
    private Long customerId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    // 고객이 남긴 문의 원문입니다.
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    // 문의 접수부터 발송 완료까지의 처리 상태입니다.
    private InquiryStatus status = InquiryStatus.RECEIVED;

    @Column(name = "created_time", nullable = false, updatable = false)
    // 문의가 최초 접수된 시각입니다.
    private OffsetDateTime createdTime;

    @Column(name = "updated_time")
    // 문의 상태가 마지막으로 변경된 시각입니다.
    private OffsetDateTime updatedTime;

    public Inquiry(Long customerId, String content) {
        this.customerId = customerId;
        this.content = content;
        this.status = InquiryStatus.RECEIVED;
    }

    // AI 처리 또는 관리자 처리 흐름에 따라 문의 상태를 변경합니다.
    public void changeStatus(InquiryStatus status) {
        this.status = status;
    }

    // 최초 저장 시 접수 시각과 기본 상태를 채웁니다.
    @PrePersist
    protected void onCreate() {
        this.createdTime = OffsetDateTime.now();

        if (this.status == null) {
            this.status = InquiryStatus.RECEIVED;
        }
    }

    // 엔티티 수정 시 수정 시각을 갱신합니다.
    @PreUpdate
    protected void onUpdate() {
        this.updatedTime = OffsetDateTime.now();
    }
}
