package com.example.employeemanagementsystem.service;

import com.example.employee.jooq.generated.tables.records.DepartmentsRecord;
import com.example.employeemanagementsystem.dto.request.CreateDepartmentRequestDTO;
import com.example.employeemanagementsystem.dto.response.DepartmentResponseDTO;
import com.example.employeemanagementsystem.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public DepartmentResponseDTO createDepartment(CreateDepartmentRequestDTO request) {
        String name = request.getName().trim();
        if (departmentRepository.existsByName(name)) {
            throw new IllegalArgumentException("Department '" + name + "' already exists");
        }
        DepartmentsRecord saved = departmentRepository.save(name);
        return toDTO(saved);
    }

    private DepartmentResponseDTO toDTO(DepartmentsRecord r) {
        return DepartmentResponseDTO.builder()
                .id(r.getId())
                .name(r.getName())
                .build();
    }
}
