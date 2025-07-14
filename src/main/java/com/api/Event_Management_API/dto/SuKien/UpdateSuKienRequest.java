package com.api.Event_Management_API.dto.SuKien;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UpdateSuKienRequest {
    private String tenSuKien;
    private String moTa;
    private MultipartFile anhSuKien;
    private String diaDiem;
    private Float phiThamGia;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer maDanhMuc;
}
