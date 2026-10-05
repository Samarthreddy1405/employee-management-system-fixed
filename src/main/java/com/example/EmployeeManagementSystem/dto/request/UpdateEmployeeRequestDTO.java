package com.example.employeemanagementsystem.dto.request;

import com.example.employeemanagementsystem.dto.metadata.EmployeeMetadata;
import com.example.employeemanagementsystem.validation.ValidEmployeeMetadata;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateEmployeeRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    /** UUID FK → departments.id  (nullable) */
    private UUID departmentId;

    /** UUID FK → designations.id  (nullable) */
    private UUID designationId;

    @Valid
    @ValidEmployeeMetadata
    private EmployeeMetadata metadata;
}
