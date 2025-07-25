package com.api.Event_Management_API.service;

import java.util.Map;
import java.util.Optional;

import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.Ticket.GetTicketResponse;
import com.api.Event_Management_API.dto.Ticket.SendAnswerRequest;
import com.api.Event_Management_API.dto.Ticket.SendTicketRequest;
import com.api.Event_Management_API.model.NhanVien;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.model.Ticket;
import com.api.Event_Management_API.repository.NhanVienRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.repository.TicketRepository;
import com.api.Event_Management_API.util.JwtUtil;
import com.api.Event_Management_API.util.RateLimiterService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class TicketService {
    private final TicketRepository ticketRepo;
    private final NhanVienRepository nhanVienRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final EmailService emailService;
    private final RateLimiterService rateLimiterService;
    private final JwtUtil jwtUtil;

    public TicketService(TicketRepository ticketRepo,
                        NhanVienRepository nhanVienRepo,
                        TaiKhoanRepository taiKhoanRepo,
                        EmailService emailService,
                        RateLimiterService rateLimiterService,
                        JwtUtil jwtUtil) {
        this.ticketRepo = ticketRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.taiKhoanRepo = taiKhoanRepo;
        this.emailService = emailService;
        this.rateLimiterService = rateLimiterService;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> createTicket(SendTicketRequest request, HttpServletRequest httpServletRequest) {
        String clientIp = httpServletRequest.getRemoteAddr();
        String rateLimitKey = "createTicket:" + clientIp;

        if (!rateLimiterService.isAllowed(rateLimitKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Too many requests, please try again later"));
        }

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

        String replyAuthor = "Nhân viên";
        Integer maNhanVien = null;
        Optional<TaiKhoan> tk = taiKhoanRepo.findById(maTaiKhoan);

        if (tk.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }

        if (ticketOpt.get().getTrangThai().equals("Đã xử lý")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Ticket has already been handled"));
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
        ticket.setTrangThai("Đã xử lý");
        ticket.setMaNhanVien(maNhanVien);

        ticketRepo.save(ticket);

        // Send email
        emailService.sendResponseTicketEmail(ticket.getEmail(), ticket.getTenKhachHang(), request.getAnswer(), replyAuthor);

        return ResponseEntity.ok(Map.of("message", "Ticket response has been sent"));
    }

    public ResponseEntity<?> getAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Ticket> pageResult;

        boolean hasSearch = search != null && !search.isBlank();

        if (hasSearch) {
            pageResult = ticketRepo.findByTenKhachHangContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search, search, pageable
            );
        } else {
            pageResult = ticketRepo.findAll(pageable);
        }
        
        Page<GetTicketResponse> pageResponse = pageResult.map(ticket -> {
            String tenNhanVien = "Unknown";

            if (ticket.getMaNhanVien() != null) {
                tenNhanVien = nhanVienRepo.findById(ticket.getMaNhanVien())
                    .map(NhanVien::getHoTen)
                    .orElse("Unknown");
            } else if ("Đã xử lý".equals(ticket.getTrangThai())) {
                tenNhanVien = "Quản lí";
            }

            return new GetTicketResponse(
                ticket.getMaHoTro(),
                ticket.getTenKhachHang(),
                ticket.getEmail(),
                ticket.getNoiDung(),
                ticket.getNoiDungGiaiDap(),
                ticket.getTrangThai(),
                tenNhanVien
            );
        });

        return ResponseEntity.ok(pageResponse);
    }

    public ResponseEntity<?> getOne(Integer maHoTro) {
        Optional<Ticket> ticketOptional = ticketRepo.findById(maHoTro);

        if (ticketOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Ticket not found"));
        }

        Ticket ticket = ticketOptional.get();
        String tenNhanVien = "Unknown";

        if (ticket.getMaNhanVien() != null) {
            tenNhanVien = nhanVienRepo.findById(ticket.getMaNhanVien())
                .map(NhanVien::getHoTen)
                .orElse("Unknown");
        } else if ("Đã xử lý".equals(ticket.getTrangThai())) {
            tenNhanVien = "Quản lí";
        }

        GetTicketResponse response = new GetTicketResponse(
            ticket.getMaHoTro(),
            ticket.getTenKhachHang(),
            ticket.getEmail(),
            ticket.getNoiDung(),
            ticket.getNoiDungGiaiDap(),
            ticket.getTrangThai(),
            tenNhanVien
        );

        return ResponseEntity.ok(response);
    }
}
