package com.api.Event_Management_API.dto.Ticket;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendTicketRequest {
    @NotBlank(message = "Please enter your name")
    private String tenKhachHang;

    @NotBlank(message = "Please enter your email")
    @Email(message = "Please enter a valid email")
    private String email;

    @NotBlank(message = "Please enter your content")
    private String noiDung;
}
