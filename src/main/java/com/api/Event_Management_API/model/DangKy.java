package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "DangKy")
@Data
public class DangKy {
    @Id
    private String maDangKy;

    private LocalDateTime ngayDangKy;

    @NotBlank(message = "Please enter your desired seat")
    @Size(max = 10, message = "Seat cannot exceed 10 characters")
    private String viTriGhe;
    private String trangThaiDangKy;

    private Integer maKhachHang;
    private Integer maSuKien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKhachHang", referencedColumnName = "maKhachHang", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSuKien", referencedColumnName = "maSuKien", insertable = false, updatable = false)
    private SuKien suKien;
}
