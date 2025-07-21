package com.api.Event_Management_API.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Page<Ticket> findByTenKhachHangContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String tenKhachHang,
        String email,
        Pageable pageable
    );
    long countByTrangThai(String trangThai);
}
