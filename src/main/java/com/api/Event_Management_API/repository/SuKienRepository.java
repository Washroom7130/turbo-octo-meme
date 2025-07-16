package com.api.Event_Management_API.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.api.Event_Management_API.model.SuKien;

public interface SuKienRepository extends JpaRepository<SuKien, Integer> {
    List<SuKien> findByNgayBatDauBeforeAndNgayKetThucAfter(LocalDateTime start, LocalDateTime after);

    List<SuKien> findByNgayKetThucBefore(LocalDateTime end);

    List<SuKien> findByNgayBatDauBeforeAndTrangThaiSuKienNot(LocalDateTime time, String trangThai);

    Page<SuKien> findByMaDanhMuc(Integer maDanhMuc, Pageable pageable);

    boolean existsByMaDanhMuc(Integer maDanhMuc);
    Page<SuKien> findByTenSuKienContainingIgnoreCase(String tenSuKien, Pageable pageable);

    Page<SuKien> findByMaDanhMucAndTenSuKienContainingIgnoreCase(Integer maDanhMuc, String tenSuKien, Pageable pageable);
    Page<SuKien> findByTrangThaiSuKien(String trangThai, Pageable pageable);
    Page<SuKien> findByMaDanhMucAndTrangThaiSuKien(Integer maDanhMuc, String trangThai, Pageable pageable);
    Page<SuKien> findByTrangThaiSuKienAndTenSuKienContainingIgnoreCase(String trangThai, String search, Pageable pageable);
    Page<SuKien> findByMaDanhMucAndTrangThaiSuKienAndTenSuKienContainingIgnoreCase(Integer maDanhMuc, String trangThai, String search, Pageable pageable);
}
