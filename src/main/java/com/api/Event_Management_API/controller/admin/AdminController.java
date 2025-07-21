package com.api.Event_Management_API.controller.admin;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.dto.Admin.AddNhanVienRequest;
import com.api.Event_Management_API.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @PreAuthorize("hasAnyAuthority('QuanLy')")
    @PostMapping("/nhanvien/add")
    public ResponseEntity<?> addNhanVien(@Valid @RequestBody AddNhanVienRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String message = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", message));
        }

        return adminService.addNhanVien(request);
    }

    @PreAuthorize("hasAnyAuthority('QuanLy')")
    @PostMapping("/nhanvien/{action}/{maNhanVien}")
    public ResponseEntity<?> updateStaffStatus(@PathVariable String action, @PathVariable Integer maNhanVien) {
        return adminService.updateStaffStatus(action, maNhanVien);
    }

    @PreAuthorize("hasAnyAuthority('QuanLy')")
    @GetMapping("/nhanvien/get/all")
    public ResponseEntity<?> getAllNhanVien(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
        return adminService.getAllNV(page, size, search);
    }

    @PreAuthorize("hasAnyAuthority('QuanLy')")
    @GetMapping("/nhanvien/get/{maNhanVien}")
    public ResponseEntity<?> getOneNhanVien(@PathVariable Integer maNhanVien) {
        return adminService.getOneNV(maNhanVien);
    }

    @PreAuthorize("hasAnyAuthority('QuanLy', 'NhanVien')")
    @GetMapping("/khachhang/get/all")
    public ResponseEntity<?> getAllKhachHang(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
        return adminService.getAllKH(page, size, search);
    }

    @PreAuthorize("hasAnyAuthority('QuanLy', 'NhanVien')")
    @GetMapping("/khachhang/get/{maKhachHang}")
    public ResponseEntity<?> getOneKhachHang(@PathVariable Integer maKhachHang) {
        return adminService.getOneKH(maKhachHang);
    }

    @PreAuthorize("hasAnyAuthority('QuanLy')")
    @GetMapping("/statistics")
    public ResponseEntity<?> statistics(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return adminService.statistics(startDate, endDate);
    }
}
