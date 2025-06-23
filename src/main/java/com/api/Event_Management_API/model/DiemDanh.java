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
@Table(name = "DiemDanh")
@Data
public class DiemDanh {
    @Id
    private String maDienDanh;

    private LocalDateTime ngayTaoVe;
    private LocalDateTime ngayDiemDanh;
    private String trangThaiDiemDanh;
    private String viTriGheNgoi;
    private String maDangKy;
}
