package com.api.Event_Management_API.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "SuKien")
@Data
public class SuKien {
    @Id
    private String maSuKien;

    @NotBlank(message = "Please enter event name")
    private String tenSuKien;

    @NotBlank(message = "Please enter event description")
    private String moTa;

    @Pattern(regexp = "^/img/[^/]{1,29}.(png|jpg|jpeg)$", message = "Invalid image name")
    private String anhSuKien;
}
