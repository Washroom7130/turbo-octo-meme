package com.api.Event_Management_API.service;

import java.net.ResponseCache;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.DangKy.GetAllDangKyStaffResponse;
import com.api.Event_Management_API.model.DangKy;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.SuKien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.repository.DangKyRepository;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.SuKienRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class DangKyService {
    private final DangKyRepository dangKyRepo;
    private final KhachHangRepository khachHangRepo;
    private final SuKienRepository suKienRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final JwtUtil jwtUtil;

    public DangKyService(DangKyRepository dangKyRepo,
                        KhachHangRepository khachHangRepo,
                        SuKienRepository suKienRepo,
                        TaiKhoanRepository taiKhoanRepo,
                        JwtUtil jwtUtil) {
        this.dangKyRepo = dangKyRepo;
        this.khachHangRepo = khachHangRepo;
        this.suKienRepo = suKienRepo;
        this.taiKhoanRepo = taiKhoanRepo;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> getAll(int page, int size, String search, String trangThaiSuKien, HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
    
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);
        String vaiTro = claims.get("vaiTro", String.class);
    
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayDangKy"));
        Page<DangKy> pageResult;
    
        if ("KhachHang".equals(vaiTro)) {
            Optional<TaiKhoan> tk = taiKhoanRepo.findById(maTaiKhoan);
            if (tk.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }
    
            Integer maKhachHang = tk.get().getMaKhachHang();
            if (trangThaiSuKien != null && !trangThaiSuKien.isBlank()) {
                pageResult = dangKyRepo.findByMaKhachHangAndSuKien_TrangThaiSuKien(maKhachHang, trangThaiSuKien, pageable);
            } else if (search != null && !search.isBlank()) {
                pageResult = dangKyRepo.findByMaKhachHangAndKhachHang_HoTenContainingIgnoreCaseOrSuKien_TenSuKienContainingIgnoreCase(
                    maKhachHang, search, search, pageable
                );
            } else {
                pageResult = dangKyRepo.findAllByMaKhachHang(maKhachHang, pageable);
            }            
    
        } else if ("NhanVien".equals(vaiTro) || "QuanLy".equals(vaiTro)) {
            if (trangThaiSuKien != null && !trangThaiSuKien.isBlank()) {
                pageResult = dangKyRepo.findBySuKien_TrangThaiSuKien(trangThaiSuKien, pageable);
            } else if (search != null && !search.isBlank()) {
                pageResult = dangKyRepo.findByKhachHang_HoTenContainingIgnoreCaseOrSuKien_TenSuKienContainingIgnoreCase(
                    search, search, pageable
                );
            } else {
                pageResult = dangKyRepo.findAll(pageable);
            }            
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authorized"));
        }
    
        Page<GetAllDangKyStaffResponse> responsePage = pageResult.map(dk -> {
            String tenKhachHang = dk.getKhachHang() != null ? dk.getKhachHang().getHoTen() : "Unknown";
            String tenSuKien = dk.getSuKien() != null ? dk.getSuKien().getTenSuKien() : "Unknown";
    
            return new GetAllDangKyStaffResponse(
                dk.getMaDangKy(),
                dk.getNgayDangKy(),
                dk.getViTriGhe(),
                dk.getTrangThaiDangKy(),
                tenKhachHang,
                tenSuKien
            );
        });
    
        return ResponseEntity.ok(responsePage);
    }    

    public ResponseEntity<?> getOne(String maDangKy, HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);
        String vaiTro = claims.get("vaiTro", String.class);

        Optional<DangKy> dk = dangKyRepo.findById(maDangKy);

        if (dk.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Entry not found"));
        }

        DangKy dangKy = dk.get();

        // check ownership if role is KhachHang
        if ("KhachHang".equals(vaiTro)) {
            Optional<TaiKhoan> tk = taiKhoanRepo.findById(maTaiKhoan);
            if (tk.isEmpty() || !tk.get().getMaKhachHang().equals(dangKy.getMaKhachHang())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Entry not found"));
            }
        }

        String tenKhachHang = khachHangRepo.findById(dangKy.getMaKhachHang())
            .map(KhachHang::getHoTen)
            .orElse("Unknown");

        String tenSuKien = suKienRepo.findById(dangKy.getMaSuKien())
            .map(SuKien::getTenSuKien)
            .orElse("Unknown");

        GetAllDangKyStaffResponse response = new GetAllDangKyStaffResponse(
            dangKy.getMaDangKy(),
            dangKy.getNgayDangKy(),
            dangKy.getViTriGhe(),
            dangKy.getTrangThaiDangKy(),
            tenKhachHang,
            tenSuKien
        );

        return ResponseEntity.ok(response);
    }

}
