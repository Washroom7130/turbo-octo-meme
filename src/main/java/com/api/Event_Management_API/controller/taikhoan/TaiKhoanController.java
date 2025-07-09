package com.api.Event_Management_API.controller.taikhoan;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.dto.TaiKhoan.ChangePasswordRequest;
import com.api.Event_Management_API.dto.TaiKhoan.UpdateTaiKhoanRequest;
import com.api.Event_Management_API.service.TaiKhoanService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/taikhoan")
public class TaiKhoanController {
    
    @Autowired
    private TaiKhoanService taiKhoanService;

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy', 'KhachHang')")
    @PutMapping("/update/me")
    public ResponseEntity<?> updateMe(@Valid @RequestBody UpdateTaiKhoanRequest request, BindingResult result, HttpServletRequest httpServletRequest) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        return taiKhoanService.updateMe(request, httpServletRequest);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy', 'KhachHang')")
    @PutMapping("/changepassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, BindingResult result, HttpServletRequest httpServletRequest) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        return taiKhoanService.changePassword(request, httpServletRequest);
    }

    @PreAuthorize("hasAnyAuthority('KhachHang')")
    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivate(HttpServletRequest request) {
        return taiKhoanService.deactive(request);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @PostMapping("/{action}/{maKhachHang}")
    public ResponseEntity<?> updateCustomerStatus(@PathVariable String action, @PathVariable Integer maKhachHang) {
        return taiKhoanService.updateCustomerStatus(action, maKhachHang);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy', 'KhachHang')")
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        return taiKhoanService.getPersonalInfo(request);
    }

}
