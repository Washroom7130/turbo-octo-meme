package com.api.Event_Management_API.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "CauHoi")
@Data
public class CauHoi {
    @Id
    private Integer maCauHoi;

    @NotBlank(message = "Please enter your question")
    private String notDungCauHoi;

    private String noiDungTraLoi;

    @Pattern(regexp = "^(Chưa xử lí|Đã xử lí)$", message = "Invalid status")
    private String trangThai;
    private String maKhachHang;
    private String maSuKien;
}
