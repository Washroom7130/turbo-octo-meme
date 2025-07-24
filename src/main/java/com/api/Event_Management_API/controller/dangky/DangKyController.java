package com.api.Event_Management_API.controller.dangky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.dto.DangKy.GetAllDangKyStaffResponse;
import com.api.Event_Management_API.service.DangKyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/dangky")
public class DangKyController {

    @Autowired
    private DangKyService dangKyService;

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy', 'KhachHang')")
    @GetMapping("/get/all")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page, 
                                    @RequestParam(defaultValue = "10") int size, 
                                    HttpServletRequest request, 
                                    @RequestParam(required = false) String search,
                                    @RequestParam(required = false) String trangThaiSuKien) {
        return dangKyService.getAll(page, size, search, trangThaiSuKien, request);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy', 'KhachHang')")
    @GetMapping("/get/{maDangKy}")
    public ResponseEntity<?> getOne(@PathVariable String maDangKy, HttpServletRequest request) {
        return dangKyService.getOne(maDangKy, request);
    }
}
