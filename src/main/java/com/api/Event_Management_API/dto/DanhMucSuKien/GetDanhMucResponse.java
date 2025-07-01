package com.api.Event_Management_API.dto.DanhMucSuKien;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDanhMucResponse {
    private Integer maDanhMuc;
    private String tenDanhMuc;
}
