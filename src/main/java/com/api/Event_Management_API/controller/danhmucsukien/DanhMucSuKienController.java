package com.api.Event_Management_API.controller.danhmucsukien;

import java.util.List;
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

import com.api.Event_Management_API.dto.DanhMucSuKien.CreateDanhMucRequest;
import com.api.Event_Management_API.dto.DanhMucSuKien.GetDanhMucResponse;
import com.api.Event_Management_API.service.DanhMucSuKienService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/danhmucsukien")
public class DanhMucSuKienController {

    @Autowired
    private DanhMucSuKienService danhMucSuKienService;
    
    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @PostMapping("/add")
    public ResponseEntity<?> addDanhMuc(@Valid @RequestBody CreateDanhMucRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        return danhMucSuKienService.createDanhMuc(request);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @PutMapping("/update/{maDanhMuc}")
    public ResponseEntity<?> updateDanhMuc(@Valid @RequestBody CreateDanhMucRequest request, BindingResult result, @PathVariable Integer maDanhMuc) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        return danhMucSuKienService.updateDanhMuc(request, maDanhMuc);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<GetDanhMucResponse>> getAll() {
        return danhMucSuKienService.getAll();
    }

    @GetMapping("/get/{maDanhMuc}")
    public ResponseEntity<?> getOne(@PathVariable Integer maDanhMuc) {
        return danhMucSuKienService.getOne(maDanhMuc);
    }
}
