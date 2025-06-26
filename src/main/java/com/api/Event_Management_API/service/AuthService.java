package com.api.Event_Management_API.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.RegisterRequest;
import com.api.Event_Management_API.model.KhachHang;
import com.api.Event_Management_API.model.TaiKhoan;
import com.api.Event_Management_API.model.Token;
import com.api.Event_Management_API.repository.KhachHangRepository;
import com.api.Event_Management_API.repository.TaiKhoanRepository;
import com.api.Event_Management_API.repository.TokenRepository;
import com.api.Event_Management_API.util.RandomGeneratorUtil;

@Service
public class AuthService {
    private final KhachHangRepository khachHangRepo;
    private final TaiKhoanRepository taiKhoanRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthService(KhachHangRepository khachHangRepo, TaiKhoanRepository taiKhoanRepo, TokenRepository tokenRepo, PasswordEncoder passwordEncoder) {
        this.khachHangRepo = khachHangRepo;
        this.taiKhoanRepo = taiKhoanRepo;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
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
    }
}
