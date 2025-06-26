package com.api.Event_Management_API.validation;

import org.springframework.beans.factory.annotation.Autowired;

import com.api.Event_Management_API.repository.TaiKhoanRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null && !taiKhoanRepository.existsByTenDangNhap(username);
    }
}
