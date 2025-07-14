package com.api.Event_Management_API.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.model.SuKien;
import com.api.Event_Management_API.repository.SuKienRepository;

import jakarta.transaction.Transactional;

@Service
public class SuKienStatusScheduler {
    private final SuKienRepository suKienRepo;

    public SuKienStatusScheduler(SuKienRepository suKienRepo) {
        this.suKienRepo = suKienRepo;
    }

    @Scheduled(fixedRate = 60_000) // runs every 60 seconds
    @Transactional
    public void updateEventStatus() {
        LocalDateTime now = LocalDateTime.now();

        // Update "Đang diễn ra"
        List<SuKien> dangDienRa = suKienRepo.findByNgayBatDauBeforeAndNgayKetThucAfter(now, now);
        for (SuKien sk : dangDienRa) {
            if(!"Đang diễn ra".equals(sk.getTrangThaiSuKien())) {
                sk.setTrangThaiSuKien("Đang diễn ra");
            }
        }

        // Update "Đã kết thúc"
        List<SuKien> daKetThuc = suKienRepo.findByNgayKetThucBefore(now);
        for (SuKien sk : daKetThuc) {
            if(!"Đã kết thúc".equals(sk.getTrangThaiSuKien())) {
                sk.setTrangThaiSuKien("Đã kết thúc");
            }
        }

        // Update "Hết hạn đăng ký"
        List<SuKien> hetHanDangKy = suKienRepo.findByNgayBatDauBeforeAndTrangThaiSuKienNot(now.plusDays(3), "Hết hạn đăng ký");
        for (SuKien sk : hetHanDangKy) {
            if ("Còn chỗ".equals(sk.getTrangThaiSuKien()) || "Hết chỗ".equals(sk.getTrangThaiSuKien())) {
                sk.setTrangThaiSuKien("Hết hạn đăng ký");
            }
        }

        // Save updated status
        suKienRepo.saveAll(dangDienRa);
        suKienRepo.saveAll(daKetThuc);
        suKienRepo.saveAll(hetHanDangKy);
    }
}
