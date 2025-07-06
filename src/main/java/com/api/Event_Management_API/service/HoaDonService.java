package com.api.Event_Management_API.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.HoaDon.GetHoaDonResponse;
import com.api.Event_Management_API.model.DangKy;
import com.api.Event_Management_API.model.HoaDon;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.SuKien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.repository.DangKyRepository;
import com.api.Event_Management_API.repository.HoaDonRepository;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.SuKienRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class HoaDonService {

    private final TaiKhoanRepository taiKhoanRepo;
    private final KhachHangRepository khachHangRepo;
    private final HoaDonRepository hoaDonRepo;
    private final DangKyRepository dangKyRepo;
    private final SuKienRepository suKienRepo;
    private final JwtUtil jwtUtil;

    public HoaDonService(TaiKhoanRepository taiKhoanRepo,
                        KhachHangRepository khachHangRepo,
                        HoaDonRepository hoaDonRepo,
                        DangKyRepository dangKyRepo,
                        SuKienRepository suKienRepo,
                        JwtUtil jwtUtil) {
        this.taiKhoanRepo = taiKhoanRepo;
        this.khachHangRepo = khachHangRepo;
        this.hoaDonRepo = hoaDonRepo;
        this.dangKyRepo = dangKyRepo;
        this.suKienRepo = suKienRepo;
        this.jwtUtil = jwtUtil;
    }
    
    public ResponseEntity<?> getAll(int page, int size, HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);
        String vaiTro = claims.get("vaiTro", String.class);

        Pageable pageable = PageRequest.of(page, size);
        Page<HoaDon> pageResult;

        if ("KhachHang".equals(vaiTro)) {
            Optional<TaiKhoan> tk = taiKhoanRepo.findById(maTaiKhoan);

            if (tk.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
            }

            Optional<KhachHang> kh = khachHangRepo.findById(tk.get().getMaKhachHang());
            pageResult = hoaDonRepo.findAllByMaKhachHang(kh.get().getMaKhachHang(), pageable);
        } else if ("NhanVien".equals(vaiTro) || "QuanLy".equals(vaiTro)) {
            pageResult = hoaDonRepo.findAll(pageable);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authorized"));
        }

        Page<GetHoaDonResponse> responsePage = pageResult.map(hd -> {
            String tenKhachHang = khachHangRepo.findById(hd.getMaKhachHang())
                                    .map(KhachHang::getHoTen)
                                    .orElse("Unknown");

            Optional<DangKy> dk = dangKyRepo.findById(hd.getMaDangKy());

            String tenSuKien = suKienRepo.findById(dk.get().getMaSuKien())
                                    .map(SuKien::getTenSuKien)
                                    .orElse("Unknown");

            return new GetHoaDonResponse(
                hd.getMaHoaDon(),
                hd.getTrangThaiHoaDon(),
                hd.getTongTien(),
                hd.getThoiGianThanhCong(),
                hd.getPhuongThucThanhToan(),
                tenKhachHang,
                tenSuKien
            );
        });

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> getOne(String maHoaDon, HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);
        String vaiTro = claims.get("vaiTro", String.class);

        Optional<HoaDon> hd = hoaDonRepo.findById(maHoaDon);

        if (hd.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Entry not found"));
        }

        HoaDon hoaDon = hd.get();

        // check ownership if role is KhachHang
        if ("KhachHang".equals(vaiTro)) {
            Optional<TaiKhoan> tk = taiKhoanRepo.findById(maTaiKhoan);
            if (tk.isEmpty() || !tk.get().getMaKhachHang().equals(hoaDon.getMaKhachHang())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Entry not found"));
            }
        }

        String tenKhachHang = khachHangRepo.findById(hoaDon.getMaKhachHang())
                                .map(KhachHang::getHoTen)
                                .orElse("Unknown");

        Optional<DangKy> dk = dangKyRepo.findById(hoaDon.getMaDangKy());

        String tenSuKien = suKienRepo.findById(dk.get().getMaSuKien())
                                .map(SuKien::getTenSuKien)
                                .orElse("Unknown");

        GetHoaDonResponse response = new GetHoaDonResponse(
            hoaDon.getMaHoaDon(),
            hoaDon.getTrangThaiHoaDon(),
            hoaDon.getTongTien(),
            hoaDon.getThoiGianThanhCong(),
            hoaDon.getPhuongThucThanhToan(),
            tenKhachHang,
            tenSuKien
        );

        return ResponseEntity.ok(response);
    }
}
