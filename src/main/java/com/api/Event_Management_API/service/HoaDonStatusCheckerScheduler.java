package com.api.Event_Management_API.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.model.HoaDon;
import com.api.Event_Management_API.repository.DangKyRepository;
import com.api.Event_Management_API.repository.HoaDonRepository;

import jakarta.transaction.Transactional;

@Service
public class HoaDonStatusCheckerScheduler {
    private final DangKyRepository dangKyRepo;
    private final HoaDonRepository hoaDonRepo;

    public HoaDonStatusCheckerScheduler(DangKyRepository dangKyRepo,
                                        HoaDonRepository hoaDonRepo) {
        this.dangKyRepo = dangKyRepo;
        this.hoaDonRepo = hoaDonRepo;
    }

    @Scheduled(fixedRate = 60_000) // every 60 seconds
    @Transactional
    public void cancelExpiredHoaDon() {
        LocalDateTime now = LocalDateTime.now();

        List<HoaDon> expiredHoaDons = hoaDonRepo.findByTrangThaiHoaDonAndThoiGianHieuLucBefore("Chưa thanh toán", now);

        for (HoaDon hd : expiredHoaDons) {
            // Update HoaDon status
            hd.setTrangThaiHoaDon("Đã hủy");

            // Update DangKy status if found
            dangKyRepo.findById(hd.getMaDangKy()).ifPresent(dk -> {
                dk.setTrangThaiDangKy("Đã hủy");
                dangKyRepo.save(dk);
            });

            hoaDonRepo.save(hd);
        }
    }
}
