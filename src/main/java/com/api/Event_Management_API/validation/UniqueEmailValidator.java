package com.api.Event_Management_API.validation;

import org.springframework.beans.factory.annotation.Autowired;

import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.NhanVienRepository;
import com.api.Event_Management_API.repository.QuanLyRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    
    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired 
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private QuanLyRepository quanLyRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) return true;

        // Check email in KhachHang, NhanVien and QuanLy tables
        boolean existsInKH = khachHangRepository.existsByEmail(email);
        boolean existsInNV = nhanVienRepository.existsByEmail(email);
        boolean existsInQL = quanLyRepository.existsByEmail(email);

        return !(existsInKH || existsInNV || existsInQL);
    }
}
