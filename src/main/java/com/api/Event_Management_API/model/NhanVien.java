package com.api.Event_Management_API.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name="NhanVien")
@Data
public class NhanVien {
    @Id
    private String maNhanVien;

    @NotBlank(message = "Please enter name")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String hoTen;

    @NotBlank(message = "Please enter address")
    @Size(max = 100, message = "Address cannot exceed 100 characters")
    private String diaChi;

    @NotBlank(message = "Please enter email")
    @Email(message = "Please enter a valid email")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Pattern(regexp = "^\\d{10}$", message = "Please enter a valid phone number")
    private String phone;

    @Pattern(regexp = "^(Nam|Nữ)$", message = "Please enter a valid gender")
    private String gioiTinh;

    @Min(value = 18, message = "minimum age is 18")
    @Max(value = 100, message = "maximum age is 100")
    private Integer soTuoi;
}
