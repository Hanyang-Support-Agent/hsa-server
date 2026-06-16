package com.example.hsa_core.domain.admin.service;

import com.example.hsa_core.domain.admin.Admin;
import com.example.hsa_core.domain.admin.repository.AdminRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    @Test
    void createAdminSavesAdmin() {
        Admin admin = adminService.createAdmin("manager");

        assertThat(admin.getId()).isNotNull();
        assertThat(admin.getName()).isEqualTo("manager");
    }

    @Test
    void createAdminThrowsExceptionWhenNameIsBlank() {
        assertThatThrownBy(() -> adminService.createAdmin(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name must not be blank");

        assertThat(adminRepository.count()).isZero();
    }

    @Test
    void getAdminsReturnsAllAdminsByIdAsc() {
        Admin firstAdmin = adminService.createAdmin("first");
        Admin secondAdmin = adminService.createAdmin("second");

        List<Admin> admins = adminService.getAdmins();

        assertThat(admins).extracting(Admin::getId)
                .containsExactly(firstAdmin.getId(), secondAdmin.getId());
    }

    @Test
    void getAdminReturnsAdmin() {
        Admin admin = adminService.createAdmin("manager");

        Admin foundAdmin = adminService.getAdmin(admin.getId());

        assertThat(foundAdmin.getId()).isEqualTo(admin.getId());
        assertThat(foundAdmin.getName()).isEqualTo("manager");
    }

    @Test
    void getAdminThrowsExceptionWhenAdminDoesNotExist() {
        assertThatThrownBy(() -> adminService.getAdmin(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("admin not found");
    }

    @Test
    void updateAdminNameChangesName() {
        Admin admin = adminService.createAdmin("before");

        Admin updatedAdmin = adminService.updateAdminName(admin.getId(), "after");

        assertThat(updatedAdmin.getName()).isEqualTo("after");

        Admin foundAdmin = adminRepository.findById(admin.getId()).orElseThrow();
        assertThat(foundAdmin.getName()).isEqualTo("after");
    }

    @Test
    void updateAdminNameThrowsExceptionWhenNameIsBlank() {
        Admin admin = adminService.createAdmin("manager");

        assertThatThrownBy(() -> adminService.updateAdminName(admin.getId(), " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name must not be blank");
    }

    @Test
    void deleteAdminDeletesAdmin() {
        Admin admin = adminService.createAdmin("manager");

        adminService.deleteAdmin(admin.getId());

        assertThat(adminRepository.existsById(admin.getId())).isFalse();
    }

    @Test
    void deleteAdminThrowsExceptionWhenAdminDoesNotExist() {
        assertThatThrownBy(() -> adminService.deleteAdmin(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("admin not found");
    }
}
