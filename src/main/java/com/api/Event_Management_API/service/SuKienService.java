package com.api.Event_Management_API.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.SuKien.CUSuKienRequest;
import com.api.Event_Management_API.util.FileUploadUtil;

@Service
public class SuKienService {
    public ResponseEntity<?> addSuKien(CUSuKienRequest request) {
        try {
            String imagePath = FileUploadUtil.saveImageFile(request.getAnhSuKien());

            return ResponseEntity.ok(Map.of("imagePath", imagePath));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Can't save file"));
        }
    }
}
