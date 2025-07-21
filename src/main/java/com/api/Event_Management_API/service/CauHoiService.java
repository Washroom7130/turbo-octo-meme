package com.api.Event_Management_API.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.CauHoi.AnswerCauHoiRequest;
import com.api.Event_Management_API.dto.CauHoi.CreateCauHoiRequest;
import com.api.Event_Management_API.dto.CauHoi.GetAllCauHoiResponse;
import com.api.Event_Management_API.model.CauHoi;
import com.api.Event_Management_API.model.DangKy;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.NhanVien;
import com.api.Event_Management_API.model.SuKien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.repository.CauHoiRepository;
import com.api.Event_Management_API.repository.DangKyRepository;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.NhanVienRepository;
import com.api.Event_Management_API.repository.SuKienRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class CauHoiService {
    private final DangKyRepository dangKyRepo;
    private final SuKienRepository suKienRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final NhanVienRepository nhanVienRepo;
    private final KhachHangRepository khachHangRepo;
    private final CauHoiRepository cauHoiRepo;
    private final JwtUtil jwtUtil;

    public CauHoiService(DangKyRepository dangKyRepo, SuKienRepository suKienRepo,
                         TaiKhoanRepository taiKhoanRepo, CauHoiRepository cauHoiRepo,
                         NhanVienRepository nhanVienRepo, KhachHangRepository khachHangRepo,
                         JwtUtil jwtUtil) {
        this.dangKyRepo = dangKyRepo;
        this.suKienRepo = suKienRepo;
        this.taiKhoanRepo = taiKhoanRepo;
        this.cauHoiRepo = cauHoiRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.khachHangRepo = khachHangRepo;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> addCauHoi(String maDangKy, CreateCauHoiRequest request, HttpServletRequest httpReq) {
        // 1. Check DangKy exists
        Optional<DangKy> dkOpt = dangKyRepo.findById(maDangKy);
        if (dkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Registration not found"));
        }

        DangKy dangKy = dkOpt.get();

        // 2. Check DangKy status
        if (!"Thành công".equals(dangKy.getTrangThaiDangKy())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Only successful registrations can submit questions"));
        }

        // 3. Get maKhachHang from JWT token
        Claims claims = jwtUtil.extractClaimsFromRequest(httpReq);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);

        Optional<TaiKhoan> tkOpt = taiKhoanRepo.findById(maTaiKhoan);
        if (tkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid account"));
        }

        Integer tokenKhachHangId = tkOpt.get().getMaKhachHang();
        if (!tokenKhachHangId.equals(dangKy.getMaKhachHang())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You are not allowed to submit question for this registration"));
        }

        if (cauHoiRepo.existsByMaKhachHangAndMaSuKien(tokenKhachHangId.toString(), dangKy.getMaSuKien().toString())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "You have already submitted question for this event"));
        }

        // 4. Check SuKien validity
        Integer maSuKien = dangKy.getMaSuKien();
        Optional<SuKien> skOpt = suKienRepo.findById(maSuKien);
        if (skOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
        }

        String trangThai = skOpt.get().getTrangThaiSuKien();
        if (!List.of("Còn chỗ", "Hết chỗ", "Hết hạn đăng ký").contains(trangThai)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Questions can only be submitted during registration period"));
        }

        // 5. Save CauHoi entity
        CauHoi cauHoi = new CauHoi();
        cauHoi.setNoiDungCauHoi(request.getCauHoi());
        cauHoi.setMaKhachHang(String.valueOf(dangKy.getMaKhachHang()));
        cauHoi.setMaSuKien(String.valueOf(dangKy.getMaSuKien()));
        cauHoi.setTrangThai("Chưa xử lý");
        cauHoi.setNoiDungTraLoi("N/A"); // set to N/A

        cauHoiRepo.save(cauHoi);

        return ResponseEntity.ok(Map.of("message", "Question submitted successfully"));
    }

    public ResponseEntity<?> answerCauHoi(Integer maCauHoi, AnswerCauHoiRequest request, HttpServletRequest httpReq) {
        Optional<CauHoi> chOpt = cauHoiRepo.findById(maCauHoi);
        if (chOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Question not found"));
        }

        CauHoi cauHoi = chOpt.get();

        if (cauHoi.getTrangThai().equals("Đã xử lý")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Question has already been answered"));
        }

        Claims claims = jwtUtil.extractClaimsFromRequest(httpReq);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);

        Optional<TaiKhoan> tkOpt = taiKhoanRepo.findById(maTaiKhoan);
        if (tkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid account"));
        }

        TaiKhoan taiKhoan = tkOpt.get();

        cauHoi.setNoiDungTraLoi(request.getAnswer());
        cauHoi.setTrangThai("Đã xử lý");

        if (taiKhoan.getMaNhanVien() != null) {
            cauHoi.setMaNhanVien(taiKhoan.getMaNhanVien().toString());
        }

        cauHoiRepo.save(cauHoi);

        return ResponseEntity.ok(Map.of("message", "Answer submitted successfully"));
    }

    public ResponseEntity<?> getAllCauHoi(int page, int size, HttpServletRequest request, String search, Integer maSuKien) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
        String vaiTro = claims.get("vaiTro", String.class);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);

        Pageable pageable = PageRequest.of(page, size);
        Page<CauHoi> pageResult;

        if ("KhachHang".equals(vaiTro)) {
            Optional<TaiKhoan> tk = taiKhoanRepo.findById(maTaiKhoan);
            if (tk.isEmpty() || tk.get().getMaKhachHang() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Khách hàng không tồn tại"));
            }

            Integer maKhachHang = tk.get().getMaKhachHang();

            if (search != null && !search.isBlank()) {
                if (maSuKien != null) {
                    pageResult = cauHoiRepo.findByMaKhachHangAndMaSuKienAndSuKien_TenSuKienContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
                        maKhachHang.toString(), maSuKien.toString(), search, search, pageable
                    );
                } else {
                    pageResult = cauHoiRepo.findByMaKhachHangAndSuKien_TenSuKienContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
                        maKhachHang.toString(), search, search, pageable
                    );
                }
            } else {
                if (maSuKien != null) {
                    pageResult = cauHoiRepo.findByMaKhachHangAndMaSuKien(maKhachHang.toString(), maSuKien.toString(), pageable);
                } else {
                    pageResult = cauHoiRepo.findByMaKhachHang(maKhachHang.toString(), pageable);
                }
            }
        } else if ("NhanVien".equals(vaiTro) || "QuanLy".equals(vaiTro)) {
            if (search != null && !search.isBlank()) {
                if (maSuKien != null) {
                    pageResult = cauHoiRepo.findByMaSuKienAndKhachHang_HoTenContainingIgnoreCaseOrSuKien_TenSuKienContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
                        maSuKien.toString(), search, search, search, pageable
                    );
                } else {
                    pageResult = cauHoiRepo.findByKhachHang_HoTenContainingIgnoreCaseOrSuKien_TenSuKienContainingIgnoreCaseOrNhanVien_HoTenContainingIgnoreCase(
                        search, search, search, pageable
                    );
                }
            } else {
                if (maSuKien != null) {
                    pageResult = cauHoiRepo.findByMaSuKien(maSuKien.toString(), pageable);
                } else {
                    pageResult = cauHoiRepo.findAll(pageable);
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Vai trò không hợp lệ"));
        }

        Page<GetAllCauHoiResponse> responsePage = pageResult.map(ch -> {
            String tenKhachHang = khachHangRepo.findById(Integer.valueOf(ch.getMaKhachHang()))
                .map(KhachHang::getHoTen)
                .orElse("Unknown");

            String tenSuKien = suKienRepo.findById(Integer.valueOf(ch.getMaSuKien()))
                .map(SuKien::getTenSuKien)
                .orElse("Unknown");

            String tenNhanVien = "";
            if (ch.getMaNhanVien() != null) {
                tenNhanVien = nhanVienRepo.findById(Integer.valueOf(ch.getMaNhanVien()))
                    .map(NhanVien::getHoTen)
                    .orElse("Unknown");
            } else if ("Đã xử lý".equals(ch.getTrangThai())) {
                tenNhanVien = "Quản lí";
            }

            return new GetAllCauHoiResponse(
                ch.getMaCauHoi(),
                ch.getNoiDungCauHoi(),
                ch.getNoiDungTraLoi(),
                ch.getTrangThai(),
                tenKhachHang,
                tenSuKien,
                tenNhanVien
            );
        });

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> getById(Integer maCauHoi, HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
        String vaiTro = claims.get("vaiTro", String.class);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);
    
        Optional<CauHoi> chOpt = cauHoiRepo.findById(maCauHoi);
        if (chOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Câu hỏi không tồn tại"));
        }
    
        CauHoi ch = chOpt.get();
    
        if ("KhachHang".equals(vaiTro)) {
            Optional<TaiKhoan> tkOpt = taiKhoanRepo.findById(maTaiKhoan);
            if (tkOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Không tìm thấy tài khoản"));
            }
    
            KhachHang kh = khachHangRepo.findById(tkOpt.get().getMaKhachHang())
                                        .orElse(null);
    
            if (kh == null || !kh.getMaKhachHang().toString().equals(ch.getMaKhachHang())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Câu hỏi không tồn tại"));
            }
        }
    
        String tenKhachHang = khachHangRepo.findById(Integer.valueOf(ch.getMaKhachHang()))
                                           .map(KhachHang::getHoTen)
                                           .orElse("Unknown");
    
        String tenSuKien = suKienRepo.findById(Integer.valueOf(ch.getMaSuKien()))
                                      .map(SuKien::getTenSuKien)
                                      .orElse("Unknown");
    
        String tenNhanVien = null;
        if ("Đã xử lý".equals(ch.getTrangThai()) && ch.getMaNhanVien() == null) {
            tenNhanVien = "Quản lí";
        } else if (ch.getMaNhanVien() != null) {
            tenNhanVien = nhanVienRepo.findById(Integer.valueOf(ch.getMaNhanVien()))
                                      .map(NhanVien::getHoTen)
                                      .orElse("Unknown");
        }

        GetAllCauHoiResponse response = new GetAllCauHoiResponse(
            ch.getMaCauHoi(),
            ch.getNoiDungCauHoi(),
            ch.getNoiDungTraLoi(),
            ch.getTrangThai(),
            tenKhachHang,
            tenSuKien,
            tenNhanVien
        );
    
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getBySuKien(Integer maSuKien, HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);

        Optional<TaiKhoan> tkOpt = taiKhoanRepo.findById(maTaiKhoan);
        if (tkOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Account not found"));
        }

        TaiKhoan taiKhoan = tkOpt.get();
        Integer maKhachHang = taiKhoan.getMaKhachHang();

        Optional<CauHoi> chOpt = cauHoiRepo.findByMaSuKienAndMaKhachHang(maSuKien.toString(), maKhachHang.toString());
        if (chOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "You haven't asked a question for this event"));
        }

        CauHoi cauHoi = chOpt.get();
        String tenKhachHang = khachHangRepo.findById(Integer.valueOf(cauHoi.getMaKhachHang()))
                                           .map(KhachHang::getHoTen)
                                           .orElse("Unknown");
    
        String tenSuKien = suKienRepo.findById(Integer.valueOf(cauHoi.getMaSuKien()))
                                      .map(SuKien::getTenSuKien)
                                      .orElse("Unknown");
    
        String tenNhanVien = null;
        if ("Đã xử lý".equals(cauHoi.getTrangThai()) && cauHoi.getMaNhanVien() == null) {
            tenNhanVien = "Quản lí";
        } else if (cauHoi.getMaNhanVien() != null) {
            tenNhanVien = nhanVienRepo.findById(Integer.valueOf(cauHoi.getMaNhanVien()))
                                      .map(NhanVien::getHoTen)
                                      .orElse("Unknown");
        }

        GetAllCauHoiResponse response = new GetAllCauHoiResponse(
            cauHoi.getMaCauHoi(),
            cauHoi.getNoiDungCauHoi(),
            cauHoi.getNoiDungTraLoi(),
            cauHoi.getTrangThai(),
            tenKhachHang,
            tenSuKien,
            tenNhanVien
        );
    
        return ResponseEntity.ok(response);
    }
    
}
