package com.example.employeemanagementsystem.handler;

import com.example.employee.jooq.generated.tables.records.DepartmentsRecord;
import com.example.employee.jooq.generated.tables.records.DesignationsRecord;
import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.response.EmployeeResponseDTO;
import com.example.employeemanagementsystem.exception.EntityNotFoundException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.repository.DepartmentRepository;
import com.example.employeemanagementsystem.repository.DesignationRepository;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetEmployeeHandler {

    private final EmployeeRepository    employeeRepository;
    private final DepartmentRepository  departmentRepository;
    private final DesignationRepository designationRepository;
    private final EmployeeMapper        employeeMapper;

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployee(UUID id) {
        EmployeesRecord employee = employeeRepository.findById(id);
        if (employee == null) {
            throw new EntityNotFoundException("Employee not found with id: " + id);
        }
        return enrichResponse(employeeMapper.toResponse(employee), employee);
    }

    EmployeeResponseDTO enrichResponse(EmployeeResponseDTO dto, EmployeesRecord record) {
        if (record.getDepartmentId() != null) {
            DepartmentsRecord dept = departmentRepository.findById(record.getDepartmentId());
            if (dept != null) dto.setDepartmentName(dept.getName());
        }
        if (record.getDesignationId() != null) {
            DesignationsRecord desig = designationRepository.findById(record.getDesignationId());
            if (desig != null) dto.setDesignationName(desig.getName());
        }
        return dto;
    }
}
