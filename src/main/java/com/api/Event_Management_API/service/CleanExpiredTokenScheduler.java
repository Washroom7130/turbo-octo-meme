package com.api.Event_Management_API.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.repository.TokenRepository;

import jakarta.transaction.Transactional;

@Service
public class CleanExpiredTokenScheduler {
    
    private final TokenRepository tokenRepository;

    public CleanExpiredTokenScheduler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(fixedRate = 60_000) // runs every 1 minute
    @Transactional
    public void deleteExpiredToken() {
        tokenRepository.deleteByThoiDiemHetHanBefore(LocalDateTime.now());
    }
}
