package com.api.Event_Management_API.dto.DiemDanh;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDiemDanhResponse {
    private String maDiemDanh;
    private LocalDateTime ngayTaoVe;
    private LocalDateTime ngayDiemDanh;
    private String trangThaiDiemDanh;
    private String viTriGheNgoi;
    private String tenKhachHang;
}
