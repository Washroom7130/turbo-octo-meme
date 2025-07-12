package com.api.Event_Management_API.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.Token;

public interface TokenRepository extends JpaRepository<Token, String> {
    long deleteByThoiDiemHetHanBefore(LocalDateTime now);
}
