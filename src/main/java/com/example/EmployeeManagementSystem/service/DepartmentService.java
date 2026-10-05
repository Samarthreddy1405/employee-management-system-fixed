package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.request.CreateDepartmentRequestDTO;
import com.example.employeemanagementsystem.dto.response.DepartmentResponseDTO;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponseDTO> getAllDepartments();
    DepartmentResponseDTO createDepartment(CreateDepartmentRequestDTO request);
}
