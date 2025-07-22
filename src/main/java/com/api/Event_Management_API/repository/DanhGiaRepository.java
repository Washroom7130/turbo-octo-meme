package com.api.Event_Management_API.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.Event_Management_API.model.DanhGia;

public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {
    boolean existsByMaKhachHangAndMaSuKien(Integer maKhachHang, Integer maSuKien);
    Page<DanhGia> findByMaSuKien(Integer maSuKien, Pageable pageable);
    List<DanhGia> findByMaSuKien(Integer maSuKien);
    Page<DanhGia> findByKhachHang_HoTenContainingIgnoreCase(String hoTen, Pageable pageable);
    // Count total ratings
    long countByNgayDanhGiaBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT dg.maSuKien, AVG(dg.loaiDanhGia) " +
       "FROM DanhGia dg " +
       "WHERE dg.ngayDanhGia BETWEEN :start AND :end " +
       "GROUP BY dg.maSuKien")
    List<Object[]> findAverageRatingPerSuKienInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
