package com.api.Event_Management_API.dto.DanhMucSuKien;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDanhMucRequest {
    @NotBlank(message = "Please enter category name")
    private String tenDanhMuc;
}
