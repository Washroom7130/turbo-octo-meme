package com.api.Event_Management_API.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.CauHoi;

public interface CauHoiRepository extends JpaRepository<CauHoi, Integer> {
    boolean existsByMaKhachHangAndMaSuKien(String maKhachHang, String maSuKien);
    Page<CauHoi> findByMaKhachHang(String maKhachHang, Pageable pageable);
   // For admin/staff to search across all 3 fields
    Page<CauHoi> findByKhachHang_HoTenContainingIgnoreCaseOrSuKien_TenSuKienContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
        String hoTenKhach, String tenSuKien, String tenNhanVien, Pageable pageable
    );

    // For KhachHang to search their own questions (by tenSuKien or hoTenNhanVien — their name is fixed)
    Page<CauHoi> findByMaKhachHangAndSuKien_TenSuKienContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
        String maKhachHang, String tenSuKien, String tenNhanVien, Pageable pageable
    );
    Page<CauHoi> findByMaKhachHangAndMaSuKien(String maKhachHang, String maSuKien, Pageable pageable);

    Page<CauHoi> findByMaKhachHangAndMaSuKienAndSuKien_TenSuKienContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
        String maKhachHang, String maSuKien, String search1, String search2, Pageable pageable
    );

    Page<CauHoi> findByMaSuKien(String maSuKien, Pageable pageable);

    Page<CauHoi> findByMaSuKienAndKhachHang_HoTenContainingIgnoreCaseOrSuKien_TenSuKienContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
        String maSuKien, String search1, String search2, String search3, Pageable pageable
    );
    int countByMaSuKien(String maSuKien);
    Optional<CauHoi> findByMaSuKienAndMaKhachHang(String maSuKien, String maKhachHang);
}
