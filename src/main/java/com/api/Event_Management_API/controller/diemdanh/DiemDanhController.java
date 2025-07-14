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

@RestController
@RequestMapping("/api/diemdanh")
public class DiemDanhController {
    
    @Autowired
    private DiemDanhService diemDanhService;

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @GetMapping("/get_visitors/{maSuKien}")
    public ResponseEntity<?> getVisitor(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @PathVariable Integer maSuKien) {
        return diemDanhService.getVisitor(page, size, maSuKien);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @PostMapping("/{maDangKy}")
    public ResponseEntity<?> diemDanh(@PathVariable String maDangKy) {
        return diemDanhService.diemDanh(maDangKy);
    }
}
