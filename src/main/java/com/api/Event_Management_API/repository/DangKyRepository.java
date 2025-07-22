package com.api.Event_Management_API.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.Event_Management_API.model.DangKy;

public interface DangKyRepository extends JpaRepository<DangKy, String> {
    long countByMaSuKienAndTrangThaiDangKy(Integer maSuKien, String trangThaiDangKy);
    boolean existsByMaKhachHangAndMaSuKien (Integer maKhachHang, Integer maSuKien);
    boolean existsByMaSuKienAndViTriGhe (Integer maSuKien, String viTriGhe);
    Page<DangKy> findAllByMaKhachHang(Integer maKhachHang, Pageable pageable);
    boolean existsByMaKhachHangAndMaSuKienAndTrangThaiDangKy(Integer maKhachHang, Integer maSuKien, String trangThaiDangKy);
    List<DangKy> findByMaSuKienAndTrangThaiDangKyIn(Integer maSuKien, List<String> status);
    Page<DangKy> findByMaSuKienAndTrangThaiDangKy(Integer maSuKien, String trangThaiDangKy, Pageable pageable);
    Page<DangKy> findByKhachHang_HoTenContainingIgnoreCaseOrSuKien_TenSuKienContainingIgnoreCase(
        String hoTen, String tenSuKien, Pageable pageable
    );
    Page<DangKy> findByMaKhachHangAndKhachHang_HoTenContainingIgnoreCaseOrSuKien_TenSuKienContainingIgnoreCase(
        Integer maKhachHang, String hoTen, String tenSuKien, Pageable pageable
    );
    boolean existsByMaSuKienAndViTriGheAndTrangThaiDangKy(Integer maSuKien, String viTriGhe, String trangThaiDangKy);
    @Query("SELECT dk.maSuKien, COUNT(dk) " +
       "FROM DangKy dk " +
       "WHERE dk.ngayDangKy BETWEEN :start AND :end " +
       "GROUP BY dk.maSuKien " +
       "ORDER BY COUNT(dk) DESC")
    List<Object[]> findTopSuKienByDangKyCountInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
