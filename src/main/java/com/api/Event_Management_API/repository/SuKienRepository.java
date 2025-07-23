package com.api.Event_Management_API.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    // Count upcoming: ngayBatDau > end of this time range
    @Query("SELECT COUNT(s) FROM SuKien s WHERE s.ngayBatDau > :rangeEnd AND s.trangThaiSuKien != 'Hủy bỏ'")
    int countUpcomingSuKienAfterRangeEnd(@Param("rangeEnd") LocalDateTime rangeEnd);

    // Count ongoing: ngayBatDau ∈ [rangeStart, rangeEnd)
    @Query("SELECT COUNT(s) FROM SuKien s WHERE s.ngayBatDau >= :rangeStart AND s.ngayBatDau < :rangeEnd")
    int countOngoingSuKienBetween(@Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd);

    // Count cancelled: ngayTao ∈ [rangeStart, rangeEnd) AND trangThai = 'Hủy bỏ'
    @Query("SELECT COUNT(s) FROM SuKien s WHERE s.ngayTaoSuKien >= :rangeStart AND s.ngayTaoSuKien < :rangeEnd AND s.trangThaiSuKien = 'Hủy bỏ'")
    int countCancelledSuKienInRange(@Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd);

    @Query("""
        SELECT s FROM SuKien s
        WHERE (:maDanhMuc IS NULL OR s.maDanhMuc = :maDanhMuc)
        AND (:search IS NULL OR LOWER(s.tenSuKien) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:trangThaiList IS NULL OR s.trangThaiSuKien IN :trangThaiList)
        AND (:costStart IS NULL OR :costEnd IS NULL OR s.phiThamGia BETWEEN :costStart AND :costEnd)
    """)
    Page<SuKien> findFiltered(
        @Param("maDanhMuc") Integer maDanhMuc,
        @Param("search") String search,
        @Param("trangThaiList") List<String> trangThaiList,
        @Param("costStart") Float costStart,
        @Param("costEnd") Float costEnd,
        Pageable pageable
    );
}
