package com.api.Event_Management_API.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.SuKien.CUSuKienRequest;
import com.api.Event_Management_API.dto.SuKien.DangKySuKienRequest;
import com.api.Event_Management_API.dto.SuKien.SuKienResponse;
import com.api.Event_Management_API.dto.SuKien.UpdateSuKienRequest;
import com.api.Event_Management_API.model.DangKy;
import com.api.Event_Management_API.model.DanhGia;
import com.api.Event_Management_API.model.DiemDanh;
import com.api.Event_Management_API.model.HoaDon;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.SuKien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.repository.DangKyRepository;
import com.api.Event_Management_API.repository.DanhGiaRepository;
import com.api.Event_Management_API.repository.DanhMucSuKienRepository;
import com.api.Event_Management_API.repository.DiemDanhRepository;
import com.api.Event_Management_API.repository.HoaDonRepository;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.SuKienRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.util.FileUploadUtil;
import com.api.Event_Management_API.util.JwtUtil;
import com.api.Event_Management_API.util.OnlinePaymentUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class SuKienService {

    private final SuKienRepository suKienRepo;
    private final DanhMucSuKienRepository danhMucRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final KhachHangRepository khachHangRepo;
    private final DangKyRepository dangKyRepo;
    private final HoaDonRepository hoaDonRepo;
    private final DanhGiaRepository danhGiaRepo;
    private final DiemDanhRepository diemDanhRepo;
    private final JwtUtil jwtUtil;

    public SuKienService(SuKienRepository suKienRepo,
                        DanhMucSuKienRepository danhMucRepo,
                        TaiKhoanRepository taiKhoanRepo,
                        KhachHangRepository khachHangRepo,
                        DangKyRepository dangKyRepo,
                        HoaDonRepository hoadDonRepo,
                        DanhGiaRepository danhGiaRepo,
                        DiemDanhRepository diemDanhRepo,
                        JwtUtil jwtUtil) {
        this.suKienRepo = suKienRepo;
        this.danhMucRepo = danhMucRepo;
        this.taiKhoanRepo = taiKhoanRepo;
        this.khachHangRepo = khachHangRepo;
        this.dangKyRepo = dangKyRepo;
        this.hoaDonRepo = hoadDonRepo;
        this.danhGiaRepo = danhGiaRepo;
        this.diemDanhRepo = diemDanhRepo;
        this.jwtUtil = jwtUtil;
    }

    private void applyUpdates(UpdateSuKienRequest request, SuKien suKien) {
        if (request.getTenSuKien() != null) suKien.setTenSuKien(request.getTenSuKien());
        if (request.getMoTa() != null) suKien.setMoTa(request.getMoTa());
        if (request.getDiaDiem() != null) suKien.setDiaDiem(request.getDiaDiem());
        if (request.getPhiThamGia() != null) suKien.setPhiThamGia(request.getPhiThamGia());
        if (request.getNgayBatDau() != null) suKien.setNgayBatDau(request.getNgayBatDau());
        if (request.getNgayKetThuc() != null) suKien.setNgayKetThuc(request.getNgayKetThuc());
        if (request.getMaDanhMuc() != null) suKien.setMaDanhMuc(request.getMaDanhMuc());
    }

    public ResponseEntity<?> addSuKien(CUSuKienRequest request) {
        try {
            String imagePath = null;
            if (request.getAnhSuKien() != null && !request.getAnhSuKien().isEmpty()) {
                imagePath = FileUploadUtil.saveImageFile(request.getAnhSuKien());
            }

            // Validate date
            LocalDateTime now = LocalDateTime.now();
            if (request.getNgayBatDau().isBefore(now) || request.getNgayKetThuc().isBefore(now)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Dates cannot be in the past"));
            }

            if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
                return ResponseEntity.badRequest().body(Map.of("error", "End date cannot be before start date"));
            }

            // Validate maDanhMuc
            if (request.getMaDanhMuc() != null) {
                if (danhMucRepo.findById(request.getMaDanhMuc()).isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid category"));
                }
            }

            SuKien suKien = new SuKien();
            suKien.setTenSuKien(request.getTenSuKien());
            suKien.setMoTa(request.getMoTa());
            suKien.setAnhSuKien(imagePath);
            suKien.setDiaDiem(request.getDiaDiem());
            suKien.setTrangThaiSuKien("Còn chỗ");
            suKien.setPhiThamGia(request.getPhiThamGia());
            suKien.setLuongChoNgoi(request.getLuongChoNgoi());
            suKien.setNgayTaoSuKien(now);
            suKien.setNgayBatDau(request.getNgayBatDau());
            suKien.setNgayKetThuc(request.getNgayKetThuc());
            suKien.setMaDanhMuc(request.getMaDanhMuc());

            suKienRepo.save(suKien);

            return ResponseEntity.ok(Map.of("message", "Event added successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Can't save file"));
        }
    }

    public ResponseEntity<?> updateSuKien(UpdateSuKienRequest request, Integer maSuKien) {
        try {
            Optional<SuKien> suKienOptional = suKienRepo.findById(maSuKien);
            if (suKienOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
            }

            SuKien suKien = suKienOptional.get();

            // Prevent updating event that has closed registration and after
            if (!suKien.getTrangThaiSuKien().equals("Còn chỗ") && !suKien.getTrangThaiSuKien().equals("Hết chỗ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Can only update event before registration date"));
            }

            // Validate image if provided
            if (request.getAnhSuKien() != null && !request.getAnhSuKien().isEmpty()) {
                String imagePath = FileUploadUtil.saveImageFile(request.getAnhSuKien());
                suKien.setAnhSuKien(imagePath);
            }

            // Validate date if provided
            if (request.getNgayBatDau() != null || request.getNgayKetThuc() != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime start = request.getNgayBatDau() != null ? request.getNgayBatDau() : suKien.getNgayBatDau();
                LocalDateTime end = request.getNgayKetThuc() != null ? request.getNgayKetThuc() : suKien.getNgayKetThuc();

                if (start.isBefore(now) || end.isBefore(now)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Date cannot be in the past"));
                }

                if (end.isBefore(start)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "End date cannot before start date"));
                }
            }

            // Validate maDanhMuc if present
            if (request.getMaDanhMuc() != null && danhMucRepo.findById(request.getMaDanhMuc()).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Category not foound"));
            }

            // Apply all provided fields
            applyUpdates(request, suKien);

            suKienRepo.save(suKien);

            return ResponseEntity.ok(Map.of("message", "Event updated successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Can't save file"));
        }
    }

    public ResponseEntity<?> getAll(int page, int size, Integer maDanhMuc) {
        Pageable pageable = PageRequest.of(page, size);

        Page<SuKien> suKienPage;

        if (maDanhMuc != null) {
            if (danhMucRepo.findById(maDanhMuc).isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid category ID"));
            }
            suKienPage = suKienRepo.findByMaDanhMuc(maDanhMuc, pageable);
        } else {
            suKienPage = suKienRepo.findAll(pageable);
        }

        Page<SuKienResponse> responsePage = suKienPage.map(sk -> {
            List<DanhGia> danhGias = danhGiaRepo.findByMaSuKien(sk.getMaSuKien());
    
            float rating = -1;
            if (!danhGias.isEmpty()) {
                float total = 0;
                for (DanhGia dg : danhGias) {
                    total += dg.getLoaiDanhGia();
                }
                rating = Math.round((total / danhGias.size()) * 10f) / 10f; // round to 1 decimal
            }

            List<DangKy> dangKys = dangKyRepo.findByMaSuKienAndTrangThaiDangKyIn(
                sk.getMaSuKien(), List.of("Đang xử lý", "Thành công")
            );

            List<String> occupiedSeat = dangKys.stream()
                .map(DangKy::getViTriGhe)
                .collect(Collectors.toList());
    
            return new SuKienResponse(
                sk.getMaSuKien(),
                sk.getTenSuKien(),
                sk.getMoTa(),
                sk.getAnhSuKien(),
                sk.getDiaDiem(),
                sk.getPhiThamGia(),
                sk.getLuongChoNgoi(),
                sk.getNgayBatDau(),
                sk.getNgayKetThuc(),
                sk.getMaDanhMuc(),
                rating,
                occupiedSeat
            );
        });

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> getOne(Integer maSuKien) {
        Optional<SuKien> suKienOptional = suKienRepo.findById(maSuKien);

        if (suKienOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
        }

        SuKien suKien = suKienOptional.get();
        List<DanhGia> dgs = danhGiaRepo.findByMaSuKien(maSuKien);
        float rating = -1;
        if (!dgs.isEmpty()) {
            float total = 0;
            for (DanhGia dg : dgs) {
                total += dg.getLoaiDanhGia();
            }
            rating = Math.round((total / dgs.size()) * 10f) / 10f; // round to 1 decimal
        }

        List<DangKy> dangKys = dangKyRepo.findByMaSuKienAndTrangThaiDangKyIn(
            suKien.getMaSuKien(), List.of("Đang xử lý", "Thành công")
        );

        List<String> occupiedSeat = dangKys.stream()
            .map(DangKy::getViTriGhe)
            .collect(Collectors.toList());

        SuKienResponse suKienResponse = new SuKienResponse(
            suKien.getMaSuKien(),
            suKien.getTenSuKien(),
            suKien.getMoTa(),
            suKien.getAnhSuKien(),
            suKien.getDiaDiem(),
            suKien.getPhiThamGia(),
            suKien.getLuongChoNgoi(),
            suKien.getNgayBatDau(),
            suKien.getNgayKetThuc(),
            suKien.getMaDanhMuc(),
            rating,
            occupiedSeat
        );

        return ResponseEntity.ok(suKienResponse);
    }

    public ResponseEntity<?> cancel(Integer maSuKien) {
        Optional<SuKien> suKienOptional = suKienRepo.findById(maSuKien);

        // Check if exist
        if (suKienOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
        }

        // Check if cancel-able
        SuKien suKien = suKienOptional.get();
        if (!suKien.getTrangThaiSuKien().equals("Còn chỗ") && !suKien.getTrangThaiSuKien().equals("Hết chỗ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Cannot cancel event that has already started"));
        }

        suKien.setTrangThaiSuKien("Hủy bỏ");
        suKienRepo.save(suKien);

        return ResponseEntity.ok(Map.of("message", "Event cancel successfully"));
    }

    public ResponseEntity<?> dangky(DangKySuKienRequest request, Integer maSuKien, HttpServletRequest httpServletRequest) {
        Optional<SuKien> suKienOptional = suKienRepo.findById(maSuKien);

        // Check if exist
        if (suKienOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
        }

        SuKien suKien = suKienOptional.get();
        String status = suKien.getTrangThaiSuKien();

        if (!status.equals("Còn chỗ")) {
            String msg = switch (status) {
                case "Hết chỗ" -> "Event is full";
                case "Hết hạn đăng ký" -> "Event has stopped receiving registration";
                case "Đã hủy" -> "Event has been cancelled";
                case "Đang diễn ra" -> "Event is in progress";
                case "Đã kết thúc" -> "Event has ended";
                default -> "Event is not available for registration";
            };
            return ResponseEntity.badRequest().body(Map.of("error", msg));
        }

        // Check if seat has taken
        if (dangKyRepo.existsByMaSuKienAndViTriGhe(maSuKien, request.getViTriGhe())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Seat has already been taken"));
        }

        // Extract maTaiKhoan from JWT
        String maTaiKhoan = jwtUtil.extractIdFromRequest(httpServletRequest);
        if (maTaiKhoan == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid token"));
        }

        // Find user
        Optional<TaiKhoan> taiKhoanOptional = taiKhoanRepo.findById(maTaiKhoan);
        if (taiKhoanOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        Optional<KhachHang> khachHang = khachHangRepo.findById(taiKhoanOptional.get().getMaKhachHang());

        // Check if user has already signed up
        if (dangKyRepo.existsByMaKhachHangAndMaSuKien(khachHang.get().getMaKhachHang(), maSuKien)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "You have already signed up for this event"));
        }

        // Create DangKy
        DangKy dangKy = new DangKy();
        dangKy.setMaDangKy(UUID.randomUUID().toString());
        dangKy.setNgayDangKy(LocalDateTime.now());
        dangKy.setViTriGhe(request.getViTriGhe());
        dangKy.setTrangThaiDangKy("Đang xử lý");
        dangKy.setMaKhachHang(khachHang.get().getMaKhachHang());
        dangKy.setMaSuKien(maSuKien);

        dangKyRepo.save(dangKy);

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(UUID.randomUUID().toString());
        hoaDon.setNgayTao(LocalDateTime.now());
        hoaDon.setTrangThaiHoaDon("Chưa thanh toán");
        hoaDon.setTongTien(suKien.getPhiThamGia());
        hoaDon.setThoiGianHieuLuc(LocalDateTime.now().plusMinutes(10)); // change this later
        hoaDon.setThoiGianThanhCong(LocalDateTime.now());
        hoaDon.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        hoaDon.setMaKhachHang(khachHang.get().getMaKhachHang());
        hoaDon.setMaDangKy(dangKy.getMaDangKy());

        hoaDonRepo.save(hoaDon);

        // Setting correct timezone
        long expireLong = hoaDon.getThoiGianHieuLuc().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toEpochSecond();
        String successUrl = dangKy.getMaDangKy() + "/" + hoaDon.getMaHoaDon() + "/success";
        String cancelUrl = dangKy.getMaDangKy() + "/" + hoaDon.getMaHoaDon() + "/cancel";

        OnlinePaymentUtil paymentUtil = new OnlinePaymentUtil();

        String url = paymentUtil.getPaymentURL(suKien.getTenSuKien(), Math.round(suKien.getPhiThamGia()), expireLong, successUrl, cancelUrl);

        long currentSuccessfulRegistration = dangKyRepo.countByMaSuKienAndTrangThaiDangKy(maSuKien, "Thành công");

        if (currentSuccessfulRegistration >= suKien.getLuongChoNgoi()) {
            suKien.setTrangThaiSuKien("Hết chỗ");
            suKienRepo.save(suKien);
        }

        return ResponseEntity.ok(Map.of("url", url));

    }

    public ResponseEntity<?> paymentSuccess(String maDangKy, String maHoaDon, HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);

        Optional<HoaDon> hdOpt = hoaDonRepo.findById(maHoaDon);
        if (hdOpt.isEmpty() || !hdOpt.get().getTrangThaiHoaDon().equals("Chưa thanh toán")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HoaDon hoaDon = hdOpt.get();

        Optional<DangKy> dkOpt = dangKyRepo.findById(hoaDon.getMaDangKy());
        if (dkOpt.isEmpty() || !dkOpt.get().getTrangThaiDangKy().equals("Đang xử lý")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // check if taikhoan exists and if maTaiKhoan from token matching the one from taiKhoan entity
        Optional<TaiKhoan> tkOpt = taiKhoanRepo.findByMaKhachHang(hoaDon.getMaKhachHang());
        if (tkOpt.isEmpty() || !tkOpt.get().getMaTaiKhoan().equals(maTaiKhoan)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Changing hoadon status
        hoaDon.setThoiGianThanhCong(LocalDateTime.now());
        hoaDon.setTrangThaiHoaDon("Đã thanh toán");
        hoaDonRepo.save(hoaDon);

        // Changing dangky status
        DangKy dangKy = dkOpt.get();
        dangKy.setTrangThaiDangKy("Thành công");
        dangKyRepo.save(dangKy);

        // Add Diemdanh entity
        DiemDanh diemDanh = new DiemDanh();
        diemDanh.setMaDiemDanh(UUID.randomUUID().toString());
        diemDanh.setNgayTaoVe(LocalDateTime.now());
        diemDanh.setTrangThaiDiemDanh("Vắng mặt");
        diemDanh.setViTriGheNgoi(dangKy.getViTriGhe());
        diemDanh.setMaDangKy(maDangKy);
        diemDanhRepo.save(diemDanh);

        return ResponseEntity.ok(Map.of("message", "Payment has been successfully processed"));
    }

    public ResponseEntity<?> paymentCancel(String maDangKy, String maHoaDon, HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaimsFromRequest(request);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);

        Optional<HoaDon> hdOpt = hoaDonRepo.findById(maHoaDon);
        if (hdOpt.isEmpty() || !hdOpt.get().getTrangThaiHoaDon().equals("Chưa thanh toán")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HoaDon hoaDon = hdOpt.get();

        Optional<DangKy> dkOpt = dangKyRepo.findById(hoaDon.getMaDangKy());
        if (dkOpt.isEmpty() || !dkOpt.get().getTrangThaiDangKy().equals("Đang xử lý")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // check if taikhoan exists and if maTaiKhoan from token matching the one from taiKhoan entity
        Optional<TaiKhoan> tkOpt = taiKhoanRepo.findByMaKhachHang(hoaDon.getMaKhachHang());
        if (tkOpt.isEmpty() || !tkOpt.get().getMaTaiKhoan().equals(maTaiKhoan)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Changing hoadon status
        hoaDon.setTrangThaiHoaDon("Đã hủy");
        hoaDonRepo.save(hoaDon);

        // Changing dangky status
        DangKy dangKy = dkOpt.get();
        dangKy.setTrangThaiDangKy("Đã hủy");
        dangKyRepo.save(dangKy);

        return ResponseEntity.ok(Map.of("message", "Payment has been successfully cancelled"));
    }
}
