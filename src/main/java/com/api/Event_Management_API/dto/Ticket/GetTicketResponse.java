package com.api.Event_Management_API.dto.Ticket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetTicketResponse {
    private Integer maHoTro;
    private String tenKhachHang;
    private String email;
    private String noiDung;
    private String noiDungGiaiDap;
    private String trangThai;
    private String tenNhanVien;
}
