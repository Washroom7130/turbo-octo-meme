package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "HoaDon")
@Data
public class HoaDon {
    @Id
    private String maHoaDon;

    private LocalDateTime ngayTao;

    @Pattern(regexp = "^(Chưa thanh toán|Đã thanh toán|Đã hủy)$", message = "Invalid status")
    private String trangThaiHoaDon;

    @Min(value = 0, message = "Total sum cannot be negative")
    private Float tongTien;
    private LocalDateTime thoiGianHieuLuc;
    private LocalDateTime thoiGianThanhCong;

    @Pattern(regexp = "^(Qua ngân hàng|Ví điện tử)$", message = "Invalid payment method")
    private String phuongThucThanhToan;
    private Integer maKhachHang;
    private String maDangKy;
    //private String maNhanVien;

    @ManyToOne
    @JoinColumn(name = "maKhachHang", insertable = false, updatable = false)
    private KhachHang khachHang;

}
