package com.api.Event_Management_API.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.KhachHang;

public interface KhachHangRepository extends JpaRepository <KhachHang, Integer> {
    boolean existsByEmail(String email);
    Optional<KhachHang> findByEmail(String email);
}
