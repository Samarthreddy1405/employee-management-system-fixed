package com.example.employeemanagementsystem.dto.request;

import com.example.employeemanagementsystem.dto.metadata.EmployeeMetadata;
import com.example.employeemanagementsystem.validation.ValidEmployeeMetadata;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateEmployeeRequestDTO {

    @NotNull(message = "Organization ID is required")
    private UUID orgId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    /** UUID FK → departments.id  (nullable — dept is optional) */
    private UUID departmentId;

    /** UUID FK → designations.id  (nullable — designation is optional) */
    private UUID designationId;

    @Valid
    @ValidEmployeeMetadata
    private EmployeeMetadata metadata;
}
