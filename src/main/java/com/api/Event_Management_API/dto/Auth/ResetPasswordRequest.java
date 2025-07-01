package com.api.Event_Management_API.dto.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Please enter your new password")
    private String newPassword;

    @NotBlank(message = "Please enter your new password")
    private String confirmNewPassword;
}
