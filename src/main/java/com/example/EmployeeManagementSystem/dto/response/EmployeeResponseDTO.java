package com.example.employeemanagementsystem.dto.response;

import com.example.employeemanagementsystem.dto.metadata.EmployeeMetadata;
import lombok.Data;

import java.util.UUID;

@Data
public class EmployeeResponseDTO {
    private UUID   id;
    private UUID   orgId;
    private String name;
    private String email;
    private String phone;

    // Department — both id (for client forms) and name (for display)
    private UUID   departmentId;
    private String departmentName;

    // Designation — both id and name
    private UUID   designationId;
    private String designationName;

    private Boolean isActive;

    /** Full metadata from JSONB column */
    private EmployeeMetadata metadata;
}
