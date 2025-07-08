package com.api.Event_Management_API.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="NhanVien")
@Data
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maNhanVien;
    private String hoTen;
    private String diaChi;
    private String email;
    private String phone;
    private String gioiTinh;
    private Integer soTuoi;
}
