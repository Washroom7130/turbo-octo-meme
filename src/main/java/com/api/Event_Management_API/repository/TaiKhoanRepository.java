package com.api.Event_Management_API.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.TaiKhoan;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    boolean existsByTenDangNhap(String tenDangNhap);
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
    Optional<TaiKhoan> findByMaKhachHang(Integer maKhachHang);
    Optional<TaiKhoan> findByMaNhanVien(Integer maNhanVien);
    Page<TaiKhoan> findByVaiTroEquals(String vaiTro, Pageable pageable);
}
