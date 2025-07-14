package com.api.Event_Management_API.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.Event_Management_API.dto.DanhMucSuKien.CreateDanhMucRequest;
import com.api.Event_Management_API.dto.DanhMucSuKien.GetDanhMucResponse;
import com.api.Event_Management_API.model.DanhMucSuKien;
import com.api.Event_Management_API.repository.DanhMucSuKienRepository;
import com.api.Event_Management_API.repository.SuKienRepository;

import ch.qos.logback.core.joran.util.beans.BeanUtil;

@Service
public class DanhMucSuKienService {
    
    @Autowired
    private DanhMucSuKienRepository danhMucSuKienRepo;

    @Autowired
    private SuKienRepository suKienRepo;

    public ResponseEntity<?> createDanhMuc(CreateDanhMucRequest request) {
        // Check for duplicated name
        if (danhMucSuKienRepo.findByTenDanhMuc(request.getTenDanhMuc()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Category name already exist"));
        }

        DanhMucSuKien newDanhMuc = new DanhMucSuKien();
        newDanhMuc.setTenDanhMuc(request.getTenDanhMuc());

        danhMucSuKienRepo.save(newDanhMuc);

        return ResponseEntity.ok(Map.of("message", "New category created successfully"));
    }

    public ResponseEntity<?> updateDanhMuc(CreateDanhMucRequest request, Integer maDanhMuc) {
        
        Optional<DanhMucSuKien> danhMucOptional = danhMucSuKienRepo.findById(maDanhMuc);
        
        // Check if category exist
        if(danhMucOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Category not found"));
        }

        // Check for duplicated name
        if (danhMucSuKienRepo.findByTenDanhMuc(request.getTenDanhMuc()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Category name already exist"));
        }

        DanhMucSuKien danhMucSuKien = danhMucOptional.get();
        danhMucSuKien.setTenDanhMuc(request.getTenDanhMuc());

        danhMucSuKienRepo.save(danhMucSuKien);

        return ResponseEntity.ok(Map.of("message", "Category updated successfully"));
    }

    public ResponseEntity<?> getAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DanhMucSuKien> pageResult;

        if (search != null && !search.trim().isEmpty()) {
            pageResult = danhMucSuKienRepo.findByTenDanhMucContainingIgnoreCase(search.trim(), pageable);
        } else {
            pageResult = danhMucSuKienRepo.findAll(pageable);
        }

        Page<GetDanhMucResponse> responsePage = pageResult.map(dm -> {
            return new GetDanhMucResponse(
                dm.getMaDanhMuc(),
                dm.getTenDanhMuc()
            );
        });

        return ResponseEntity.ok(responsePage);
    }

    public ResponseEntity<?> getOne(Integer maDanhMuc) {
        Optional<DanhMucSuKien> danhMucOptional = danhMucSuKienRepo.findById(maDanhMuc);

        // Check if category exist
        if(danhMucOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Category not found"));
        }

        DanhMucSuKien danhMucSuKien = danhMucOptional.get();

        GetDanhMucResponse response = new GetDanhMucResponse();
        BeanUtils.copyProperties(danhMucSuKien, response);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> delete(Integer maDanhMuc) {
        Optional<DanhMucSuKien> danhMucOptional = danhMucSuKienRepo.findById(maDanhMuc);

        // Check if category exist
        if(danhMucOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Category not found"));
        }

        // Check if category in use
        if (suKienRepo.existsByMaDanhMuc(maDanhMuc)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Category is in use"));
        }

        danhMucSuKienRepo.deleteById(maDanhMuc);
        return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
    }
}
