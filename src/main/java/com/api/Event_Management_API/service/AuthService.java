package com.api.Event_Management_API.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.LoginRequest;
import com.api.Event_Management_API.dto.RegisterRequest;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.model.Token;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.repository.TokenRepository;
import com.api.Event_Management_API.util.JwtUtil;
import com.api.Event_Management_API.util.RandomGeneratorUtil;

@Service
public class AuthService {
    private final KhachHangRepository khachHangRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public AuthService(KhachHangRepository khachHangRepo, 
                        TaiKhoanRepository taiKhoanRepo, 
                        TokenRepository tokenRepo, 
                        PasswordEncoder passwordEncoder,
                        EmailService emailService,
                        JwtUtil jwtUtil) {
        this.khachHangRepo = khachHangRepo;
        this.taiKhoanRepo = taiKhoanRepo;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest request) {
        // Create and save to KhachHang
        KhachHang kh = new KhachHang();
        kh.setHoTen(request.getName());
        kh.setDiaChi(request.getAddress());
        kh.setEmail(request.getEmail());
        kh.setPhone(request.getPhone());
        kh.setGioiTinh(request.getGender());
        kh.setSoTuoi(request.getAge());
        kh = khachHangRepo.save(kh); // save entity

        // Create and save to TaiKhoan
        TaiKhoan tk = new TaiKhoan();
        tk.setMaTaiKhoan(UUID.randomUUID().toString());
        tk.setTenDangNhap(request.getUsername());
        tk.setMatKhau(passwordEncoder.encode(request.getPassword())); // Hash password
        tk.setTrangThai("Hoạt Động");
        tk.setVaiTro("KhachHang");
        tk.setNgayTao(LocalDateTime.now());
        tk.setMaKhachHang(kh.getMaKhachHang());

        taiKhoanRepo.save(tk);

        // Create and save to Token
        Token token = new Token();
        token.setMaToken(RandomGeneratorUtil.generateToken(50));
        token.setLoaiToken("AccountRegister");
        token.setThoiDiemHetHan(LocalDateTime.now().plusDays(3));
        token.setMaTaiKhoan(tk.getMaTaiKhoan());
        tokenRepo.save(token);

        emailService.sendVerificationEmail(request.getEmail(), token.getMaToken());
    }

    public ResponseEntity<?> verifyToken(String tokenId) {
        Optional<Token> optionalToken = tokenRepo.findById(tokenId);

        if (optionalToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token invalid or outdated"));
        }

        Token token = optionalToken.get();

        // Check if token type is AccountRegister
        if (!"AccountRegister".equals(token.getLoaiToken())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token invalid or outdated"));
        }

        // Check if token has expired
        if (token.getThoiDiemHetHan() == null || token.getThoiDiemHetHan().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token invalid or outdated"));
        }

        Optional<TaiKhoan> optionalTaikhoan = taiKhoanRepo.findById(token.getMaTaiKhoan());

        if (optionalTaikhoan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Account not found"));
        }

        TaiKhoan taiKhoan = optionalTaikhoan.get();
        taiKhoan.setXacMinhEmail(true);
        taiKhoanRepo.save(taiKhoan);
        tokenRepo.delete(token);

        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        Optional<TaiKhoan> optionalTaikhoan = taiKhoanRepo.findByTenDangNhap(loginRequest.getUsername());

        if (optionalTaikhoan.isEmpty() ||
            !passwordEncoder.matches(loginRequest.getPassword(), optionalTaikhoan.get().getMatKhau())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Incorrect credentials"));
            }
        
        TaiKhoan taiKhoan = optionalTaikhoan.get();

        if (!Boolean.TRUE.equals(taiKhoan.getXacMinhEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Please verify your email first"));
        }

        String token = jwtUtil.generateToken(
            taiKhoan.getMaTaiKhoan(),
            taiKhoan.getTenDangNhap(),
            taiKhoan.getVaiTro()
        );

        return ResponseEntity.ok(Map.of("token", token));
    }

    public ResponseEntity<?> forgotPassword(String identifier) {
        // Check for username first
        Optional<TaiKhoan> optionalTaiKhoan = taiKhoanRepo.findByTenDangNhap(identifier);

        if (optionalTaiKhoan.isEmpty()) {
            // If username not found try email
            Optional<KhachHang> kh = khachHangRepo.findByEmail(identifier);
            if (kh.isPresent()) {
                optionalTaiKhoan = taiKhoanRepo.findByMaKhachHang(kh.get().getMaKhachHang());
            }
        }

        if (optionalTaiKhoan.isPresent()) {
            TaiKhoan taiKhoan = optionalTaiKhoan.get();

            Token token = new Token();
            token.setMaToken(RandomGeneratorUtil.generateToken(50));
            token.setLoaiToken("ResetPassword");
            token.setThoiDiemHetHan(LocalDateTime.now().plusMinutes(30));
            token.setMaTaiKhoan(taiKhoan.getMaTaiKhoan());

            tokenRepo.save(token);

            // Send email
            String email = khachHangRepo.findById(taiKhoan.getMaKhachHang())
                            .map(KhachHang::getEmail)
                            .orElse(null);
            if (email != null) {
                emailService.sendPasswordResetEmail(email, token.getMaToken());
            }

        }

        return ResponseEntity.ok(Map.of("message", "We have sent a reset link to your email"));
    }
}
