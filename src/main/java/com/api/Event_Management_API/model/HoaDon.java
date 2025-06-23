package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "HoaDon")
@Data
public class HoaDon {
    @Id
    private String maHoaDon;

    private LocalDateTime ngayTao;
    private String trangThaiHoaDon;
    private Float tongTien;
    private LocalDateTime thoiGianHieuLuc;
    private LocalDateTime thoiGianThanhCong;
    private String phuongThucThanhToan;
    private String maKhachHang;
    private String maDangKy;
}
