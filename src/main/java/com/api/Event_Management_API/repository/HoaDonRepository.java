package com.api.Event_Management_API.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    Page<HoaDon> findAllByMaKhachHang(Integer maKhachHang, Pageable pageable);
    List<HoaDon> findByTrangThaiHoaDonAndThoiGianHieuLucBefore(String status, LocalDateTime now);
    Page<HoaDon> findByKhachHang_HoTenContainingIgnoreCase(String hoTen, Pageable pageable);
    Page<HoaDon> findByMaKhachHangAndKhachHang_HoTenContainingIgnoreCase(Integer maKhachHang, String hoTen, Pageable pageable);
    Optional<HoaDon> findByMaDangKy(String maDangKy);
}
