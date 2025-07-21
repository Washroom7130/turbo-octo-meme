package com.api.Event_Management_API.controller.cauhoi;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.Event_Management_API.dto.CauHoi.AnswerCauHoiRequest;
import com.api.Event_Management_API.dto.CauHoi.CreateCauHoiRequest;
import com.api.Event_Management_API.service.CauHoiService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cauhoi")
public class CauHoiController {
    private final CauHoiService cauHoiService;

    public CauHoiController(CauHoiService cauHoiService) {
        this.cauHoiService = cauHoiService;
    }

    @PostMapping("/{maDangKy}/add")
    @PreAuthorize("hasAnyAuthority('KhachHang')")
    public ResponseEntity<?> addCauHoi(
        @PathVariable String maDangKy,
        @RequestBody @Valid CreateCauHoiRequest request,
        BindingResult result,
        HttpServletRequest httpReq
    ) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        return cauHoiService.addCauHoi(maDangKy, request, httpReq);
    }

    @PostMapping("/answer/{maCauHoi}")
    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy')")
    public ResponseEntity<?> answerCauHoi(
        @PathVariable Integer maCauHoi,
        @RequestBody @Valid AnswerCauHoiRequest request,
        BindingResult result,
        HttpServletRequest httpReq
    ) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        return cauHoiService.answerCauHoi(maCauHoi, request, httpReq);
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy', 'KhachHang')")
    public ResponseEntity<?> getAllCauHoi(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Integer maSuKien,
        HttpServletRequest request
    ) {
        return cauHoiService.getAllCauHoi(page, size, request, search, maSuKien);
    }

    @GetMapping("/get/{maCauHoi}")
    @PreAuthorize("hasAnyAuthority('NhanVien', 'QuanLy', 'KhachHang')")
    public ResponseEntity<?> getOne(@PathVariable Integer maCauHoi, HttpServletRequest request) {
        return cauHoiService.getById(maCauHoi, request);
    }

    @GetMapping("/get/sukien/{maSuKien}")
    @PreAuthorize("hasAnyAuthority( 'KhachHang')")
    public ResponseEntity<?> getBySuKien(@PathVariable Integer maSuKien, HttpServletRequest request) {
        return cauHoiService.getBySuKien(maSuKien, request);
    }

}
