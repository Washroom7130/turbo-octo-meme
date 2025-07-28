package com.api.Event_Management_API.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;

import com.api.Event_Management_API.dto.Auth.LoginRequest;
import com.api.Event_Management_API.dto.Auth.RegisterRequest;
import com.api.Event_Management_API.dto.Auth.ResetPasswordRequest;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.model.Token;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.repository.TokenRepository;
import com.api.Event_Management_API.util.JwtUtil;
import com.api.Event_Management_API.util.RandomGeneratorUtil;
import com.api.Event_Management_API.util.RateLimiterService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {
    private final KhachHangRepository khachHangRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final RateLimiterService rateLimiterService;

    public AuthService(KhachHangRepository khachHangRepo, 
                        TaiKhoanRepository taiKhoanRepo, 
                        TokenRepository tokenRepo, 
                        PasswordEncoder passwordEncoder,
                        EmailService emailService,
                        JwtUtil jwtUtil,
                        RateLimiterService rateLimiterService) {
        this.khachHangRepo = khachHangRepo;
        this.taiKhoanRepo = taiKhoanRepo;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.rateLimiterService = rateLimiterService;
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
        tk.setTrangThai("Hoạt động");
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

    public ResponseEntity<?> login(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        String clientIp = httpServletRequest.getRemoteAddr();
        String rateLimitKey = "login:" + clientIp;

        if (!rateLimiterService.isAllowed(rateLimitKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Too many requests, please try again later"));
        }

        Optional<TaiKhoan> optionalTaikhoan = taiKhoanRepo.findByTenDangNhap(loginRequest.getUsername());

        if (optionalTaikhoan.isEmpty() ||
            !passwordEncoder.matches(loginRequest.getPassword(), optionalTaikhoan.get().getMatKhau())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Incorrect credentials"));
            }
        
        TaiKhoan taiKhoan = optionalTaikhoan.get();

        // Disallow login for account that hasn't verified email
        if (!Boolean.TRUE.equals(taiKhoan.getXacMinhEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Please verify your email first"));
        }

        // Disallow login for account that has been deactivated
        if (!"Hoạt động".equals(taiKhoan.getTrangThai())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Incorrect credentials"));
        }

        String token = jwtUtil.generateToken(
            taiKhoan.getMaTaiKhoan(),
            taiKhoan.getTenDangNhap(),
            taiKhoan.getVaiTro()
        );

        ResponseCookie cookie = ResponseCookie.from("token", token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(7 * 60 * 60 * 24)
                                .sameSite("Lax") // set Lax instead of Strict due to having to call to external API
                                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Login successful"));
    }

    @Async
    public void asyncForgotPasswordHandler(String identifier) {
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
    }
    
    public ResponseEntity<?> forgotPassword(String identifier, HttpServletRequest httpServletRequest) {
        String clientIp = httpServletRequest.getRemoteAddr();
        String rateLimitKey = "forgotPassword:" + clientIp;

        if (!rateLimiterService.isAllowed(rateLimitKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Too many requests, please try again later"));
        } 

        ResponseEntity<?> response = ResponseEntity.ok(Map.of("message", "We have sent a reset link to your email"));

        // Using async to prevent username/email enumeration
        asyncForgotPasswordHandler(identifier);

        return response;
    }

    public ResponseEntity<?> resetPassword(String tokenValue, ResetPasswordRequest request) {
        Optional<Token> optionalToken = tokenRepo.findById(tokenValue);

        if (optionalToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or expired token"));
        }

        Token token = optionalToken.get();

        // Check if token has correct type and not expired
        if (!"ResetPassword".equals(token.getLoaiToken()) ||
            token.getThoiDiemHetHan().isBefore(LocalDateTime.now())) 
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or expired token"));
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "New passwords don't match"));
        }

        // Fetch account and check if exist
        Optional<TaiKhoan> optionalTaiKhoan = taiKhoanRepo.findById(token.getMaTaiKhoan());

        if (optionalTaiKhoan.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Account not found"));
        }

        TaiKhoan taiKhoan = optionalTaiKhoan.get();
        taiKhoan.setMatKhau(passwordEncoder.encode(request.getNewPassword()));
        taiKhoanRepo.save(taiKhoan);

        // Delete used token
        tokenRepo.delete(token);

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    } 

    public ResponseEntity<?> checkResetToken(String token, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String rateLimitKey = "checkResetToken:" + clientIp;

        if (!rateLimiterService.isAllowed(rateLimitKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Too many requests, please try again later"));
        }
        
        Optional<Token> tokenOpt = tokenRepo.findById(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or expired token"));
        }

        Token tkn = tokenOpt.get();

        // Check if token has correct type and not expired
        if (!"ResetPassword".equals(tkn.getLoaiToken()) ||
        tkn.getThoiDiemHetHan().isBefore(LocalDateTime.now())) 
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or expired token"));
        }

        return ResponseEntity.ok(null);
    }
}
