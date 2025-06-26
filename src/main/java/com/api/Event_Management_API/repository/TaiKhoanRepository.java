package com.api.Event_Management_API.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.TaiKhoan;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    boolean existsByTenDangNhap(String tenDangNhap);
}
