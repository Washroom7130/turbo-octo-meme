package com.api.Event_Management_API.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "SuKien")
@Data
public class SuKien {
    @Id
    private String maSuKien;

    @NotBlank(message = "Please enter event name")
    private String tenSuKien;

    @NotBlank(message = "Please enter event description")
    private String moTa;

    @Pattern(regexp = "^/img/[^/]{1,29}.(png|jpg|jpeg)$", message = "Invalid image name")
    private String anhSuKien;

    @NotBlank(message = "Please enter a location")
    private String diaDiem;

    @Min(value = 0, message = "Please enter a non-negative number")
    private Float phiThamGia;

    @Min(value = 0, message = "Please enter a non-negative number")
    private Integer luongChoNgoi;
    
    private LocalDateTime ngayTaoSuKien;

    @NotBlank(message = "Please enter start day")
    private LocalDateTime ngayBatDau;

    @NotBlank(message = "Please enter end day")
    private LocalDateTime ngayKetThuc;

    @NotBlank(message = "Please enter category id")
    private String maDanhMuc;
}
