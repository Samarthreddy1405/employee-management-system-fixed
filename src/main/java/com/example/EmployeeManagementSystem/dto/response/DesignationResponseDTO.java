package com.example.employeemanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignationResponseDTO {
    private UUID   id;
    private String name;
    private UUID   departmentId;
    private String departmentName;
}
