package com.api.Event_Management_API.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.DiemDanh.GetDiemDanhResponse;
import com.api.Event_Management_API.model.DangKy;
import com.api.Event_Management_API.model.DiemDanh;
import com.api.Event_Management_API.model.SuKien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.repository.DangKyRepository;
import com.api.Event_Management_API.repository.DiemDanhRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class DiemDanhService {
    
    private final DangKyRepository dangKyRepo;
    private final DiemDanhRepository diemDanhRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final JwtUtil jwtUtil;

    public DiemDanhService(DangKyRepository dangKyRepo,
                        DiemDanhRepository diemDanhRepo,
                        TaiKhoanRepository taiKhoanRepo,
                        JwtUtil jwtUtil) {
        this.dangKyRepo = dangKyRepo;
        this.diemDanhRepo = diemDanhRepo;
        this.taiKhoanRepo = taiKhoanRepo;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> getAllByMaSuKien(Integer maSuKien, int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DiemDanh> diemDanhPage;
        if (search != null && !search.trim().isEmpty()) {
            diemDanhPage = diemDanhRepo.findByDangKy_SuKien_MaSuKienAndDangKy_KhachHang_HoTenContainingIgnoreCase(
                maSuKien, search.trim(), pageable);
        } else {
            diemDanhPage = diemDanhRepo.findByDangKy_SuKien_MaSuKien(maSuKien, pageable);
        }

        Page<GetDiemDanhResponse> responsePage = diemDanhPage.map(dd -> {
            DangKy dk = dd.getDangKy();
            String tenKhachHang = (dk != null && dk.getKhachHang() != null)
                ? dk.getKhachHang().getHoTen()
                : "Unknown";

            return new GetDiemDanhResponse(
                dd.getMaDiemDanh(),
                dd.getNgayTaoVe(),
                dd.getNgayDiemDanh(),
                dd.getTrangThaiDiemDanh(),
                dd.getViTriGheNgoi(),
                tenKhachHang
            );
        });

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> getByMaDiemDanh(String maDiemDanh, HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);
        String vaiTro = claims.get("vaiTro", String.class);
        
        Optional<DiemDanh> diemDanhOpt = diemDanhRepo.findById(maDiemDanh);
        if (diemDanhOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Check-in record not found"));
        }
    
        DiemDanh dd = diemDanhOpt.get();
    
        DangKy dk = dd.getDangKy();

        if (vaiTro.equals("KhachHang")) {
            Optional<TaiKhoan> tk = taiKhoanRepo.findById(maTaiKhoan);
            if (!tk.get().getMaKhachHang().equals(dk.getMaKhachHang())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Check-in record not found"));
            }
        }

        String tenKhachHang = (dk != null && dk.getKhachHang() != null)
            ? dk.getKhachHang().getHoTen()
            : "Unknown";
    
        GetDiemDanhResponse response = new GetDiemDanhResponse(
            dd.getMaDiemDanh(),
            dd.getNgayTaoVe(),
            dd.getNgayDiemDanh(),
            dd.getTrangThaiDiemDanh(),
            dd.getViTriGheNgoi(),
            tenKhachHang
        );
    
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> diemDanh(String maDiemDanh) {
        // 1. Check if maDiemDanh exists
        Optional<DiemDanh> diemDanhOpt = diemDanhRepo.findById(maDiemDanh);
        if (diemDanhOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Check-in record not found"));
        }

        DiemDanh diemDanh = diemDanhOpt.get();

        // 2. Get maDangKy and find corresponding DangKy
        DangKy dangKy = diemDanh.getDangKy();
        if (dangKy == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Registration not found"));
        }

        // 3. Get SuKien from DangKy
        SuKien suKien = dangKy.getSuKien();
        if (suKien == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Event not found"));
        }

        // 4. Check if event is 'Đang diễn ra'
        if (!"Đang diễn ra".equals(suKien.getTrangThaiSuKien())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Event has not started"));
        }

        // 5. Update both DiemDanh and DangKy
        diemDanh.setTrangThaiDiemDanh("Có mặt");
        diemDanh.setNgayDiemDanh(LocalDateTime.now());

        dangKy.setTrangThaiDangKy("Đã điểm danh");

        // Save changes
        diemDanhRepo.save(diemDanh);
        dangKyRepo.save(dangKy);

        return ResponseEntity.ok(Map.of("message", "Check-in successful"));
    }

}
