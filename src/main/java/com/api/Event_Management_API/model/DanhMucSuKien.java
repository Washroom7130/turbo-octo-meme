package com.api.Event_Management_API.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "DanhMucSuKien")
@Data
public class DanhMucSuKien {
    @Id
    private Integer maDanhMuc;

    @NotBlank(message = "Please enter category name")
    private String tenDanhMuc;
}
