package com.api.Event_Management_API.controller.ticket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.dto.Ticket.SendAnswerRequest;
import com.api.Event_Management_API.dto.Ticket.SendTicketRequest;
import com.api.Event_Management_API.service.TicketService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;

    @PostMapping("/create")
    public ResponseEntity<?> createTicket(@Valid @RequestBody SendTicketRequest request, BindingResult result, HttpServletRequest httpServletRequest) {
        if (result.hasErrors()) {
            String error = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }

        return ticketService.createTicket(request, httpServletRequest);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @PostMapping("/{maHoTro}/answer")
    public ResponseEntity<?>  answerTicket(@PathVariable Integer maHoTro, @Valid @RequestBody SendAnswerRequest request, BindingResult result, HttpServletRequest httpServletRequest) {
        if (result.hasErrors()) {
            String error = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }

        return ticketService.answerTicket(maHoTro, request, httpServletRequest);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @GetMapping("/get/all")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
        return ticketService.getAll(page, size, search);
    }

    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    @GetMapping("/get/{maHoTro}")
    public ResponseEntity<?> getOne(@PathVariable Integer maHoTro) {
        return ticketService.getOne(maHoTro);
    }
}
