package com.example.hsa_core.domain.mock;


import com.example.hsa_core.domain.customer.Customer;
import com.example.hsa_core.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mock_deliveries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MockDelivery{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 20)
    private String trackingNumber;

    @Column(nullable = false, length = 20)
    private String carrier; // 택배사

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(nullable = false, length = 20)
    private String currentLocation; // 현재 위치
}
