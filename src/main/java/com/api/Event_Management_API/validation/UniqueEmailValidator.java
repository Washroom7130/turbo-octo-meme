package com.api.Event_Management_API.validation;

import org.springframework.beans.factory.annotation.Autowired;

import com.api.Event_Management_API.repository.KhachHangRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    
    @Autowired
    private KhachHangRepository khachHangRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && !khachHangRepository.existsByEmail(email);
    }
}
