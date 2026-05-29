package com.example.hsa_core.domain.customer;

import com.example.hsa_core.domain.channel.Channel;
import com.example.hsa_core.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    // 식별 값  (메일일 땐 메일 주소, 카톡일 땐 카톡 유저키)
    @Column(nullable = false, length = 100)
    private String identifier;


}
