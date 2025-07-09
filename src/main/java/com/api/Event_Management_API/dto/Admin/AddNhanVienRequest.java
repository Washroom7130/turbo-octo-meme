package com.api.Event_Management_API.dto.Admin;

import com.api.Event_Management_API.validation.UniqueEmail;
import com.api.Event_Management_API.validation.UniqueUsername;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddNhanVienRequest {
    @UniqueUsername
    @NotBlank(message = "Please enter username")
    @Size(max = 50, message = "Username cannot exceed 50 characters")
    private String tenDangNhap;

    @NotBlank(message = "Please enter password")
    private String matKhau;

    @NotBlank(message = "Please confirm your password")
    private String confirmMatKhau;

    @NotBlank(message = "Please enter name")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String hoTen;

    @NotBlank(message = "Please enter address")
    @Size(max = 100, message = "Address cannot exceed 100 characters")
    private String diaChi;

    @UniqueEmail
    @NotBlank(message = "Please enter email")
    @Email(message = "Please enter a valid email")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Pattern(regexp = "^\\d{10}$", message = "Please enter a valid phone number")
    private String phone;

    @Pattern(regexp = "^(Nam|Nữ|Khác)$", message = "Please enter a valid gender")
    private String gioiTinh;

    @Min(value = 18, message = "Minimum age is 18")
    @Max(value = 100, message = "Maximum age is 100")
    private Integer soTuoi;
}
