package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @NotBlank(message = "Please enter a valid date")
    private LocalDateTime ngayDiemDanh;

    @Pattern(regexp = "^(Vắng mặt|Có mặt)$", message = "Invalid status")
    private String trangThaiDiemDanh;

    @NotBlank(message = "Please enter your seat")
    private String viTriGheNgoi;
    private String maDangKy;
}
