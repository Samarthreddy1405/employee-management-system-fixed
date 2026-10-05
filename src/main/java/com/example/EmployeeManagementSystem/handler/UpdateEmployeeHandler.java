package com.example.employeemanagementsystem.handler;

import com.example.employee.jooq.generated.tables.records.DepartmentsRecord;
import com.example.employee.jooq.generated.tables.records.DesignationsRecord;
import com.example.employee.jooq.generated.tables.records.EmployeesRecord;
import com.example.employeemanagementsystem.dto.request.UpdateEmployeeRequestDTO;
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
public class UpdateEmployeeHandler {

    private final EmployeeRepository    employeeRepository;
    private final DepartmentRepository  departmentRepository;
    private final DesignationRepository designationRepository;
    private final EmployeeMapper        employeeMapper;

    @Transactional
    public EmployeeResponseDTO handle(UUID id, UpdateEmployeeRequestDTO request) {
        log.info("Updating employee with id: {}", id);

        EmployeesRecord employee = employeeRepository.findById(id);
        if (employee == null) {
            throw new EntityNotFoundException("Employee not found with id: " + id);
        }

        // ── email uniqueness ──────────────────────────────────────────────────
        String email = (request.getEmail() != null) ? request.getEmail().trim() : null;
        if (email != null && employeeRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("Employee with email already exists");
        }

        // ── phone uniqueness ──────────────────────────────────────────────────
        String phone = (request.getPhone() != null) ? request.getPhone().trim() : null;
        if (phone != null && employeeRepository.existsByPhoneAndIdNot(phone, id)) {
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

        // ── merge + persist ───────────────────────────────────────────────────
        employeeMapper.updateEmployee(request, employee);
        employee.setUpdatedOn(LocalDateTime.now());
        if (request.getMetadata() != null) {
            employee.setMetadata(employeeMapper.toJsonb(request.getMetadata()));
        }

        EmployeesRecord updated = employeeRepository.update(id, employee);

        // ── enrich response ───────────────────────────────────────────────────
        EmployeeResponseDTO response = employeeMapper.toResponse(updated);
        if (dept  != null) response.setDepartmentName(dept.getName());
        if (desig != null) response.setDesignationName(desig.getName());

        // Also carry forward names if dept/desig not changed but still present
        if (dept == null && updated.getDepartmentId() != null) {
            DepartmentsRecord existing = departmentRepository.findById(updated.getDepartmentId());
            if (existing != null) response.setDepartmentName(existing.getName());
        }
        if (desig == null && updated.getDesignationId() != null) {
            DesignationsRecord existing = designationRepository.findById(updated.getDesignationId());
            if (existing != null) response.setDesignationName(existing.getName());
        }

        return response;
    }
}
