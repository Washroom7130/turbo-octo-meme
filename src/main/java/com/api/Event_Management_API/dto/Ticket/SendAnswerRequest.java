package com.api.Event_Management_API.dto.Ticket;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendAnswerRequest {
    @NotBlank(message = "Answer cannot be blank")
    private String answer;
}
