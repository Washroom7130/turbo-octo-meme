package com.api.Event_Management_API.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTaiKhoanListResponse {
    private String tenDangNhap;
    private String trangThai;
    private String vaiTro;
    private Integer maId;

    private String hoTen;
    private String diaChi;
    private String email;
    private String phone;
    private String gioiTinh;
    private Integer soTuoi;
}
