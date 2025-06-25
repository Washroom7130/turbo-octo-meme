package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name="TaiKhoan")
@Data
public class TaiKhoan {
    @Id
    private String maTaiKhoan;

    @NotBlank(message = "Please enter username")
    @Size(max = 50, message = "Username cannot exceed 100 characters")
    private String tenDangNhap;

    @NotBlank(message = "Please enter password")
    private String MatKhau;

    @Pattern(regexp = "^(Hoạt Động|Dừng hoạt động)$", message = "Invalid status")
    private String trangThai;

    @Pattern(regexp = "^(QuanLy|NhanVien|KhachHang)$", message = "Invalid role")
    private String vaiTro;

    private LocalDateTime ngayTao;

    private Boolean xacMinhEmail;

    private String maQuanLy;
    private String maNhanVien;
    private String maKhachHang;
    
}
