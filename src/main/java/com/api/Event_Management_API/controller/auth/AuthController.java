package com.api.Event_Management_API.controller.auth;

import com.api.Event_Management_API.dto.Auth.ForgotPasswordRequest;
import com.api.Event_Management_API.dto.Auth.LoginRequest;
import com.api.Event_Management_API.dto.Auth.RegisterRequest;
import com.api.Event_Management_API.dto.Auth.ResetPasswordRequest;
import com.api.Event_Management_API.service.AuthService;
import com.api.Event_Management_API.util.RateLimiterService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    private final RateLimiterService rateLimiterService;


    public AuthController(AuthService authService, RateLimiterService rateLimiterService) {
        this.authService = authService;
        this.rateLimiterService = rateLimiterService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request, BindingResult result, HttpServletRequest httpServletRequest) {
        String clientIp = httpServletRequest.getRemoteAddr();
        String rateLimitKey = "register:" + clientIp;

        if (!rateLimiterService.isAllowed(rateLimitKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", "Too many requests, please try again later"));
        }

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

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result, HttpServletRequest httpRequest) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        return authService.login(loginRequest, httpRequest);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, BindingResult result, HttpServletRequest httpRequest) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }
        return authService.forgotPassword(request.getIdentifier(), httpRequest);
    }

    @GetMapping("/reset_password/{token}")
    public ResponseEntity<?> checkResetToken(@PathVariable("token") String token, HttpServletRequest request) {
        return authService.checkResetToken(token, request);
    }

    @PostMapping("/reset_password/{token}")
    public ResponseEntity<?> resetPassword(@Valid @PathVariable("token") String token, @RequestBody ResetPasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        return authService.resetPassword(token, request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Delete the cookie by setting maxAge to 0
        ResponseCookie cookie = ResponseCookie.from("token", "")
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .sameSite("None")
                                .maxAge(0) // instantly expire
                                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(Map.of("message", "Logout successfully"));
    }
}
