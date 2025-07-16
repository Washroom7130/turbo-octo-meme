package com.api.Event_Management_API.controller.danhgia;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.dto.DanhGia.ModDanhGiaRequest;
import com.api.Event_Management_API.service.DanhGiaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/danhgia")
public class DanhGiaController {
    
    @Autowired
    private DanhGiaService danhGiaService;

    @PreAuthorize("hasAnyAuthority('KhachHang')")
    @PostMapping("/{maSuKien}/add")
    public ResponseEntity<?> addDanhGia(@PathVariable Integer maSuKien,
                                        @RequestBody @Valid ModDanhGiaRequest request,
                                        BindingResult result,
                                        HttpServletRequest httpServletRequest
                                        ) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }
        
        return danhGiaService.addDanhGia(maSuKien, request, httpServletRequest);
    }

    @PreAuthorize("hasAnyAuthority('QuanLy', 'NhanVien')")
    @DeleteMapping("/delete/{maDanhGia}")
    public ResponseEntity<?> deleteDanhGia(@PathVariable Integer maDanhGia) {
        return danhGiaService.deleteDanhGia(maDanhGia);
    }

    @PreAuthorize("hasAnyAuthority('QuanLy', 'NhanVien')")
    @GetMapping("/get/all")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
        return danhGiaService.getAllDanhGia(page, size, search);
    }

    @GetMapping("/sukien/{maSuKien}/get/all")
    public ResponseEntity<?> getAllByMaSuKien(@PathVariable Integer maSuKien, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return danhGiaService.getAllBySuKien(maSuKien, page, size);
    }

    @PreAuthorize("hasAnyAuthority('QuanLy', 'NhanVien')")
    @GetMapping("/get/{maDanhGia}")
    public ResponseEntity<?> getOne(@PathVariable Integer maDanhGia) {
        return danhGiaService.getOneDanhGia(maDanhGia);
    }
}
