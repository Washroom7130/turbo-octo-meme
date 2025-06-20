package com.api.Event_Management_API.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name="NhanVien")
@Data
public class NhanVien {
    @Id
    private String maNhanVien;

    @NotBlank(message = "Please enter name")
    private String hoTen;

    @NotBlank(message = "Please enter address")
    private String diaChi;

    @NotBlank(message = "Please enter email")
    @Email(message = "Please enter a valid email")
    private String email;

    @Pattern(regexp = "^\\d{10}$", message = "Please enter a valid phone number")
    private String phone;

    @Pattern(regexp = "^(Nam|Nữ|Khác)$", message = "Please enter a valid gender")
    private String gioiTinh;

    @Min(value = 18, message = "minimum age is 18")
    @Max(value = 100, message = "maximum age is 100")
    private Integer soTuoi;
}
