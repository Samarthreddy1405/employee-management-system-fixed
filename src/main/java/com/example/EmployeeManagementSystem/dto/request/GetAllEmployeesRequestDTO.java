package com.example.employeemanagementsystem.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllEmployeesRequestDTO {

    @Builder.Default @Min(1)
    private Integer page = 1;

    @Builder.Default @Min(1) @Max(100)
    private Integer pageSize = 10;

    @Builder.Default
    private String nameFilter = "";

    /** Filter by department UUID (replaces old departmentFilter String) */
    private UUID departmentId;

    private UUID orgId;
    private Boolean isActive;
    private boolean sortByNameAsc;
    private boolean sortByNameDesc;
    private boolean sortByCreatedDateDesc;
}
