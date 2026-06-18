package com.example.hsa_core.domain.admin.controller;

import com.example.hsa_core.domain.admin.Admin;
import com.example.hsa_core.domain.admin.dto.AdminCreateRequest;
import com.example.hsa_core.domain.admin.dto.AdminResponse;
import com.example.hsa_core.domain.admin.dto.AdminUpdateRequest;
import com.example.hsa_core.domain.admin.service.AdminService;
import com.example.hsa_core.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ApiResponse<AdminResponse>> createAdmin(
            @Valid @RequestBody AdminCreateRequest request
    ) {
        Admin admin = adminService.createAdmin(request.name());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(AdminResponse.from(admin)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminResponse>>> getAdmins() {
        List<AdminResponse> response = adminService.getAdmins().stream()
                .map(AdminResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<ApiResponse<AdminResponse>> getAdmin(
            @PathVariable Long adminId
    ) {
        Admin admin = adminService.getAdmin(adminId);

        return ResponseEntity.ok(ApiResponse.onSuccess(AdminResponse.from(admin)));
    }

    @PatchMapping("/{adminId}")
    public ResponseEntity<ApiResponse<AdminResponse>> updateAdminName(
            @PathVariable Long adminId,
            @Valid @RequestBody AdminUpdateRequest request
    ) {
        Admin admin = adminService.updateAdminName(adminId, request.name());

        return ResponseEntity.ok(ApiResponse.onSuccess(AdminResponse.from(admin)));
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(
            @PathVariable Long adminId
    ) {
        adminService.deleteAdmin(adminId);

        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
