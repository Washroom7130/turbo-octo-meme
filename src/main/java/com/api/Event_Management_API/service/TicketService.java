package com.api.Event_Management_API.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.Ticket.SendAnswerRequest;
import com.api.Event_Management_API.dto.Ticket.SendTicketRequest;
import com.api.Event_Management_API.model.NhanVien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.model.Ticket;
import com.api.Event_Management_API.repository.NhanVienRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.repository.TicketRepository;
import com.api.Event_Management_API.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class TicketService {
    private final TicketRepository ticketRepo;
    private final NhanVienRepository nhanVienRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public TicketService(TicketRepository ticketRepo,
                        NhanVienRepository nhanVienRepo,
                        TaiKhoanRepository taiKhoanRepo,
                        EmailService emailService,
                        JwtUtil jwtUtil) {
        this.ticketRepo = ticketRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.taiKhoanRepo = taiKhoanRepo;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> createTicket(SendTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTenKhachHang(request.getTenKhachHang());
        ticket.setEmail(request.getEmail());
        ticket.setNoiDung(request.getNoiDung());
        ticket.setTrangThai("Chưa xử lý");

        ticketRepo.save(ticket);

        emailService.sendTicketConfirmationEmail(request.getEmail(), request.getTenKhachHang());

        return ResponseEntity.ok(Map.of("message", "Ticket created successfully"));
    }

    public ResponseEntity<?> answerTicket(Integer maHoTro, SendAnswerRequest request, HttpServletRequest httpServletRequest) {
        Claims claims = jwtUtil.extractClaimsFromRequest(httpServletRequest);
        String maTaiKhoan = claims.get("maTaiKhoan", String.class);
        String vaiTro = claims.get("vaiTro", String.class);
        
        Optional<Ticket> ticketOpt = ticketRepo.findById(maHoTro);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Ticket not found"));
        }

        String replyAuthor;
        Integer maNhanVien = null;
        Optional<TaiKhoan> tk = taiKhoanRepo.findById(maTaiKhoan);

        if (tk.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        if ("NhanVien".equals(vaiTro)) {
            replyAuthor = nhanVienRepo.findById(tk.get().getMaNhanVien()).get().getHoTen();
            maNhanVien = tk.get().getMaNhanVien();
        } else if ("QuanLy".equals(vaiTro)) {
            replyAuthor = "Quản lí";
        }

        Ticket ticket = ticketOpt.get();

        // Update ticket
        ticket.setNoiDungGiaiDap(request.getAnswer());
        ticket.setTrangThai("Đã xử lí");
        ticket.setMaNhanVien(maNhanVien);

        // TODO: continue this part
    }
}
