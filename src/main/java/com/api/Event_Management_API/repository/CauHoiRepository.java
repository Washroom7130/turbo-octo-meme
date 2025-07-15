package com.api.Event_Management_API.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.CauHoi;

public interface CauHoiRepository extends JpaRepository<CauHoi, Integer> {
    boolean existsByMaKhachHang(String maKhachHang);
    Page<CauHoi> findByMaKhachHang(String maKhachHang, Pageable pageable);
   // For admin/staff to search across all 3 fields
    Page<CauHoi> findByKhachHang_HoTenContainingIgnoreCaseOrSuKien_TenSuKienContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
        String hoTenKhach, String tenSuKien, String tenNhanVien, Pageable pageable
    );

    // For KhachHang to search their own questions (by tenSuKien or hoTenNhanVien — their name is fixed)
    Page<CauHoi> findByMaKhachHangAndSuKien_TenSuKienContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
        String maKhachHang, String tenSuKien, String tenNhanVien, Pageable pageable
    );
}
