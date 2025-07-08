package com.api.Event_Management_API.service;

import java.util.Map;
import java.util.function.Consumer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api.Event_Management_API.controller.taikhoan.ChangePasswordRequest;
import com.api.Event_Management_API.dto.TaiKhoan.UpdateTaiKhoanRequest;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.NhanVienRepository;
import com.api.Event_Management_API.repository.QuanLyRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class TaiKhoanService {
    private final TaiKhoanRepository taiKhoanRepo;
    private final KhachHangRepository khachHangRepo;
    private final NhanVienRepository nhanVienRepo;
    private final QuanLyRepository quanLyRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // function to set value if field is present
    private <T> void safeSet(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public TaiKhoanService(TaiKhoanRepository taiKhoanRepo,
                        KhachHangRepository khachHangRepo,
                        NhanVienRepository nhanVienRepo,
                        QuanLyRepository quanLyRepo,
                        JwtUtil jwtUtil,
                        PasswordEncoder passwordEncoder) {
        this.taiKhoanRepo = taiKhoanRepo;
        this.khachHangRepo = khachHangRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.quanLyRepo = quanLyRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> updateMe(UpdateTaiKhoanRequest request, HttpServletRequest httpServletRequest) {
        Claims claims = jwtUtil.extractClaimsFromRequest(httpServletRequest);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);
        String vaiTro = claims.get("vaiTro", String.class);

        TaiKhoan taiKhoan = taiKhoanRepo.findById(maTaiKhoan).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        safeSet(taiKhoan::setTenDangNhap, request.getTenDangNhap());
        
        switch(vaiTro) {
            case "KhachHang" -> khachHangRepo.findById(taiKhoan.getMaKhachHang())
                .ifPresentOrElse(kh -> {
                    safeSet(kh::setHoTen, request.getHoTen());
                    safeSet(kh::setDiaChi, request.getDiaChi());
                    safeSet(kh::setEmail, request.getEmail());
                    safeSet(kh::setPhone, request.getPhone());
                    safeSet(kh::setGioiTinh, request.getGioiTinh());
                    safeSet(kh::setSoTuoi, request.getSoTuoi());
                    khachHangRepo.save(kh);
                }, () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"); });

            case "NhanVien" -> nhanVienRepo.findById(taiKhoan.getMaNhanVien())
                .ifPresentOrElse(nv -> {
                    safeSet(nv::setHoTen, request.getHoTen());
                    safeSet(nv::setDiaChi, request.getDiaChi());
                    safeSet(nv::setEmail, request.getEmail());
                    safeSet(nv::setPhone, request.getPhone());
                    safeSet(nv::setGioiTinh, request.getGioiTinh());
                    safeSet(nv::setSoTuoi, request.getSoTuoi());
                    nhanVienRepo.save(nv);
                }, () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"); });

            case "QuanLy" -> quanLyRepo.findById(taiKhoan.getMaQuanLy())
                .ifPresentOrElse(ql -> {
                    safeSet(ql::setHoTen, request.getHoTen());
                    safeSet(ql::setDiaChi, request.getDiaChi());
                    safeSet(ql::setEmail, request.getEmail());
                    safeSet(ql::setPhone, request.getPhone());
                    safeSet(ql::setGioiTinh, request.getGioiTinh());
                    safeSet(ql::setSoTuoi, request.getSoTuoi());
                    quanLyRepo.save(ql);
                }, () -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found"); });

            default -> throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid role");
        }
        taiKhoanRepo.save(taiKhoan);

        return ResponseEntity.ok(Map.of("message", "Personal info updated successfully"));
    }

    public ResponseEntity<?> changePassword(ChangePasswordRequest request, HttpServletRequest httpServletRequest) {
        Claims claims = jwtUtil.extractClaimsFromRequest(httpServletRequest);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);

        TaiKhoan taiKhoan = taiKhoanRepo.findById(maTaiKhoan).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        // Check if old password match
        if (!passwordEncoder.matches(request.getOldPassword(), taiKhoan.getMatKhau())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Old password doesn't match"));
        }

        // Check if new password match
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "New passwords don't match"));
        }

        // Hash and update
        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        taiKhoan.setMatKhau(hashedPassword);
        taiKhoanRepo.save(taiKhoan);

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    public ResponseEntity<?> deactive(HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);

        TaiKhoan taiKhoan = taiKhoanRepo.findById(maTaiKhoan).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (taiKhoan.getMaKhachHang() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid role"));
        }

        taiKhoan.setTrangThai("Dừng hoạt động");
        taiKhoanRepo.save(taiKhoan);

        // TODO: add auto-logout here

        return ResponseEntity.ok(Map.of("message", "Account deactivated successfully"));
    }
}
