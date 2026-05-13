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

public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private InquiryStatus status = InquiryStatus.RECEIVED; //RECEIVED 상태로 기본 저장

    @Column(name = "created_time", nullable = false, updatable = false)
    private OffsetDateTime createdTime;

    @Column(name = "updated_time")
    private OffsetDateTime updatedTime;

    public Inquiry(Long customerId, String content) {
        this.customerId = customerId;
        this.content = content;
        this.status = InquiryStatus.RECEIVED;
    }

    public void changeStatus(InquiryStatus status) {
        this.status = status;
    }

    @PrePersist //처음 저장하기전 문의 업로드 시간 업데이트
    protected void onCreate() {
        this.createdTime = OffsetDateTime.now();

        if (this.status == null) {
            this.status = InquiryStatus.RECEIVED;
        } //상태 저장 안 돼있다면 다시 한 번 상태 업데이트
    }

    @PreUpdate //수정하기전 시간 업데이트
    protected void onUpdate() {
        this.updatedTime = OffsetDateTime.now();
    }
}
