package com.example.hsa_core.domain.inquiry;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
@Getter
@Entity
@Table(name = "inquiry_result")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 결과아이디

    @Column(name = "inquiry_id", nullable = false, unique = true)
    private Long inquiryId; // 대응되는 문의 번호

    @Column(name = "delivery_id")
    private Long deliveryId; //배송 데이터 관련 저장 값

    @Column(name = "order_id")
    private Long orderId; //주문 데이터 관련 저장 값

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private InquiryCategory category; //문의 유형

    @Column(name = "auto_reply", nullable = false)
    private boolean autoReply = false; //자동 응답 여부

    @Column(name = "admin_review", nullable = false)
    private boolean adminReview = true; //관리자 검토 필요 여부 기본 값은 검토 필요

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason; //분석 결과 근거

    @Column(name = "created_time", nullable = false, updatable = false)
    private OffsetDateTime createdTime; //분석 결과 생성 시간

    public InquiryResult(
            Long inquiryId,
            Long deliveryId,
            Long orderId,
            InquiryCategory category,
            boolean autoReply,
            boolean adminReview,
            String reason
    ) {
        this.inquiryId = inquiryId;
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.category = category;
        this.autoReply = autoReply;
        this.adminReview = adminReview;
        this.reason = reason;
    }

    @PrePersist
    protected void onCreate() {
        this.createdTime = OffsetDateTime.now();
    }
}