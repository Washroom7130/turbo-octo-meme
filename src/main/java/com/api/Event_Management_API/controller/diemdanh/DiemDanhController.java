package com.api.Event_Management_API.controller.diemdanh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.service.DiemDanhService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/diemdanh")
public class DiemDanhController {
    
    @Autowired
    private DiemDanhService diemDanhService;

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @GetMapping("/get/all/{maSuKien}")
    public ResponseEntity<?> getAllByEvent(
        @PathVariable Integer maSuKien,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ) {
        return diemDanhService.getAllByMaSuKien(maSuKien, page, size, search);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy', 'KhachHang')")
    @GetMapping("/get/{maDiemDanh}")
    public ResponseEntity<?> getById(@PathVariable String maDiemDanh, HttpServletRequest request) {
        return diemDanhService.getByMaDiemDanh(maDiemDanh, request);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @PostMapping("/{maDiemDanh}")
    public ResponseEntity<?> diemDanh(@PathVariable String maDiemDanh) {
        return diemDanhService.diemDanh(maDiemDanh);
    }
}
