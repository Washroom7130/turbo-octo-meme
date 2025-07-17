package com.api.Event_Management_API.dto.SuKien;

import java.time.LocalDateTime;
import java.util.List;

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
    private String trangThaiSuKien;
    private Float phiThamGia;
    private Integer luongChoNgoi;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer maDanhMuc;
    private Float rating;
    private List<String> occupiedSeat;
    private Integer soLuongCauHoi;
}
