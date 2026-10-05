package com.example.employeemanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateDesignationRequestDTO {

    @NotBlank(message = "Designation name is required")
    @Size(max = 100, message = "Designation name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Department ID is required")
    private UUID departmentId;
}
