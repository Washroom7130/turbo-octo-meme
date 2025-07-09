package com.api.Event_Management_API.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.QuanLy;

public interface QuanLyRepository extends JpaRepository<QuanLy, Integer> {
    boolean existsByEmail(String email);
}
