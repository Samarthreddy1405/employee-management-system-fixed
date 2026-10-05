package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.request.CreateEmployeeRequestDTO;
import com.example.employeemanagementsystem.dto.request.GetAllEmployeesRequestDTO;
import com.example.employeemanagementsystem.dto.request.UpdateEmployeeRequestDTO;

import com.example.employeemanagementsystem.dto.response.ActivateDeactivateEmployeeResponseDTO;
import com.example.employeemanagementsystem.dto.response.EmployeeResponseDTO;
import com.example.employeemanagementsystem.dto.response.GetAllEmployeesResponseDTO;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeResponseDTO getEmployee(UUID id);

    EmployeeResponseDTO createEmployee(
            CreateEmployeeRequestDTO requestDTO);

    EmployeeResponseDTO updateEmployee(
            UUID id,
            UpdateEmployeeRequestDTO requestDTO);

    GetAllEmployeesResponseDTO getAllEmployees(
            GetAllEmployeesRequestDTO request);

    List<EmployeeResponseDTO> getEmployeesByOrganization(
            UUID orgId);

    ActivateDeactivateEmployeeResponseDTO activateEmployee(UUID id);

    ActivateDeactivateEmployeeResponseDTO deactivateEmployee(UUID id);
}