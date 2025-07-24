package com.api.Event_Management_API.controller.hoadon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.service.HoaDonService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/hoadon")
public class HoaDonController {
    
    @Autowired
    private HoaDonService hoaDonService;

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy', 'KhachHang')")
    @GetMapping("/get/all")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page, 
                                    @RequestParam(defaultValue = "10") int size, 
                                    @RequestParam(required = false) String search,
                                    @RequestParam(required = false) String trangThaiSuKien,
                                    HttpServletRequest request) {
        return hoaDonService.getAll(page, size, request, search, trangThaiSuKien);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy', 'KhachHang')")
    @GetMapping("/get/{maHoaDon}")
    public ResponseEntity<?> getOne(@PathVariable String maHoaDon, HttpServletRequest request) {
        return hoaDonService.getOne(maHoaDon, request);
    }
}
