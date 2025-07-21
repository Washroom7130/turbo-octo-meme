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
    Page<SuKien> findByTrangThaiSuKienIn(List<String> trangThaiList, Pageable pageable);
    Page<SuKien> findByTenSuKienContainingIgnoreCaseAndTrangThaiSuKienIn(String tenSuKien, List<String> trangThaiList, Pageable pageable);
    Page<SuKien> findByMaDanhMucAndTrangThaiSuKienIn(Integer maDanhMuc, List<String> trangThaiList, Pageable pageable);
    Page<SuKien> findByMaDanhMucAndTenSuKienContainingIgnoreCaseAndTrangThaiSuKienIn(Integer maDanhMuc, String tenSuKien, List<String> trangThaiList, Pageable pageable);
    long countByNgayTaoSuKienBetween(LocalDateTime start, LocalDateTime end);
    long countByNgayTaoSuKienBetweenAndTrangThaiSuKien(LocalDateTime start, LocalDateTime end, String status);
    long countByNgayTaoSuKienBetweenAndTrangThaiSuKienIn(LocalDateTime start, LocalDateTime end, List<String> statuses);
}
