package com.api.Event_Management_API.dto.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "Please enter either username or email")
    private String identifier;
}
