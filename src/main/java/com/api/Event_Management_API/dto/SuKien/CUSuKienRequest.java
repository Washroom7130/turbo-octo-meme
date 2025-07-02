package com.api.Event_Management_API.dto.SuKien;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class CUSuKienRequest {
    @NotBlank(message = "Please enter event name")
    @Size(max = 100, message = "Event's name cannot exceed 100 characters")
    private String tenSuKien;

    @NotBlank(message = "Please enter event description")
    private String moTa;

    //@Pattern(regexp = "^[^/.]{1,29}.(png|jpg|jpeg)$", message = "Invalid image name")
    private MultipartFile anhSuKien;

    @NotBlank(message = "Please enter a location")
    private String diaDiem;

    @Min(value = 0, message = "Please enter a non-negative number")
    private Float phiThamGia;

    @Min(value = 0, message = "Please enter a non-negative number")
    private Integer luongChoNgoi;

    @NotNull(message = "Please enter start day")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Please enter end day")
    private LocalDateTime ngayKetThuc;

    //@NotBlank(message = "Please enter category id")
    private Integer maDanhMuc;
}
