package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "Token")
@Data
public class Token {
    @Id
    private String maToken;

    @NotBlank(message = "Missing token type")
    private String loaiToken;

    private LocalDateTime thoiDiemHetHan;
    private String maTaiKhoan;
}
