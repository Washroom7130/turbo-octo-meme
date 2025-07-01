package com.api.Event_Management_API.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.DanhMucSuKien;

public interface DanhMucSuKienRepository  extends JpaRepository<DanhMucSuKien, Integer> {
    Optional<DanhMucSuKien> findByTenDanhMuc(String tenDanhMuc);
}
