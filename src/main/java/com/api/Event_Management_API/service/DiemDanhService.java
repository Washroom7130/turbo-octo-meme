package com.api.Event_Management_API.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.DiemDanh.GetVisitorResponse;
import com.api.Event_Management_API.model.DangKy;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.SuKien;
import com.api.Event_Management_API.repository.DangKyRepository;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.SuKienRepository;

@Service
public class DiemDanhService {
    
    private final DangKyRepository dangKyRepo;
    private final SuKienRepository suKienRepo;
    private final KhachHangRepository khachHangRepo;

    public DiemDanhService(DangKyRepository dangKyRepo,
                        SuKienRepository suKienRepo,
                        KhachHangRepository khachHangRepo) {
        this.dangKyRepo = dangKyRepo;
        this.suKienRepo = suKienRepo;
        this.khachHangRepo = khachHangRepo;
    }

    public ResponseEntity<?> getVisitor(int page, int size, Integer maSuKien) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DangKy> pageResult = dangKyRepo.findByMaSuKienAndTrangThaiDangKy(maSuKien, "Thành công", pageable);

        Optional<SuKien> skOpt = suKienRepo.findById(maSuKien);
        if (skOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
        }

        SuKien suKien = skOpt.get();
        if (!suKien.getTrangThaiSuKien().equals("Đang diễn ra")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Event has not started"));
        }

        Page<GetVisitorResponse> responsePage = pageResult.map(dk -> {
            String hoTen = khachHangRepo.findById(dk.getMaKhachHang())
                            .map(KhachHang::getHoTen)
                            .orElse("Unknown");

            return new GetVisitorResponse(
                dk.getMaDangKy(),
                hoTen,
                dk.getViTriGhe()
            );
        });

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> diemDanh(String maDangKy) {
        Optional<DangKy> dkOpt = dangKyRepo.findById(maDangKy);

        if (maDangKy.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Entry not found"));
        }

        DangKy dangKy = dkOpt.get();
        if (!dangKy.getTrangThaiDangKy().equals("Thành công")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid entry"));
        }

        Optional<SuKien> skOpt = suKienRepo.findById(dangKy.getMaSuKien());
        if (skOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
        }

        SuKien suKien = skOpt.get();
        if (!suKien.getTrangThaiSuKien().equals("Đang diễn ra")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Event has not started"));
        }

        dangKy.setTrangThaiDangKy("Đã điểm danh");
        dangKyRepo.save(dangKy);

        return ResponseEntity.ok(Map.of("message", "Success"));
    }

}
