package com.api.Event_Management_API.controller.ticket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.dto.Ticket.SendTicketRequest;
import com.api.Event_Management_API.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;

    @PostMapping("/create")
    public ResponseEntity<?> createTicket(@Valid @RequestBody SendTicketRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String error = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }

        return ticketService.createTicket(request);
    }
}
