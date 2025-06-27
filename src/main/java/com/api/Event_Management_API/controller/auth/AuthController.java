package com.api.Event_Management_API.controller.auth;

import com.api.Event_Management_API.dto.RegisterRequest;
import com.api.Event_Management_API.service.AuthService;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request, BindingResult result) {
        // Validate user input
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        // Check if password equals to confirm password
        if(!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match"));
        }

        authService.register(request);

        return ResponseEntity.ok(Map.of(
            "message", "User registered successfully"
        ));
    }

    @GetMapping("/verify/{tokenID}")
    public ResponseEntity<?> verifyAccount(@PathVariable("tokenID") String tokenID) {
        return authService.verifyToken(tokenID);
    }
}
