package com.api.Event_Management_API.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "CauHoi")
@Data
public class CauHoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maCauHoi;

    @NotBlank(message = "Please enter your question")
    private String noiDungCauHoi;
    private String noiDungTraLoi;
    private String trangThai;
    private String maKhachHang;
    private String maSuKien;
    private String maNhanVien;
    @ManyToOne
    @JoinColumn(name = "maKhachHang", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "maSuKien", insertable = false, updatable = false)
    private SuKien suKien;

    @ManyToOne
    @JoinColumn(name = "maNhanVien", insertable = false, updatable = false)
    private NhanVien nhanVien;
}
