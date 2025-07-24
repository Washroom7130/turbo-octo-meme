package com.api.Event_Management_API.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.Event_Management_API.model.HoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    Page<HoaDon> findAllByMaKhachHang(Integer maKhachHang, Pageable pageable);
    List<HoaDon> findByTrangThaiHoaDonAndThoiGianHieuLucBefore(String status, LocalDateTime now);
    Page<HoaDon> findByKhachHang_HoTenContainingIgnoreCase(String hoTen, Pageable pageable);
    Page<HoaDon> findByMaKhachHangAndKhachHang_HoTenContainingIgnoreCase(Integer maKhachHang, String hoTen, Pageable pageable);
    Optional<HoaDon> findByMaDangKy(String maDangKy);
    
    @Query("SELECT SUM(h.tongTien) FROM HoaDon h WHERE h.trangThaiHoaDon = :trangThai AND h.thoiGianThanhCong BETWEEN :start AND :end")
    Float sumTongTienByTrangThaiHoaDonAndThoiGianThanhCongBetween(
        @Param("trangThai") String trangThai,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    @Query("SELECT SUM(h.tongTien) FROM HoaDon h " +
        "WHERE h.trangThaiHoaDon = 'Đã thanh toán' AND h.thoiGianThanhCong BETWEEN :start AND :end")
    Float sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
        @Query("""
        SELECT h FROM HoaDon h
        JOIN DangKy d ON h.maDangKy = d.maDangKy
        JOIN SuKien s ON d.maSuKien = s.maSuKien
        WHERE (:trangThai IS NULL OR s.trangThaiSuKien = :trangThai)
    """)
    Page<HoaDon> findBySuKien_TrangThaiSuKien(
        @Param("trangThai") String trangThai,
        Pageable pageable
    );
    @Query("""
        SELECT h FROM HoaDon h
        JOIN DangKy d ON h.maDangKy = d.maDangKy
        JOIN SuKien s ON d.maSuKien = s.maSuKien
        WHERE s.trangThaiSuKien = :trangThai
        AND h.maKhachHang = :maKhachHang
    """)
    Page<HoaDon> findByMaKhachHangAndSuKien_TrangThaiSuKien(
        @Param("maKhachHang") Integer maKhachHang,
        @Param("trangThai") String trangThai,
        Pageable pageable
    );

}
