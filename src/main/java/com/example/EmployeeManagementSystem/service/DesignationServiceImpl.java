package com.example.employeemanagementsystem.service;

import com.example.employee.jooq.generated.tables.records.DepartmentsRecord;
import com.example.employee.jooq.generated.tables.records.DesignationsRecord;
import com.example.employeemanagementsystem.dto.request.CreateDesignationRequestDTO;
import com.example.employeemanagementsystem.dto.response.DesignationResponseDTO;
import com.example.employeemanagementsystem.exception.EntityNotFoundException;
import com.example.employeemanagementsystem.repository.DepartmentRepository;
import com.example.employeemanagementsystem.repository.DesignationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository designationRepository;
    private final DepartmentRepository  departmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DesignationResponseDTO> getAllDesignations(UUID departmentId) {
        List<DesignationsRecord> records = (departmentId != null)
                ? designationRepository.findByDepartmentId(departmentId)
                : designationRepository.findAll();

        return records.stream()
                .map(r -> toDTO(r, null))
                .toList();
    }

    @Override
    @Transactional
    public DesignationResponseDTO createDesignation(CreateDesignationRequestDTO request) {
        DepartmentsRecord dept = departmentRepository.findById(request.getDepartmentId());
        if (dept == null) {
            throw new EntityNotFoundException(
                    "Department not found with id: " + request.getDepartmentId());
        }

        String name = request.getName().trim();
        if (designationRepository.existsByNameAndDepartmentId(name, request.getDepartmentId())) {
            throw new IllegalArgumentException(
                    "Designation '" + name + "' already exists in department '" + dept.getName() + "'");
        }

        DesignationsRecord saved = designationRepository.save(name, request.getDepartmentId());
        return toDTO(saved, dept.getName());
    }

    private DesignationResponseDTO toDTO(DesignationsRecord r, String deptName) {
        // When deptName is not pre-loaded, lazily resolve it
        String resolvedDeptName = deptName;
        if (resolvedDeptName == null) {
            DepartmentsRecord dept = departmentRepository.findById(r.getDepartmentId());
            resolvedDeptName = (dept != null) ? dept.getName() : null;
        }
        return DesignationResponseDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .departmentId(r.getDepartmentId())
                .departmentName(resolvedDeptName)
                .build();
    }
}
