package com.example.employeemanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllEmployeesResponseDTO {

    /** Slim list items — each contains only id, orgId, isActive. */
    private List<EmployeeListItemDTO> employees;

    private Integer currentPage;

    private Integer pageSize;

    private Long totalCount;

    private Integer totalPages;

    private Boolean hasNext;

    private Boolean hasPrevious;

    private Integer currentPageSize;

    private String appliedFilters;
}
