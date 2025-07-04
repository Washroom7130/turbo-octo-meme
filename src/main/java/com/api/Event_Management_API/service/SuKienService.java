package com.api.Event_Management_API.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.SuKien.CUSuKienRequest;
import com.api.Event_Management_API.dto.SuKien.SuKienResponse;
import com.api.Event_Management_API.dto.SuKien.UpdateSuKienRequest;
import com.api.Event_Management_API.model.SuKien;
import com.api.Event_Management_API.repository.DanhMucSuKienRepository;
import com.api.Event_Management_API.repository.SuKienRepository;
import com.api.Event_Management_API.util.FileUploadUtil;

@Service
public class SuKienService {

    private final SuKienRepository suKienRepo;
    private final DanhMucSuKienRepository danhMucRepo;

    public SuKienService(SuKienRepository suKienRepo,
                        DanhMucSuKienRepository danhMucRepo) {
        this.suKienRepo = suKienRepo;
        this.danhMucRepo = danhMucRepo;
    }

    private void applyUpdates(UpdateSuKienRequest request, SuKien suKien) {
        if (request.getTenSuKien() != null) suKien.setTenSuKien(request.getTenSuKien());
        if (request.getMoTa() != null) suKien.setMoTa(request.getMoTa());
        if (request.getDiaDiem() != null) suKien.setDiaDiem(request.getDiaDiem());
        if (request.getLuongChoNgoi() != null) suKien.setLuongChoNgoi(request.getLuongChoNgoi());
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

        Page<SuKienResponse> responsePage = suKienPage.map(sk -> new SuKienResponse(
            sk.getMaSuKien(),
            sk.getTenSuKien(),
            sk.getMoTa(),
            sk.getAnhSuKien(),
            sk.getDiaDiem(),
            sk.getPhiThamGia(),
            sk.getLuongChoNgoi(),
            sk.getNgayBatDau(),
            sk.getNgayKetThuc(),
            sk.getMaDanhMuc()
        ));

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> getOne(Integer maSuKien) {
        Optional<SuKien> suKienOptional = suKienRepo.findById(maSuKien);

        if (suKienOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
        }

        SuKien suKien = suKienOptional.get();
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
            suKien.getMaDanhMuc()
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
}
