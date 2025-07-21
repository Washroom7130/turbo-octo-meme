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

    // Sum total rating points
    @Query("SELECT SUM(d.loaiDanhGia) FROM DanhGia d WHERE d.ngayDanhGia BETWEEN :start AND :end")
    Integer sumLoaiDanhGiaByNgayDanhGiaBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT d.suKien.tenSuKien, AVG(d.loaiDanhGia) " +
        "FROM DanhGia d " +
        "WHERE d.ngayDanhGia BETWEEN :start AND :end " +
        "GROUP BY d.suKien.tenSuKien")
    List<Object[]> findAverageRatingPerEvent(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
