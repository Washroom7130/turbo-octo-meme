package com.api.Event_Management_API.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.Admin.AddNhanVienRequest;
import com.api.Event_Management_API.dto.Admin.GetTaiKhoanListResponse;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.NhanVien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.NhanVienRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;

@Service
public class AdminService {
    private final TaiKhoanRepository taiKhoanRepo;
    private final NhanVienRepository nhanVienRepo;
    private final KhachHangRepository khachHangRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminService(TaiKhoanRepository taiKhoanRepo,
                        NhanVienRepository nhanVienRepo,
                        KhachHangRepository khachHangRepo,
                        PasswordEncoder passwordEncoder) {
        this.taiKhoanRepo = taiKhoanRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.khachHangRepo = khachHangRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> addNhanVien(AddNhanVienRequest request) {
        if (!request.getMatKhau().equals(request.getConfirmMatKhau())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Passwords don't match"));
        }

        NhanVien nhanVien = new NhanVien();
        nhanVien.setHoTen(request.getHoTen());
        nhanVien.setDiaChi(request.getDiaChi());
        nhanVien.setEmail(request.getEmail());
        nhanVien.setPhone(request.getPhone());
        nhanVien.setGioiTinh(request.getGioiTinh());
        nhanVien.setSoTuoi(request.getSoTuoi());
        nhanVienRepo.save(nhanVien);

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMaTaiKhoan(UUID.randomUUID().toString());
        taiKhoan.setTenDangNhap(request.getTenDangNhap());
        taiKhoan.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        taiKhoan.setTrangThai("Hoạt Động");
        taiKhoan.setVaiTro("NhanVien");
        taiKhoan.setXacMinhEmail(true);
        taiKhoan.setNgayTao(LocalDateTime.now());
        taiKhoan.setMaNhanVien(nhanVien.getMaNhanVien());
        taiKhoanRepo.save(taiKhoan);

        return ResponseEntity.ok(Map.of("message", "Staff account created successfully"));
    }

    public ResponseEntity<?> updateStaffStatus(String action, Integer maNhanVien) {
        if (!action.equals("activate") && !action.equals("deactivate")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid action"));
        }

        Optional<TaiKhoan> tkOpt = taiKhoanRepo.findByMaNhanVien(maNhanVien);
        if (tkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Staff not found"));
        }

        TaiKhoan taiKhoan = tkOpt.get();
        taiKhoan.setTrangThai(action.equals("activate") ? "Hoạt Động" : "Dừng hoạt động");

        taiKhoanRepo.save(taiKhoan);
        
        return ResponseEntity.ok(Map.of("message", "Account has been " + (action.equals("activate") ? "activated" : "deactivated")));
    }

    public ResponseEntity<?> getAllNV(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaiKhoan> pageResult;

        if (search != null && !search.isBlank()) {
            pageResult = taiKhoanRepo.findByVaiTroEqualsAndNhanVien_HoTenContainingIgnoreCase("NhanVien", search, pageable);
        } else {
            pageResult = taiKhoanRepo.findByVaiTroEquals("NhanVien", pageable);
        }

        Page<GetTaiKhoanListResponse> responsePage = pageResult.map(tk -> {
            GetTaiKhoanListResponse dto = new GetTaiKhoanListResponse();
            dto.setTenDangNhap(tk.getTenDangNhap());
            dto.setVaiTro(tk.getVaiTro());
            dto.setTrangThai(tk.getTrangThai());

            if (tk.getMaNhanVien() != null) {
                nhanVienRepo.findById(tk.getMaNhanVien()).ifPresent(nv -> {
                    dto.setHoTen(nv.getHoTen());
                    dto.setDiaChi(nv.getDiaChi());
                    dto.setEmail(nv.getEmail());
                    dto.setPhone(nv.getPhone());
                    dto.setGioiTinh(nv.getGioiTinh());
                    dto.setSoTuoi(nv.getSoTuoi());
                });
            }

            return dto;
        });

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> getOneNV(Integer maNhanVien) {
        Optional<NhanVien> nvOpt = nhanVienRepo.findById(maNhanVien);
        if (nvOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Staff not found"));
        }

        NhanVien nv = nvOpt.get();

        TaiKhoan tk = taiKhoanRepo.findByMaNhanVien(maNhanVien).get();
        if (tk == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Account not found"));
        }

        GetTaiKhoanListResponse dto = new GetTaiKhoanListResponse();
        dto.setTenDangNhap(tk.getTenDangNhap());
        dto.setTrangThai(tk.getTrangThai());
        dto.setVaiTro(tk.getVaiTro());

        dto.setHoTen(nv.getHoTen());
        dto.setDiaChi(nv.getDiaChi());
        dto.setEmail(nv.getEmail());
        dto.setPhone(nv.getPhone());
        dto.setGioiTinh(nv.getGioiTinh());
        dto.setSoTuoi(nv.getSoTuoi());

        return ResponseEntity.ok(dto);

    }

    public ResponseEntity<?> getAllKH(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaiKhoan> pageResult;

        if (search != null && !search.isBlank()) {
            pageResult = taiKhoanRepo.findByVaiTroEqualsAndKhachHang_HoTenContainingIgnoreCase("KhachHang", search, pageable);
        } else {
            pageResult = taiKhoanRepo.findByVaiTroEquals("KhachHang", pageable);
        }

        Page<GetTaiKhoanListResponse> responsePage = pageResult.map(tk -> {
            GetTaiKhoanListResponse dto = new GetTaiKhoanListResponse();
            dto.setTenDangNhap(tk.getTenDangNhap());
            dto.setVaiTro(tk.getVaiTro());
            dto.setTrangThai(tk.getTrangThai());

            if (tk.getMaKhachHang() != null) {
                khachHangRepo.findById(tk.getMaKhachHang()).ifPresent(kh -> {
                    dto.setHoTen(kh.getHoTen());
                    dto.setDiaChi(kh.getDiaChi());
                    dto.setEmail(kh.getEmail());
                    dto.setPhone(kh.getPhone());
                    dto.setGioiTinh(kh.getGioiTinh());
                    dto.setSoTuoi(kh.getSoTuoi());
                });
            }

            return dto;
        });

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> getOneKH(Integer maKhachHang) {
        Optional<KhachHang> khOpt = khachHangRepo.findById(maKhachHang);
        if (khOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Customer not found"));
        }

        KhachHang kh = khOpt.get();

        TaiKhoan tk = taiKhoanRepo.findByMaKhachHang(maKhachHang).get();
        if (tk == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Account not found"));
        }

        GetTaiKhoanListResponse dto = new GetTaiKhoanListResponse();
        dto.setTenDangNhap(tk.getTenDangNhap());
        dto.setTrangThai(tk.getTrangThai());
        dto.setVaiTro(tk.getVaiTro());

        dto.setHoTen(kh.getHoTen());
        dto.setDiaChi(kh.getDiaChi());
        dto.setEmail(kh.getEmail());
        dto.setPhone(kh.getPhone());
        dto.setGioiTinh(kh.getGioiTinh());
        dto.setSoTuoi(kh.getSoTuoi());

        return ResponseEntity.ok(dto);

    }
}
