package com.example.employeemanagementsystem.handler;

import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.request.GetAllEmployeesRequestDTO;
import com.example.employeemanagementsystem.dto.response.EmployeeListItemDTO;
import com.example.employeemanagementsystem.dto.response.GetAllEmployeesResponseDTO;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetAllEmployeesHandler {

    private final EmployeeRepository employeeRepository;

    @Value("${employee.default.page-size:10}")
    private Integer defaultPageSize;

    @Transactional(readOnly = true)
    public GetAllEmployeesResponseDTO handle(GetAllEmployeesRequestDTO request) {
        if (request.getPage() == null || request.getPage() < 1) request.setPage(1);
        if (request.getPageSize() == null || request.getPageSize() < 1)
            request.setPageSize(defaultPageSize);

        List<EmployeesRecord> employees = employeeRepository.findAllWithFilters(request);
        long totalCount = employeeRepository.countWithFilters(request);

        List<EmployeeListItemDTO> items = employees.stream()
                .map(r -> EmployeeListItemDTO.builder()
                        .id(r.getId())
                        .orgId(r.getOrgId())
                        .isActive(r.getIsActive())
                        .build())
                .toList();

        int totalPages = (int) Math.ceil((double) totalCount / request.getPageSize());

        return GetAllEmployeesResponseDTO.builder()
                .employees(items)
                .currentPage(request.getPage())
                .pageSize(request.getPageSize())
                .totalCount(totalCount)
                .totalPages(totalPages)
                .hasNext(request.getPage() < totalPages)
                .hasPrevious(request.getPage() > 1)
                .currentPageSize(items.size())
                .build();
    }
}
