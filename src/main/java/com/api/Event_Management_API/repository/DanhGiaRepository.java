package com.api.Event_Management_API.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.DanhGia;

public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {
    boolean existsByMaKhachHangAndMaSuKien(Integer maKhachHang, Integer maSuKien);
    Page<DanhGia> findByMaSuKien(Integer maSuKien, Pageable pageable);
}
