package com.api.Event_Management_API.controller.sukien;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.dto.SuKien.CUSuKienRequest;
import com.api.Event_Management_API.service.SuKienService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sukien")
public class SuKienController {
    
    @Autowired
    private SuKienService suKienService;

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @PostMapping("/add")
    public ResponseEntity<?> addSuKien(@Valid @ModelAttribute CUSuKienRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        return suKienService.addSuKien(request);
    }
}
