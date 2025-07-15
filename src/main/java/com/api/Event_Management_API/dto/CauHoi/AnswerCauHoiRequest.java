package com.api.Event_Management_API.dto.CauHoi;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerCauHoiRequest {
    @NotBlank(message = "Answer must not be empty")
    private String answer;
}
