package com.example.hsa_core.domain.admin.repository;

import com.example.hsa_core.domain.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    List<Admin> findAllByOrderByIdAsc();
}
