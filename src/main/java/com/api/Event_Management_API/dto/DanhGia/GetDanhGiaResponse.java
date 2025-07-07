package com.api.Event_Management_API.dto.DanhGia;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetDanhGiaResponse {
    private Integer maDanhGia;
    private Integer loaiDanhGia;
    private String binhLuan;
    private LocalDateTime ngayDanhGia;
    private String tenKhachHang;
    private String tenSuKien; 
}
