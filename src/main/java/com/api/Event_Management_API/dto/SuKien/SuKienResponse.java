package com.api.Event_Management_API.dto.SuKien;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuKienResponse {
    private Integer maSuKien;
    private String tenSuKien;
    private String moTa;
    private String anhSuKien;
    private String diaDiem;
    private Float phiThamGia;
    private Integer luongChoNgoi;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer maDanhMuc;
}
