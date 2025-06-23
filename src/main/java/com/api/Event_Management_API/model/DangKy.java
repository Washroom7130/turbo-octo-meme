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
@Table(name = "DangKy")
@Data
public class DangKy {
    @Id
    private String maDangKy;

    private LocalDateTime ngayDangKy;

    @NotBlank(message = "Please enter your desired seat")
    private String viTriGhe;

    private String trangThaiDangKy;

    private String maKhachHang;
    private String maSuKien;
}
