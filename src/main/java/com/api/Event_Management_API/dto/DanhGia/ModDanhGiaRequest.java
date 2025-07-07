package com.api.Event_Management_API.dto.DanhGia;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ModDanhGiaRequest {
    @Min(value = 1, message = "Please enter your rating (1-5)")
    @Max(value = 5, message = "Please enter your rating (1-5)")
    private Integer loaiDanhGia;

    @NotBlank(message = "Please enter your comment")
    private String binhLuan;
}
