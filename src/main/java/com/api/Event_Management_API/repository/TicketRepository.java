package com.api.Event_Management_API.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.Event_Management_API.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    
}
