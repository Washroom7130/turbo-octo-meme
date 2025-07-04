package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "DangKy")
@Data
public class DangKy {
    @Id
    private String maDangKy;

    private LocalDateTime ngayDangKy;

    @NotBlank(message = "Please enter your desired seat")
    @Size(max = 10, message = "Seat cannot exceed 10 characters")
    private String viTriGhe;

    @Pattern(regexp = "^(Đang xử lí|Đã hủy|Thành công|Đã điểm danh)$", message = "Invalid status")
    private String trangThaiDangKy;

    private String maKhachHang;
    private String maSuKien;
}
