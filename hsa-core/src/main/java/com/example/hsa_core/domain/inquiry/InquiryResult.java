package com.example.hsa_core.domain.inquiry;

import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryData;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryError;
import com.example.hsa_core.domain.inquiry.dto.ai.AiInquiryResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Entity
@Table(name = "inquiry_result")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// AI 처리 결과 중 답변 본문을 제외한 분석 메타데이터를 저장합니다.
public class InquiryResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    // 문의 분석 결과 고유 식별자입니다.
    private Long id;

    @Column(name = "inquiry_id", nullable = false, unique = true)
    // 분석 결과가 연결된 문의 식별자입니다.
    private Long inquiryId;

    @Column(name = "delivery_id")
    // 배송 데이터와 연결할 때 사용하는 식별자입니다.
    private Long deliveryId;

    @Column(name = "order_id")
    // 주문 데이터와 연결할 때 사용하는 식별자입니다.
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    // AI가 분류한 문의 유형입니다.
    private InquiryCategory category;

    @Column(name = "auto_reply", nullable = false)
    // AI 판단상 자동응답을 즉시 보낼 수 있는지 나타냅니다.
    private boolean autoReply = false;

    @Column(name = "admin_review", nullable = false)
    // 관리자 검토가 필요한 문의인지 나타냅니다.
    private boolean adminReview = true;

    @Column(name = "reason", columnDefinition = "TEXT")
    // AI가 자동응답 또는 관리자 검토를 판단한 이유입니다.
    private String reason;

    @Column(name = "risk_tags", columnDefinition = "TEXT")
    // 환불, 클레임, 정책 충돌 등 AI가 판단한 위험 태그 목록입니다.
    private String riskTags;

    @Column(name = "used_sources", columnDefinition = "TEXT")
    // AI 판단에 사용된 context 필드나 정책 문서 식별자 목록입니다.
    private String usedSources;

    @Column(name = "error_code")
    // AI 처리 실패 시 반환된 오류 코드입니다.
    private String errorCode;

    @Column(name = "error_message")
    // AI 처리 실패 시 반환된 오류 메시지입니다.
    private String errorMessage;

    @Column(name = "created_time", nullable = false, updatable = false)
    // 분석 결과가 저장된 시각입니다.
    private OffsetDateTime createdTime;

    public InquiryResult(
            Long inquiryId,
            Long deliveryId,
            Long orderId,
            InquiryCategory category,
            boolean autoReply,
            boolean adminReview,
            String reason,
            String riskTags,
            String usedSources,
            String errorCode,
            String errorMessage
    ) {
        this.inquiryId = inquiryId;
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.category = category;
        this.autoReply = autoReply;
        this.adminReview = adminReview;
        this.reason = reason;
        this.riskTags = riskTags;
        this.usedSources = usedSources;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    // AI 응답에서 문의 상태 판단 근거와 오류 정보를 추출해 분석 결과 엔티티를 생성합니다.
    public static InquiryResult fromAiResponse(Long inquiryId, AiInquiryResponse response) {
        AiInquiryData data = response.data();
        AiInquiryError error = response.error();

        return new InquiryResult(
                inquiryId,
                null,
                null,
                InquiryCategory.OTHER,
                data != null && data.autoReplyAvailable(),
                data == null || data.needsAdminReview(),
                data != null ? data.reason() : null,
                data != null ? joinValues(data.riskTags()) : null,
                data != null ? joinValues(data.usedSources()) : null,
                error != null ? error.code() : null,
                error != null ? error.message() : null
        );
    }

    // 리스트 형태의 AI 메타데이터를 단일 TEXT 컬럼에 저장할 문자열로 변환합니다.
    private static String joinValues(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }

        return String.join(",", values);
    }

    // 최초 저장 시 분석 결과 생성 시각을 기록합니다.
    @PrePersist
    protected void onCreate() {
        this.createdTime = OffsetDateTime.now();
    }
}
