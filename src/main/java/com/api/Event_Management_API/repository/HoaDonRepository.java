package com.api.Event_Management_API.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    Page<HoaDon> findAllByMaKhachHang(Integer maKhachHang, Pageable pageable);
    List<HoaDon> findByTrangThaiHoaDonAndThoiGianHieuLucBefore(String status, LocalDateTime now);
}
