package com.api.Event_Management_API.dto.SuKien;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DangKySuKienRequest {

    @NotBlank(message = "Please enter your desired seat")
    @Size(max = 10, message = "Seat cannot exceed 10 characters")
    private String viTriGhe;

    @Pattern(regexp = "^(Qua ngân hàng|Ví điện tử)$", message = "Invalid payment method")
    private String phuongThucThanhToan;
}
