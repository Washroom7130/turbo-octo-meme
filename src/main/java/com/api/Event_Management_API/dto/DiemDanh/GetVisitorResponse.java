package com.api.Event_Management_API.dto.DiemDanh;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetVisitorResponse {
    private String maDangKy;
    private String hoTen;
    private String viTriGhe;
}
