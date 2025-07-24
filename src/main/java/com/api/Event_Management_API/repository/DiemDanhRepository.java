package com.api.Event_Management_API.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.DiemDanh;

public interface DiemDanhRepository extends JpaRepository<DiemDanh, String> {
    Page<DiemDanh> findByDangKy_SuKien_MaSuKien(Integer maSuKien, Pageable pageable);
    Page<DiemDanh> findByDangKy_SuKien_MaSuKienAndDangKy_KhachHang_HoTenContainingIgnoreCase(
        Integer maSuKien,
        String hoTen,
        Pageable pageable
    );
    Optional<DiemDanh> findByMaDangKy(String maDangKy);

}
