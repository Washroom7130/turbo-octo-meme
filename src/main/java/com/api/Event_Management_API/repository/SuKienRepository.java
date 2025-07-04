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

    Page<SuKien> findByMaDanhMuc(Integer maDanhMuc, Pageable pageable);

    boolean existsByMaDanhMuc(Integer maDanhMuc);
}
