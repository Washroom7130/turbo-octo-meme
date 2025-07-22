package com.api.Event_Management_API.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.Event_Management_API.model.TaiKhoan;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    boolean existsByTenDangNhap(String tenDangNhap);
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
    Optional<TaiKhoan> findByMaKhachHang(Integer maKhachHang);
    Optional<TaiKhoan> findByMaNhanVien(Integer maNhanVien);
    Page<TaiKhoan> findByVaiTroEquals(String vaiTro, Pageable pageable);
    Page<TaiKhoan> findByVaiTroEqualsAndMaNhanVienIsNotNull(Pageable pageable, String vaiTro);
    Page<TaiKhoan> findByVaiTroEqualsAndNhanVien_HoTenContainingIgnoreCase(
        String vaiTro,
        String hoTen,
        Pageable pageable
    );
    Page<TaiKhoan> findByVaiTroEqualsAndKhachHang_HoTenContainingIgnoreCase(
        String vaiTro,
        String hoTen,
        Pageable pageable
    );
    @Query("SELECT COUNT(t) FROM TaiKhoan t " +
       "WHERE t.vaiTro = 'KhachHang' AND t.trangThai = 'Hoạt Động' AND t.xacMinhEmail = true AND t.ngayTao BETWEEN :start AND :end")
    Long countActiveKhachHangBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(t) FROM TaiKhoan t " +
        "WHERE t.vaiTro = 'KhachHang' AND t.trangThai = 'Dừng hoạt động' AND t.ngayTao BETWEEN :start AND :end")
    Long countNonActiveKhachHangBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
