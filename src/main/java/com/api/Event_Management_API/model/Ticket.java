package com.api.Event_Management_API.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "Ticket")
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHoTro;

    @NotBlank(message = "Please enter your name")
    private String tenKhachHang;

    @NotBlank(message = "Please enter your email")
    @Email(message = "Please enter a valid email")
    private String email;

    @NotBlank(message = "Please enter your content")
    private String noiDung;

    //@NotBlank(message = "Please enter your answer")
    private String noiDungGiaiDap;

    @Pattern(regexp = "^(Chưa xử lý|Đã xử lý)$", message = "Please enter a valid status")
    private String trangThai;
    private Integer maNhanVien;
}
