package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "DanhGia")
@Data
public class DanhGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDanhGia;

    @Min(value = 1, message = "Please enter your rating")
    @Max(value = 5, message = "Please enter your rating")
    private Integer loaiDanhGia;

    @NotBlank(message = "Please enter your comment")
    private String binhLuan;
    private LocalDateTime ngayDanhGia;

    private Integer maKhachHang;
    private Integer maSuKien;

    @ManyToOne
    @JoinColumn(name = "maKhachHang", insertable = false, updatable = false)
    private KhachHang khachHang;

}
