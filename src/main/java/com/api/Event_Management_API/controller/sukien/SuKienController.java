package com.api.Event_Management_API.controller.sukien;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.dto.SuKien.CUSuKienRequest;
import com.api.Event_Management_API.dto.SuKien.DangKySuKienRequest;
import com.api.Event_Management_API.dto.SuKien.UpdateSuKienRequest;
import com.api.Event_Management_API.service.SuKienService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sukien")
public class SuKienController {
    
    @Autowired
    private SuKienService suKienService;

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @PostMapping("/add")
    public ResponseEntity<?> addSuKien(@Valid @ModelAttribute CUSuKienRequest request
                                        , BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        return suKienService.addSuKien(request);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @PostMapping("/update/{maSuKien}")
    public ResponseEntity<?> updateSuKien(@ModelAttribute UpdateSuKienRequest request, @PathVariable Integer maSuKien) {
        return suKienService.updateSuKien(request, maSuKien);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @PutMapping("/cancel/{maSuKien}")
    public ResponseEntity<?> cancelSuKien(@PathVariable Integer maSuKien) {
        return suKienService.cancel(maSuKien);
    }

    @PreAuthorize("hasAnyAuthority('KhachHang')")
    @PostMapping("/dangky/{maSuKien}")
    public ResponseEntity<?> dangKy(@PathVariable Integer maSuKien, @RequestBody @Valid DangKySuKienRequest request, HttpServletRequest httpServletRequest) {
        return suKienService.dangky(request, maSuKien, httpServletRequest);
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page, 
                                    @RequestParam(defaultValue = "10") int size, 
                                    @RequestParam(required = false) Integer maDanhMuc, 
                                    @RequestParam(required = false) String search, 
                                    @RequestParam(required = false) String trangThai,
                                    @RequestParam(required = false) Float costStart,
                                    @RequestParam(required = false) Float costEnd) {
        return suKienService.getAll(page, size, maDanhMuc, search, trangThai, costStart, costEnd);
    }

    @GetMapping("/get/{maSuKien}")
    public ResponseEntity<?> getOne(@PathVariable Integer maSuKien) {
        return suKienService.getOne(maSuKien);
    }

    @PreAuthorize("hasAnyAuthority('KhachHang')")
    @GetMapping("dangky/{token}/success")
    public ResponseEntity<?> paymentSuccess(@PathVariable String token, HttpServletRequest request) {
        return suKienService.paymentSuccess(token, request);
    }

    @PreAuthorize("hasAnyAuthority('KhachHang')")
    @GetMapping("dangky/{token}/cancel")
    public ResponseEntity<?> paymentCancel(@PathVariable String token, HttpServletRequest request) {
        return suKienService.paymentCancel(token, request);
    }

    @GetMapping("/get/img/{maAnh}")
    public ResponseEntity<?> getAnh(@PathVariable String maAnh) {
        return suKienService.getAnhSuKien(maAnh);
    }
}
