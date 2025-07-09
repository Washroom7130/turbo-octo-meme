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
import com.api.Event_Management_API.model.NhanVien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.repository.NhanVienRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;

@Service
public class AdminService {
    private final TaiKhoanRepository taiKhoanRepo;
    private final NhanVienRepository nhanVienRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminService(TaiKhoanRepository taiKhoanRepo,
                        NhanVienRepository nhanVienRepo,
                        PasswordEncoder passwordEncoder) {
        this.taiKhoanRepo = taiKhoanRepo;
        this.nhanVienRepo = nhanVienRepo;
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

    public ResponseEntity<?> getAllTK(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaiKhoan> pageResult = taiKhoanRepo.findAll(pageable);

        Page<GetTaiKhoanListResponse> responsePage = pageResult.map(tk -> {
            GetTaiKhoanListResponse dto = new GetTaiKhoanListResponse();
            dto.setTenDangNhap(tk.getTenDangNhap());
            dto.setTrangThai(tk.getTrangThai());
            dto.setVaiTro(tk.getVaiTro());

            // TODO: continue this part
        })
    }
}
