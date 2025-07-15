package com.api.Event_Management_API.dto.CauHoi;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAllCauHoiResponse {
    private Integer maCauHoi;
    private String noiDungCauHoi;
    private String noiDungTraLoi;
    private String trangThai;
    private String tenKhachHang;
    private String tenSuKien;
    private String tenNhanVien;
}
