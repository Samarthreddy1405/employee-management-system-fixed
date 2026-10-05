package com.example.employeemanagementsystem.handler;

import com.example.employee.jooq.generated.tables.records.DepartmentsRecord;
import com.example.employee.jooq.generated.tables.records.DesignationsRecord;
import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.request.CreateEmployeeRequestDTO;
import com.example.employeemanagementsystem.dto.response.EmployeeResponseDTO;
import com.example.employeemanagementsystem.exception.EntityNotFoundException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.repository.DepartmentRepository;
import com.example.employeemanagementsystem.repository.DesignationRepository;
import com.example.employeemanagementsystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateEmployeeHandler {

    private final EmployeeRepository    employeeRepository;
    private final DepartmentRepository  departmentRepository;
    private final DesignationRepository designationRepository;
    private final EmployeeMapper        employeeMapper;

    @Transactional
    public EmployeeResponseDTO handle(CreateEmployeeRequestDTO request) {
        log.info("Creating new employee");

        // ── email uniqueness ──────────────────────────────────────────────────
        String email = request.getEmail().trim();
        if (employeeRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Employee with email already exists");
        }

        // ── phone uniqueness ──────────────────────────────────────────────────
        String phone = (request.getPhone() != null) ? request.getPhone().trim() : null;
        if (phone != null && employeeRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Employee with phone number already exists");
        }

        // ── department FK validation ──────────────────────────────────────────
        DepartmentsRecord dept = null;
        if (request.getDepartmentId() != null) {
            dept = departmentRepository.findById(request.getDepartmentId());
            if (dept == null) {
                throw new EntityNotFoundException(
                        "Department not found with id: " + request.getDepartmentId());
            }
        }

        // ── designation FK validation (must belong to the given dept) ─────────
        DesignationsRecord desig = null;
        if (request.getDesignationId() != null) {
            desig = designationRepository.findById(request.getDesignationId());
            if (desig == null) {
                throw new EntityNotFoundException(
                        "Designation not found with id: " + request.getDesignationId());
            }
            if (dept != null && !desig.getDepartmentId().equals(dept.getId())) {
                throw new IllegalArgumentException(
                        "Designation '" + desig.getName() + "' does not belong to department '"
                                + dept.getName() + "'");
            }
        }

        // ── build and persist ────────────────────────────────────────────────
        EmployeesRecord employee = employeeMapper.toNewEmployee(request);
        employee.setId(UUID.randomUUID());
        employee.setIsActive(true);
        employee.setCreatedOn(LocalDateTime.now());
        employee.setUpdatedOn(LocalDateTime.now());
        if (request.getMetadata() != null) {
            employee.setMetadata(employeeMapper.toJsonb(request.getMetadata()));
        }

        EmployeesRecord saved = employeeRepository.save(employee);

        // ── enrich response with resolved names ───────────────────────────────
        EmployeeResponseDTO response = employeeMapper.toResponse(saved);
        if (dept  != null) response.setDepartmentName(dept.getName());
        if (desig != null) response.setDesignationName(desig.getName());
        return response;
    }
}
