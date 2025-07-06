package com.api.Event_Management_API.dto.DangKy;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllDangKyStaffResponse {
    private String maDangKy;
    private LocalDateTime ngayDangKy;
    private String viTriGhe;
    private String trangThaiDangKy;
    private String tenKhachHang;
    private String tenSuKien;
}
