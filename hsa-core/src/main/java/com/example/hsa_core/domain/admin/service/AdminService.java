package com.example.hsa_core.domain.admin.service;

import com.example.hsa_core.domain.admin.Admin;
import com.example.hsa_core.domain.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    @Transactional
    public Admin createAdmin(String name) {
        validateName(name);

        // 검증된 이름으로 관리자 엔티티를 생성하고 저장합니다.
        Admin admin = Admin.builder()
                .name(name)
                .build();

        return adminRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public List<Admin> getAdmins() {
        return adminRepository.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public Admin getAdmin(Long adminId) {
        return findAdmin(adminId);
    }

    @Transactional
    public Admin updateAdminName(Long adminId, String name) {
        Admin admin = findAdmin(adminId);
        validateName(name);

        // 관리자 이름은 의미 있는 도메인 메서드로 변경합니다.
        admin.updateName(name);

        return admin;
    }

    @Transactional
    public void deleteAdmin(Long adminId) {
        Admin admin = findAdmin(adminId);

        // 존재하는 관리자만 삭제합니다.
        adminRepository.delete(admin);
    }

    private Admin findAdmin(Long adminId) {
        if (adminId == null) {
            throw new IllegalArgumentException("adminId must not be null");
        }

        return adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("admin not found"));
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }
}
