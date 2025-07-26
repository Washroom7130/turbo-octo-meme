package com.api.Event_Management_API.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.Admin.AddNhanVienRequest;
import com.api.Event_Management_API.dto.Admin.GetTaiKhoanListResponse;
import com.api.Event_Management_API.dto.DanhGia.SuKienRating;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.NhanVien;
import com.api.Event_Management_API.model.SuKien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.repository.DangKyRepository;
import com.api.Event_Management_API.repository.DanhGiaRepository;
import com.api.Event_Management_API.repository.HoaDonRepository;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.NhanVienRepository;
import com.api.Event_Management_API.repository.SuKienRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.repository.TicketRepository;

@Service
public class AdminService {
    private final TaiKhoanRepository taiKhoanRepo;
    private final NhanVienRepository nhanVienRepo;
    private final KhachHangRepository khachHangRepo;
    private final HoaDonRepository hoaDonRepo;
    private final SuKienRepository suKienRepo;
    private final TicketRepository ticketRepo;
    private final DanhGiaRepository danhGiaRepo;
    private final DangKyRepository dangKyRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminService(TaiKhoanRepository taiKhoanRepo,
                        NhanVienRepository nhanVienRepo,
                        KhachHangRepository khachHangRepo,
                        HoaDonRepository hoaDonRepo,
                        SuKienRepository suKienRepo,
                        TicketRepository ticketRepo,
                        DanhGiaRepository danhGiaRepo,
                        DangKyRepository dangKyRepo,
                        PasswordEncoder passwordEncoder) {
        this.taiKhoanRepo = taiKhoanRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.khachHangRepo = khachHangRepo;
        this.hoaDonRepo = hoaDonRepo;
        this.ticketRepo = ticketRepo;
        this.suKienRepo = suKienRepo;
        this.danhGiaRepo = danhGiaRepo;
        this.dangKyRepo = dangKyRepo;
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
        taiKhoan.setTrangThai("Hoạt động");
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
        taiKhoan.setTrangThai(action.equals("activate") ? "Hoạt động" : "Dừng hoạt động");

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
            dto.setMaId(tk.getMaNhanVien());

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
            dto.setMaId(tk.getMaKhachHang());

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

    public ResponseEntity<?> statistics(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime defaultEnd = now;
        LocalDateTime defaultStart = now.minusMonths(12);

        // Sanitize inputs
        if (startDate == null || endDate == null || startDate.isAfter(endDate) || startDate.isAfter(now) || endDate.isAfter(now)) {
            startDate = defaultStart;
            endDate = defaultEnd;
        }

        // Basic data
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSuKien", suKienRepo.countByNgayTaoSuKienBetween(startDate, endDate));
        stats.put("suKienDangDienRa", suKienRepo.countByNgayTaoSuKienBetweenAndTrangThaiSuKien(startDate, endDate, "Đang diễn ra"));
        stats.put("suKienSapDienRa", suKienRepo.countByNgayTaoSuKienBetweenAndTrangThaiSuKienIn(startDate, endDate, List.of("Còn chỗ", "Hết chỗ", "Hết hạn đăng ký")));
        stats.put("totalKhachHang", khachHangRepo.count());
        stats.put("totalNhanVien", nhanVienRepo.count());
        stats.put("totalUnAnsweredTicket", ticketRepo.countByTrangThai("Chưa xử lý"));
        stats.put("totalRevenue", hoaDonRepo.sumTongTienByTrangThaiHoaDonAndThoiGianThanhCongBetween("Đã thanh toán", startDate, endDate));

        // Rating stuff
        // Rating part will go here
        List<Object[]> avgRatings = danhGiaRepo.findAverageRatingPerSuKienInRange(startDate, endDate);

        Map<Integer, Double> avgMap = new HashMap<>();
        for (Object[] row : avgRatings) {
            Integer maSuKien = (Integer) row[0];
            Double avg = (Double) row[1];
            avgMap.put(maSuKien, avg);
        }

        List<SuKien> allRatedSuKiens = suKienRepo.findAllById(avgMap.keySet());
        List<SuKienRating> good = new ArrayList<>();
        List<SuKienRating> bad = new ArrayList<>();

        for (SuKien sk : allRatedSuKiens) {
            double avg = avgMap.get(sk.getMaSuKien());
            SuKienRating skRating = new SuKienRating();
            skRating.setTenSuKien(sk.getTenSuKien());
            skRating.setAvg(avg);
        
            if (avg >= 3.0) good.add(skRating);
            else bad.add(skRating);
        }        

        int total = good.size() + bad.size();
        double goodPercent = total > 0 ? Math.round(((double) good.size() / total) * 1000.0) / 10.0 : 0.0;
        double badPercent = 100.0 - goodPercent;

        List<Map<String, Object>> top3Good = good.stream()
                .sorted(Comparator.comparingDouble(SuKienRating::getAvg).reversed())
                .limit(3)
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("tenSuKien", r.getTenSuKien());
                    m.put("avg", Math.round(r.getAvg() * 10.0) / 10.0); // round to 1 decimal
                    return m;
                })
                .toList();

        List<Map<String, Object>> top3Bad = bad.stream()
                .sorted(Comparator.comparingDouble(SuKienRating::getAvg))
                .limit(3)
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("tenSuKien", r.getTenSuKien());
                    m.put("avg", Math.round(r.getAvg() * 10.0) / 10.0); // round to 1 decimal
                    return m;
                })
                .toList();

        Map<String, Object> ratingStats = new HashMap<>();
        ratingStats.put("suKienTot", goodPercent);
        ratingStats.put("suKienTe", badPercent);
        ratingStats.put("top3SuKienTot", top3Good);
        ratingStats.put("top3SuKienTe", top3Bad);
        
        stats.put("ratingStats", ratingStats);

        // List top 3 events with highest dangKy count
        List<Object[]> topDangKyRaw = dangKyRepo.findTopSuKienByDangKyCountInRange(startDate, endDate);
        List<Integer> topMaSuKienList = topDangKyRaw.stream()
                .limit(3)
                .map(row -> (Integer) row[0])
                .toList();

        Map<Integer, Long> dangKyCountMap = topDangKyRaw.stream()
                .limit(3)
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Long) row[1]
                ));

        List<SuKien> topSuKiens = suKienRepo.findAllById(topMaSuKienList);

        List<Map<String, Object>> top3MostRegistered = topSuKiens.stream()
                .map(sk -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("maSuKien", sk.getMaSuKien());
                    m.put("tenSuKien", sk.getTenSuKien());
                    m.put("soLuongDangKy", dangKyCountMap.get(sk.getMaSuKien()));
                    return m;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("soLuongDangKy"), (Long) a.get("soLuongDangKy")))
                .toList();

        stats.put("top3MostRegistered", top3MostRegistered);

        // calculate revenue in smaller time ranges
        Map<String, Float> revenueTimeline = new LinkedHashMap<>();
        Duration totalDuration = Duration.between(startDate, endDate);
        Duration step = totalDuration.dividedBy(10);
        LocalDateTime rangeStart = startDate;

        for (int i = 0; i < 10; i++) {
            LocalDateTime rangeEnd = (i < 9) ? rangeStart.plus(step) : endDate;

            Float sum = hoaDonRepo.sumRevenueBetween(rangeStart, rangeEnd);
            String key = rangeStart.toLocalDate() + " - " + rangeEnd.toLocalDate();

            revenueTimeline.put(key, sum != null ? sum : 0f);
            rangeStart = rangeEnd;
        }
        stats.put("revenueTimeline", revenueTimeline);

        // Statistics for KhachHang trangThai
        rangeStart = startDate;
        Map<String, Map<String, Long>> khachHangTimeline = new LinkedHashMap<>();
        
        for (int i = 0; i < 10; i++) {
            LocalDateTime rangeEnd = (i < 9) ? rangeStart.plus(step) : endDate;
        
            Long active = taiKhoanRepo.countActiveKhachHangBetween(rangeStart, rangeEnd);
            Long nonActive = taiKhoanRepo.countNonActiveKhachHangBetween(rangeStart, rangeEnd);
        
            Map<String, Long> countMap = new HashMap<>();
            countMap.put("active", active != null ? active : 0);
            countMap.put("non-active", nonActive != null ? nonActive : 0);
        
            String key = rangeStart.toLocalDate() + " - " + rangeEnd.toLocalDate();
            khachHangTimeline.put(key, countMap);
        
            rangeStart = rangeEnd;
        }
        stats.put("khachHangTimeline", khachHangTimeline);
        
        // Statistics for trangThai of suKien entity
        rangeStart = startDate;
        Map<String, Map<String, Integer>> suKienTrangThaiTimeline = new LinkedHashMap<>();

        for (int i = 0; i < 10; i++) {
            LocalDateTime rangeEnd = (i < 9) ? rangeStart.plus(step) : endDate;

            int upcoming = suKienRepo.countUpcomingSuKienAfterRangeEnd(rangeEnd);
            int ongoing = suKienRepo.countOngoingSuKienBetween(rangeStart, rangeEnd);
            int cancelled = suKienRepo.countCancelledSuKienInRange(rangeStart, rangeEnd);

            Map<String, Integer> countMap = new HashMap<>();
            countMap.put("upcoming", upcoming);
            countMap.put("ongoing", ongoing);
            countMap.put("cancelled", cancelled);

            String key = rangeStart.toLocalDate() + " - " + rangeEnd.toLocalDate();
            suKienTrangThaiTimeline.put(key, countMap);

            rangeStart = rangeEnd;
        }
        stats.put("suKienTrangThaiTimeline", suKienTrangThaiTimeline);

        return ResponseEntity.ok(stats);
    }
}
