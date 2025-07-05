package com.api.Event_Management_API.service;

import java.net.ResponseCache;

import org.springframework.http.ResponseEntity;

import com.api.Event_Management_API.repository.DangKyRepository;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.SuKienRepository;

public class DangKyService {
    private final DangKyRepository dangKyRepo;
    private final KhachHangRepository khachHangRepo;
    private final SuKienRepository suKienRepo;

    public DangKyService(DangKyRepository dangKyRepo,
                        KhachHangRepository khachHangRepo,
                        SuKienRepository suKienRepo) {
        this.dangKyRepo = dangKyRepo;
        this.khachHangRepo = khachHangRepo;
        this.suKienRepo = suKienRepo;
    }

    public ResponseEntity<?> getAllStaff(int page, int size) {
        // TODO: continue this part
    }

}
