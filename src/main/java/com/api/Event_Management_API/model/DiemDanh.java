package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "DiemDanh")
@Data
public class DiemDanh {
    @Id
    private String maDiemDanh;

    private LocalDateTime ngayTaoVe;
    private LocalDateTime ngayDiemDanh;
    private String trangThaiDiemDanh;

    @NotBlank(message = "Please enter your seat")
    private String viTriGheNgoi;
    private String maDangKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDangKy", insertable = false, updatable = false)
    private DangKy dangKy;
}
