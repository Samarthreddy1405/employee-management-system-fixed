package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dto.request.CreateEmployeeRequestDTO;
import com.example.employeemanagementsystem.dto.request.GetAllEmployeesRequestDTO;
import com.example.employeemanagementsystem.dto.request.UpdateEmployeeRequestDTO;

import com.example.employeemanagementsystem.dto.response.ActivateDeactivateEmployeeResponseDTO;
import com.example.employeemanagementsystem.dto.response.EmployeeResponseDTO;
import com.example.employeemanagementsystem.dto.response.GetAllEmployeesResponseDTO;

import com.example.employeemanagementsystem.handler.ActivateDeactivateEmployeeHandler;
import com.example.employeemanagementsystem.handler.CreateEmployeeHandler;
import com.example.employeemanagementsystem.handler.GetAllEmployeesHandler;
import com.example.employeemanagementsystem.handler.GetEmployeeHandler;
import com.example.employeemanagementsystem.handler.UpdateEmployeeHandler;
import com.example.employeemanagementsystem.handler.GetEmployeesByOrganizationHandler;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final GetEmployeeHandler getEmployeeHandler;
    private final CreateEmployeeHandler createEmployeeHandler;
    private final UpdateEmployeeHandler updateEmployeeHandler;
    private final GetAllEmployeesHandler getAllEmployeesHandler;
    private final ActivateDeactivateEmployeeHandler activateDeactivateEmployeeHandler;
    private final GetEmployeesByOrganizationHandler getEmployeesByOrganizationHandler;

    public EmployeeServiceImpl(
            GetEmployeeHandler employeeGetHandler,
            CreateEmployeeHandler employeeCreateHandler,
            UpdateEmployeeHandler employeeUpdateHandler,
            GetAllEmployeesHandler employeeGetAllHandler,
            ActivateDeactivateEmployeeHandler employeeActivateDeactivateHandler,
            GetEmployeesByOrganizationHandler employeeGetByOrganizationHandler) {

        getEmployeeHandler = employeeGetHandler;
        createEmployeeHandler = employeeCreateHandler;
        updateEmployeeHandler = employeeUpdateHandler;
        getAllEmployeesHandler = employeeGetAllHandler;
        activateDeactivateEmployeeHandler = employeeActivateDeactivateHandler;
        getEmployeesByOrganizationHandler =
                employeeGetByOrganizationHandler;
    }

    @Override
    public EmployeeResponseDTO getEmployee(UUID id) {

        return getEmployeeHandler.getEmployee(id);
    }

    @Override
    public EmployeeResponseDTO createEmployee(
            CreateEmployeeRequestDTO requestDTO) {

        return createEmployeeHandler.handle(requestDTO);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(
            UUID id,
            UpdateEmployeeRequestDTO requestDTO) {

        return updateEmployeeHandler.handle(id, requestDTO);
    }

    @Override
    public GetAllEmployeesResponseDTO getAllEmployees(
            GetAllEmployeesRequestDTO request) {

        return getAllEmployeesHandler.handle(request);
    }

    @Override
    public List<EmployeeResponseDTO> getEmployeesByOrganization(
            UUID orgId) {

        return getEmployeesByOrganizationHandler.handle(orgId);
    }

    @Override
    public ActivateDeactivateEmployeeResponseDTO activateEmployee(UUID id) {

        return activateDeactivateEmployeeHandler.handleActivate(id);
    }

    @Override
    public ActivateDeactivateEmployeeResponseDTO deactivateEmployee(UUID id) {

        return activateDeactivateEmployeeHandler.handleDeactivate(id);
    }
}