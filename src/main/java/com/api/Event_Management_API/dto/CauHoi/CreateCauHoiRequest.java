package com.api.Event_Management_API.dto.CauHoi;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCauHoiRequest {
    @NotBlank(message = "Question content must not be empty")
    private String cauHoi;
}
