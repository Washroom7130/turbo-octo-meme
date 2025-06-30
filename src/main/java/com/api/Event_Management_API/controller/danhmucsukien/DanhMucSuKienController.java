package com.api.Event_Management_API.controller.danhmucsukien;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/danhmucsukien")
public class DanhMucSuKienController {
    
    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("message", "Authorized"));
    }
}
