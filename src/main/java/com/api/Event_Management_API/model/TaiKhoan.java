package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name="TaiKhoan")
@Data
public class TaiKhoan {
    @Id
    private String maTaiKhoan;

    @NotBlank(message = "Please enter username")
    private String tenDangNhap;

    @NotBlank(message = "Please enter password")
    private String MatKhau;
    private String trangThai;
    private String vaiTro;

    private LocalDateTime ngayTao;

    private Boolean xacMinhEmail;

    // private String maQuanLy;
    // private String maNhanVien;
    // private String maKhachHang;
    
}
