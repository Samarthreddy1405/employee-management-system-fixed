package com.example.employeemanagementsystem.handler;

import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.response.EmployeeResponseDTO;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetEmployeesByOrganizationHandler {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper     employeeMapper;
    private final GetEmployeeHandler getEmployeeHandler;   // reuse name-enrichment

    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> handle(UUID orgId) {
        List<EmployeesRecord> employees = employeeRepository.findByOrganizationId(orgId);
        return employees.stream()
                .map(r -> getEmployeeHandler.enrichResponse(employeeMapper.toResponse(r), r))
                .toList();
    }
}
