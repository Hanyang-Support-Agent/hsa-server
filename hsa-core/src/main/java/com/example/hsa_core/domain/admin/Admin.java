package com.example.hsa_core.domain.admin;

import com.example.hsa_core.global.apiPayload.code.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "admins")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Admin extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    public void updateName(String name) {
        this.name = name;
    }
}
