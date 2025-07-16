package com.api.Event_Management_API.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.DanhGia.GetDanhGiaResponse;
import com.api.Event_Management_API.dto.DanhGia.ModDanhGiaRequest;
import com.api.Event_Management_API.model.DanhGia;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.SuKien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.repository.DangKyRepository;
import com.api.Event_Management_API.repository.DanhGiaRepository;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.SuKienRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class DanhGiaService {
    private final DangKyRepository dangKyRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final KhachHangRepository khachHangRepo;
    private final SuKienRepository suKienRepo;
    private final DanhGiaRepository danhGiaRepo;
    private final JwtUtil jwtUtil;

    public DanhGiaService(DangKyRepository dangKyRepo,
                        TaiKhoanRepository taiKhoanRepo,
                        KhachHangRepository khachHangRepo,
                        SuKienRepository suKienRepo,
                        DanhGiaRepository danhGiaRepo,
                        JwtUtil jwtUtil) {
        this.dangKyRepo = dangKyRepo;
        this.taiKhoanRepo = taiKhoanRepo;
        this.khachHangRepo = khachHangRepo;
        this.suKienRepo = suKienRepo;
        this.danhGiaRepo = danhGiaRepo;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> addDanhGia(Integer maSuKien, ModDanhGiaRequest request, HttpServletRequest httpServletRequest) {
        Claims claims = jwtUtil.extractClaimsFromRequest(httpServletRequest);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);
        String vaiTro = claims.get("vaiTro", String.class);

        Optional<SuKien> skOpt = suKienRepo.findById(maSuKien);
        if (skOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
        }

        if (!"KhachHang".equals(vaiTro)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Only khach hang is allowed to comment"));
        }

        Optional<TaiKhoan> tk = taiKhoanRepo.findById(maTaiKhoan);
        if (tk.isEmpty() || tk.get().getMaKhachHang() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        Integer maKhachHang = tk.get().getMaKhachHang();
        SuKien sk = skOpt.get();

        if (!dangKyRepo.existsByMaKhachHangAndMaSuKienAndTrangThaiDangKy(maKhachHang, maSuKien, "Thành công")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "You didn't sign up for this event"));
        }

        if (!"Đã kết thúc".equals(sk.getTrangThaiSuKien())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "You can only comment when the event has ended"));
        }

        if (danhGiaRepo.existsByMaKhachHangAndMaSuKien(maKhachHang, maSuKien)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "You have already reviewed this event"));
        }

        DanhGia dg = new DanhGia();
        dg.setLoaiDanhGia(request.getLoaiDanhGia());
        dg.setBinhLuan(request.getBinhLuan());
        dg.setNgayDanhGia(LocalDateTime.now());
        dg.setMaKhachHang(maKhachHang);
        dg.setMaSuKien(maSuKien);

        danhGiaRepo.save(dg);

        return ResponseEntity.ok(Map.of("message", "Review submitted successfully"));
    }

    public ResponseEntity<?> deleteDanhGia(Integer maDanhGia) {
        Optional<DanhGia> dg = danhGiaRepo.findById(maDanhGia);

        if (dg.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Comment not found"));
        }

        danhGiaRepo.deleteById(maDanhGia);

        return ResponseEntity.ok(Map.of("message", "Comment has been deleted successfully"));
    }

    public ResponseEntity<?> getAllDanhGia(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DanhGia> pageResult;

        if (search != null && !search.isBlank()) {
            pageResult = danhGiaRepo.findByKhachHang_HoTenContainingIgnoreCase(search, pageable);
        } else {
            pageResult = danhGiaRepo.findAll(pageable);
        }
        
        Page<GetDanhGiaResponse> responsePage = pageResult.map(dg -> {
            String tenKhachHang = khachHangRepo.findById(dg.getMaKhachHang())
                .map(KhachHang::getHoTen)
                .orElse("Unknown");
            
            String tenSuKien = suKienRepo.findById(dg.getMaSuKien())
                .map(SuKien::getTenSuKien)
                .orElse("Unknown");

            return new GetDanhGiaResponse(
                dg.getMaDanhGia(),
                dg.getLoaiDanhGia(),
                dg.getBinhLuan(),
                dg.getNgayDanhGia(),
                tenKhachHang,
                tenSuKien
            );
        });

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> getAllBySuKien(Integer maSuKien, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DanhGia> pageResult = danhGiaRepo.findByMaSuKien(maSuKien, pageable);

        Page<GetDanhGiaResponse> responsePage = pageResult.map(dg -> {
            String tenKhachHang = khachHangRepo.findById(dg.getMaKhachHang())
                .map(KhachHang::getHoTen)
                .orElse("Unknown");
            
            String tenSuKien = suKienRepo.findById(dg.getMaSuKien())
                .map(SuKien::getTenSuKien)
                .orElse("Unknown");

            return new GetDanhGiaResponse(
                dg.getMaDanhGia(),
                dg.getLoaiDanhGia(),
                dg.getBinhLuan(),
                dg.getNgayDanhGia(),
                tenKhachHang,
                tenSuKien
            );
        });

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> getOneDanhGia(Integer maDanhGia) {
        Optional<DanhGia> danhGiaOpt = danhGiaRepo.findById(maDanhGia);
        
        if (danhGiaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Review not found"));
        }
    
        DanhGia dg = danhGiaOpt.get();
    
        String tenKhachHang = khachHangRepo.findById(dg.getMaKhachHang())
            .map(KhachHang::getHoTen)
            .orElse("Unknown");
    
        String tenSuKien = suKienRepo.findById(dg.getMaSuKien())
            .map(SuKien::getTenSuKien)
            .orElse("Unknown");
    
        GetDanhGiaResponse response = new GetDanhGiaResponse(
            dg.getMaDanhGia(),
            dg.getLoaiDanhGia(),
            dg.getBinhLuan(),
            dg.getNgayDanhGia(),
            tenKhachHang,
            tenSuKien
        );
    
        return ResponseEntity.ok(response);
    }
    
}
