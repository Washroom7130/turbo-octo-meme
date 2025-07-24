package com.api.Event_Management_API.dto.HoaDon;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetHoaDonResponse {
    private String maHoaDon;
    private String trangThaiHoaDon;
    private Float tongTien;
    private LocalDateTime thoiGianThanhCong;
    private String phuongThucThanhToan;
    private String tenKhachHang;
    private String tenSuKien;
    private Integer maSuKien;
    private String maDiemDanh;
}
