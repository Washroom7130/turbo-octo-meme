package com.api.Event_Management_API.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.NhanVien;

public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    boolean existsByEmail(String email);
}
