package com.example.employeemanagementsystem.handler;

import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.response.ActivateDeactivateEmployeeResponseDTO;
import com.example.employeemanagementsystem.exception.EntityNotFoundException;
import com.example.employeemanagementsystem.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ActivateDeactivateEmployeeHandler {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public ActivateDeactivateEmployeeResponseDTO handleActivate(UUID id) {

        EmployeesRecord employee = employeeRepository.findById(id);

        if (employee == null) {
            throw new EntityNotFoundException(
                    "Employee not found with id: " + id
            );
        }

        employeeRepository.updateStatus(id, true);

        return ActivateDeactivateEmployeeResponseDTO.builder()
                .id(id)
                .isActive(true)
                .build();
    }

    @Transactional
    public ActivateDeactivateEmployeeResponseDTO handleDeactivate(UUID id) {

        EmployeesRecord employee = employeeRepository.findById(id);

        if (employee == null) {
            throw new EntityNotFoundException(
                    "Employee not found with id: " + id
            );
        }

        employeeRepository.updateStatus(id, false);

        return ActivateDeactivateEmployeeResponseDTO.builder()
                .id(id)
                .isActive(false)
                .build();
    }
}